package com.androidex.capbox.data.net.base;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.androidex.capbox.data.pojo.FileUploadItem;
import com.androidex.capbox.module.FileUploadModel;
import com.androidex.capbox.utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.net.ConnectException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * @author liyp
 * @version 1.0.0
 * @description 请求客户端
 * @createTime 2015/11/13
 * @editTime
 * @editor
 */
public class RequestClient {
    private static RequestClient instance;
    private OkHttpClient okHttpClient;
    private Handler delivery;

    public static final int STATUS_CODE_NONE = -1;        //无返回状态码
    public static boolean MODE_DEBUG = true;      //调试状态

    private RequestClient() {
        okHttpClient = new OkHttpClient();
        okHttpClient.connectTimeoutMillis();
        okHttpClient.retryOnConnectionFailure();
        okHttpClient.readTimeoutMillis();
        okHttpClient.writeTimeoutMillis();
        //CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(ctx));
        okHttpClient.cookieJar();
        delivery = new Handler(Looper.getMainLooper());
    }

    public static RequestClient getInstance() {
        if (instance == null) {
            synchronized (RequestClient.class) {
                if (instance == null) {
                    L.e("new RequestClient");
                    instance = new RequestClient();
                }
            }
        } else
            L.e("old RequestClient");
        return instance;
    }


