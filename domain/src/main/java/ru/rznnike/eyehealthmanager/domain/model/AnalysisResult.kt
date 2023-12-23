package ru.rznnike.eyehealthmanager.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnalysisResult(
    val testResults: List<TestResult>,
    var leftEyeAnalysisResult: SingleEyeAnalysisResult,
    var rightEyeAnalysisResult: SingleEyeAnalysisResult,
    var showWarningAboutVision: Boolean = false,
    var lastResultRecognizedAsNoise: Boolean
) : Parcelable