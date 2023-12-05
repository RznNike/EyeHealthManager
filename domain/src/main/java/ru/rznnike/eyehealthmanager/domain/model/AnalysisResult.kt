package ru.rznnike.eyehealthmanager.domain.model

data class AnalysisResult(
    val testResults: List<TestResult>,
    var leftEyeAnalysisResult: SingleEyeAnalysisResult,
    var rightEyeAnalysisResult: SingleEyeAnalysisResult,
    var showWarningAboutVision: Boolean = false,
    var lastResultRecognizedAsNoise: Boolean
)