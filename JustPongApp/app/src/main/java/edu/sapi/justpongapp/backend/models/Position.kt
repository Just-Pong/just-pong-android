package edu.sapi.justpongapp.backend.models

import java.lang.Double.min

class Position(private val maxHeight: Double, private val maxVelocity: Double) {
    private var currPosition: Double = 0.0;

    fun move(amount: Double) {
        currPosition += min(amount, maxVelocity);

        if (currPosition < 0) {
            currPosition = 0.0;
        } else if (currPosition > maxHeight) {
            currPosition = maxHeight;
        }
    }

    fun getPosition(): Double {
        return this.currPosition;
    }
}