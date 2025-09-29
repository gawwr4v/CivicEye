package com.example.reportapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reportapp.R

class ArticlesAdapter(
    private val articles: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.articleTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.titleText.text = article
        holder.itemView.setOnClickListener { onClick(article) }
    }

    override fun getItemCount(): Int = articles.size
}
