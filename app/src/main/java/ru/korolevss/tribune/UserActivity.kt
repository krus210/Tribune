package ru.korolevss.tribune

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.item_load_after_fail.*
import kotlinx.coroutines.launch
import ru.korolevss.tribune.model.PostModel
import ru.korolevss.tribune.postadapter.PostAdapter
import ru.korolevss.tribune.postadapter.PostDiffUtilCallback
import ru.korolevss.tribune.postadapter.PostViewHolder
import ru.korolevss.tribune.postadapter.PostViewHolder.Companion.USERNAME
import ru.korolevss.tribune.repository.Repository
import java.io.IOException

class UserActivity : AppCompatActivity(), PostAdapter.OnLikeBtnClickListener,
    PostAdapter.OnDislikeBtnClickListener {

    val username: String by lazy { intent.getStringExtra(USERNAME)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        setSupportActionBar(my_toolbar_user)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        lifecycleScope.launch {
            try {
                determinateBarUser.isVisible = true
                val result = if (username == getString(R.string.me)) {
                    Repository.getPostsOfMe()
                } else {
                    Repository.getPostsOfUser(username)
                }
                if (result.isSuccessful) {
                    with(containerUser) {
                        layoutManager = LinearLayoutManager(this@UserActivity)
                        adapter = PostAdapter(
                            (result.body() ?: emptyList()) as MutableList<PostModel>
                        ).apply {
                            likeBtnClickListener = this@UserActivity
                            dislikeBtnClickListener = this@UserActivity
                        }
                    }
                } else {
                    Toast.makeText(
                        this@UserActivity,
                        R.string.loading_posts_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@UserActivity,
                    R.string.connect_to_server_failed,
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                determinateBarUser.isVisible = false
            }
        }
        swipeContainerUser.setOnRefreshListener {
            refreshData()
        }

    }

    private fun refreshData() {
        lifecycleScope.launch {
            try {
                val newData = if (username == getString(R.string.me)) {
                    Repository.getPostsOfMe()
                } else {
                    Repository.getPostsOfUser(username)
                }
                swipeContainerFeed.isRefreshing = false
                if (newData.isSuccessful) {
                    with(containerUser) {
                        try {
                            val oldList = (adapter as PostAdapter).list
                            val newList = newData.body()!! as MutableList<PostModel>
                            val postDiffUtilCallback = PostDiffUtilCallback(oldList, newList)
                            val postDiffResult = DiffUtil.calculateDiff(postDiffUtilCallback)
                            (adapter as PostAdapter).newRecentPosts(newList)
                            postDiffResult.dispatchUpdatesTo(adapter as PostAdapter)
                        } catch (e: TypeCastException) {
                            layoutManager = LinearLayoutManager(this@UserActivity)
                            adapter = PostAdapter(
                                (newData.body() ?: emptyList()) as MutableList<PostModel>
                            ).apply {
                                likeBtnClickListener = this@UserActivity
                                dislikeBtnClickListener = this@UserActivity
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                swipeContainerFeed.isRefreshing = false
                showDialogLoadAfterFail()
            }
        }
    }

    private fun showDialogLoadAfterFail() {
        val dialog = AlertDialog.Builder(this)
            .setView(R.layout.item_load_after_fail)
            .show()
        dialog.buttonTryElse.setOnClickListener {
            refreshData()
            dialog.dismiss()
        }
    }

    override fun onLikeBtnClicked(item: PostModel, position: Int) {
        lifecycleScope.launch {
            determinateBarUser.isVisible = true
            try {
                item.likeActionPerforming = true
                with(containerUser) {
                    adapter?.notifyItemChanged(position)
                    val response = if (item.likedByUser) {
                        Toast.makeText(
                            this@UserActivity,
                            R.string.vote_only_once,
                            Toast.LENGTH_SHORT
                        ).show()
                        null
                    } else {
                        Repository.likedByUser(item.id)
                    }
                    item.likeActionPerforming = false
                    if (response != null && response.isSuccessful) {
                        item.updatePost(response.body()!!)
                    }
                    adapter?.notifyItemChanged(position)
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@UserActivity,
                    R.string.connect_to_server_failed,
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                determinateBarUser.isVisible = false
            }
        }
    }

    override fun onDislikeBtnClicked(item: PostModel, position: Int) {
        lifecycleScope.launch {
            determinateBarUser.isVisible = true
            try {
                item.dislikeActionPerforming = true
                with(containerUser) {
                    adapter?.notifyItemChanged(position)
                    val response = if (item.dislikedByUser) {
                        Toast.makeText(
                            this@UserActivity,
                            R.string.vote_only_once,
                            Toast.LENGTH_SHORT
                        ).show()
                        null
                    } else {
                        Repository.dislikedByUser(item.id)
                    }
                    item.dislikeActionPerforming = false
                    if (response != null && response.isSuccessful) {
                        item.updatePost(response.body()!!)
                    }
                    adapter?.notifyItemChanged(position)
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@UserActivity,
                    R.string.connect_to_server_failed,
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                determinateBarUser.isVisible = false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }
        R.id.action_my_posts -> {
            if (username != getString(R.string.me)) {
                val intent = Intent(this, UserActivity::class.java)
                intent.putExtra(USERNAME, getString(R.string.me))
                startActivity(intent)
                finish()
            }
            true
        } else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return true
    }
}