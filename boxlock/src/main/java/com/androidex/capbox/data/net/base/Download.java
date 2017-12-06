package com.androidex.capbox.data.net.base;

import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * @author liyp
 * @version 1.0.0
 * @description
 * @createTime 2015/11/13
 * @editTime
 * @editor
 */
public class Download extends Get {
    private String destFileDir;
    private String destFileName;


    protected Download(String url, String tag, Headers headers, RequestParams params, String destFileDir, String destFileName) {
        super(url, tag, headers, params);
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }


    @Override
    protected OkRequest invoke(final ResultCallBack callBack) {
        prepareInvoke(callBack);

        //logInfo();

        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                requestClient.sendFailMessage(RequestClient.STATUS_CODE_NONE, request, e, callBack);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    saveFile(response, callBack);
                } catch (IOException e) {
                    requestClient.sendFailMessage(RequestClient.STATUS_CODE_NONE, request, e, callBack);
                }
            }
        });

        return this;
    }


    public void saveFile(Response response, final ResultCallBack callback) throws IOException {
        InputStream inputStream = null;
        FileOutputStream fos = null;
        try {
            inputStream = response.body().byteStream();

            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            if (TextUtils.isEmpty(destFileName)) {
                destFileName = getFileName(url);
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[2048];
            int len = 0;
            long writenLen = 0;
            long totalLen = response.body().contentLength();

            while ((len = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                writenLen += len;
                requestClient.sendProgressMessage(writenLen, totalLen, callback);
            }
            fos.flush();

            requestClient.sendSuccessMessage(response.code(), response.headers(), file.getAbsolutePath(), callback);

        } catch (IOException e) {
            requestClient.sendFailMessage(RequestClient.STATUS_CODE_NONE, request, e, callback);
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                L.e(e);
            }
        }

    }

    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

}
