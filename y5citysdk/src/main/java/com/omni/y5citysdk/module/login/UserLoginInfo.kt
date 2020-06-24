package com.omni.y5citysdk.module.login

import java.io.Serializable

data class UserLoginInfo(var id: String? = null,
                         var token: String? = null,
                         var userName: String? = null,
                         var photoUrl: String? = null,
                         var gender: String? = null,
                         var email: String? = null,
                         var birthday: String? = null,
                         var loginType: LoginType? = null,
                         var serverLoginToken: String? = null,
                         var account: String? = null,
                         var phone: String? = null,
                         var address: String? = null,
                         var shouldFirebasePush: Boolean = true,
                         var firebaseToken: String? = null) : Serializable