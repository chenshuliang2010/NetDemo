package com.csl.net

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import com.csl.net.model.*
import com.csl.net.support.serviceRsp
import com.csl.net.support.toLiveData
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

class MainActivity : AppCompatActivity() {
    val netUrl: String = "https://course.api.cniao5.com/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var textView = findViewById<TextView>(R.id.tv_hello)

        //region retrofit请求
        val retrofitCall = KtRetrofit.initConfig(netUrl)
            .getService(CniaoService::class.java)
            .userInfo()
        //ktx的liveData
        val liveInfo = retrofitCall.toLiveData()
        liveInfo.observe(this, Observer {
            LogUtils.d("retrofit liveRsp ${it.toString()}")
        })

        val loginCall = KtRetrofit.initConfig(netUrl, OkHttpApi.getInstance().getClient())
            .getService(CniaoService::class.java)
            .login(LoginReq())
        lifecycleScope.launch {
            //表达式声明,使用when
            when (val serverRsp = loginCall.serviceRsp()) {
                is ApiSuccessResponse -> {
                    LogUtils.d("apiService ${serverRsp.body.toString()}")
                }
                is ApiErrorResponse -> {
                    LogUtils.d("apiService ${serverRsp.errorMessage}")
                }
                is ApiEmptyResponse -> {
                    LogUtils.d("empty apiResponse")
                }
            }
        }

        KtRetrofit.initConfig(netUrl)
            .getService(CniaoService::class.java)
            .userInfo2().observe(this, Observer {
                LogUtils.d("retrofit liveRsp ${it.toString()}")
            })
    }


    data class LoginReq(
        val mobj: String = "18648957777",
        val password: String = "cn5123456"
    )
}

interface CniaoService {
    @POST("accounts/course/10301/login")
    fun login(@Body body: MainActivity.LoginReq): Call<NetResponse>

    @GET("member/userinfo")
    fun userInfo(): Call<NetResponse>

    @GET("member/userinfo")
    fun userInfo2(): LiveData<ApiResponse<NetResponse>>
}


