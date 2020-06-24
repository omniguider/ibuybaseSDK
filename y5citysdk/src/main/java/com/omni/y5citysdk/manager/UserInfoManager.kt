package com.omni.y5citysdk.manager

import android.app.Activity
import android.support.v4.app.FragmentActivity
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.omni.y5citysdk.R
import com.omni.y5citysdk.module.OmniEvent
import com.omni.y5citysdk.module.login.CheckUserLoginData
import com.omni.y5citysdk.module.login.LoginType
import com.omni.y5citysdk.module.login.LogoutResponseData
import com.omni.y5citysdk.module.login.UserLoginInfo
import com.omni.y5citysdk.network.NetworkManager
import com.omni.y5citysdk.network.Y5CityAPI
import com.omni.y5citysdk.tool.DialogTools
import com.omni.y5citysdk.tool.PreferencesTools
import com.omni.y5citysdk.tool.Y5CityText.LOG_TAG
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import java.io.UnsupportedEncodingException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class UserInfoManager {

    private val IvAES = "ogamin://nlpiapp"
    private val KeyAES = "nlpiapp://ogaminnlpiapp://ogamin"

    interface UserInfoListener {
        fun onSavedUserInfo()
    }

    companion object {

        const val KEY_USER_LOGIN_INFO = "key_preferences_user_login_info"
        const val RC_SIGN_IN: Int = 20

        private val manager = UserInfoManager()

        fun getInstance(): UserInfoManager {
            return manager
        }
    }

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mGSO: GoogleSignInOptions? = null
    private var shouldGoogleLogout: Boolean = false
    private var mFirebaseAuth: FirebaseAuth? = null

    fun isLoggedIn(activity: Activity): Boolean {
        val userLoginInfo = getUserInfo(activity)

        return userLoginInfo != null && !TextUtils.isEmpty(userLoginInfo!!.serverLoginToken)
    }

    fun getUserInfo(activity: Activity): UserLoginInfo? {
        return PreferencesTools.getInstance().getProperty(activity, KEY_USER_LOGIN_INFO, UserLoginInfo::class.java)
    }

    fun getUserLoginToken(activity: Activity): String? {
        val loginInfo = getUserInfo(activity)
        if (loginInfo == null) {
//            DialogTools.getInstance().showErrorMessage(activity,
//                    R.string.dialog_title_text_note, R.string.dialog_message_no_login)
            return null
        } else {
            val loginToken = loginInfo.serverLoginToken
            if (TextUtils.isEmpty(loginToken)) {
//                DialogTools.getInstance().showErrorMessage(activity, R.string.dialog_title_text_note, R.string.dialog_message_no_login)
                return null
            }
            return loginToken
        }
    }

    fun addFirebaseToken(activity: Activity, firebaseTokenId: String) {
        val loginInfo = getUserInfo(activity)
        var loginToken = ""
        if (loginInfo?.serverLoginToken != null) {
            loginToken = loginInfo.serverLoginToken!!
        }

        Y5CityAPI.getInstance().checkLogin(activity, loginToken, firebaseTokenId, object : NetworkManager.NetworkManagerListener<Array<CheckUserLoginData>> {
            override fun onSucceed(loginData: Array<CheckUserLoginData>) {
                UserInfoManager.getInstance().updateUserLoginToken(activity, loginData[0].getLoginToken())
            }

            override fun onFail(errorMsg: String, shouldRetry: Boolean) {
//                DialogTools.getInstance().showErrorMessage(activity, R.string.error_dialog_title_text_normal, errorMsg) { UserInfoManager.getInstance().userLoggedout(activity) }
            }
        })

    }

    fun userLoggedout(activity: Activity) {
        PreferencesTools.getInstance().removeProperty(activity, KEY_USER_LOGIN_INFO)
    }

    fun updateUserLoginToken(activity: Activity, loginToken: String) {
        val userLoginInfo = getUserInfo(activity) ?: return

        userLoginInfo.serverLoginToken = loginToken

        PreferencesTools.getInstance().saveProperty(activity, KEY_USER_LOGIN_INFO, userLoginInfo)
    }

    fun getUserLoginTokenLoginHint(activity: Activity): String? {
        val loginInfo = getUserInfo(activity)
        if (loginInfo == null) {
            DialogTools.getInstance().showErrorMessage(activity,
                    R.string.dialog_title_text_note, R.string.dialog_message_no_login)
            return null
        } else {
            val loginToken = loginInfo.serverLoginToken
            if (TextUtils.isEmpty(loginToken)) {
                DialogTools.getInstance().showErrorMessage(activity, R.string.dialog_title_text_note, R.string.dialog_message_no_login)
                return null
            }
            return loginToken
        }
    }

    fun saveUserData(activity: FragmentActivity, checkUserLoginData: CheckUserLoginData) {
        DialogTools.getInstance().showProgress(activity)

        val accessToken = checkUserLoginData.loginToken
        val email = checkUserLoginData.email
        val id = checkUserLoginData.getuId()
        val name = checkUserLoginData.userName

        try {
            var userLoginInfo = getUserInfo(activity)
            if (userLoginInfo == null) {
                userLoginInfo = UserLoginInfo(id = id, userName = name, email = email,
                        token = accessToken, loginType = LoginType.SDK)
            } else {
                userLoginInfo.id = id
                userLoginInfo.userName = name
                userLoginInfo.email = email
                userLoginInfo.serverLoginToken = accessToken
                userLoginInfo.loginType = LoginType.SDK
            }

            PreferencesTools.getInstance().saveProperty(activity, KEY_USER_LOGIN_INFO, userLoginInfo)

        } catch (e: JSONException) {
            DialogTools.getInstance().showErrorMessage(activity, R.string.error_dialog_title_text_normal, R.string.error_dialog_title_text_json_parse_error)
        }
    }

    fun logout(activity: FragmentActivity) {
        val userLoginInfo = getUserInfo(activity)

        if (userLoginInfo != null) {

            val loginToken = userLoginInfo.serverLoginToken

//            when (userLoginInfo.loginType) {
//                LoginType.FACEBOOK -> {
//                    LoginManager.getInstance().logOut()
//                    logoutFromSever(activity, loginToken!!)
//                }
//
//                LoginType.GOOGLE -> {
//                    shouldGoogleLogout = true
//
//                    mGoogleSignInClient ?: initGoogleSignInClient(activity)
//                    mGoogleSignInClient!!.signOut().addOnCompleteListener(activity) {
//                        val userLoginInfo = getUserInfo(activity)
//
//                        if (shouldGoogleLogout &&
//                                userLoginInfo != null &&
//                                !TextUtils.isEmpty(userLoginInfo.serverLoginToken)) {
//                            shouldGoogleLogout = false
//                            logoutFromSever(activity, userLoginInfo.serverLoginToken!!)
//                        }
//                    }
//                }
//            }

        }
    }

//    fun loginWithGoogle(activity: FragmentActivity) {
//        initGoogleSignInClient(activity)
//
//        val signInIntent = mGoogleSignInClient!!.signInIntent
//        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
//    }

//    fun firebaseAuthWithGoogle(activity: Activity, account: GoogleSignInAccount, listener: UserInfoListener) {
//        DialogTools.getInstance().showProgress(activity)
//
//        val authCredential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
//        mFirebaseAuth = mFirebaseAuth ?: FirebaseAuth.getInstance()
//        mFirebaseAuth!!.signInWithCredential(authCredential)
//                ?.addOnCompleteListener(activity!!) {
//                    if (it.isSuccessful) {
//                        val user: FirebaseUser? = mFirebaseAuth?.currentUser
//
//                        UserInfoManager.getInstance().saveGoogleUserData(activity!!, account, listener)
//                    } else {
//                        DialogTools.getInstance().showErrorMessage(activity, activity.getString(R.string.dialog_title_text_note), "Firebase Auth Failed")
//                    }
//                }
//    }

    private fun initGoogleSignInClient(activity: FragmentActivity) {
        mGSO = mGSO ?: GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken("637949011005-8l6de6o7jdp1202a9e4n6k1vubujetjp.apps.googleusercontent.com")
                /** must use "Web client (Auto-created for Google Sign-in)  client id!!!" */
                .build()

        mGoogleSignInClient = mGoogleSignInClient ?: GoogleSignIn.getClient(activity, mGSO!!)
    }

//    public fun saveGoogleUserData(activity: Activity, account: GoogleSignInAccount, listener: UserInfoListener) {
//
//        val photoUrl = if (account.photoUrl == null) "" else account.photoUrl!!.toString()
//
//        var userLoginInfo = getUserInfo(activity)
//        if (userLoginInfo == null) {
//            userLoginInfo = UserLoginInfo(id = account.id,
//                    userName = account.displayName,
//                    email = account.email,
//                    photoUrl = photoUrl,
//                    token = account.idToken,
//                    loginType = LoginType.GOOGLE)
//
//        } else {
//            userLoginInfo.id = account.id
//            userLoginInfo.userName = account.displayName
//            userLoginInfo.email = account.email
//            userLoginInfo.photoUrl = photoUrl
//            userLoginInfo.token = account.idToken
//            userLoginInfo.loginType = LoginType.GOOGLE
//        }
//
//        PreferencesTools.getInstance().saveProperty(activity, KEY_USER_LOGIN_INFO, userLoginInfo)
//
//        Y5CityAPI.getInstance().loginWithGoogle(activity,
//                getServerLoginParams(account.id!!, account.displayName!!, account.email!!),
//                object : NetworkManager.NetworkManagerListener<GoogleLoginVerify> {
//                    override fun onSucceed(googleLoginVerify: GoogleLoginVerify?) {
//                        val loginInfo = PreferencesTools.getInstance().getProperty(activity, KEY_USER_LOGIN_INFO, UserLoginInfo::class.java)
//
//                        if (loginInfo != null) {
//                            loginInfo!!.serverLoginToken = googleLoginVerify!!.loginToken
//                            PreferencesTools.getInstance().saveProperty(activity, KEY_USER_LOGIN_INFO, loginInfo)
//
//                            listener.onSavedUserInfo()
//                            EventBus.getDefault().post(OmniEvent(OmniEvent.TYPE_LOGIN_STATUS_CHANGED, ""))
//                        }
//                    }
//
//                    override fun onFail(errorMsg: String?, shouldRetry: Boolean) {
//                        DialogTools.getInstance().showErrorMessage(activity, activity.getString(R.string.dialog_title_text_note), errorMsg)
//                        PreferencesTools.getInstance().removeProperty(activity, KEY_USER_LOGIN_INFO)
//                    }
//                })
//    }

    private fun getServerLoginParams(id: String, name: String, email: String): String {
        val map: MutableMap<String, String> = mutableMapOf()
        map["id"] = id
        map["name"] = name
        map["email"] = email

        return "[${NetworkManager.getInstance().getGson().toJson(map)}]"
    }

    private fun logoutFromSever(activity: Activity, loginToken: String) {
        if (TextUtils.isEmpty(loginToken)) {
            PreferencesTools.getInstance().removeProperty(activity, KEY_USER_LOGIN_INFO)

            EventBus.getDefault().post(OmniEvent(OmniEvent.TYPE_LOGIN_STATUS_CHANGED, ""))

            DialogTools.getInstance().dismissProgress(activity)
        } else {
            Y5CityAPI.getInstance().logout(activity, loginToken, object : NetworkManager.NetworkManagerListener<LogoutResponseData> {

                override fun onSucceed(response: LogoutResponseData?) {
                    PreferencesTools.getInstance().removeProperty(activity, KEY_USER_LOGIN_INFO)

                    EventBus.getDefault().post(OmniEvent(OmniEvent.TYPE_LOGIN_STATUS_CHANGED, ""))

                    DialogTools.getInstance().dismissProgress(activity)
//                    DialogTools.getInstance().showErrorMessage(activity, "", activity.getString(R.string.dialog_message_log_out_complete))
                }

                override fun onFail(errorMsg: String?, shouldRetry: Boolean) {
                    PreferencesTools.getInstance().removeProperty(activity, KEY_USER_LOGIN_INFO)

                    EventBus.getDefault().post(OmniEvent(OmniEvent.TYPE_LOGIN_STATUS_CHANGED, ""))

                    DialogTools.getInstance().dismissProgress(activity)
//                    DialogTools.getInstance().showErrorMessage(activity, "", activity.getString(R.string.dialog_message_log_out_complete))
                }
            })
        }
    }

//    fun loginWithFB(fragment: Fragment) {
//        if (AccessToken.getCurrentAccessToken() == null) {
//            LoginManager.getInstance().logInWithReadPermissions(fragment, Arrays.asList("public_profile", "email"))
//        }
//    }
//
//    fun saveFBUserData(activity: FragmentActivity, loginResult: LoginResult, listener: UserInfoListener) {
//        DialogTools.getInstance().showProgress(activity)
//
//        val accessToken = loginResult.accessToken
//
//        val request = GraphRequest.newMeRequest(
//                accessToken
//        ) { `object`, response ->
//
//            try {
//                val email = `object`.getString("email")
//                val id = `object`.getString("id")
//                val name = `object`.getString("name")
//
////                Log.e(LOG_TAG, "account.getEmail()$email")
//
//                var userLoginInfo = getUserInfo(activity)
//                if (userLoginInfo == null) {
//                    userLoginInfo = UserLoginInfo(id = id, userName = name, email = email,
//                            photoUrl = "https://graph.facebook.com/" + accessToken.userId + "/picture?type=large",
//                            token = accessToken.token, loginType = LoginType.FACEBOOK)
//                } else {
//                    userLoginInfo.id = id
//                    userLoginInfo.userName = name
//                    userLoginInfo.email = email
//                    userLoginInfo.photoUrl = "https://graph.facebook.com/" + accessToken.userId + "/picture?type=large"
//                    userLoginInfo.token = accessToken.token
//                    userLoginInfo.loginType = LoginType.FACEBOOK
//                }
//
//                PreferencesTools.getInstance().saveProperty(activity, KEY_USER_LOGIN_INFO, userLoginInfo)
//
//                loginToServerByFB(activity, getServerLoginParams(userLoginInfo.id!!, userLoginInfo.userName!!, userLoginInfo.email!!), listener)
//
//            } catch (e: JSONException) {
//                LoginManager.getInstance().logOut()
//                DialogTools.getInstance().showErrorMessage(activity, R.string.error_dialog_title_text_normal, R.string.error_dialog_title_text_json_parse_error)
//            }
//        }
//        val parameters = Bundle()
//        parameters.putString("fields", "id,name,email")
//        request.parameters = parameters
//        request.executeAsync()
//    }

//    private fun loginToServerByFB(activity: FragmentActivity, parameter: String,
//                                  listener: UserInfoListener) {
//        Y5CityAPI.getInstance().loginWithFB(activity, parameter,
//                object : NetworkManager.NetworkManagerListener<FBLoginVerify> {
//                    override fun onSucceed(response: FBLoginVerify?) {
//                        val loginInfo = PreferencesTools.getInstance().getProperty(activity, KEY_USER_LOGIN_INFO, UserLoginInfo::class.java)
//
//                        if (loginInfo != null) {
//                            loginInfo.serverLoginToken = response!!.loginToken
//                            PreferencesTools.getInstance().saveProperty(activity, KEY_USER_LOGIN_INFO, loginInfo)
//
//                            listener.onSavedUserInfo()
//                            EventBus.getDefault().post(OmniEvent(OmniEvent.TYPE_LOGIN_STATUS_CHANGED, ""))
//                        }
//                    }
//
//                    override fun onFail(errorMsg: String?, shouldRetry: Boolean) {
//                        logout(activity)
//                        DialogTools.getInstance().showErrorMessage(activity, R.string.error_dialog_title_text_normal, R.string.error_dialog_title_text_unknown)
//                    }
//                })
//    }

    private fun EncryptAES(iv: ByteArray, key: ByteArray, text: ByteArray): ByteArray? {
        try {
            val mAlgorithmParameterSpec = IvParameterSpec(iv)
            val mSecretKeySpec = SecretKeySpec(key, "AES")
            var mCipher: Cipher? = null
            mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            mCipher!!.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, mAlgorithmParameterSpec)

            return mCipher.doFinal(text)
        } catch (ex: Exception) {
            return null
        }

    }

    private fun DecryptAES(iv: ByteArray, key: ByteArray, text: ByteArray): ByteArray? {
        try {
            val mAlgorithmParameterSpec = IvParameterSpec(iv)
            val mSecretKeySpec = SecretKeySpec(key, "AES")
            val mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            mCipher.init(Cipher.DECRYPT_MODE,
                    mSecretKeySpec,
                    mAlgorithmParameterSpec)

            return mCipher.doFinal(text)
        } catch (ex: Exception) {
            return null
        }

    }

    private fun encrypt(encrypt: String?): String {
        var encrypt_String = ""
        try {
            if (encrypt != null) {
                val TextByte = EncryptAES(IvAES.toByteArray(charset("UTF-8")), KeyAES.toByteArray(charset("UTF-8")), encrypt.toByteArray(charset("UTF-8")))
                encrypt_String = Base64.encodeToString(TextByte, Base64.DEFAULT)
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return encrypt_String
    }

    private fun decrypt(decrypt: String?): String {
        var decrypt_String = ""
        try {
            if (decrypt != null) {
                val TextByte = DecryptAES(IvAES.toByteArray(charset("UTF-8")), KeyAES.toByteArray(charset("UTF-8")),
                        Base64.decode(decrypt.toByteArray(charset("UTF-8")), Base64.DEFAULT))
                if (TextByte != null) {
                    decrypt_String = String(TextByte!!, charset("UTF-8"))
                }
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return decrypt_String
    }
}