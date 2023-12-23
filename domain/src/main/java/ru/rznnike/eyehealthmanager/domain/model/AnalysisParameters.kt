package ru.rznnike.eyehealthmanager.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.rznnike.eyehealthmanager.domain.model.enums.AnalysisType

@Parcelize
data class AnalysisParameters(
    var dateFrom: Long = 0,
    var dateTo: Long = 0,
    var analysisType: AnalysisType = AnalysisType.CONSOLIDATED_REPORT,
    var applyDynamicCorrections: Boolean = false
) : Parcelable