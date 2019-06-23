package hit.TechNews.Utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.internal.$Gson$Types;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    private static HttpUtil mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;

    private HttpUtil(){
        File httpCacheDirectory = new File( "httpcache");

        //缓存文件最大限制大小20M
        long cacheSize = 1024 * 1024 * 20;
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        mOkHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(10,TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .cache(cache)
                .cookieJar(new CookieJar() {
                    private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
                        cookieStore.put(url, cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url);
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();
        mDelivery = new Handler(Looper.getMainLooper());
    }

    private synchronized static HttpUtil getmInstance() {
        if (mInstance == null) {
            mInstance = new HttpUtil();
        }
        return mInstance;
    }
    private void getRequest(String url, final ResultCallback<String> callback) {
        Request.Builder builder = new Request.Builder().url(url);
        builder.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
        Request request = builder.build();
        deliveryResult(callback, request);
    }
    private void postRequest(String url, final ResultCallback<String> callback, List<Param> params) {
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request);
    }
    private Request buildPostRequest(String url, List<Param> params) {
        FormBody.Builder builder= new FormBody.Builder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }
    private void deliveryResult(final ResultCallback<String> callback, Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("HTTPPOST",e.getMessage());

                sendFailCallback(callback, e);

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    assert response.body() != null;
                    String str = response.body().string();

                    if (callback.mType == String.class) {
                        sendSuccessCallBack(callback, str);
                    } else {
                        Object object = JsonUtil.deserialize(str, callback.mType);
                        sendSuccessCallBack(callback, object);
                    }
                } catch (final Exception e) {
                    //LogUtils.e(TAG, "convert json failure", e);
                    sendFailCallback(callback, e);
                }
            }
        });
    }
    private void sendFailCallback(final ResultCallback<String> callback, final Exception e) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onFailure(e);
                }
            }
        });
    }

    /**
     * get请求
     *
     * @param url      请求url
     * @param callback 请求回调
     */
    public static void get(String url, ResultCallback<String> callback) {
        getmInstance().getRequest(url, callback);
    }

    /**
     * post请求
     * @param url      请求url
     * @param callback 请求回调
     * @param params   请求参数
     */
    public static void post(String url, final ResultCallback<String> callback, List<Param> params) {
        getmInstance().postRequest(url, callback, params);
    }
    private void sendSuccessCallBack(final ResultCallback<String> callback, final Object obj) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onSuccess((String) obj);
                }
            }
        });
    }
    public static abstract class ResultCallback<T> {

        Type mType;

        public ResultCallback() {
            mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            assert parameterized != null;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }
//         请求成功回调

        public abstract void onSuccess(T response);

//         请求失败回调

        public abstract void onFailure(Exception e);
    }
    public static class Param {

        String key;
        String value;

        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

    }

}
