package com.utsman.kemana.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.utsman.kemana.auth.AuthApp
import com.utsman.kemana.auth.AuthListener
import com.utsman.kemana.auth.R
import com.utsman.kemana.auth.UserLogin
import com.utsman.kemana.base.Key
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment(private val authListener: AuthListener) : Fragment() {

    private lateinit var authApp: AuthApp

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authApp = AuthApp(Key.APP_ID, Key.REST_KEY)

        btn_login.setOnClickListener {
            authListener.onLoaded()
            val email = input_email.text.toString()
            val password = input_password.text.toString()

            val userLogin = UserLogin(email, password)
            authApp.login(userLogin, authListener, password)
        }

        btn_to_register.setOnClickListener {
            authListener.toRegister()
        }
    }

}