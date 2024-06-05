package com.dicoding.storyapp.ui.home
import com.dicoding.storyapp.databinding.FragmentBookmarkBinding
import com.dicoding.storyapp.factory.ViewModelFactory
import com.dicoding.storyapp.ui.appAdapters.StoriesBookmarkAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup



class BookmarkFragment : Fragment() {

    private lateinit var appBinding: FragmentBookmarkBinding
    private lateinit var appViewModel: HomeViewModel
    private lateinit var storiesBookmarkAdapter: StoriesBookmarkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appBinding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return appBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupViewModel()
        setupData()
        data()
    }

    private fun setupViewModel() {
        appViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[HomeViewModel::class.java]
    }

    private fun setupAdapter() {
        storiesBookmarkAdapter = StoriesBookmarkAdapter { story ->
            if (story.isBookmarked) {
                appViewModel.deleteStory(story)
            } else {
                appViewModel.saveStory(story)
            }
        }
    }

    private fun setupData() {
        appViewModel.getBookmarkedStories().observe(viewLifecycleOwner) { bookmarkedStory ->
            if (bookmarkedStory.isEmpty()) {
                appBinding.tvMessage.visibility = View.VISIBLE
                storiesBookmarkAdapter.submitList(bookmarkedStory)
            } else {
                appBinding.tvMessage.visibility = View.GONE
                storiesBookmarkAdapter.submitList(bookmarkedStory)
            }
        }
    }

    fun toTop() {
        val recyclerView = appBinding.rvStories
        recyclerView.smoothScrollToPosition(0)
    }

    private fun data() {
        appBinding.rvStories.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = storiesBookmarkAdapter
        }
    }

}