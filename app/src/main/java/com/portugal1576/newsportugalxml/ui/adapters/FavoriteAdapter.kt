package com.portugal1576.newsportugalxml.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.portugal1576.newsportugalxml.R
import com.portugal1576.newsportugalxml.databinding.ItemFavoriteBinding
import com.portugal1576.newsportugalxml.models.Article


class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.FavoriteNewsViewHolder>() {

    inner class FavoriteNewsViewHolder(val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.apply {
                if (article.urlToImage != null) {
                    Glide.with(root.context).load(article.urlToImage).into(favImage)
                } else {
                    Glide.with(root.context).load(R.drawable.default_image).into(favImage)
                }

                favImage.clipToOutline = true
                favData.text = article.publishedAt
                favTitle.text = article.title

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
    private var onDeleteClickListener: ((Article) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteNewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFavoriteBinding.inflate(inflater, parent, false)
        return FavoriteNewsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: FavoriteNewsViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.bind(article)

        holder.binding.favDelete.setOnClickListener {
            onDeleteClickListener?.invoke(article)
        }
    }

    fun setOnDeleteClickListener(listener: (Article) -> Unit) {
        onDeleteClickListener = listener
    }
    fun setOnClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}