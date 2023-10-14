package com.portugal1576.newsportugalxml.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portugal1576.newsportugalxml.data.api.NewsRepository
import com.portugal1576.newsportugalxml.models.Article
import com.portugal1576.newsportugalxml.utils.Resourse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    val favoritesLiveData: MutableLiveData<Resourse<List<Article>>> = MutableLiveData()

    init {
        getFavoriteNews()
    }

    private fun getFavoriteNews() =
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.getFavoriteArticles()
            favoritesLiveData.postValue(Resourse.Success(res))
        }

    fun saveFavoriteArticle(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        repository.addFavorite(article = article)
        getFavoriteNews()
    }
}