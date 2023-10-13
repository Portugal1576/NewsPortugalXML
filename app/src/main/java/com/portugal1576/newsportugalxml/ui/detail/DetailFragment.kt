package com.portugal1576.newsportugalxml.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.portugal1576.newsportugalxml.R
import com.portugal1576.newsportugalxml.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val mBinding get() = _binding!!

    private val bundleArgs: DetailFragmentArgs by navArgs()
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val articleArg = bundleArgs.article
        articleArg.let { article ->
            article.urlToImage.let {

                if (article.urlToImage != null) {
                    Glide.with(this).load(article.urlToImage).into(mBinding.headrImage)
                } else {
                    Glide.with(this).load(R.drawable.default_image).into(mBinding.headrImage)
                }
            }
            mBinding.headrImage.clipToOutline = true
            mBinding.articleDetailsTitle.text = article.title
            mBinding.articleDetailsText.text = article.description
            mBinding.detailsButtomUrl.setOnClickListener {
                try {
                    Intent()
                        .setAction(Intent.ACTION_VIEW)
                        .addCategory(Intent.CATEGORY_BROWSABLE)
                        .setData(Uri.parse(takeIf { URLUtil.isValidUrl(article.url) }
                            ?.let {
                                article.url
                            } ?: "https://google.com"))
                        .let {
                            ContextCompat.startActivity(requireContext(), it, null)
                        }
                } catch (e: Exception) {
                    Toast.makeText(
                        context, "The device doesn't have any browser to view the " +
                                "document", Toast.LENGTH_LONG
                    ).show()
                }
            }
            mBinding.iconFavorite.setOnClickListener {
                viewModel.saveFavoriteArticle(article)
                Toast.makeText(
                    context, "The news has been added to favorites.", Toast.LENGTH_LONG
                ).show()
            }
        }
        mBinding.iconShare.setOnClickListener {
            val shareText = "Перевір цю новину: ${articleArg.title}\n${articleArg.url}"

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(sendIntent, "Поділитися через"))
        }
        mBinding.iconBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}