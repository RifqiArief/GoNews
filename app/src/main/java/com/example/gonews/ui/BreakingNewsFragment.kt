package com.example.gonews.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gonews.MainActivity
import com.example.gonews.R
import com.example.gonews.adapters.NewsAdapter
import com.example.gonews.util.Constants
import com.example.gonews.util.Resource
import com.example.gonews.viewModel.NewsViewModel
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.log

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news){

    lateinit var viewModel : NewsViewModel
    lateinit var newsAdapter : NewsAdapter

    var newsCategory = arrayOf("business","entertainmen","general","health","science","sports","technology")
    var category : String = "technology"

    val TAG = "NewsLog"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupRecylerAdapter()
        setupSpinner()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                    R.id.action_breakingNewsFragment_to_articleFragment,
                    bundle
            )
        }

//        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
//            when(response){
//                is Resource.Success -> {
//                    hideProgressBar()
//                    response.data?.let { newsResponse ->
//                        newsAdapter.differ.submitList(newsResponse.articles.toList())
//                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
//                        isLastPage = viewModel.breakingNewsPage == totalPages
//                        if(isLastPage){
//                            rv_breaking_news.setPadding(0,0,0,0)
//                        }
//                    }
//                }
//                is Resource.Error -> {
//                    hideProgressBar()
//                    response.message?.let { message ->
//                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG)
//                        Log.i(TAG, "Error : $message")
//                    }
//                }
//                is Resource.Loading ->{
//                    showProgressBar()
//                }
//            }
//        })

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if(isLastPage) {
                            rv_breaking_news.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                Toast.makeText(context,parent?.getItemAtPosition(position).toString(),Toast.LENGTH_LONG).show()

                category = parent?.getItemAtPosition(position).toString()

                viewModel.getBreakingNews(category)

//                var job: Job? = null
//                job?.cancel()
//                job = MainScope().launch {
//                    delay(Constants.SEARCH_NEWS_TIME_DELAY)
//                    category?.let {
//                        if(category.isNotEmpty()){
//                            viewModel.getBreakingNews(category)
//                        }
//                    }
//                }

            }
        }
    }

    private fun hideProgressBar(){
        pb_breaking_news.visibility = View.GONE
        isLoading = false
    }

    private fun showProgressBar(){
        pb_breaking_news.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate) {
                viewModel.getBreakingNews(category)
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupRecylerAdapter(){
        newsAdapter = NewsAdapter()
        rv_breaking_news.apply {
            adapter =  newsAdapter
            layoutManager =LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    private fun setupSpinner(){
        val listCategory = context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, newsCategory) }
        listCategory?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.setAdapter(listCategory)
    }
}