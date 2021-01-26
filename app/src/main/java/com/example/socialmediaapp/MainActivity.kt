package com.example.socialmediaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.adapter.IPostAdapter
import com.example.socialmediaapp.adapter.PostAdapter
import com.example.socialmediaapp.model.Post
import com.example.socialmediaapp.model.dao.PostDao
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity(),IPostAdapter {
    private lateinit var adapter: PostAdapter
    lateinit var postDao: PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        postDao=PostDao()
        val fab =findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener {
              val intent =Intent(this,PostActivity::class.java)
              startActivity(intent)
        }
        setUpRecyclerView()

    }

    private fun setUpRecyclerView() {
        val postCollection =postDao.postCollection
        val query=postCollection.orderBy("createdAt",Query.Direction.DESCENDING)
        val recycelerViewOptions =FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()
        val recycler =findViewById<RecyclerView>(R.id.recyclerView)
        adapter=PostAdapter(recycelerViewOptions,this)
        recycler.adapter=adapter
        recycler.layoutManager=LinearLayoutManager(this)
    }

    override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        adapter.stopListening()
        super.onStop()
    }
}