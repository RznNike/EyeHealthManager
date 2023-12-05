package ru.rznnike.eyehealthmanager.domain.model

class LinearFunction(
    var a: Double = 0.0,
    var b: Double = 0.0
) {
    /**
    * y = a*x+b
    * */
    fun getY(x: Double): Double {
        return a * x + b
    }
}