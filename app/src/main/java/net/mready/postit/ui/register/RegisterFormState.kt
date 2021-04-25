package net.mready.postit.ui.register

data class RegisterFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val repeatError: Int? = null,
    val isDataValid: Boolean = false
)