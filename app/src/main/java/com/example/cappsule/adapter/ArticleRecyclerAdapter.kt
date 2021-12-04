package com.example.cappsule.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.cappsule.R
import com.example.cappsule.database.DatabaseHelperArticle
import com.example.cappsule.objects.Article

class ArticleRecyclerAdapter(private val context: Activity, private val articles: ArrayList<Article>, private val databaseHelperArticle: DatabaseHelperArticle, private val onClickArticleListener: OnClickArticleListener) : RecyclerView.Adapter<ArticleRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.article_recycler_item, parent, false), databaseHelperArticle, onClickArticleListener, articles)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.imageView.setImageBitmap(article.image)
        holder.checkBox.isChecked = article.availability == 1
        holder.textViewType.text = article.type
        holder.textViewWarmth.text = article.warmth
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    class ViewHolder(view: View, databaseHelperArticle: DatabaseHelperArticle, onClickArticleListener: OnClickArticleListener, articles: ArrayList<Article>) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val imageView: ImageView
        val checkBox: CheckBox
        val textViewType: TextView
        val textViewWarmth: TextView
        private val databaseHelperArticle: DatabaseHelperArticle
        private val onClickArticleListener: OnClickArticleListener
        private val articles: ArrayList<Article>
        private val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                databaseHelperArticle.updateAvailability(articles[adapterPosition].id, 1)
            } else {
                databaseHelperArticle.updateAvailability(articles[adapterPosition].id, 0)
            }
        }

        override fun onClick(v: View) {
            onClickArticleListener.onClickArticleListener(adapterPosition)
        }

        init {
            imageView = view.findViewById(R.id.article_recycler_image)
            checkBox = view.findViewById(R.id.article_recycler_checkbox)
            textViewType = view.findViewById(R.id.article_recycler_type)
            textViewWarmth = view.findViewById(R.id.article_recycler_warmth)
            this.articles = articles
            val delete = view.findViewById<Button>(R.id.article_recycler_delete)
            this.databaseHelperArticle = databaseHelperArticle
            this.onClickArticleListener = onClickArticleListener
            checkBox.setOnCheckedChangeListener(onCheckedChangeListener)
            delete.setOnClickListener { onClickArticleListener.onClickDeleteListener(adapterPosition) }
            view.setOnClickListener(this)
        }
    }

    interface OnClickArticleListener {
        fun onClickArticleListener(position: Int)
        fun onClickDeleteListener(position: Int)
    }
}