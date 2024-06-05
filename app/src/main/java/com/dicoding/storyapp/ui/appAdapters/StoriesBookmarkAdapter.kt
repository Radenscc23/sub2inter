package com.dicoding.storyapp.ui.appAdapters
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.entity.StoryEntity
import com.dicoding.storyapp.databinding.ItemRowStoryBinding
import com.dicoding.storyapp.ui.detail.DetailActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView



class StoriesBookmarkAdapter(private val bookmarkClick: (StoryEntity) -> Unit) :
    ListAdapter<StoryEntity, StoriesBookmarkAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val storyImage = binding.ivItemPhoto
        val storyName = binding.tvItemName
        val storyDescription = binding.tvItemDescription
        val storyBookmark = binding.ivBookmark

        fun bind(story: StoryEntity) {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.STORY, story)
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<StoryEntity> =
            object : DiffUtil.ItemCallback<StoryEntity>() {
                override fun areItemsTheSame(
                    oldItem: StoryEntity,
                    newItem: StoryEntity
                ) : Boolean = oldItem.id == newItem.id

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: StoryEntity,
                    newItem: StoryEntity
                ) : Boolean = oldItem == newItem
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)

        Glide.with(holder.itemView.context).load(story.photoUrl).into(holder.storyImage)

        holder.storyName.text = story.name
        holder.storyDescription.text = story.description

        val ivBookmark = holder.storyBookmark

        if (story.isBookmarked) {
            ivBookmark.setImageDrawable(ContextCompat.getDrawable(
                ivBookmark.context,
                R.drawable.baseline_bookmark_48)
            )
        } else {
            ivBookmark.setImageDrawable(ContextCompat.getDrawable(
                ivBookmark.context,
                R.drawable.baseline_bookmark_border_48)
            )
        }

        ivBookmark.setOnClickListener {
            bookmarkClick(story)
        }

        holder.bind(story)
    }


}