package com.androidex.capbox.data.net.base;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author liyp
 * @editTime 2017/9/22
 */
public class RequestParams {
    private HashMap<String, Object> map = new LinkedHashMap<String, Object>();
    private ConcurrentMap<String, List<FileWrapper>> fileWrapperListMap = new ConcurrentHashMap<String, List<FileWrapper>>();
    private ConcurrentMap<String, List<StreamWrapper>> streamWrapperListMap = new ConcurrentHashMap<String, List<StreamWrapper>>();

    public static final String TYPE_PNG = ".png";
    public static final String TYPE_JPEG = ".jpeg";
    public static final String TYPE_JPG = ".jpg";
    public static final String TYPE_DOC = ".doc";
    public static final String TYPE_DOCX = ".docx";
    public static final String TYPE_TXT = ".txt";
    public static final String TYPE_EXCEL = ".xls";
    public static final String TYPE_PPT = ".ppt";
    public static final String TYPE_PPTX = ".pptx";
    public static final String TYPE_PDF = ".pdf";


    private RequestParams() {
        map.clear();
        fileWrapperListMap.clear();
        streamWrapperListMap.clear();
    }

    public static RequestParams newInstance() {
        return new RequestParams();
    }

    public RequestParams put(String key, Object value) {
        if (value instanceof File) {
            put(key, (File) value, MEDIA.JPEG);
        } else if (value instanceof InputStream) {
            put(key, (InputStream) value, MEDIA.JPEG);
        } else {
            map.put(key, value);
        }
        return this;
    }

    public RequestParams put(boolean flag, String key, Object value) {
        if (flag) {
            put(key, value);
        }
        return this;
    }

    public RequestParams put(String key, File file, MEDIA media) {
        return put(key, file, generateFileName(media.getType()));
    }

    public RequestParams put(String key, File file, String fileName) {
        if (file != null) {
            List<FileWrapper> fileWrapperList = fileWrapperListMap.get(key);
            if (fileWrapperList == null || fileWrapperList.size() < 1) {
                fileWrapperList = new ArrayList<FileWrapper>();
            }
            fileWrapperList.add(FileWrapper.newInstance(fileName, file));
            fileWrapperListMap.put(key, fileWrapperList);
        }
        return this;
    }

    public RequestParams put(String key, InputStream inputStream, MEDIA media) {
        return put(key, inputStream, generateFileName(media.getType()));
    }

    public RequestParams put(String key, InputStream inputStream, String fileName) {
        if (inputStream != null) {
            List<StreamWrapper> streamWrapperList = streamWrapperListMap.get(key);
            if (streamWrapperList == null || streamWrapperList.size() < 1) {
                streamWrapperList = new ArrayList<StreamWrapper>();
            }
            streamWrapperList.add(StreamWrapper.newInstance(fileName, inputStream));
            streamWrapperListMap.put(key, streamWrapperList);
        }
        return this;
    }


    public RequestParams remove(String key) {
        if (map.containsKey(key)) {
            map.remove(key);
        }
        if (fileWrapperListMap.containsKey(key)) {
            fileWrapperListMap.remove(key);
        }
        if (streamWrapperListMap.containsKey(key)) {
            streamWrapperListMap.remove(key);
        }
        return this;
    }

    public RequestParams clear() {
        map.clear();
        fileWrapperListMap.clear();
        streamWrapperListMap.clear();
        return this;
    }

    /**
     * 参数拼接
     *
     * @return
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String key : map.keySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(key).append("=").append(map.get(key));
        }

        return builder.toString();
    }

    /**
     * url拼接
     *
     * @param url
     * @return
     */
    public String getUrl(String url) {
        StringBuilder builder = new StringBuilder(url);

        if (!url.endsWith("?")) {
            builder.append("?");
        }
        return builder.append(toString()).toString();
    }

    public HashMap<String, Object> getMap() {
        return map;
    }

    public ConcurrentMap<String, List<FileWrapper>> getFileWrapperListMap() {
        return fileWrapperListMap;
    }

    public ConcurrentMap<String, List<StreamWrapper>> getStreamWrapperListMap() {
        return streamWrapperListMap;
    }

    public static class FileWrapper {
        File file;
        String name;

        private FileWrapper(String name, File file) {
            this.name = name;
            this.file = file;
        }

        public static FileWrapper newInstance(String name, File file) {
            return new FileWrapper(name, file);
        }

        public String getFileName() {
            return name;
        }

        public File getFileBody() {
            return file;
        }
    }

    public static class StreamWrapper {
        InputStream inputStream;
        String name;

        private StreamWrapper(String name, InputStream inputStream) {
            this.name = name;
            this.inputStream = inputStream;
        }

        public static StreamWrapper newInstance(String name, InputStream inputStream) {
            return new StreamWrapper(name, inputStream);
        }

        public String getFileName() {
            return name;
        }

        public byte[] getFileBody() {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] data = null;
            byte[] buffer = new byte[1024];
            int len = 0;
            try {
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                data = outputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                        inputStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return data;
        }
    }


    /**
     * 媒体类型
     */
    public enum MEDIA {
        PNG(".png"),
        JPEG(".jpeg"),
        JPG(".jpg"),
        DOC(".doc"),
        DOCX(".docx"),
        TXT(".txt"),
        EXCEL(".xls"),
        PPT(".ppt"),
        PPTX(".pptx"),
        PDF(".pdf");

        private String type;

        MEDIA(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }


    /**
     * 产生文件名
     *
     * @param suffix 文件名后缀
     * @return
     */
    public static String generateFileName(String suffix) {
        String NUMBERS_AND_LETTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        return new StringBuilder("android_")
                .append(Calendar.getInstance().getTimeInMillis())
                .append(getRandom(NUMBERS_AND_LETTERS.toCharArray(), 5))
                .append(suffix)
                .toString();
    }

    public static String getRandom(char[] sourceChar, int length) {
        if (sourceChar == null || sourceChar.length == 0 || length < 0) {
            return null;
        }

        StringBuilder str = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            str.append(sourceChar[random.nextInt(sourceChar.length)]);
        }
        return str.toString();
    }

    /**
     * 参数中是否包含文件
     *
     * @return
     */
    public boolean containFiles() {

        return (!(fileWrapperListMap.isEmpty() && streamWrapperListMap.isEmpty()));
    }

    /**
     * 参数是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return map.isEmpty() && fileWrapperListMap.isEmpty() && streamWrapperListMap.isEmpty();
    }

}
