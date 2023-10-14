package com.portugal1576.newsportugalxml.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portugal1576.newsportugalxml.data.api.NewsRepository
import com.portugal1576.newsportugalxml.models.NewsResponse
import com.portugal1576.newsportugalxml.utils.Resourse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    val searchNewsLiveData: MutableLiveData<Resourse<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1

    init {
        getsearchNews("")
    }

    fun getsearchNews(query: String) =
        viewModelScope.launch {
            searchNewsLiveData.postValue(Resourse.Loading())
            val response = repository.getSearchNews(query = query, pageNumber = searchNewsPage)
            if (response.isSuccessful) {
                response.body().let { res ->
                    searchNewsLiveData.postValue(Resourse.Success(res))
                }
            } else {
                searchNewsLiveData.postValue(Resourse.Error(message = response.message()))
            }
        }
}