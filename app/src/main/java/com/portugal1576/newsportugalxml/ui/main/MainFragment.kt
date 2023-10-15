package com.portugal1576.newsportugalxml.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.portugal1576.newsportugalxml.R
import com.portugal1576.newsportugalxml.databinding.FragmentMainBinding
import com.portugal1576.newsportugalxml.ui.adapters.NewsAdapter
import com.portugal1576.newsportugalxml.utils.Resourse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel by viewModels<MainViewModel>()

    lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.countryCodeLiveData.value.isNullOrEmpty()) {
            viewModel.countryCodeLiveData.value = "us" // Встановити значення "us" за замовчуванням
        }

        viewModel.countryCodeLiveData.observe(viewLifecycleOwner) { countryCode ->
            updateTitleText(countryCode)
        }

        mBinding.flagUs.setOnClickListener {
            viewModel.countryCodeLiveData.value = "us"
            viewModel.getNews("us")
        }

        mBinding.flagPt.setOnClickListener {
            viewModel.countryCodeLiveData.value = "pt"
            viewModel.getNews("pt")
        }

        mBinding.flagUa.setOnClickListener {
            viewModel.countryCodeLiveData.value = "ua"
            viewModel.getNews("ua")
        }

        initAdapter()

        newsAdapter.setOnClickListener {
            val bundle = bundleOf("article" to it)
            view.findNavController().navigate(
                R.id.action_mainFragment_to_detailFragment,
                bundle
            )
        }


        viewModel.newsLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resourse.Success -> {
                    mBinding.pagProgressBar.visibility = View.INVISIBLE
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles)
                    }
                }

                is Resourse.Error -> {
                    mBinding.pagProgressBar.visibility = View.INVISIBLE
                    response.data?.let {
                        Log.d("checkData", "MainFragment: error $it")
                    }
                }

                is Resourse.Loading -> {
                    mBinding.pagProgressBar.visibility = View.VISIBLE
                }
            }
        }
        newsAdapter.setOnShareClickListener {
                newsItem ->
            // Створіть текстовий контент для поділу, наприклад, заголовок і посилання на новину
            val shareText = "Check this news: ${newsItem.title}\n${newsItem.url}"

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(sendIntent, "Share via: "))
        }
    }

    private fun initAdapter() {
        newsAdapter = NewsAdapter()
        mBinding.newsAdapter.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updateTitleText(countryCode: String) {
        val titleText = when (countryCode) {
            "us" -> "USA News"
            "pt" -> "Portugal News"
            "ua" -> "Ukraine News"
            else -> "Unknown Country"
        }
        mBinding.titleText.text = titleText
    }
}