package af.amir.hesabham.presentation.onboarding

sealed interface OnboardingEffect {
    object FinishScreen : OnboardingEffect
}