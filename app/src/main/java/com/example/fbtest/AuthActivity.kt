package com.example.fbtest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.fbtest.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(MyApplication.checkAuth()){
            changeVisibility("login")
        }else {
            changeVisibility("logout")
        }

        binding.logoutBtn.setOnClickListener {
            //로그아웃...........
            MyApplication.auth.signOut()
            MyApplication.email = null
            changeVisibility("layout")
        }

        binding.goSignInBtn.setOnClickListener{
            changeVisibility("signin")
        }

        binding.googleLoginBtn.setOnClickListener {
            //구글 로그인....................
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val signInIntent = GoogleSignIn.getClient(this,gso).signInIntent
            startActivityForResult(signInIntent, 10)
        }

        binding.signBtn.setOnClickListener {
            //이메일,비밀번호 회원가입........................
            val email:String = binding.authEmailEditView.text.toString()
            val passward:String =binding.authPasswordEditView.toString()
            MyApplication.auth.createUserWithEmailAndPassword(email, passward)
                .addOnCompleteListener(this) { task ->
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    if(task.isSuccessful){
                        MyApplication.auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { sendTask ->
                            if(sendTask.isSuccessful){
                                Toast.makeText(baseContext, "성공",Toast.LENGTH_SHORT).show()
                                changeVisibility("layout")
                            }
                            else{
                                Toast.makeText(baseContext, "실패c",Toast.LENGTH_SHORT).show()
                                changeVisibility("layout")
                            }
                        }
                    }
                    else{
                        Toast.makeText(baseContext, "실패d",Toast.LENGTH_SHORT).show()
                        changeVisibility("layout")
                    }
                }

        }

        binding.loginBtn.setOnClickListener {
            //이메일, 비밀번호 로그인.......................
            val email:String = binding.authEmailEditView.text.toString()
            val passward:String = binding.authPasswordEditView.text.toString()
            MyApplication.auth.createUserWithEmailAndPassword(email, passward)
                .addOnCompleteListener(this) { task ->
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    if(task.isSuccessful){

                            if(MyApplication.checkAuth()){
                                MyApplication.email = email
                                changeVisibility("login")
                            }
                            else{
                                Toast.makeText(baseContext, "실패a",Toast.LENGTH_SHORT).show()
                            }

                    }
                    else{
                        Toast.makeText(baseContext, "실패b",Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //구글 로그인 결과 처리...........................
        if(resultCode == 10){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                MyApplication.auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            MyApplication.email = account.email
                            changeVisibility("login")
                        } else {
                            changeVisibility("logout")
                        }
                    }
            } catch (e:ApiException){
                changeVisibility("logout")

            }            }
    }

    fun changeVisibility(mode: String){
        if(mode === "login"){
            binding.run {
                authMainTextView.text = "${MyApplication.email} 님 반갑습니다."
                logoutBtn.visibility= View.VISIBLE
                goSignInBtn.visibility= View.GONE
                googleLoginBtn.visibility= View.GONE
                authEmailEditView.visibility= View.GONE
                authPasswordEditView.visibility= View.GONE
                signBtn.visibility= View.GONE
                loginBtn.visibility= View.GONE
            }

        }else if(mode === "logout"){
            binding.run {
                authMainTextView.text = "로그인 하거나 회원가입 해주세요."
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.VISIBLE
                googleLoginBtn.visibility = View.VISIBLE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                signBtn.visibility = View.GONE
                loginBtn.visibility = View.VISIBLE
            }
        }else if(mode === "signin"){
            binding.run {
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.GONE
                googleLoginBtn.visibility = View.GONE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                signBtn.visibility = View.VISIBLE
                loginBtn.visibility = View.GONE
            }
        }
    }
}