package ru.rznnike.eyehealthmanager.app

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.github.terrakok.cicerone.androidx.ActivityScreen
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.navigation.getFragmentScreen
import ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.AcuityFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.doctor.AcuityDoctorResultFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.info.AcuityInfoFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.instruction.AcuityInstructionFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.result.AcuityResultFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.test.AcuityTestFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.analysis.AnalysisFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.analysis.parameters.AnalysisParametersFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.analysis.result.AnalysisResultFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.astigmatism.AstigmatismFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.astigmatism.answer.AstigmatismAnswerFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.astigmatism.info.AstigmatismInfoFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.astigmatism.result.AstigmatismResultFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.astigmatism.test.AstigmatismTestFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.colorperception.ColorPerceptionFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.colorperception.info.ColorPerceptionInfoFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.colorperception.result.ColorPerceptionResultFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.colorperception.test.ColorPerceptionTestFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.contrast.ContrastFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.contrast.info.ContrastInfoFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.contrast.result.ContrastResultFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.contrast.test.ContrastTestFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.daltonism.DaltonismFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.daltonism.info.DaltonismInfoFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.daltonism.result.DaltonismResultFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.daltonism.test.DaltonismTestFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.journal.backup.ExportJournalFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.journal.restore.ImportJournalFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.main.MainFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.main.MainFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.nearfar.NearFarFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.nearfar.answer.NearFarAnswerFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.nearfar.info.NearFarInfoFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.nearfar.result.NearFarResultFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.nearfar.test.NearFarTestFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.settings.testing.TestingSettingsFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.splash.SplashFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.splash.SplashFragment
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.AnalysisResult
import ru.rznnike.eyehealthmanager.domain.model.enums.AstigmatismAnswerType
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart
import ru.rznnike.eyehealthmanager.domain.model.enums.NearFarAnswerType

object Screens {
    object Flow {
        fun splash() = SplashFlowFragment::class.getFragmentScreen()

        fun main() = MainFlowFragment::class.getFragmentScreen()

        fun colorPerception() = ColorPerceptionFlowFragment::class.getFragmentScreen()

        fun nearFar() = NearFarFlowFragment::class.getFragmentScreen()

        fun contrast() = ContrastFlowFragment::class.getFragmentScreen()

        fun daltonism() = DaltonismFlowFragment::class.getFragmentScreen()

        fun acuity() = AcuityFlowFragment::class.getFragmentScreen()

        fun astigmatism() = AstigmatismFlowFragment::class.getFragmentScreen()

        fun analysis() = AnalysisFlowFragment::class.getFragmentScreen()
    }

    object Screen {
        fun splash() = SplashFragment::class.getFragmentScreen()

        fun main() = MainFragment::class.getFragmentScreen()

        fun colorPerceptionInfo() = ColorPerceptionInfoFragment::class.getFragmentScreen()

        fun colorPerceptionTest() = ColorPerceptionTestFragment::class.getFragmentScreen()

        fun colorPerceptionResult(
            recognizedCount: Int,
            allCount: Int
        ) = ColorPerceptionResultFragment::class.getFragmentScreen(
            ColorPerceptionResultFragment.RECOGNIZED_COUNT to recognizedCount,
            ColorPerceptionResultFragment.ALL_COUNT to allCount
        )

        fun nearFarInfo() = NearFarInfoFragment::class.getFragmentScreen()

        fun nearFarTest() = NearFarTestFragment::class.getFragmentScreen()

        fun nearFarAnswer() = NearFarAnswerFragment::class.getFragmentScreen()

        fun nearFarResult(
            answerLeftEye: NearFarAnswerType,
            answerRightEye: NearFarAnswerType
        ) = NearFarResultFragment::class.getFragmentScreen(
            NearFarResultFragment.ANSWER_LEFT_EYE to answerLeftEye,
            NearFarResultFragment.ANSWER_RIGHT_EYE to answerRightEye
        )

        fun contrastInfo() = ContrastInfoFragment::class.getFragmentScreen()

        fun contrastTest() = ContrastTestFragment::class.getFragmentScreen()

        fun contrastResult(
            recognizedDelta: Int
        ) = ContrastResultFragment::class.getFragmentScreen(
            ContrastResultFragment.RECOGNIZED_DELTA to recognizedDelta
        )

        fun daltonismInfo() = DaltonismInfoFragment::class.getFragmentScreen()

        fun daltonismTest() = DaltonismTestFragment::class.getFragmentScreen()

        fun daltonismResult(
            errorsCount: Int,
            resultType: String
        ) = DaltonismResultFragment::class.getFragmentScreen(
            DaltonismResultFragment.ERRORS_COUNT to errorsCount,
            DaltonismResultFragment.RESULT_TYPE to resultType
        )

