package edu.sapi.justpongapp.backend.models

import java.lang.Double.min

class Position(private val maxHeight: Double, private val maxVelocity: Double) {
    private var _currPosition: Double = 0.0;
    val currPosition get() = _currPosition

    fun move(amount: Double) {
        _currPosition += min(amount, maxVelocity);

        if (_currPosition < 0) {
            _currPosition = 0.0;
        } else if (_currPosition > maxHeight) {
            _currPosition = maxHeight;
        }
    }
}