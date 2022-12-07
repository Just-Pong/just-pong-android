package edu.sapi.justpongapp.backend

import android.content.Context
import android.content.ContextWrapper
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import edu.sapi.justpongapp.backend.models.Position
import java.util.*
import kotlin.math.abs

class MovementService(context: Context) : ContextWrapper(context), SensorEventListener {
    val TAG = "MovementService";

    private var sensorManager: SensorManager;
    private var accelerometerSensor: Sensor? = null;
    private var gravitySensor: Sensor? = null;
    private var magneticSensor: Sensor? = null;

    private var lastGravityData: FloatArray? = null;
    private var lastMagneticData: FloatArray? = null;
    private var lastEarthAccelData: FloatArray? = null;
    private var accelDataQueue: Queue<FloatArray> = LinkedList();
    private var avgGroupQueue: Queue<FloatArray> = LinkedList();
    private var position: Position = Position(MAX_POSITION, MAX_VELOCITY);

    private lateinit var mainHandler: Handler;

    companion object {
        // Might need adjustment
        val THRESHOLD: Double = 0.5;
        val TIME: Long = 25;
        val AVG_GROUP_LENGTH: Int = 5;
        val MAX_POSITION: Double = 1000.0;
        val MAX_VELOCITY: Double = 250.0;
    }

    fun getPositiond(): Double {
        return position.getPosition();
    }

    private val processMovement = object: Runnable {
        override fun run() {
            mainHandler.postDelayed(this, TIME);

            var accDataSum = FloatArray(3);

            // TODO: Remove retroactive of movement

            //Log.d(TAG, accelDataQueue.size.toString());
            accelDataQueue.forEach {
                accDataSum[0] += it[0];
                accDataSum[1] += it[1];
                accDataSum[2] += it[2];
            }
            accelDataQueue.clear();
            avgGroupQueue.add(accDataSum);

            if (avgGroupQueue.size == AVG_GROUP_LENGTH) {
                var d: Double = 0.0;

                avgGroupQueue.forEach {
                    d += it[2] * AVG_GROUP_LENGTH * TIME;
                }

                avgGroupQueue.clear();

                position.move(d);

                Log.d(TAG, "$d");
                Log.d(TAG, "Current height: ${position.getPosition()}")
            }

            //Log.d(TAG, accDataSum.toList().toString());
            //Log.d(TAG, "d: $currHeight v: $velocity a: ${accDataSum[2]}");
        }
    }

    init {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager;

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mainHandler = Handler(Looper.getMainLooper());
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        Log.d(TAG, "Accuracy changed: $accuracy");
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                if ((lastGravityData == null) || (lastMagneticData == null)) {
                    Log.d(TAG, "Not ready to calculate movement");
                    return;
                }

                val R = FloatArray(9);
                val I = FloatArray(9);
                SensorManager.getRotationMatrix(R, I, lastGravityData, lastMagneticData);
                val A_D = event.values;
                val A_W = FloatArray(3);

                // A_W = {X, Y, Z}
                // X axis -> East
                // Y axis -> North Pole
                // Z axis -> Sky
                A_W[0] = R[0] * A_D[0] + R[1] * A_D[1] + R[2] * A_D[2];
                A_W[1] = R[3] * A_D[0] + R[4] * A_D[1] + R[5] * A_D[2];
                A_W[2] = R[6] * A_D[0] + R[7] * A_D[1] + R[8] * A_D[2];

                if (this.lastEarthAccelData != null) {
                    val nA_W: FloatArray = this.lastEarthAccelData!!;
                    nA_W[0] = (Math.round((nA_W[0] - A_W[0]) * 100.0) / 100.0).toFloat();
                    nA_W[1] = (Math.round((nA_W[1] - A_W[1]) * 100.0) / 100.0).toFloat();
                    nA_W[2] = (Math.round((nA_W[2] - A_W[2]) * 100.0) / 100.0).toFloat();

                    if (abs(nA_W.average()) > THRESHOLD) {
                        this.accelDataQueue.add(nA_W);
                    }
                }

                this.lastEarthAccelData = A_W;
            }
            Sensor.TYPE_GRAVITY -> {
                lastGravityData = event.values;
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                lastMagneticData = event.values;
            }
        }
    }

    fun pause() {
        sensorManager.unregisterListener(this);
        mainHandler.removeCallbacks(processMovement);
    }

    fun resume() {
        accelerometerSensor?.also { accelerometer ->
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        gravitySensor?.also { gravity ->
            sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        }
        magneticSensor?.also { magnetic ->
            sensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_NORMAL);
        }
        mainHandler.post(processMovement);
    }
}