    public <T> void invoke(final Request request, ResultCallBack<T> callBack) {
        if (callBack == null) {
            callBack = new ResultCallBack<T>() {
            };
        }
        sendStartMessage(callBack);

        final ResultCallBack<T> resultCallBack = callBack;

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailMessage(STATUS_CODE_NONE, request, e, resultCallBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode >= 400 && statusCode <= 599) {
                    try {
                        sendFailMessage(statusCode, request, new RuntimeException(response.body().string()), resultCallBack);
                    } catch (IOException e) {
                        e.printStackTrace();
                        sendFailMessage(statusCode, request, new RuntimeException(""), resultCallBack);
                    } finally {
                        return;
                    }
                }
                String result = response.body().string();

                if (response != null && !TextUtils.isEmpty(result)) {

                    try {
                        ParameterizedType pt = (ParameterizedType) resultCallBack
                                .getClass().getGenericSuperclass();
                        if (pt.getActualTypeArguments()[0] instanceof Class) {

                            Class<?> clazz = (Class<?>) pt.getActualTypeArguments()[0];
                            if ("java.lang.String".equals(clazz.getName())) {
                                sendSuccessMessage(statusCode, response.headers(), (T) result, resultCallBack);

                            } else {
                                T model = (T) JSON.parseObject(result, clazz);
                                sendSuccessMessage(statusCode, response.headers(), model, resultCallBack);
                            }
                        } else if (pt.getActualTypeArguments()[0] instanceof ParameterizedType) {
                            ParameterizedType type = (ParameterizedType) pt.getActualTypeArguments()[0];
                            Class<?> clazz = (Class<?>) type.getActualTypeArguments()[0];

                            T model = (T) JSON.parseArray(result, clazz);
                            sendSuccessMessage(statusCode, response.headers(), model, resultCallBack);
                        }

                    } catch (Exception ex) {
                        sendFailMessage(statusCode, request, ex, resultCallBack);
                    } finally {
                        Log.i("RequestClient", "result:" + result);
                    }

                }
            }
        });

    }

    /**
     * 取消请求
     *
     * @param tag
     */
    public void cancel(Object tag) {

        if (tag == null) {
            return;
        }

        synchronized (okHttpClient.dispatcher().getClass()) {
            for (Call call : okHttpClient.dispatcher().queuedCalls()) {
                if (tag.equals(call.request().tag())) call.cancel();
            }

            for (Call call : okHttpClient.dispatcher().runningCalls()) {
                if (tag.equals(call.request().tag())) call.cancel();
            }
        }

    }


    public <T> void sendFailMessage(final int statusCode, final Request request, final Exception e, final ResultCallBack<T> callBack) {
        delivery.post(new Runnable() {
            @Override
            public void run() {
                if (e != null) {
                    Log.e("RequestClient", e.toString());
                    if (e instanceof ConnectException) {
                        callBack.onFailure(statusCode, request, new RuntimeException("网络无法连接"));
                    } else if (e instanceof JSONException) {
                        callBack.onFailure(statusCode, request, new RuntimeException("数据出现问题"));
                    } else {
                        callBack.onFailure(statusCode, request, new RuntimeException("出现了点小麻烦"));
                    }
                }
                callBack.onFinish();
            }
        });
    }

    public <T> void sendSuccessMessage(final int statusCode, final Headers headers, final T model, final ResultCallBack<T> callBack) {
        delivery.post(new Runnable() {
            @Override
            public void run() {
                callBack.onSuccess(statusCode, headers, model);
                callBack.onFinish();
            }
        });
    }

    public <T> void sendStartMessage(final ResultCallBack<T> callBack) {
        delivery.post(new Runnable() {
            @Override
            public void run() {
                callBack.onStart();
            }
        });
    }

    public <T> void sendProgressMessage(final long bytesWritten, final long totalSize, final ResultCallBack<T> callBack) {
        delivery.post(new Runnable() {
            @Override
            public void run() {
                callBack.onProgress(bytesWritten, totalSize);
            }
        });
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public TrustManager[] prepareTrustManager(InputStream... certificates) {
        if (certificates == null || certificates.length <= 0) return null;

        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);

            int index = 0;
            for (InputStream cerficate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(cerficate));

                try {
                    if (cerficate != null) {
                        cerficate.close();
                    }
                } catch (IOException e) {
                }
            }

            TrustManagerFactory trustManagerFactory = null;
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            return trustManagerFactory.getTrustManagers();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) return null;

            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCertificates(InputStream[] certificates, InputStream bksFile, String password) {
        try {
            TrustManager[] trustManagers = prepareTrustManager(certificates);
            KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
            SSLContext sslContext = SSLContext.getInstance("TLS");

            sslContext.init(keyManagers, new TrustManager[]{new MyTrustManager(chooseTrustManager(trustManagers))}, new SecureRandom());
            okHttpClient.sslSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    private X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }


    private class MyTrustManager implements X509TrustManager {
        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        public MyTrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
            TrustManagerFactory var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            var4.init((KeyStore) null);
            defaultTrustManager = chooseTrustManager(var4.getTrustManagers());
            this.localTrustManager = localTrustManager;
        }


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException ce) {
                localTrustManager.checkServerTrusted(chain, authType);
            }
        }


        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * 设置通用头信息
     *
     * @param headers
     */
    public void setCommonHeaders(final Headers headers) {

        okHttpClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Headers localHeaders = chain.request().headers();
                Request wrapperReq = null;

                if (headers == null || headers.toMultimap().size() < 1)
                    return chain.proceed(chain.request());

                if (localHeaders == null || localHeaders.size() < 1) {
                    wrapperReq = chain.request().newBuilder().headers(headers).build();
                } else {
                    Headers.Builder builder = localHeaders.newBuilder();
                    for (String key : headers.toMultimap().keySet()) {
                        if (TextUtils.isEmpty(localHeaders.get(key))) {
                            builder.add(key, headers.get(key));
                        }
                    }
                    wrapperReq = chain.request().newBuilder().headers(builder.build()).build();
                }
                return chain.proceed(wrapperReq);
            }
        });
    }


    /**
     * 设置代理服务器
     */
    public void setProxy() {
        okHttpClient.proxy();
    }

    /**
     * 多文件上传
     *
     * @param fileUploadList
     * @param resultModel
     * @param callBack
     */
    public static void uploadFile(
            final List<FileUploadItem> fileUploadList,
            final List<FileUploadModel> resultModel,
            final MultiFileResultCallBack callBack) {

        if (callBack == null) return;

        if (fileUploadList.size() > 0) {

            callBack.onStart();

            final FileUploadItem fileUploadItem = fileUploadList.get(0);

            new OkRequest.Builder().url(fileUploadItem.getTargetUrl()).params(fileUploadItem.getParams()).post(new ResultCallBack<FileUploadModel>() {

                @Override
                public void onSuccess(int statusCode, Headers headers, FileUploadModel model) {
                    super.onSuccess(statusCode, headers, model);

                    if (model != null
                            && model.code == Constants.API.API_CODE_UPLOAD_OK) {

                        callBack.onProgress(1, 1, fileUploadItem); // 提示上传成功

                        // 上传成功
                        model.setTag(fileUploadItem.getTag());
                        resultModel.add(model);

                        fileUploadList.remove(fileUploadItem); // 删除此文件

                        uploadFile(fileUploadList,
                                resultModel, callBack);

                    } else {
                        callBack.onFailure(statusCode, null, fileUploadItem, model);
                    }
                }


                @Override
                public void onFailure(int statusCode, Request request, Exception e) {
                    super.onFailure(statusCode, request, e);
                    callBack.onFailure(statusCode, e, fileUploadItem, null);
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    super.onProgress(bytesWritten, totalSize);
                    callBack.onProgress(bytesWritten, totalSize, fileUploadItem);
                }
            });

        } else {
            callBack.onSuccess(resultModel);
        }
    }
}
