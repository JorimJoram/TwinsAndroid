package twins.fan.twinsandroid.retrofit

import okhttp3.Interceptor
import okhttp3.Response

class CsrfTokenInterceptor(
    private val csrfToken:String
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .header("X-CSRF-TOKEN", csrfToken)
            .build()
        return chain.proceed(newRequest)
    }
}