        fun acuityInfo() = AcuityInfoFragment::class.getFragmentScreen()

        fun acuityInstruction(
            dayPart: DayPart
        ) = AcuityInstructionFragment::class.getFragmentScreen(
            AcuityInstructionFragment.DAY_PART to dayPart
        )

        fun acuityTest(
            dayPart: DayPart
        ) = AcuityTestFragment::class.getFragmentScreen(
            AcuityTestFragment.DAY_PART to dayPart
        )

        fun acuityResult(
            result: AcuityTestResult
        ) = AcuityResultFragment::class.getFragmentScreen(
            AcuityResultFragment.TEST_RESULT to result
        )

        fun acuityDoctorResult() = AcuityDoctorResultFragment::class.getFragmentScreen()

        fun astigmatismInfo() = AstigmatismInfoFragment::class.getFragmentScreen()

        fun astigmatismTest() = AstigmatismTestFragment::class.getFragmentScreen()

        fun astigmatismAnswer() = AstigmatismAnswerFragment::class.getFragmentScreen()

        fun astigmatismResult(
            answerLeftEye: AstigmatismAnswerType,
            answerRightEye: AstigmatismAnswerType
        ) = AstigmatismResultFragment::class.getFragmentScreen(
            AstigmatismResultFragment.ANSWER_LEFT_EYE to answerLeftEye,
            AstigmatismResultFragment.ANSWER_RIGHT_EYE to answerRightEye
        )

        fun testingSettings() = TestingSettingsFragment::class.getFragmentScreen()

        fun exportJournal() = ExportJournalFragment::class.getFragmentScreen()

        fun importJournal() = ImportJournalFragment::class.getFragmentScreen()

        fun analysisParameters() = AnalysisParametersFragment::class.getFragmentScreen()

        fun analysisResult(
            result: AnalysisResult
        ) = AnalysisResultFragment::class.getFragmentScreen(
            AnalysisResultFragment.RESULT to result
        )
    }

    // COMMON ACTIONS
    object Common {
        @Suppress("unused")
        fun actionAppSettings() = ActivityScreen("actionAppSettings") { context ->
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + context.packageName)
            ).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }

        @Suppress("unused", "UNUSED_ANONYMOUS_PARAMETER")
        fun actionOpenLink(
            link: String,
            specificPackage: String? = null
        ) = ActivityScreen("actionOpenLink") { context ->
            Intent(
                Intent.ACTION_VIEW
            ).apply {
                data = Uri.parse(link)
                setPackage(specificPackage)
            }
        }

        @Suppress("unused", "UNUSED_ANONYMOUS_PARAMETER")
        fun actionOpenDial(
            phone: String
        ) = ActivityScreen("actionOpenDial") { context ->
            Intent(
                Intent.ACTION_DIAL
            ).apply {
                data = Uri.fromParts("tel", phone, null)
            }
        }

        @Suppress("unused")
        fun actionMailTo(
            email: String,
            subject: String = ""
        ) = ActivityScreen("actionMailTo") { context ->
            Intent.createChooser(
                Intent(
                    Intent.ACTION_SENDTO
                ).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                },
                context.resources.getString(R.string.write_on_email)
            )
        }

        @Suppress("unused")
        fun actionShareText(
            text: String,
            header: String? = null
        ) = ActivityScreen("actionShareText") { context ->
            Intent.createChooser(
                Intent(
                    Intent.ACTION_SEND
                ).apply {
                    putExtra(Intent.EXTRA_TEXT, text)
                    putExtra("sms_body", text)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                    type = "text/plain"
                },
                header ?: context.resources.getString(R.string.share)
            )
        }

        @Suppress("unused")
        fun actionShareFile(
            uri: Uri,
            mimeType: String
        ) = ActivityScreen("actionShareFile") { context ->
            Intent.createChooser(
                Intent(
                    Intent.ACTION_SEND
                ).apply {
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = mimeType
                },
                context.resources.getString(R.string.share)
            )
        }

        @Suppress("unused")
        fun actionOpenFolder(
            uri: Uri
        ) = ActivityScreen("actionOpenFolder") { context ->
            Intent.createChooser(
                Intent(
                    Intent.ACTION_VIEW
                ).apply {
                    setDataAndType(uri, "*/*")
                },
                context.resources.getString(R.string.open_folder)
            )
        }

        @Suppress("unused")
        fun actionOpenFile(
            uri: Uri,
            mimeType: String
        ) = ActivityScreen("actionOpenFile") { context ->
            Intent.createChooser(
                Intent(
                    Intent.ACTION_VIEW
                ).apply {
                    setDataAndType(uri, mimeType)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                },
                context.resources.getString(R.string.open_file)
            )
        }
    }
}
