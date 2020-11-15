package com.example.gonews.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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

    val TAG = "NewsLog"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupRecylerAdapter()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.i(TAG, "Error : $message")
                    }
                }
                is Resource.Loading ->{
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar(){
        pb_breaking_news.visibility = View.GONE
    }

    private fun showProgressBar(){
        pb_breaking_news.visibility = View.VISIBLE
    }

    private fun setupRecylerAdapter(){
        newsAdapter = NewsAdapter()
        rv_breaking_news.apply {
            adapter =  newsAdapter
            layoutManager =LinearLayoutManager(activity)
        }
    }
}