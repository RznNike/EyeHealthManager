package ru.rznnike.eyehealthmanager.domain.model.analysis

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.rznnike.eyehealthmanager.domain.model.test.TestResult

@Parcelize
data class AnalysisResult(
    val testResults: List<TestResult>,
    var leftEyeAnalysisResult: SingleEyeAnalysisResult,
    var rightEyeAnalysisResult: SingleEyeAnalysisResult,
    var showWarningAboutVision: Boolean = false,
    var lastResultRecognizedAsNoise: Boolean
) : Parcelable