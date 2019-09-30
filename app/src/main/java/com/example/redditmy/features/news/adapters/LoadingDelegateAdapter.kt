package com.example.redditmy.features.news.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redditmy.R
import com.example.redditmy.commons.adapter.ViewType
import com.example.redditmy.commons.adapter.ViewTypeDelegateAdapter
import com.example.redditmy.commons.extensions.inflate

class LoadingDelegateAdapter : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup) = LoadingViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
    }

    class LoadingViewHolder(parent: ViewGroup) :RecyclerView.ViewHolder(
        parent.inflate(R.layout.news_item_loading)
    )
}