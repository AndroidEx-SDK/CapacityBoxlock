package com.androidex.capbox.base;

/**
 * @Description: 文件对象
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/4/27
 */
public class FileMessage extends BaseMessage {

    private String fileName;
    private int fileLength;
    private int fileNameLength;

    public FileMessage() {
    }

    public String getFileName() {
        return fileName;
    }

    public FileMessage setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public int getFileLength() {
        return fileLength;
    }

    public FileMessage setFileLength(int fileLength) {
        this.fileLength = fileLength;
        return this;
    }

    public int getFileNameLength() {
        return fileNameLength;
    }

    public FileMessage setFileNameLength(int fileNameLength) {
        this.fileNameLength = fileNameLength;
        return this;
    }

    @Override
    public String toString() {
        return "FileMessage{" +
                "fileName='" + fileName + '\'' +
                ", fileLength=" + fileLength +
                ", fileNameLength=" + fileNameLength +
                '}';
    }
}

