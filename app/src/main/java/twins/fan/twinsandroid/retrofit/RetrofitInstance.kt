package twins.fan.twinsandroid.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import twins.fan.twinsandroid.BuildConfig
import twins.fan.twinsandroid.retrofit.api.AccountApi
import twins.fan.twinsandroid.retrofit.api.GalleryApi
import twins.fan.twinsandroid.retrofit.api.GameApi
import twins.fan.twinsandroid.retrofit.api.LoginApi
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private val retrofitInit by lazy{
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)

        Retrofit.Builder()
            .baseUrl(BuildConfig.myUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()
    }

    val loginApi: LoginApi by lazy { retrofitInit.create(LoginApi::class.java) }
    val accountApi: AccountApi by lazy { retrofitInit.create(AccountApi::class.java) }
    val gallApi:GalleryApi by lazy { retrofitInit.create(GalleryApi::class.java) }
    val gameApi:GameApi by lazy { retrofitInit.create(GameApi::class.java) }
}