package net.mready.postit.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.mready.postit.data.Post
import net.mready.postit.data.PostItAPI
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(private val api: PostItAPI) : ViewModel() {

    private val _postResult = MutableLiveData<Post>()
    val postResult: LiveData<Post> get() = _postResult

    private val _isValid = MutableLiveData<Boolean>()
    val isValid: LiveData<Boolean> get() = _isValid

    fun postMessage(message: String) {
        viewModelScope.launch {
            val response = api.postMessage(message.trim())
            _postResult.value = response.body()?.post
        }
    }

    fun validateMessage(message: String) {
        _isValid.value = message.isNotBlank()
    }
}