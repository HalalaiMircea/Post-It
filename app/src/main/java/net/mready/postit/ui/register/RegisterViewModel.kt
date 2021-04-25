package net.mready.postit.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.mready.postit.R
import net.mready.postit.data.AuthRepository
import net.mready.postit.data.LoggedInUser
import net.mready.postit.data.RegisterRequest
import net.mready.postit.data.Result
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    private val _regFormState = MutableLiveData<RegisterFormState>()
    val regFormState: LiveData<RegisterFormState> = _regFormState

    private val _registerResult = MutableLiveData<Result<LoggedInUser>>()
    val registerResult: LiveData<Result<LoggedInUser>> = _registerResult

    fun register(registerData: RegisterRequest) {
        // can be launched in a separate asynchronous job
        viewModelScope.launch {
            _registerResult.value = repository.register(registerData)
        }
    }

    /**
     * Validates credentials and updates liveData
     */
    fun registerDataChanged(registerData: RegisterRequest, repeatPass: String) {
        if (!isUserNameValid(registerData.username)) {
            _regFormState.value = RegisterFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(registerData.password)) {
            _regFormState.value = RegisterFormState(passwordError = R.string.invalid_password)
        } else if (!isRepeatValid(registerData.password, repeatPass)) {
            _regFormState.value = RegisterFormState(repeatError = R.string.repeat_password)
        } else {
            _regFormState.value = RegisterFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.trim().length > 1
    }

    private fun isRepeatValid(pass: String, repeatPass: String): Boolean {
        return pass == repeatPass
    }
}