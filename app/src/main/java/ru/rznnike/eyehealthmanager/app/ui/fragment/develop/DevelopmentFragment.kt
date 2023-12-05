package ru.rznnike.eyehealthmanager.app.ui.fragment.develop

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToMargin
import ru.rznnike.eyehealthmanager.app.utils.extensions.setVisible
import ru.rznnike.eyehealthmanager.databinding.FragmentDevelopmentBinding

class DevelopmentFragment : BaseFragment(R.layout.fragment_development) {
    private val binding by viewBinding(FragmentDevelopmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.content.addSystemWindowInsetToMargin(top = true)
        binding.buttonBack.apply {
            setVisible(
                arguments?.getBoolean(SHOW_BACK_BUTTON) ?: false
            )
            setOnClickListener {
                onBackPressed()
            }
        }
    }

    companion object {
        const val SHOW_BACK_BUTTON = "SHOW_BACK_BUTTON"
    }
}