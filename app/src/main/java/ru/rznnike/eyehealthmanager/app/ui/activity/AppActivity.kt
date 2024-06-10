package ru.rznnike.eyehealthmanager.app.ui.activity

import android.app.ActivityManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.updatePadding
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import moxy.presenter.InjectPresenter
import org.koin.android.ext.android.inject
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogAction
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogParameters
import ru.rznnike.eyehealthmanager.app.dialog.showAlertDialog
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.SystemMessage
import ru.rznnike.eyehealthmanager.app.global.ui.activity.BaseActivity
import ru.rznnike.eyehealthmanager.app.navigation.AppRouter
import ru.rznnike.eyehealthmanager.app.navigation.SupportAppNavigation
import ru.rznnike.eyehealthmanager.app.presentation.app.AppPresenter
import ru.rznnike.eyehealthmanager.app.presentation.app.AppView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToMargin
import ru.rznnike.eyehealthmanager.app.utils.extensions.getParcelableExtraCompat
import ru.rznnike.eyehealthmanager.app.utils.extensions.getString
import ru.rznnike.eyehealthmanager.app.utils.extensions.hideKeyboard
import ru.rznnike.eyehealthmanager.app.utils.extensions.setBackgroundTint
import ru.rznnike.eyehealthmanager.app.utils.extensions.setGone
import ru.rznnike.eyehealthmanager.app.utils.extensions.setVisible
import ru.rznnike.eyehealthmanager.databinding.ActivityBinding
import ru.rznnike.eyehealthmanager.databinding.ViewSnackbarBottomBinding
import ru.rznnike.eyehealthmanager.databinding.ViewSnackbarTopBinding
import ru.rznnike.eyehealthmanager.device.notification.Notificator
import ru.rznnike.eyehealthmanager.domain.global.CoroutineScopeProvider
import ru.rznnike.eyehealthmanager.domain.model.notification.Notification
import ru.rznnike.eyehealthmanager.domain.model.notification.toNotification

private const val TOP_BAR_TIME_MS = 10_000

class AppActivity : BaseActivity(R.layout.activity), AppView {
    @InjectPresenter
    lateinit var presenter: AppPresenter

    private val binding by viewBinding(ActivityBinding::bind)

    private val navigatorHolder: NavigatorHolder by inject()
    private val notifier: Notifier by inject()
    private val coroutineScopeProvider: CoroutineScopeProvider by inject()
    private val router: AppRouter by inject()

    private val navigator: Navigator = object : SupportAppNavigation(this, notifier, R.id.container) {}

    private var notificationsJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindowFlags()
        updateTaskDescription()

