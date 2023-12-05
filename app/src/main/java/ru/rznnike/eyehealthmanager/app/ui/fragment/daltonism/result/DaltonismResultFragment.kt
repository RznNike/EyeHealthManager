package ru.rznnike.eyehealthmanager.app.ui.fragment.daltonism.result

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.daltonism.result.DaltonismResultPresenter
import ru.rznnike.eyehealthmanager.app.presentation.daltonism.result.DaltonismResultView
import ru.rznnike.eyehealthmanager.app.utils.extensions.*
import ru.rznnike.eyehealthmanager.databinding.FragmentDaltonismResultBinding
import ru.rznnike.eyehealthmanager.domain.model.enums.DaltonismAnomalyType
import java.util.*

class DaltonismResultFragment : BaseFragment(R.layout.fragment_daltonism_result),
    DaltonismResultView {
    @InjectPresenter
    lateinit var presenter: DaltonismResultPresenter

    @ProvidePresenter
    fun providePresenter() = DaltonismResultPresenter(
        errorsCount = getIntArg(ERRORS_COUNT),
        resultType = DaltonismAnomalyType.parseName(getStringArg(RESULT_TYPE)) ?: DaltonismAnomalyType.NONE
    )

    private val binding by viewBinding(FragmentDaltonismResultBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            layoutToolbarContainer.addSystemWindowInsetToPadding(top = true)
            layoutControls.addSystemWindowInsetToPadding(bottom = true)
        }
        initToolbar()
        initOnClickListeners()
    }

    private fun initToolbar() = binding.toolbar.apply {
        textViewToolbarHeader.setText(R.string.test_daltonism)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initOnClickListeners() = binding.apply {
        buttonClose.setOnClickListener {
            routerFinishFlow()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun populateData(errorsCount: Int, resultType: DaltonismAnomalyType) {
        binding.apply {
            textViewResult.text = "%s %s\n%s".format(
                errorsCount,
                resources.getQuantityString(R.plurals.errors, errorsCount),
                getString(resultType.nameResId)
            )
            if (resultType == DaltonismAnomalyType.NONE) {
                textViewMessage.setInvisible()
                textViewResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
            } else {
                textViewMessage.setVisible()
                textViewResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
            }
        }
    }

    companion object {
        const val ERRORS_COUNT = "ERRORS_COUNT"
        const val RESULT_TYPE = "RESULT_TYPE"
    }
}
