package ru.rznnike.eyehealthmanager.app.ui.fragment.main

import android.net.Uri
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogAction
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogParameters
import ru.rznnike.eyehealthmanager.app.dialog.showAlertDialog
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.main.MainPresenter
import ru.rznnike.eyehealthmanager.app.presentation.main.MainView
import ru.rznnike.eyehealthmanager.app.ui.fragment.main.journal.JournalFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.main.settings.SettingsFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.main.tests.TestsFragment
import ru.rznnike.eyehealthmanager.app.ui.view.NavbarItemView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.databinding.DialogChangelogBinding
import ru.rznnike.eyehealthmanager.databinding.FragmentMainBinding

class MainFragment : BaseFragment(R.layout.fragment_main), MainView {
    @InjectPresenter
    lateinit var presenter: MainPresenter

    private val binding by viewBinding(FragmentMainBinding::bind)

    private val tabMenus: MutableMap<MainView.NavigationTab, NavbarItemView> = mutableMapOf()
    private val tabFragments: MutableMap<MainView.NavigationTab, Fragment> = mutableMapOf()

    override var progressCallback: ((Boolean) -> Unit)? = { show ->
        binding.apply {
            progressViewMain.setProgress(show)
        }
    }

    override fun onBackPressed() {
        tabFragments
            .filter { it.value.isVisible }
            .forEach { (it.value as BaseFragment).onBackPressed() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initContainers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            navigation.addSystemWindowInsetToPadding(bottom = true)
        }
        initTabMenus()
        openFragmentByTab(presenter.selectedNavigationTab)
    }

    override fun onResume() {
        super.onResume()
        tabFragments
            .asSequence()
            .filter { it.value.isVisible }
            .firstOrNull()
            ?.key
            ?.let {
                presenter.selectedNavigationTab = it
            }
        setNavigationSelection()
    }

    private fun initContainers() {
        fun FragmentManager.getFragmentByTag(
            tag: String,
            defaultFragment: Fragment
        ): Fragment {
            var fragment = findFragmentByTag(tag)
            if (fragment == null) {
                fragment = defaultFragment
                commitNow {
                    add(R.id.fragmentContainer, fragment, tag)
                    detach(fragment)
                }
            }
            return fragment
        }

        fun getTabPair(
            tab: MainView.NavigationTab,
            defaultFragment: Fragment
        ): Pair<MainView.NavigationTab, Fragment> = Pair(
            tab,
            childFragmentManager.getFragmentByTag(tab.toString(), defaultFragment)
        )

        tabFragments.clear()
        tabFragments.putAll(
            mapOf(
                getTabPair(MainView.NavigationTab.TESTS, TestsFragment()),
                getTabPair(MainView.NavigationTab.JOURNAL, JournalFragment()),
                getTabPair(MainView.NavigationTab.SETTINGS, SettingsFragment())
            )
        )
    }

    private fun initTabMenus() = binding.apply {
        tabMenus.clear()
        tabMenus.putAll(
            mapOf(
                MainView.NavigationTab.TESTS to navbarItemTests,
                MainView.NavigationTab.JOURNAL to navbarItemJournal,
                MainView.NavigationTab.SETTINGS to navbarItemSettings
            )
        )
        tabMenus.entries.forEach {
            it.value.setOnClickListener { _ ->
                openFragmentByTab(it.key)
                it.value.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            }
        }
    }

    private fun openFragmentByTab(tab: MainView.NavigationTab) {
        childFragmentManager.commitNow {
            for (tabFragment in tabFragments) {
                if (tabFragment.key == tab) {
                    attach(tabFragment.value)
                } else {
                    detach(tabFragment.value)
                }
            }
        }

        presenter.selectedNavigationTab = tab
        setNavigationSelection()
    }

    private fun setNavigationSelection() {
        tabMenus.entries.forEach {
            it.value.selection = it.key == presenter.selectedNavigationTab
        }
    }

    override fun showSuccessfulExportDialog(uri: Uri) {
        val message = "%s\n<i>%s</i>".format(
            getString(R.string.successful_export),
            uri.lastPathSegment
        )
        showAlertDialog(
            parameters = AlertDialogParameters.VERTICAL_2_OPTIONS_TOP_ACCENT,
            header = getString(R.string.export),
            message = message,
            actions = listOf(
                AlertDialogAction(getString(R.string.open_folder)) {
                    it.dismiss()
                    routerStartFlow(Screens.Common.actionOpenFolder(uri))
                },
                AlertDialogAction(getString(R.string.close)) {
                    it.dismiss()
                }
            )
        )
    }

    override fun showSuccessfulImportDialog() {
        showAlertDialog(
            parameters = AlertDialogParameters.VERTICAL_2_OPTIONS_TOP_ACCENT,
            header = getString(R.string.import_string),
            message = getString(R.string.successful_import),
            actions = listOf(
                AlertDialogAction(getString(R.string.close)) {
                    it.dismiss()
                },
                AlertDialogAction(getString(R.string.delete_duplicates)) {
                    it.dismiss()
                    presenter.deleteDuplicatesInJournal()
                }
            )
        )
    }

    override fun showWelcomeDialog() {
        showAlertDialog(
            parameters = AlertDialogParameters.VERTICAL_1_OPTION_ACCENT,
            header = getString(R.string.welcome_header),
            message = getString(R.string.welcome_message),
            actions = listOf(
                AlertDialogAction(getString(R.string.close)) {
                    it.dismiss()
                }
            )
        )
    }

    override fun showChangelogDialog() {
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
