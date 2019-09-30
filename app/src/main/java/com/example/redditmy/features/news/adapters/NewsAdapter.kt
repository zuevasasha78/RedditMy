package com.example.redditmy.features.news.adapters

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import com.droidcba.kedditbysteps.commons.RedditNewsItem
import com.example.redditmy.commons.adapter.AdapterConstants
import com.example.redditmy.commons.adapter.ViewType
import com.example.redditmy.commons.adapter.ViewTypeDelegateAdapter

class NewsAdapter(listener: NewsDelegateAdapter.onViewSelectedListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: ArrayList<ViewType>
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()
    private val loadingItem = object : ViewType {
        override fun getViewType() = AdapterConstants.LOADING
    }

    init {
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(AdapterConstants.NEWS, NewsDelegateAdapter(listener))
        items = ArrayList()
        items.add(loadingItem)
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegateAdapters.get(viewType).onCreateViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position)).onBindViewHolder(holder, items[position])
    }

    override fun getItemViewType(position: Int): Int = items[position].getViewType()

    fun addNew(news: List<RedditNewsItem>) {
        items.clear()
        notifyItemRangeRemoved(0, getLastPosition())

        items.addAll(news)
        items.add(loadingItem)
        notifyItemRangeInserted(0, items.size)
    }

    fun getNews(): List<RedditNewsItem> =
        items
            .filter { it.getViewType() == AdapterConstants.NEWS }
            .map { it as RedditNewsItem }

    private fun getLastPosition() = if (items.lastIndex == -1) 0 else items.lastIndex
}