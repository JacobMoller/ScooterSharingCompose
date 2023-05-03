package dk.itu.moapd.scootersharing.jacj.feature_authentication.domain.model

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)