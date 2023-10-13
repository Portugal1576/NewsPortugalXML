package com.portugal1576.newsportugalxml.data.bd

import androidx.room.Database
import com.portugal1576.newsportugalxml.models.Article
import androidx.room.RoomDatabase

@Database(entities = [Article::class], version = 1, exportSchema = true)

abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao
}