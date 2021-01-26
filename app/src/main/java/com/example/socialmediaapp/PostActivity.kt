package com.example.socialmediaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.socialmediaapp.model.dao.PostDao

class PostActivity : AppCompatActivity() {
    lateinit var postDao: PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val post =findViewById<EditText>(R.id.postContent)
        val btn =findViewById<Button>(R.id.postbutton)
        postDao= PostDao()
        btn.setOnClickListener {
          val input =post.text.toString().trim()
            if (input.isNotEmpty()){
                 postDao.addPost(input)
                finish()
            }
        }
    }
}