package ru.rznnike.eyehealthmanager.domain.model.analysis

/**
 * y = a*x+b
 * */
class LinearFunction(
    val a: Double = 0.0,
    val b: Double = 0.0
) {
    fun getY(x: Double) = a * x + b
}