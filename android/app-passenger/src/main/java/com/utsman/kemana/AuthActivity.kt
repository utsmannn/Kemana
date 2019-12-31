package com.utsman.kemana

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.utsman.easygooglelogin.EasyGoogleLogin
import com.utsman.easygooglelogin.LoginResultListener
import com.utsman.feature.base.*
import com.utsman.feature.remote.instance.CheckInstance
import com.utsman.feature.remote.instance.RequestServiceInstance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.dialog_auth.view.*
import java.lang.Exception

class AuthActivity : RxAppCompatActivity(), LoginResultListener {

    private val checkInstance by lazy {
        CheckInstance.create()
    }

    private val requestServiceInstance by lazy {
        RequestServiceInstance.create()
    }

    private val easyGoogleLogin by lazy {
        EasyGoogleLogin(this)
    }

    private val dialogView by lazy {
        LayoutInflater.from(this).inflate(R.layout.dialog_auth, null)
    }

    private val dialog by lazy {
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        return@lazy builder.create()
    }

    private var onRequest = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val token = getString(R.string.default_web_client_id)
        easyGoogleLogin.initGoogleLogin(token, this)

        setupPermission {
            btn_sign.setOnClickListener {
                easyGoogleLogin.signIn(this)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkServiceBackend(email: String?) {
        dialog.show()
        val observable = checkInstance.checkService(email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                if (it.data.contains(email ?: "check ok")) {
                    onRequest = false
                    dialog.dismiss()
                    intentTo(MainActivity::class.java)
                    finish()
                }
            }
            .doOnError {
                dialogView.text_dialog.text = "Initialize..."
                requestService(email)
            }
            .subscribe({
                logi(it.data)
            }, {
                it.printStackTrace()
            })

        composite.add(observable)
    }

    private fun requestService(mail: String?) {
        if (!onRequest) {
            onRequest = true
            val observable = requestServiceInstance.requestToServer(mail)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logi(it.status)
                    checkServiceBackend(mail)
                }, {
                    it.printStackTrace()
                })
            composite.add(observable)
        } else {
            Handler().postDelayed({
                checkServiceBackend(mail)
            }, 2000)
        }
    }

    private fun setupPermission(ready: () -> Unit) {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    ready.invoke()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    loge("permission denied")
                }

            })
            .check()
    }

    override fun onLoginSuccess(user: FirebaseUser?) {
        val email = user?.email
        Preferences_saveEmail(email)
        checkServiceBackend(email)
    }

    override fun onLogoutSuccess(task: Task<Void>?) {
        logi("logout success")
    }

    override fun onLoginFailed(exception: Exception?) {
        loge("login failed")
        exception?.printStackTrace()
    }

    override fun onLogoutError(exception: Exception?) {
        loge("logout failed")
        exception?.printStackTrace()
    }

    override fun onStart() {
        super.onStart()
        easyGoogleLogin.initOnStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyGoogleLogin.onActivityResult(this, requestCode, data)
    }

}