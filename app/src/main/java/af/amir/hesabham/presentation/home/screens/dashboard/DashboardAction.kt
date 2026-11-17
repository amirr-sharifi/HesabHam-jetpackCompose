package af.amir.hesabham.presentation.home.screens.dashboard

sealed interface DashboardAction {

    object  NextMonth:DashboardAction
    object  PreviousMonth:DashboardAction
}