        if (savedInstanceState == null) {
            presenter.processNotificationIntent(getNotificationFromIntent(intent))
            router.newRootScreen(Screens.Flow.splash())
        } else {
            window.setBackgroundDrawableResource(R.color.colorBackground)
        }
    }

    private fun getNotificationFromIntent(intent: Intent): Notification? =
        intent.getParcelableExtraCompat(Notificator.PARAM_NOTIFICATION)
            ?: intent.extras
                ?.keySet()
                ?.associate { it to (intent.getStringExtra(it) ?: "") }
                ?.toNotification()

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            presenter.processNotificationIntent(getNotificationFromIntent(intent))
        }
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        notificationsJob?.cancel()
        notificationsJob = coroutineScopeProvider.scopeMainImmediate.launch {
            notifier.subscribe().collect(::onNextMessageNotify)
        }
    }

    override fun onStop() {
        notificationsJob?.cancel()
        super.onStop()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onResume() {
        super.onResume()
        binding.activityContainer.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) hideKeyboard(view)
        }
        currentFocus?.clearFocus()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun initWindowFlags() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun updateTaskDescription() {
        val taskDesc = when {
            Build.VERSION.SDK_INT >= 33 -> {
                ActivityManager.TaskDescription.Builder()
                    .setLabel(resources.getString(R.string.app_name))
                    .setIcon(R.mipmap.ic_launcher)
                    .setPrimaryColor(getColor(R.color.colorTextLight))
                    .build()
            }
            Build.VERSION.SDK_INT >= 28 -> {
                @Suppress("DEPRECATION")
                ActivityManager.TaskDescription(
                    resources.getString(R.string.app_name),
                    R.mipmap.ic_launcher,
                    getColor(R.color.colorTextLight)
                )
            }
            else -> {
                @Suppress("DEPRECATION")
                ActivityManager.TaskDescription(
                    resources.getString(R.string.app_name),
                    BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher),
                    getColor(R.color.colorTextLight)
                )
            }
        }

        setTaskDescription(taskDesc)
    }

    private fun onNextMessageNotify(systemMessage: SystemMessage) {
        systemMessage.text = systemMessage.textRes
            ?.let { getString(it) }
            ?: systemMessage.text
                    ?: ""
        systemMessage.actionText = systemMessage.actionTextRes
            ?.let { getString(it) }
            ?: systemMessage.actionText
                    ?: ""
        when (systemMessage.type) {
            SystemMessage.Type.ALERT -> showAlertMessage(systemMessage)
            SystemMessage.Type.BAR -> showBarMessage(systemMessage)
        }
    }

    private fun showAlertMessage(systemMessage: SystemMessage) {
        if (systemMessage.text.isNullOrBlank()) return

        if (!isFinishing) {
            showAlertDialog(
                parameters = AlertDialogParameters.VERTICAL_1_OPTION_ACCENT,
                header = systemMessage.text ?: "",
                actions = listOf(
                    AlertDialogAction(getString(R.string.ok)) {
                        it.dismiss()
                    }
                )
            )
        }
    }

    private fun showBarMessage(systemMessage: SystemMessage) =
        if (systemMessage.showOnTop) showTopSnackBar(systemMessage) else showBottomSnackBar(systemMessage)

    private fun showTopSnackBar(
        systemMessage: SystemMessage
    ) {
        ViewSnackbarTopBinding.inflate(layoutInflater).apply {
            val snackBar = Snackbar.make(findViewById(R.id.topSnackBarContainer), "", TOP_BAR_TIME_MS)
            snackBar.view.apply {
                addSystemWindowInsetToMargin(
                    top = true,
                    bottom = true,
                    left = true,
                    right = true,
                    topOffset = resources.getDimension(R.dimen.baseline_grid_8).toInt(),
                    bottomOffset = resources.getDimension(R.dimen.baseline_grid_8).toInt()
                )
                updatePadding(0, 0, 0, 0)
                requestApplyInsets()
                background = ContextCompat.getDrawable(context, R.drawable.bg_rounded_8_background)
                (this as ViewGroup).apply {
                    removeAllViews()
                    addView(root)
                }
            }

            textViewMessage.text = systemMessage.text ?: ""
            if (systemMessage.actionText.isNullOrBlank() || (systemMessage.actionCallback == null)) {
                buttonAction.setGone()
            } else {
                buttonAction.setVisible()
                buttonAction.text =
                    (systemMessage.actionText ?: "").ifBlank { getString(R.string.ok) }
                buttonAction.setOnClickListener {
                    systemMessage.actionCallback.invoke()
                }
            }

            snackBar.show()
        }
    }

    private fun showBottomSnackBar(
        systemMessage: SystemMessage
    ) {
        ViewSnackbarBottomBinding.inflate(layoutInflater).apply {
            val snackBar = Snackbar.make(findViewById(R.id.snackBarContainer), "", Snackbar.LENGTH_LONG)
            snackBar.view.apply {
                addSystemWindowInsetToMargin(
                    top = true,
                    bottom = true,
                    left = true,
                    right = true,
                    topOffset = resources.getDimension(R.dimen.baseline_grid_8).toInt(),
                    bottomOffset = resources.getDimension(R.dimen.baseline_grid_8).toInt()
                )
                updatePadding(0, 0, 0, 0)
                requestApplyInsets()
                background = ContextCompat.getDrawable(context, R.drawable.bg_rounded_8_background)
                (this as ViewGroup).apply {
                    removeAllViews()
                    addView(root)
                }
            }

            root.setBackgroundTint(
                if (systemMessage.level == SystemMessage.Level.ERROR) R.color.colorRed else R.color.colorAccent
            )
            textViewMessage.text = systemMessage.text ?: ""
            if (systemMessage.actionText.isNullOrBlank() || (systemMessage.actionCallback == null)) {
                buttonAction.setGone()
            } else {
                buttonAction.setVisible()
                buttonAction.text =
                    (systemMessage.actionText ?: "").ifBlank { getString(R.string.ok) }
                buttonAction.setOnClickListener {
                    systemMessage.actionCallback.invoke()
                }
            }

            snackBar.show()
        }
    }
}
