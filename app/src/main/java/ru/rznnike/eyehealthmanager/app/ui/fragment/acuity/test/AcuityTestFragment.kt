package ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.test

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.view.updateLayoutParams
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogAction
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogParameters
import ru.rznnike.eyehealthmanager.app.dialog.showAlertDialog
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.acuity.test.AcuityTestPresenter
import ru.rznnike.eyehealthmanager.app.presentation.acuity.test.AcuityTestView
import ru.rznnike.eyehealthmanager.app.ui.item.CantAnswerItem
import ru.rznnike.eyehealthmanager.app.ui.item.SymbolItem
import ru.rznnike.eyehealthmanager.app.ui.view.EmptyDividerDecoration
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.context
import ru.rznnike.eyehealthmanager.app.utils.extensions.convertMmToPx
import ru.rznnike.eyehealthmanager.app.utils.extensions.createFastAdapter
import ru.rznnike.eyehealthmanager.app.utils.extensions.getParcelableArg
import ru.rznnike.eyehealthmanager.app.utils.extensions.setGone
import ru.rznnike.eyehealthmanager.app.utils.extensions.setVisible
import ru.rznnike.eyehealthmanager.app.utils.extensions.withDelay
import ru.rznnike.eyehealthmanager.databinding.FragmentAcuityTestBinding
import ru.rznnike.eyehealthmanager.domain.model.EmptyAcuitySymbol
import ru.rznnike.eyehealthmanager.domain.model.IAcuitySymbol
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuitySymbolLetterEn
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuitySymbolLetterRu
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuitySymbolSquare
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuitySymbolTriangle
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType

private const val BASIC_HEIGHT_MM = 7f
private const val BASIC_DISTANCE_MM = 5000f
private const val VISION_MULTIPLIER = 100
private const val BUTTON_NEXT_ACTIVATION_DELAY = 500L

class AcuityTestFragment : BaseFragment(R.layout.fragment_acuity_test), AcuityTestView {
    @InjectPresenter
    lateinit var presenter: AcuityTestPresenter

    @ProvidePresenter
    fun providePresenter() = AcuityTestPresenter(
        dayPart = getParcelableArg(DAY_PART)!!
    )

    private val binding by viewBinding(FragmentAcuityTestBinding::bind)

    private lateinit var adapter: FastAdapter<IItem<*>>
    private lateinit var itemAdapter: ItemAdapter<IItem<*>>

    override var progressCallback: ((Boolean) -> Unit)? = { show ->
        binding.apply {
            progressView.setProgress(show)
        }
    }

    override fun onBackPressed() {
        showExitDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapters()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            layoutToolbarContainer.addSystemWindowInsetToPadding(top = true)
            content.addSystemWindowInsetToPadding(bottom = true)
        }
        initToolbar()
        initRecyclerViews()
        initOnClickListeners()
    }

    private fun initToolbar() = binding.toolbar.apply {
        textViewToolbarHeader.setText(R.string.testing_left_eye)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initAdapters() {
        itemAdapter = ItemAdapter()
        adapter = createFastAdapter(itemAdapter)
        adapter.setHasStableIds(true)

        adapter.onClickListener = { _, _, item, _ ->
            when (item) {
                is SymbolItem -> {
                    presenter.onSymbolSelected(item.symbol)
                    true
                }
                is CantAnswerItem -> {
                    presenter.onSymbolSelected(null)
                    true
                }
                else -> false
            }
        }
    }

    private fun initRecyclerViews() = binding.apply {
        recyclerViewAnswerVariants.apply {
            layoutManager = FlexboxLayoutManager(requireContext(), FlexDirection.ROW).apply {
                justifyContent = JustifyContent.CENTER
            }
            adapter = this@AcuityTestFragment.adapter
            itemAnimator = null
            addItemDecoration(
                EmptyDividerDecoration(
                    context = requireContext(),
                    cardInsets = R.dimen.baseline_grid_4,
                    applyOutsideDecoration = false
                )
            )
        }
    }

    private fun initOnClickListeners() = binding.apply {
        buttonNext.setOnClickListener {
            presenter.onNextStep()
        }
    }

    override fun showTestProgress(progress: Int) {
        binding.apply {
            percentProgressView.progress = progress
        }
    }

    override fun showInfo(eyesType: TestEyesType) {
        binding.apply {
            layoutTest.setGone()
            layoutAnswerVariants.setGone()
            layoutInfo.setVisible()

            if (eyesType == TestEyesType.LEFT) {
                textViewInfoMessage.setText(R.string.close_right_eye)
                toolbar.textViewToolbarHeader.setText(R.string.testing_left_eye)
            } else {
                textViewInfoMessage.setText(R.string.close_left_eye)
                toolbar.textViewToolbarHeader.setText(R.string.testing_right_eye)
            }
            disableButtonNextForDelay()
        }
    }

    override fun showTestStep(
        @DrawableRes imageResId: Int,
        vision: Int,
        dpmm: Float,
        distance: Int
    ) {
        binding.apply {
            layoutInfo.setGone()
            layoutAnswerVariants.setGone()
            layoutTest.setVisible()

            imageViewTest.setImageResource(imageResId)
            val finalDpmm = if (dpmm > 0) dpmm else context.convertMmToPx(1f)
            val heightMm = BASIC_HEIGHT_MM * VISION_MULTIPLIER / vision * distance / BASIC_DISTANCE_MM
            val heightPx = heightMm * finalDpmm
            imageViewTest.updateLayoutParams {
                height = heightPx.toInt()
            }

            buttonNext.setText(R.string.answer)
            disableButtonNextForDelay()
        }
    }

    override fun showAnswerVariants(
        symbolsType: AcuityTestSymbolsType,
        selectedSymbol: IAcuitySymbol?
    ) {
        binding.apply {
            layoutInfo.setGone()
            layoutTest.setGone()
            layoutAnswerVariants.setVisible()

            val items = when (symbolsType) {
                AcuityTestSymbolsType.LETTERS_RU -> AcuitySymbolLetterRu.entries
                AcuityTestSymbolsType.LETTERS_EN -> AcuitySymbolLetterEn.entries
                AcuityTestSymbolsType.SQUARE -> AcuitySymbolSquare.entries
                AcuityTestSymbolsType.TRIANGLE -> AcuitySymbolTriangle.entries
            }
                .map { symbol ->
                    SymbolItem(symbol).also {
                        it.isSelected = symbol == selectedSymbol
                    }
                }
                .plus(
                    CantAnswerItem().also {
                        it.isSelected = selectedSymbol == EmptyAcuitySymbol
                    }
                )
            itemAdapter.setNewList(items)
            buttonNext.setText(R.string.continue_string)
            buttonNext.isEnabled = selectedSymbol != null
        }
    }

    private fun disableButtonNextForDelay() = binding.apply {
        buttonNext.isEnabled = false
        withDelay(BUTTON_NEXT_ACTIVATION_DELAY) {
            buttonNext.isEnabled = true
        }
    }

    private fun showExitDialog() {
        showAlertDialog(
            parameters = AlertDialogParameters.HORIZONTAL_2_OPTIONS_LEFT_ACCENT,
            header = getString(R.string.test_cancel_message),
            cancellable = true,
            actions = listOf(
                AlertDialogAction(getString(R.string.cancel)) {
                    it.dismiss()
                },
                AlertDialogAction(getString(R.string.exit)) {
                    it.dismiss()
                    routerFinishFlow()
                }
            )
        )
    }

    companion object {
        const val DAY_PART = "DAY_PART"
    }
}
