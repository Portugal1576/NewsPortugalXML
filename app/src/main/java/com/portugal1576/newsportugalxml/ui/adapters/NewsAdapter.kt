package com.portugal1576.newsportugalxml.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.portugal1576.newsportugalxml.R
import com.portugal1576.newsportugalxml.databinding.ItemArticleBinding
import com.portugal1576.newsportugalxml.models.Article

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {

            binding.apply {
                if (article.urlToImage != null) {
                    Glide.with(root.context).load(article.urlToImage).into(articleImage)
                } else {
                    Glide.with(root.context).load(R.drawable.default_image).into(articleImage)
                }
                articleImage.clipToOutline = true
                articleData.text = article.publishedAt
                articleTitle.text = article.title

                root.setOnClickListener {
                    onItemClickListener?.let {
                        it(article)
                    }
                }
            }
        }
    }

    private val callback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, callback)
    private var onItemClickListener: ((Article) -> Unit)? = null
    private var onShareClickListener: ((Article) -> Unit)? = null

    fun setOnClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnShareClickListener(listener: (Article) -> Unit) {
        onShareClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemArticleBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.bind(article)
        holder.binding.articleShare.setOnClickListener {
            onShareClickListener?.invoke(article)
        }
    }
}