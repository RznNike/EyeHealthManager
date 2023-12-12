package ru.rznnike.eyehealthmanager.app.ui.fragment.main.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.BuildConfig
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogAction
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogParameters
import ru.rznnike.eyehealthmanager.app.dialog.bottom.BottomDialogAction
import ru.rznnike.eyehealthmanager.app.dialog.showAlertDialog
import ru.rznnike.eyehealthmanager.app.dialog.showBottomDialog
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.main.settings.SettingsPresenter
import ru.rznnike.eyehealthmanager.app.presentation.main.settings.SettingsView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.restartApp
import ru.rznnike.eyehealthmanager.databinding.DialogAboutAppBinding
import ru.rznnike.eyehealthmanager.databinding.DialogChangelogBinding
import ru.rznnike.eyehealthmanager.databinding.FragmentSettingsBinding
import ru.rznnike.eyehealthmanager.domain.model.enums.Language

class SettingsFragment : BaseFragment(R.layout.fragment_settings), SettingsView {
    @InjectPresenter
    lateinit var presenter: SettingsPresenter

    private val binding by viewBinding(FragmentSettingsBinding::bind)

    override var progressCallback: ((Boolean) -> Unit)? = { show ->
        binding.apply {
            progressView.setProgress(show)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            layoutScrollableContent.addSystemWindowInsetToPadding(top = true)
        }
        initOnClickListeners()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    private fun initOnClickListeners() = binding.apply {
        buttonTestingSettings.setOnClickListener {
            presenter.openTestingSettings()
        }
        buttonExportData.setOnClickListener {
            presenter.exportData()
        }
        buttonImportData.setOnClickListener {
            presenter.importData()
        }
        buttonClearJournal.setOnClickListener {
            showClearJournalDialog()
        }
        buttonAboutApp.setOnClickListener {
            showAboutAppDialog()
        }
        buttonChangelog.setOnClickListener {
            showChangelogDialog()
        }
        buttonDeleteDuplicates.setOnClickListener {
            presenter.deleteDuplicatesInJournal()
        }
    }

    override fun populateData(currentLanguage: Language) {
        binding.apply {
            textViewCurrentLanguage.text = currentLanguage.localizedName
            buttonLanguage.setOnClickListener {
                showLanguageSelectionBottomDialog(currentLanguage)
            }
        }
    }

    private fun showLanguageSelectionBottomDialog(currentLanguage: Language) {
        showBottomDialog(
            header = getString(R.string.choose_language),
            actions = Language.entries.map { language ->
                BottomDialogAction(
                    text = language.localizedName,
                    selected = language == currentLanguage
                ) {
                    it.dismiss()
                    presenter.changeLanguage(language)
                }
            }
        )
    }

    override fun updateUiLanguage() = requireActivity().restartApp()

    private fun showClearJournalDialog() {
        showAlertDialog(
            parameters = AlertDialogParameters.HORIZONTAL_2_OPTIONS_LEFT_ACCENT,
            header = getString(R.string.clear_journal_dialog_header),
            message = getString(R.string.clear_journal_dialog_message),
            actions = listOf(
                AlertDialogAction(getString(R.string.cancel)) {
                    it.dismiss()
                },
                AlertDialogAction(getString(R.string.delete)) {
                    it.dismiss()
                    presenter.clearJournal()
                }
            )
        )
    }

    private fun showAboutAppDialog() {
        DialogAboutAppBinding.inflate(layoutInflater).apply {
            val dialog = AlertDialog.Builder(requireContext(), R.style.AppTheme_Dialog_Alert)
                .setView(root)
                .setCancelable(true)
                .create()

            textViewDialogVersion.text = BuildConfig.VERSION_NAME
            buttonDialogEmail.setOnClickListener {
                dialog.dismiss()
                routerStartFlow(
                    Screens.Common.actionMailTo(
                        email = getString(R.string.feedback_email_address),
                        subject = getString(R.string.app_name)
                    )
                )
            }
            buttonDialogSourceCode.setOnClickListener {
                dialog.dismiss()
                routerStartFlow(
                    Screens.Common.actionOpenLink(getString(R.string.repository_link))
                )
            }

            dialog.show()
        }
    }

    private fun showChangelogDialog() {
        DialogChangelogBinding.inflate(layoutInflater).apply {
            val dialog = AlertDialog.Builder(requireContext(), R.style.AppTheme_Dialog_Alert)
                .setView(root)
                .setCancelable(true)
                .create()

            buttonDialogClose.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}
