package com.dicoding.storyapp.ui.appAdapters
import com.dicoding.storyapp.ui.home.BookmarkFragment
import com.dicoding.storyapp.ui.home.HomeFragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter


class SectionsPagerAdapter internal constructor(
    activity: AppCompatActivity
    ) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        var pagerFragment: Fragment? = null

        when (position) {
            0 -> pagerFragment = HomeFragment()
            1 -> pagerFragment = BookmarkFragment()
        }

        return pagerFragment as Fragment
    }

    override fun getItemCount(): Int = 2
}