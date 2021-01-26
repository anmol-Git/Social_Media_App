package com.example.socialmediaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.socialmediaapp.model.User
import com.example.socialmediaapp.model.dao.UserDao
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignInActivity : AppCompatActivity() {
    private lateinit var googleSignInClient :GoogleSignInClient
    private lateinit var auth :FirebaseAuth

    private val RC_SIGN_IN : Int =123
    private val TAG="SignIn Activity tag"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val button =findViewById<SignInButton>(R.id.signInButton)
        googleSignInClient=GoogleSignIn.getClient(this,gso)
        auth=Firebase.auth

        button.setOnClickListener {
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser =auth.currentUser
        updateUi(currentUser)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
              try {
                  val account = task?.getResult(ApiException::class.java)!!
                  Log.d(TAG,"Firebase"+account.id)
                  firebaseAuth(account.idToken)
              }catch (e : ApiException){
                  Log.d("TAG","failed"+e.statusCode)
              }
    }

    private fun firebaseAuth(idToken: String?) {
        val button =findViewById<SignInButton>(R.id.signInButton)
        val  bar =findViewById<ProgressBar>(R.id.progressBar)
          val credentail =GoogleAuthProvider.getCredential(idToken,null)
           button.visibility=View.GONE
           bar.visibility=View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            val auth =auth.signInWithCredential(credentail).await()
            val firebase =auth.user
            withContext(Dispatchers.Main){
                updateUi(firebase)
            }
        }
    }

    private fun updateUi(firebase: FirebaseUser?) {
        val button =findViewById<SignInButton>(R.id.signInButton)
        val  bar =findViewById<ProgressBar>(R.id.progressBar)
           if (firebase!=null){
               val user=User(firebase.uid,firebase.displayName,firebase.photoUrl.toString())
               val usersDao =UserDao()
               usersDao.addUsers(user)

               val intent =Intent(this,MainActivity::class.java)
               startActivity(intent)
               finish()
           }else{
               Toast.makeText(this,"Google Sign In Failed!",Toast.LENGTH_LONG).show()
               button.visibility=View.VISIBLE
               bar.visibility=View.GONE
           }
    }
}