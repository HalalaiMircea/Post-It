package net.mready.postit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.mready.postit.data.AuthRepository
import net.mready.postit.data.Post
import net.mready.postit.data.PostItAPI
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: PostItAPI,
    val authRepository: AuthRepository
) : ViewModel() {

    /*val postsLiveData = liveData {
        val postList: PostList = api.getPosts()
        emit(postList.posts)    // Emit List<Post>
    }*/

    private val _postsLiveData = MutableLiveData<List<Post>>()
    val postsLiveData: LiveData<List<Post>> get() = _postsLiveData

    fun updateData() {
        viewModelScope.launch {
            _postsLiveData.value = api.getPosts().posts
        }
    }
}