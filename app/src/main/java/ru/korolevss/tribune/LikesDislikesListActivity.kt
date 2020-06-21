package ru.korolevss.tribune

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_likes_dislikes_list.*
import kotlinx.coroutines.launch
import ru.korolevss.tribune.model.LikeDislikeModel
import ru.korolevss.tribune.postadapter.LikeDislikeAdapter
import ru.korolevss.tribune.postadapter.PostViewHolder
import ru.korolevss.tribune.postadapter.PostViewHolder.Companion.POST_ID
import ru.korolevss.tribune.repository.Repository
import java.io.IOException

class LikesDislikesListActivity : AppCompatActivity() {

    private val postId: Long by lazy { intent.getLongExtra(POST_ID, 0L) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_likes_dislikes_list)
        setSupportActionBar(my_toolbar_likes_dislikes_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        lifecycleScope.launch {
            try {
                determinateBarLikesDislikesList.isEnabled = true
                val result = Repository.getLikeDislikeUsers(postId)
                if (result.isSuccessful) {
                    with(containerLikesDislikesList) {
                        layoutManager = LinearLayoutManager(this@LikesDislikesListActivity)
                        adapter = LikeDislikeAdapter(
                            (result.body() ?: emptyList()) as MutableList<LikeDislikeModel>
                        )
                    }
                } else {
                    Toast.makeText(
                        this@LikesDislikesListActivity,
                        R.string.loading_posts_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@LikesDislikesListActivity,
                    R.string.connect_to_server_failed,
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                determinateBarLikesDislikesList.isEnabled = true
            }
        }

        swipeContainerLikesDislikes.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData() {
        lifecycleScope.launch {
            try {
                val newData = Repository.getLikeDislikeUsers(postId)
                swipeContainerLikesDislikes.isRefreshing = false
                if (newData.isSuccessful) {
                    with(containerLikesDislikesList) {
                        (adapter as LikeDislikeAdapter).refresh(newData.body()!!)
                    }
                }
            } catch (e: IOException) {
                swipeContainerLikesDislikes.isRefreshing = false
                Toast.makeText(
                    this@LikesDislikesListActivity,
                    R.string.connect_to_server_failed,
                    Toast.LENGTH_SHORT
                ).show()
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
            val intent = Intent(this, UserActivity::class.java)
            intent.putExtra(PostViewHolder.USERNAME, getString(R.string.me))
            startActivity(intent)
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