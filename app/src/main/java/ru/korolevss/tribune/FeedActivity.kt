package ru.korolevss.tribune

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.item_load_after_fail.*
import kotlinx.coroutines.launch
import ru.korolevss.tribune.model.PostModel
import ru.korolevss.tribune.model.Token
import ru.korolevss.tribune.postadapter.PostAdapter
import ru.korolevss.tribune.postadapter.PostDiffUtilCallback
import ru.korolevss.tribune.postadapter.PostViewHolder
import ru.korolevss.tribune.repository.Repository
import java.io.IOException

class FeedActivity : AppCompatActivity(), PostAdapter.OnLikeBtnClickListener,
    PostAdapter.OnDislikeBtnClickListener {

    private companion object {
        const val CREATE_POST_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        setSupportActionBar(my_toolbar_feed)

        requestToken()

        fab.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivityForResult(intent, CREATE_POST_REQUEST_CODE)
        }

        lifecycleScope.launch {
            try {
                switchDeterminateBar(true)
                val result = Repository.getRecent()
                if (result.isSuccessful) {
                    with(containerFeed) {
                        layoutManager = LinearLayoutManager(this@FeedActivity)
                        adapter = PostAdapter(
                            (result.body() ?: emptyList()) as MutableList<PostModel>
                        ).apply {
                            likeBtnClickListener = this@FeedActivity
                            dislikeBtnClickListener = this@FeedActivity
                        }
                    }
                } else {
                    Toast.makeText(
                        this@FeedActivity,
                        R.string.loading_posts_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@FeedActivity,
                    R.string.connect_to_server_failed,
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                switchDeterminateBar(false)
            }
        }

        swipeContainerFeed.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData() {
        lifecycleScope.launch {
            try {
                val newData = Repository.getRecent()
                swipeContainerFeed.isRefreshing = false
                if (newData.isSuccessful) {
                    with(containerFeed) {
                        try {
                            val oldList = (adapter as PostAdapter).list
                            val newList = newData.body()!! as MutableList<PostModel>
                            val postDiffUtilCallback = PostDiffUtilCallback(oldList, newList)
                            val postDiffResult = DiffUtil.calculateDiff(postDiffUtilCallback)
                            (adapter as PostAdapter).newRecentPosts(newList)
                            postDiffResult.dispatchUpdatesTo(adapter as PostAdapter)
                        } catch (e: TypeCastException) {
                            layoutManager = LinearLayoutManager(this@FeedActivity)
                            adapter = PostAdapter(
                                (newData.body() ?: emptyList()) as MutableList<PostModel>
                            ).apply {
                                likeBtnClickListener = this@FeedActivity
                                dislikeBtnClickListener = this@FeedActivity
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return true
    }

    private fun requestToken() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@FeedActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }

            if (isUserResolvableError(code)) {
                getErrorDialog(this@FeedActivity, code, 9000).show()
                return
            }

            Snackbar.make(
                constraint_feed,
                getString(R.string.google_play_unavailable),
                Snackbar.LENGTH_LONG
            ).show()
            return
        }
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            lifecycleScope.launch {
                val token = Token(it.token)
                try {
                    Repository.firebasePushToken(token)
                } catch (e: IOException) {
                    Toast.makeText(
                        this@FeedActivity,
                        R.string.connect_to_server_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun switchDeterminateBar(isLaunch: Boolean) {
        if (isLaunch) {
            determinateBarFeed.isVisible = true
            fab.isEnabled = false

        } else {
            determinateBarFeed.isVisible = false
            fab.isEnabled = true
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
            switchDeterminateBar(true)
            try {
                item.likeActionPerforming = true
                with(containerFeed) {
                    adapter?.notifyItemChanged(position)
                    val response = if (item.likedByUser) {
                        Toast.makeText(
                            this@FeedActivity,
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
                    this@FeedActivity,
                    R.string.connect_to_server_failed,
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                switchDeterminateBar(false)
            }
        }
    }

    override fun onDislikeBtnClicked(item: PostModel, position: Int) {
        lifecycleScope.launch {
            switchDeterminateBar(true)
            try {
                item.dislikeActionPerforming = true
                with(containerFeed) {
                    adapter?.notifyItemChanged(position)
                    val response = if (item.dislikedByUser) {
                        Toast.makeText(
                            this@FeedActivity,
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
                    this@FeedActivity,
                    R.string.connect_to_server_failed,
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                switchDeterminateBar(false)
            }
        }
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
}