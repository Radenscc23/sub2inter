package com.dicoding.storyapp.ui.home
import com.dicoding.storyapp.databinding.FragmentHomeBinding
import com.dicoding.storyapp.factory.ViewModelFactory
import com.dicoding.storyapp.ui.appAdapters.LoadingAdapter
import com.dicoding.storyapp.ui.appAdapters.StoriesHomeAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager


class HomeFragment : Fragment() {

    private lateinit var appBinding: FragmentHomeBinding
    private lateinit var appViewModel: HomeViewModel
    private lateinit var storiesHomeAdapter: StoriesHomeAdapter



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupViewModel()
        setupData()
        setData()
        setupAction()
    }

    private fun setupData() {
        appViewModel.getLogin().observe(viewLifecycleOwner) { user ->
            if (user.token.isNotBlank()) {
                getAllStories(user.token)
            }
        }

        appBinding.swipeRefreshLayout.isRefreshing = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return appBinding.root
    }


    private fun setupAction() {
        appBinding.swipeRefreshLayout.setOnRefreshListener {
            setupData()
        }
    }

    private fun setData() {
        appBinding.rvStories.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = storiesHomeAdapter.withLoadStateFooter(
                footer = LoadingAdapter {
                    storiesHomeAdapter.retry()
                }
            )
        }
    }

    private fun getAllStories(token: String) {
        appViewModel.getAllStories(token).observe(viewLifecycleOwner) {
            storiesHomeAdapter.submitData(lifecycle, it)
        }
    }

    private fun setupAdapter() {
        storiesHomeAdapter = StoriesHomeAdapter { story ->
            if (story.isBookmarked) {
                appViewModel.deleteStory(story)
            } else {
                appViewModel.saveStory(story)
            }
        }
    }

    private fun setupViewModel() {
        appViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[HomeViewModel::class.java]
    }

    fun scrollToTop() {
        val recyclerView = appBinding.rvStories
        recyclerView.smoothScrollToPosition(0)
    }
}