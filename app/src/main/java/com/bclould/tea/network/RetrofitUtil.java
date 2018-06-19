package com.bclould.tea.network;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.NetworkUtils;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dada on 2017/6/14.
 */


@RequiresApi(api = Build.VERSION_CODES.N)
public class RetrofitUtil {

    private static final long DEFAULT_DIR_CACHE = 2000;
    private Context mContext;

    GsonConverterFactory factory = GsonConverterFactory.create(new GsonBuilder().create());
    private static RetrofitUtil instance = null;
    private Retrofit mRetrofit = null;

    public static RetrofitUtil getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitUtil(context);
        }
        return instance;
    }

    public class CacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();//获取请求
            //这里就是说判读我们的网络条件，要是有网络的话我么就直接获取网络上面的数据，要是没有网络的话我么就去缓存里面取数据
            if (!NetworkUtils.isNetworkAvailable(mContext)) {
                request = request.newBuilder()
                        //这个的话内容有点多啊，大家记住这么写就是只从缓存取，想要了解这个东西我等下在
                        // 给大家写连接吧。大家可以去看下，获取大家去找拦截器资料的时候就可以看到这个方面的东西反正也就是缓存策略。
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
                Log.d("CacheInterceptor", "no network");
            }
            Response originalResponse = chain.proceed(request);
            if (NetworkUtils.isNetworkAvailable(mContext)) {
                //这里大家看点开源码看看.header .removeHeader做了什么操作很简答，就是的加字段和减字段的。
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        //这里设置的为0就是说不进行缓存，我们也可以设置缓存时间
                        .header("Cache-Control", "public, max-age=" + 10)
                        .removeHeader("Pragma")
                        .build();
            } else {
                int maxTime = 4 * 24 * 60 * 60;
                return originalResponse.newBuilder()
                        //这里的设置的是我们的没有网络的缓存时间，想设置多少就是多少。
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxTime)
                        .removeHeader("Pragma")
                        .build();
            }
        }

    }

    private RetrofitUtil(Context Context) {
        mContext = Context;//设置缓存路径
        File cacheFile = new File(mContext.getCacheDir(), "caheData");
        //设置缓存大小
        Cache cache = new Cache(cacheFile, DEFAULT_DIR_CACHE);
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)//连接失败后是否重新连接
//                .cookieJar(cookieJar)
                .connectTimeout(20, TimeUnit.SECONDS)//超时时间20S
                .addInterceptor(new CacheInterceptor())//也就这里不同
                .addNetworkInterceptor(new CacheInterceptor())//也就这里不同
                .cache(cache)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(factory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public MyService getServer() {
        return mRetrofit.create(MyService.class);
    }
}
