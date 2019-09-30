package com.example.redditmy.features.news

import com.droidcba.kedditbysteps.commons.RedditNews
import com.droidcba.kedditbysteps.commons.RedditNewsItem

sealed class NewsState{
    class Success(val redditNews: RedditNews) : NewsState()
    class Error(val message: String?) : NewsState()
}