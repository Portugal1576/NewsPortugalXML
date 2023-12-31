package com.portugal1576.newsportugalxml.ui.favorite

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
class FavoriteViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    val favoritesLiveData: MutableLiveData<Resourse<List<Article>>> = MutableLiveData()

    init {
        getFavoriteNews()
    }

    fun getFavoriteNews() =
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.getFavoriteArticles()
            favoritesLiveData.postValue(Resourse.Success(res))
        }

    fun deleteFavoriteArticle(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteFromFavorite(article = article)
        getFavoriteNews()
    }
}
