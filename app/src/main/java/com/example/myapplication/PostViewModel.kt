package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repository.PostRepository
import com.example.myapplication.util.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel
    @Inject
    constructor(private val repository: PostRepository) : ViewModel(){

        private val postStateFlow: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)

        val _postStateFlow: StateFlow<ApiState> = postStateFlow

    fun getPost() = viewModelScope.launch {
        postStateFlow.value = ApiState.Loading
        repository.getPost()
            .catch { e->
                postStateFlow.value = ApiState.Failure(e)
            }.collect { data->
                postStateFlow.value = ApiState.Success(data)
            }
    }


}