package com.portugal1576.newsportugalxml.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.portugal1576.newsportugalxml.R
import com.portugal1576.newsportugalxml.databinding.FragmentSearchBinding
import com.portugal1576.newsportugalxml.ui.adapters.NewsAdapter
import com.portugal1576.newsportugalxml.utils.Resourse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel by viewModels<SearchViewModel>()
    lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        var job: Job? = null
        mBinding.edSearch.addTextChangedListener { text ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                text?.let {
                    if (it.toString().isNotEmpty()) {
                        viewModel.getsearchNews(query = it.toString())
                        delay(4000)

                        hideKeyboard()
                    }
                }
            }
        }

        viewModel.searchNewsLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resourse.Success -> {
                    mBinding.searchPrBar.visibility = View.INVISIBLE
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles)
                    }
                }

                is Resourse.Error -> {
                    mBinding.searchPrBar.visibility = View.INVISIBLE
                    response.data?.let {
                        Log.d("checkData", "MainFragment: error $it")
                    }
                }

                is Resourse.Loading -> {
                    mBinding.searchPrBar.visibility = View.VISIBLE
                }
            }
        }
        newsAdapter.setOnClickListener {
            val bundle = bundleOf("article" to it)
            view.findNavController().navigate(
                R.id.action_searchFragment_to_detailFragment,
                bundle
            )
        }

        newsAdapter.setOnShareClickListener { newsItem ->
            // Створіть текстовий контент для поділу, наприклад, заголовок і посилання на новину
            val shareText = "Перевір цю новину: ${newsItem.title}\n${newsItem.url}"

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(sendIntent, "Поділитися через"))
        }
    }

    private fun initAdapter() {
        newsAdapter = NewsAdapter()
        mBinding.searchAdapter.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = activity?.currentFocus
        currentFocusView?.let {
            inputMethodManager.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}