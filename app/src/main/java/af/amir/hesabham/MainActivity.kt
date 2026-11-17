package af.amir.hesabham

import af.amir.hesabham.presentation.core.config.setting.LocalAppCalendar
import af.amir.hesabham.presentation.core.config.setting.LocalAppCurrency
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.layoutDirectionFor
import af.amir.hesabham.presentation.core.mapper.toUiModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import af.amir.hesabham.presentation.core.theme.HesabHamTheme
import af.amir.hesabham.presentation.navigation.RootNavigationGraph
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val viewModel = koinViewModel<AppConfigViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            splash.setKeepOnScreenCondition { state.isLoading }
            HesabHamTheme(darkTheme = state.setting.darkMode) {
                val currentLocale = state.setting.locale
                CompositionLocalProvider(
                    LocalAppLocale provides state.setting.locale.locale,
                    LocalAppCurrency provides state.setting.currency.toUiModel(currentLocale.locale),
                    LocalLayoutDirection provides layoutDirectionFor(currentLocale),
                    LocalAppCalendar provides state.setting.calendar
                ) {

                    val navController = rememberNavController()

                        RootNavigationGraph(
                            navController,
                            viewModel,
                        )



                }
            }
        }
    }
}