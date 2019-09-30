package com.example.redditmy.features.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidcba.kedditbysteps.commons.RedditNews
import com.example.redditmy.R
import com.example.redditmy.commons.extensions.androidLazy
import com.example.redditmy.commons.extensions.inflate
import com.example.redditmy.features.news.adapters.NewsAdapter
import com.example.redditmy.features.news.adapters.NewsDelegateAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.news_fragment.*


class NewsFragment : Fragment(), NewsDelegateAdapter.onViewSelectedListener {

    override fun onItemSelected(url: String?) {
        if(url.isNullOrEmpty()){
            Snackbar.make(news_list, "No URL assigned to this news", Snackbar.LENGTH_LONG).show()
        }else{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }

    companion object{
        private const val KEY_REDDIT_NEWS = "redditNews"
    }

    private var redditNews: RedditNews? = null
    private val newsAddapter by androidLazy(NewsAdapter(this))

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<NewsViewModel>
    private val newsViewModel by androidLazy {
        getViewModel<NewsViewModel>(viewModelFactory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KedditApp.newsComponent.inject(this)

        newsViewModel.newsState.observe(this, Observer<NewsState> {
            manageState(it)
        })
    }

    private fun manageState(kedditState: NewsState?) {
        val state = kedditState ?: return
        when (state) {
            is NewsState.Success -> {
                redditNews = state.redditNews
                newsAdapter.addNews(state.redditNews.news)
            }
            is NewsState.Error -> {
                Snackbar.make(news_list, state.message.orEmpty(), Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY") { requestNews() }
                    .show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.news_fragment)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        news_list.apply {
            setHasFixedSize(true)
            val linearLayout = LinearLayoutManager(context)
            layoutManager = linearLayout
            clearOnScrollListeners()
            addOnScrollListener(InfiniteScrollListener({ requestNews() }, linearLayout))
        }

        news_list.adapter = newsAdapter

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_REDDIT_NEWS)) {
            redditNews = savedInstanceState.get(KEY_REDDIT_NEWS) as RedditNews
            newsAdapter.clearAndAddNews(redditNews!!.news)
        } else {
            requestNews()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val news = newsAdapter.getNews()
        if (redditNews != null && news.isNotEmpty()) {
            outState.putParcelable(KEY_REDDIT_NEWS, redditNews?.copy(news = news))
        }
    }

    private fun requestNews() {
        /**
         * first time will send empty string for 'after' parameter.
         * Next time we will have redditNews set with the next page to
         * navigate with the 'after' param.
         */
        newsViewModel.fetchNews(redditNews?.after.orEmpty())
    }
}
