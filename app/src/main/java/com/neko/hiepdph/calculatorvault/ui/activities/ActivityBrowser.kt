package com.neko.hiepdph.calculatorvault.ui.activities

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.customview.ChromeProgressBar
import com.neko.hiepdph.calculatorvault.common.extensions.SnackBarType
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.extensions.showSnackBar
import com.neko.hiepdph.calculatorvault.data.database.model.BookmarkModel
import com.neko.hiepdph.calculatorvault.databinding.ActivityBrowserBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirm
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirmType
import com.neko.hiepdph.calculatorvault.sharedata.ShareData
import com.neko.hiepdph.calculatorvault.viewmodel.BrowserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActivityBrowser : AppCompatActivity() {
    private lateinit var binding: ActivityBrowserBinding
    private val viewModel by viewModels<BrowserViewModel>()
    private var listFavorite = mutableListOf<BookmarkModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        observeListBookmark()
        ShareData.getInstance().clearBrowser.observe(this) {
            binding.webView.clearCache(true)
            ShareData.getInstance().setBrowserClear(false)
        }
    }

    private fun initView() {
        binding.edtUrl.setText(intent.getStringExtra(Constant.KEY_URL))
        binding.progressBar.max = 100
        binding.progressBar.progressTintList = ColorStateList.valueOf(getColor(R.color.gradient_01))
        binding.progressBar.setOnProgressBarChange(listener = object :
            ChromeProgressBar.OnProgressChange {
            override fun onChange(progress: Int) {
                if (progress == 100) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(300)
                        binding.progressBar.progress = 0
                        binding.progressBar.hide()
                    }

                }
                if (progress > 0) {
                    binding.progressBar.show()
                }
            }

        })
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.allowContentAccess = true
            settings.allowFileAccess = true
            settings.domStorageEnabled = true
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    binding.progressBar.progress = newProgress
                }

            }

            webViewClient = object : WebViewClient() {
                override fun onLoadResource(view: WebView?, url: String?) {
                    super.onLoadResource(view, url)
                    binding.edtUrl.setText(binding.webView.url)
                    if (!binding.webView.canGoBack()) {
                        binding.btnBack.setColorFilter(getColor(R.color.neutral_04))
                    } else {
                        binding.btnBack.setColorFilter(getColor(R.color.neutral_05))
                    }
                    if (!binding.webView.canGoForward()) {
                        binding.btnNext.setColorFilter(getColor(R.color.neutral_04))
                    } else {
                        binding.btnNext.setColorFilter(getColor(R.color.neutral_05))
                    }

                    if (checkFavorite(listFavorite)) {
                        binding.imvFavorite.setImageResource(R.drawable.ic_favorite_active)
                    } else {
                        binding.imvFavorite.setImageResource(R.drawable.ic_favorite_inactive)
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                }

            }

        }.loadUrl(intent.getStringExtra(Constant.KEY_URL).toString())


        initButton()
        initEdittext()
    }

    private fun initEdittext() {
        binding.edtUrl.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.edtUrl.text.isNotBlank()) {
                    binding.webView.loadUrl(binding.edtUrl.text.toString())
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun initButton() {

        binding.imvFavorite.clickWithDebounce {
            when (binding.webView.url) {
                Constant.GOOGLE, Constant.DUCKDUCKGO, Constant.QWANT, Constant.SEARCH_ENCRYPT -> return@clickWithDebounce
            }
            if (checkFavorite(listFavorite)) {
                val bookmarkModel =
                    if (listFavorite.any { it.url == binding.webView.url }) listFavorite.filter { it.url == binding.webView.url }[0] else null

                bookmarkModel?.let {
                    viewModel.deleteBookmark(it.id)
                    showSnackBar(getString(R.string.remove_bookmark), type = SnackBarType.SUCCESS)
                }
            } else {
                val imageIcon =
                    Constant.PREFIX_URL_ICON + binding.webView.url + Constant.SUFFIX_URL_ICON
                viewModel.insertBookmark(
                    BookmarkModel(
                        -1,
                        binding.webView.title.toString(),
                        binding.webView.url.toString(),
                        imageIcon,
                        false
                    )
                )
                showSnackBar(getString(R.string.add_bookmark), type = SnackBarType.SUCCESS)

            }

        }
        binding.btnReload.clickWithDebounce {
            binding.webView.reload()
        }
        binding.btnBack.clickWithDebounce {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
                if (!binding.webView.canGoBack()) {
                    binding.btnBack.setColorFilter(getColor(R.color.neutral_04))
                } else {
                    binding.btnBack.setColorFilter(getColor(R.color.neutral_06))
                }
            }
        }

        binding.btnNext.clickWithDebounce {
            if (binding.webView.canGoForward()) {
                binding.webView.goForward()
                if (!binding.webView.canGoForward()) {
                    binding.btnNext.setColorFilter(getColor(R.color.neutral_04))
                } else {
                    binding.btnNext.setColorFilter(getColor(R.color.neutral_06))
                }
            }
        }

        binding.btnExit.clickWithDebounce {
            val dialogConfirm =
                DialogConfirm(onPositiveClicked = { finish() }, DialogConfirmType.BACK_BROWSER)

            dialogConfirm.show(supportFragmentManager, dialogConfirm.tag)

        }
        binding.btnCancel.clickWithDebounce {
            binding.containerTop.show()
            binding.progressBar.show()
            binding.containerController.show()
            binding.btnCancel.hide()
        }
        binding.btnFullscreen.clickWithDebounce {
            binding.containerTop.hide()
            binding.progressBar.hide()
            binding.containerController.hide()
            binding.btnCancel.show()
        }
    }

    private fun observeListBookmark() {
        viewModel.getListBookmark().observe(this) {
            listFavorite = it.toMutableList()
            if (checkFavorite(listFavorite)) {
                binding.imvFavorite.setImageResource(R.drawable.ic_favorite_active)
            } else {
                binding.imvFavorite.setImageResource(R.drawable.ic_favorite_inactive)
            }
        }
    }

    private fun checkFavorite(list: List<BookmarkModel>): Boolean {
        return list.map {
            it.url
        }.any { it == binding.webView.url }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            val dialogConfirm =
                DialogConfirm(onPositiveClicked = { finish() }, DialogConfirmType.BACK_BROWSER)

            dialogConfirm.show(supportFragmentManager, dialogConfirm.tag)
        }
    }
}