package com.portugal1576.newsportugalxml.data.api

import com.portugal1576.newsportugalxml.data.bd.ArticleDao
import com.portugal1576.newsportugalxml.models.Article
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsService: NewsService,
    private val articleDao: ArticleDao
    ) {

    suspend fun getNews(countryCode: String, pageNumber: Int) =
        newsService.getHeadlines(countryCode = countryCode, page = pageNumber)

    suspend fun getSearchNews(query: String, pageNumber: Int) =
        newsService.getEverything(query = query, page = pageNumber)

    fun getFavoriteArticles(): List<Article> = articleDao.getAllArticles()

    suspend fun addFavorite(article: Article) = articleDao.insert(article = article)

    suspend fun deleteFromFavorite(article: Article) = articleDao.delete(article = article)
}