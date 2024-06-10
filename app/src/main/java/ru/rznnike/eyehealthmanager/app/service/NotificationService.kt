package ru.rznnike.eyehealthmanager.app.service

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.rznnike.eyehealthmanager.domain.global.CoroutineScopeProvider
import ru.rznnike.eyehealthmanager.domain.interactor.notification.ObserveCancelNotificationUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.notification.ObserveShowNotificationUseCase

class NotificationService : JobService() {
    private val coroutineScopeProvider: CoroutineScopeProvider by inject()
    private val observeShowNotificationUseCase: ObserveShowNotificationUseCase by inject()
    private val observeCancelNotificationUseCase: ObserveCancelNotificationUseCase by inject()
    private val notificator: ru.rznnike.eyehealthmanager.app.notification.Notificator by inject()

    private var notificationShowJob: Job? = null
    private var notificationCancelJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        startNotificationUseCases()
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        startNotificationUseCases()
        return true
    }

    private fun startNotificationUseCases() {
        observeShowNotification()
        observeCancelNotification()
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        notificationShowJob?.cancel()
        notificationCancelJob?.cancel()
        return true
    }

    private fun observeShowNotification() {
        if (notificationShowJob?.isActive == true) return

        notificationShowJob = coroutineScopeProvider.ui.launch {
            observeShowNotificationUseCase().cancellable().collect { notification ->
                if (!notification.showed) {
                    notificator.show(notification)
                    notification.showed = true
                }
            }
        }
    }

    private fun observeCancelNotification() {
        if (notificationCancelJob?.isActive == true) return

        notificationCancelJob = coroutineScopeProvider.ui.launch {
            observeCancelNotificationUseCase().collect { notification ->
                notificator.cancel(notification)
            }
        }
    }

    override fun onDestroy() {
        stopAsJob(baseContext)
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopAsJob(baseContext)
        super.onTaskRemoved(rootIntent)
    }

    companion object {
        private const val JOB_ID = 777

        fun startAsJob(context: Context) {
            val serviceName = ComponentName(context, NotificationService::class.java)
            val jobInfo = JobInfo.Builder(JOB_ID, serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build()
            val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            scheduler.schedule(jobInfo)
        }

        fun stopAsJob(ctx: Context) {
            val scheduler = ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            scheduler.cancel(JOB_ID)
        }
    }
}