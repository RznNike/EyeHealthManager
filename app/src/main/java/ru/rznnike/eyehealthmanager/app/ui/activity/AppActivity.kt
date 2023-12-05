package ru.rznnike.eyehealthmanager.app.ui.activity

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.core.view.updateLayoutParams
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Screen
import com.google.android.material.snackbar.Snackbar
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
import ru.rznnike.eyehealthmanager.app.utils.extensions.*
import ru.rznnike.eyehealthmanager.databinding.ActivityBinding
import ru.rznnike.eyehealthmanager.device.notification.Notificator
import ru.rznnike.eyehealthmanager.domain.global.CoroutineProvider
import ru.rznnike.eyehealthmanager.domain.model.Notification
import ru.rznnike.eyehealthmanager.domain.model.toNotification

private const val TOP_BAR_TIME_MS = 10_000

class AppActivity : BaseActivity(R.layout.activity), AppView {
    @InjectPresenter
    lateinit var presenter: AppPresenter

    private val binding by viewBinding(ActivityBinding::bind)

    private val navigatorHolder: NavigatorHolder by inject()
    private val notifier: Notifier by inject()
    private val coroutineProvider: CoroutineProvider by inject()
    private val router: AppRouter by inject()

    private val navigator: Navigator = object : SupportAppNavigation(this, notifier, R.id.container) {}

    private var subscribedToNotifications = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindowFlags()
        updateTaskDescription()

        if (savedInstanceState == null) {
            presenter.processNotificationIntent(getNotificationFromIntent(intent))
            routerNewRootFlow(Screens.Flow.splash())
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
        subscribeOnSystemMessages()
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

    private fun subscribeOnSystemMessages() {
        if (subscribedToNotifications) return

        subscribedToNotifications = true
        coroutineProvider.scopeMainImmediate.launch {
            notifier.subscribe().collect(::onNextMessageNotify)
        }
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
            SystemMessage.Type.BAR_ACTION -> showActionMessage(systemMessage)
        }
    }

    private fun showAlertMessage(systemMessage: SystemMessage) {
        if (systemMessage.text.isNullOrBlank()) return

        if (!isFinishing) {
            showAlertDialog(
                parameters = AlertDialogParameters.VERTICAL_1_OPTION_ACCENT,
                header = systemMessage.text ?: "",
                cancellable = true,
                actions = listOf(
                    AlertDialogAction(getString(R.string.yes)) {
                        it.dismiss()
                    }
                )
            )
        }
    }

    @SuppressLint("ShowToast")
    private fun prepareSnackBar(systemMessage: SystemMessage): Snackbar? {
        if (systemMessage.text.isNullOrBlank()) return null

        val backgroundResource = R.drawable.bg_snackbar
        val backgroundTint = when {
            systemMessage.level == SystemMessage.Level.ERROR -> R.color.colorRed
            systemMessage.showOnTop -> R.color.colorBackground
            else -> R.color.colorAccent
        }

        val snackBar = if (systemMessage.showOnTop) {
            Snackbar.make(findViewById(R.id.topSnackBarContainer), systemMessage.text ?: "", TOP_BAR_TIME_MS)
        } else {
            Snackbar.make(findViewById(R.id.snackBarContainer), systemMessage.text ?: "", Snackbar.LENGTH_LONG)
        }
        val snackView = snackBar.view

        snackView.findViewById<TextView>(R.id.snackbar_text).apply {
            isSingleLine = false
            setTextColorRes(if (systemMessage.showOnTop) R.color.colorText else R.color.colorTextLight)
            if (systemMessage.showOnTop) {
                setDrawableRes(
                    left = R.drawable.icon
                )
                compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.baseline_grid_16)
            }
        }

        snackView.run {
            addSystemWindowInsetToMargin(
                top = true,
                bottom = true,
                topOffset = resources.getDimension(R.dimen.baseline_grid_8).toInt(),
                bottomOffset = resources.getDimension(R.dimen.baseline_grid_8).toInt()
            )
            requestApplyInsets()
            setBackgroundResource(backgroundResource)
            setBackgroundTint(backgroundTint)
        }

        return snackBar
    }

    private fun showBarMessage(systemMessage: SystemMessage) {
        prepareSnackBar(systemMessage)?.let { snackBar ->
            snackBar.view.setOnClickListener {
                snackBar.dismiss()
                systemMessage.onClickCallback?.invoke()
            }

            snackBar.show()
        }
    }

    private fun showActionMessage(systemMessage: SystemMessage) {
        prepareSnackBar(systemMessage)?.let { snackBar ->
            if (!systemMessage.actionText.isNullOrBlank() && systemMessage.actionCallback != null) {
                val actionTitle = systemMessage.actionText ?: ""
                snackBar.setAction(actionTitle) { systemMessage.actionCallback.invoke() }
                snackBar.view.findViewById<Button>(R.id.snackbar_action).apply {
                    setTextColorRes(if (systemMessage.showOnTop) R.color.colorAccent else R.color.colorTextLight)
                    setBackgroundTint(
                        if (systemMessage.showOnTop) R.color.colorBackground else R.color.colorAccent
                    )
                    updateLayoutParams {
                        width = ViewGroup.LayoutParams.MATCH_PARENT
                    }
                    gravity = Gravity.CENTER
                }
            }

            snackBar.show()
        }
    }

    override fun routerNewRootFlow(flow: Screen) = router.newRootScreen(flow)

    override fun routerStartSingle(flow: Screen) = router.startSingle(flow)
}
