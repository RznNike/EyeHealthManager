package ru.rznnike.eyehealthmanager.domain.model.analysis

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnalysisParameters(
    var dateFrom: Long = 0,
    var dateTo: Long = 0,
    var analysisType: AnalysisType = AnalysisType.CONSOLIDATED_REPORT,
    var applyDynamicCorrections: Boolean = false
) : Parcelable