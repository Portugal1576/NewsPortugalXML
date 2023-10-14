package com.portugal1576.newsportugalxml.ui.favorite

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
import com.portugal1576.newsportugalxml.databinding.FragmentFavoriteBinding
import com.portugal1576.newsportugalxml.ui.adapters.FavoriteAdapter
import com.portugal1576.newsportugalxml.utils.Resourse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel by viewModels<FavoriteViewModel>()
    lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        viewModel.getFavoriteNews()
        favoriteAdapter.notifyDataSetChanged()

        favoriteAdapter.setOnClickListener {
            val bundle = bundleOf("article" to it)
            view.findNavController().navigate(
                R.id.action_favoriteFragment_to_detailFragment,
                bundle
            )
        }

        viewModel.favoritesLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resourse.Success -> {
                    mBinding.pagProgressBarFav.visibility = View.INVISIBLE
                    response.data?.let {
                        favoriteAdapter.differ.submitList(response.data)
                    }
                }

                is Resourse.Error -> {
                    mBinding.pagProgressBarFav.visibility = View.INVISIBLE
                    response.data?.let {
                        Log.d("checkData", "MainFragment: error $it")
                    }
                }

                is Resourse.Loading -> {
                    mBinding.pagProgressBarFav.visibility = View.VISIBLE
                }
            }
        }
        favoriteAdapter.setOnDeleteClickListener { article ->
            viewModel.deleteFavoriteArticle(article)
            favoriteAdapter.notifyDataSetChanged()
        }
    }

    private fun initAdapter() {
        favoriteAdapter = FavoriteAdapter()
        mBinding.newsAdapterFav.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}