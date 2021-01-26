package com.example.socialmediaapp.model.dao

import com.example.socialmediaapp.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {

    var db =FirebaseFirestore.getInstance()
    private val userCollection= db.collection("user")

    fun addUsers(user: User?){
      user?.let  {
           GlobalScope.launch(Dispatchers.IO) {
               userCollection.document(user.uid).set(it)
           }
       }
    }

    fun getUserById(uid :String): Task<DocumentSnapshot>{
        return userCollection.document(uid).get()
    }
}