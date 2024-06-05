package com.dicoding.storyapp.ui.appAdapters
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.storyapp.databinding.ItemLoadingBinding
import android.view.LayoutInflater
import android.view.ViewGroup


class LoadingAdapter(private val optionRetry: () -> Unit) : LoadStateAdapter<LoadingAdapter.LoadingStateViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingStateViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding, optionRetry)
    }
    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
    class LoadingStateViewHolder(private val appBinding: ItemLoadingBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(appBinding.root) {
        init {
            appBinding.retryButton.setOnClickListener { retry.invoke() }
        }
        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                appBinding.errorMsg.text = loadState.error.localizedMessage
            }
            appBinding.progressBar.isVisible = loadState is LoadState.Loading
            appBinding.retryButton.isVisible = loadState is LoadState.Error
            appBinding.errorMsg.isVisible = loadState is LoadState.Error
        }
    }
}