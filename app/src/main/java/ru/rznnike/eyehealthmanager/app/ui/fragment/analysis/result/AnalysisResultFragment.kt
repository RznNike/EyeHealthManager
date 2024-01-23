package ru.rznnike.eyehealthmanager.app.ui.fragment.analysis.result

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.analysis.result.AnalysisResultPresenter
import ru.rznnike.eyehealthmanager.app.presentation.analysis.result.AnalysisResultView
import ru.rznnike.eyehealthmanager.app.ui.item.TestResultItem
import ru.rznnike.eyehealthmanager.app.ui.view.EmptyDividerDecoration
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToMargin
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.context
import ru.rznnike.eyehealthmanager.app.utils.extensions.createFastAdapter
import ru.rznnike.eyehealthmanager.app.utils.extensions.getParcelableArg
import ru.rznnike.eyehealthmanager.app.utils.extensions.getString
import ru.rznnike.eyehealthmanager.app.utils.extensions.resources
import ru.rznnike.eyehealthmanager.app.utils.extensions.toHtmlSpanned
import ru.rznnike.eyehealthmanager.databinding.FragmentAnalysisResultBinding
import ru.rznnike.eyehealthmanager.domain.model.AnalysisResult
import ru.rznnike.eyehealthmanager.domain.model.AnalysisStatistics
import ru.rznnike.eyehealthmanager.domain.model.SingleEyeAnalysisResult
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType
import ru.rznnike.eyehealthmanager.domain.utils.toDate
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class AnalysisResultFragment : BaseFragment(R.layout.fragment_analysis_result), AnalysisResultView {
    @InjectPresenter
    lateinit var presenter: AnalysisResultPresenter

    @ProvidePresenter
    fun providePresenter() = AnalysisResultPresenter(
        result = getParcelableArg(RESULT)!!
    )

    private val binding by viewBinding(FragmentAnalysisResultBinding::bind)

    private lateinit var adapter: FastAdapter<IItem<*>>
    private lateinit var itemAdapter: ItemAdapter<IItem<*>>

    private var xAxisMinimum = Float.MAX_VALUE
    private var xAxisMaximum = Float.MIN_VALUE
    private var yAxisMaximum = Float.MIN_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            layoutToolbarContainer.addSystemWindowInsetToPadding(top = true)
            layoutScrollableContent.addSystemWindowInsetToPadding(bottom = true)
            buttonClose.addSystemWindowInsetToMargin(bottom = true)
        }
        initToolbar()
        initRecyclerView()
        initOnClickListeners()
    }

    private fun initToolbar() = binding.toolbar.apply {
        textViewToolbarHeader.setText(R.string.analysis_report)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initAdapter() {
        itemAdapter = ItemAdapter()
        adapter = createFastAdapter(itemAdapter)
        adapter.setHasStableIds(true)
    }

    private fun initRecyclerView() = binding.apply {
        recyclerViewTestResults.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@AnalysisResultFragment.adapter
            itemAnimator = null
            addItemDecoration(
                EmptyDividerDecoration(
                    context = requireContext(),
                    cardInsets = R.dimen.baseline_grid_8,
                    applyOutsideDecoration = false
                )
            )
        }
    }

    private fun initOnClickListeners() = binding.apply {
        buttonClose.setOnClickListener {
            routerFinishFlow()
        }
    }

    override fun populateData(analysisResult: AnalysisResult) {
        binding.apply {
            initChart()
            addChartLine(analysisResult.leftEyeAnalysisResult, TestEyesType.LEFT)
            addChartLine(analysisResult.rightEyeAnalysisResult, TestEyesType.RIGHT)
            configureChartAxis()
            chartVisionDynamic.invalidate()
            chartVisionDynamic.notifyDataSetChanged()

            val statisticStringBuilder = StringBuilder()
            if (analysisResult.showWarningAboutVision) {
                statisticStringBuilder.append(getString(R.string.vision_problems_detected))
                statisticStringBuilder.appendLine(
                    " <font color=\"#FF3334\"><b>%s</b></font>".format(
                        getString(R.string.go_to_doctor)
                    )
                )
                statisticStringBuilder.appendLine()
            }

            statisticStringBuilder.appendLine(
                getString(R.string.average_vision_for_eye).format(
                    (analysisResult.leftEyeAnalysisResult.statistics?.visionAverageValue ?: 0) / 100f,
                    (analysisResult.rightEyeAnalysisResult.statistics?.visionAverageValue ?: 0) / 100f
                )
            )
            statisticStringBuilder.appendLine()

            fun getVisionDeltaPercent(statistics: AnalysisStatistics?) = statistics?.let {
                if (it.visionAverageValue > 0) {
                    it.visionDynamicValue.toFloat() / it.visionAverageValue * 100
                } else null
            } ?: 0f

            val daysCount = ((analysisResult.leftEyeAnalysisResult.statistics?.analysisPeriod ?: 0) / 1000 / 86400).toInt()
            statisticStringBuilder.appendLine(
                getString(R.string.delta_vision_for_eye).format(
                    daysCount, // ms to days
                    resources.getQuantityString(R.plurals.days, daysCount),
                    getVisionDeltaPercent(analysisResult.leftEyeAnalysisResult.statistics),
                    getVisionDeltaPercent(analysisResult.rightEyeAnalysisResult.statistics)
                )
            )
            statisticStringBuilder.appendLine()

            statisticStringBuilder.append(
                getString(R.string.eyes_vision_difference).format(
                    abs((analysisResult.leftEyeAnalysisResult.statistics?.visionAverageValue ?: 0)
                            - (analysisResult.rightEyeAnalysisResult.statistics?.visionAverageValue ?: 0)) / 100f
                )
            )

            textViewStatistic.text = statisticStringBuilder.toString()
                .replace("\n", "<br>")
                .toHtmlSpanned()

            itemAdapter.setNewList(
                analysisResult.testResults.map {
                    TestResultItem(
                        testResult = it,
                        scalable = false
                    )
                }
            )
        }
    }

    private fun initChart() = binding.apply {
        chartVisionDynamic.data = LineData()
        chartVisionDynamic.description = Description().apply {
            text = ""
        }
        chartVisionDynamic.setTouchEnabled(false)
    }

    private fun addChartLine(eyeAnalysisResult: SingleEyeAnalysisResult, eye: TestEyesType) = binding.apply {
        val entries = eyeAnalysisResult.chartData
            .sortedBy { it.timestamp }
            .map {
                Entry(
                    it.timestamp.toFloat(),
                    it.value / 100f
                )
            }

        val baseHeaderResId: Int
        val extrapolationHeaderResId: Int
        val baseColorResId: Int
        val extrapolationColorResId: Int
        if (eye == TestEyesType.LEFT) {
            baseHeaderResId = R.string.left_eye
            extrapolationHeaderResId = R.string.left_eye_extrapolation
            baseColorResId = R.color.colorChartLeftEye
            extrapolationColorResId = R.color.colorChartLeftEyeExtrapolation
        } else {
            baseHeaderResId = R.string.right_eye
            extrapolationHeaderResId = R.string.right_eye_extrapolation
            baseColorResId = R.color.colorChartRightEye
            extrapolationColorResId = R.color.colorChartRightEyeExtrapolation
        }

        val baseDataSet = LineDataSet(
            entries,
            getString(baseHeaderResId)
        ).apply {
            color = context.getColor(baseColorResId)
            setCircleColor(context.getColor(R.color.colorTransparent))
            circleHoleColor = context.getColor(baseColorResId)
            valueTextColor = context.getColor(R.color.colorText)
        }
        chartVisionDynamic.data.addDataSet(baseDataSet)

        var yMaximum = entries.maxOfOrNull { it.y } ?: 0f
        val xValues = entries.map { it.x }
        var xMinimum = xValues.minOrNull() ?: 0f
        var xMaximum = xValues.maxOrNull() ?: 0f

        eyeAnalysisResult.extrapolatedResult?.let { extrapolationResult ->
            val extrapolationEntries = listOf(
                entries.last(),
                Entry(
                    extrapolationResult.timestamp.toFloat(),
                    extrapolationResult.value / 100f
                )
            )
            val extrapolationDataSet = LineDataSet(
                extrapolationEntries,
                getString(extrapolationHeaderResId)
            ).apply {
                color = context.getColor(extrapolationColorResId)
                setCircleColor(context.getColor(R.color.colorTransparent))
                circleHoleColor = context.getColor(extrapolationColorResId)
                valueTextColor = context.getColor(R.color.colorText)
            }
            chartVisionDynamic.data.addDataSet(extrapolationDataSet)

            yMaximum = max(yMaximum, extrapolationEntries.last().y)
            xMinimum = min(xMinimum, extrapolationEntries.last().x)
            xMaximum = max(xMaximum, extrapolationEntries.last().x)
        }

        yAxisMaximum = max(yAxisMaximum, yMaximum)
        xAxisMinimum = min(xAxisMinimum, xMinimum)
        xAxisMaximum = max(xAxisMaximum, xMaximum)
    }

    private fun configureChartAxis() = binding.chartVisionDynamic.apply {
        axisRight.isEnabled = false
        axisLeft.apply {
            axisMinimum = 0f
            axisMaximum = yAxisMaximum + 0.05f
            textColor = context.getColor(R.color.colorTextDark)
        }
        xAxis.apply {
            axisMinimum = xAxisMinimum
            axisMaximum = xAxisMaximum
            setDrawGridLines(true)
            position = XAxis.XAxisPosition.BOTTOM
            labelRotationAngle = -45f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toLong().toDate()
                }
            }
            textColor = context.getColor(R.color.colorTextDark)
        }
        legend.apply {
            isWordWrapEnabled = true
            textColor = context.getColor(R.color.colorText)
        }
    }

    companion object {
        const val RESULT = "RESULT"
    }
}
