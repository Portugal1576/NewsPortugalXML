package com.portugal1576.newsportugalxml.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portugal1576.newsportugalxml.data.api.NewsRepository
import com.portugal1576.newsportugalxml.models.Article
import com.portugal1576.newsportugalxml.models.NewsResponse
import com.portugal1576.newsportugalxml.utils.Resourse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    val newsLiveData: MutableLiveData<Resourse<NewsResponse>> = MutableLiveData()
    val newsPage = 1

    init {
        getNews(
            "us" +
                    ""
        )
    }

    private fun getNews(countryCode: String) =
        viewModelScope.launch {
            newsLiveData.postValue(Resourse.Loading())
            val response = repository.getNews(countryCode = countryCode, pageNumber = newsPage)
            if (response.isSuccessful) {
                response.body().let { res ->
                    newsLiveData.postValue(Resourse.Success(res))
                }
            } else {
                newsLiveData.postValue(Resourse.Error(message = response.message()))
            }
        }

    fun shareNews(){

    }
}