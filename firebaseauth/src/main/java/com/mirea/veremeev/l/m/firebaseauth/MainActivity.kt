package com.mirea.veremeev.l.m.firebaseauth


import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var mStatusTextView: TextView
    private lateinit var mDetailTextView: TextView
    private lateinit var mEmailField: EditText
    private lateinit var mPasswordField: EditText

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mStatusTextView = findViewById(R.id.status)
        mDetailTextView = findViewById(R.id.detail)
        mEmailField = findViewById(R.id.fieldEmail)
        mPasswordField = findViewById(R.id.fieldPassword)
        findViewById<View>(R.id.emailSignInButton).setOnClickListener(this)
        findViewById<View>(R.id.emailCreateAccountButton).setOnClickListener(this)
        findViewById<View>(R.id.signOutButton).setOnClickListener(this)
        findViewById<View>(R.id.verifyEmailButton).setOnClickListener(this)
        FirebaseApp.initializeApp(this)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            mStatusTextView.text = getString(
                R.string.emailpassword_status_fmt,
                user.email, user.isEmailVerified
            )
            mDetailTextView.text = getString(R.string.firebase_status_fmt, user.uid)
            findViewById<View>(R.id.emailSignInButton).visibility = View.GONE
            findViewById<View>(R.id.emailCreateAccountButton).visibility = View.GONE
            mEmailField.visibility = View.GONE
            mPasswordField.visibility = View.GONE
            findViewById<View>(R.id.signOutButton).visibility = View.VISIBLE
            findViewById<View>(R.id.verifyEmailButton).isEnabled = !user.isEmailVerified
        } else {
            mStatusTextView.setText(R.string.signed_out)
            mDetailTextView.text = null
            findViewById<View>(R.id.emailSignInButton).visibility = View.VISIBLE
            findViewById<View>(R.id.emailCreateAccountButton).visibility = View.VISIBLE
            mEmailField.visibility = View.VISIBLE
            mPasswordField.visibility = View.VISIBLE
            findViewById<View>(R.id.signOutButton).visibility = View.GONE
        }
    }

    private fun validateForm(): Boolean {
        var valid = true
        val email = mEmailField.text.toString()
        if (TextUtils.isEmpty(email)) {
            mEmailField.error = "Required."
            valid = false
        } else {
            mEmailField.error = null
        }
        val password = mPasswordField.text.toString()
        if (TextUtils.isEmpty(password)) {
            mPasswordField.error = "Required."
            valid = false
        } else {
            mPasswordField.error = null
        }
        return valid
    }

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this@MainActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this@MainActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
                if (!task.isSuccessful) {
                    mStatusTextView.setText(R.string.auth_failed)
                }
            }
    }

    private fun signOut() {
        mAuth.signOut()
        updateUI(null)
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.emailCreateAccountButton) {
            createAccount(mEmailField.text.toString(), mPasswordField.text.toString())
        } else if (i == R.id.emailSignInButton) {
            signIn(mEmailField.text.toString(), mPasswordField.text.toString())
        } else if (i == R.id.signOutButton) {
            signOut()
        } else if (i == R.id.verifyEmailButton) {
        }
    }

}