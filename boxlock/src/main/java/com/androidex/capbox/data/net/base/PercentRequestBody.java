package com.androidex.capbox.data.net.base;


import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * @author liyp
 * @version 1.0.0
 * @description 进度请求体
 * @createTime 2015/11/13
 * @editTime
 * @editor
 */
public class PercentRequestBody extends RequestBody {
    protected RequestBody delegate;
    protected RercentCallBack listener;

    protected CountingSink countingSink;

    public PercentRequestBody(RequestBody delegate, RercentCallBack listener) {
        this.delegate = delegate;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        BufferedSink bufferedSink;

        countingSink = new CountingSink(sink);
        bufferedSink = Okio.buffer(countingSink);

        delegate.writeTo(bufferedSink);

        bufferedSink.flush();
    }

    protected final class CountingSink extends ForwardingSink {

        private long bytesWritten = 0;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);

            bytesWritten += byteCount;
            listener.onChange(bytesWritten, contentLength());
        }

    }

    public interface RercentCallBack {
        void onChange(long bytesWritten, long totalSize);
    }
}

