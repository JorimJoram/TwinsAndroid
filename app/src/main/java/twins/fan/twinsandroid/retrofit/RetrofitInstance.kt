package twins.fan.twinsandroid.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import twins.fan.twinsandroid.retrofit.api.AccountApi
import twins.fan.twinsandroid.retrofit.api.LoginApi
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private val retrofitInit by lazy{
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)

        clientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

        Retrofit.Builder()
            .baseUrl("http://49.173.81.98:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()
    }

    val loginApi: LoginApi by lazy { retrofitInit.create(LoginApi::class.java) }
    val accountApi: AccountApi by lazy { retrofitInit.create(AccountApi::class.java) }
}