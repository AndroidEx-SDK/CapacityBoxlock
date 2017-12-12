package com.androidex.capbox.module;

/**
 * @author liyp
 * @version 1.0.0
 * @description Model基类
 * @createTime 2015/10/19
 * @editTime
 * @editor
 */
public class CheckVersionModel extends BaseModel {
    /**
     * code:0，
     * appVersion:’0.0.1’,          //APP版本号
     * appFileName:’20171129.apk’,  //APP文件名
     * boxVersion:’0.0.1’,          //箱体固件版本号
     * boxFileName:’ 20171129.hex’, //箱体固件名字
     * watchVersion:’0.0.2’         //腕表固件版本号
     * watchFileName:’ 20171129.hex’, //腕表固件名字
     */
    public int appVersion;
    public String appFileName;
    public String boxVersion;
    public String boxFileName;
    public String watchVersion;
    public String watchFileName;

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppFileName() {
        return appFileName;
    }

    public void setAppFileName(String appFileName) {
        this.appFileName = appFileName;
    }

    public String getBoxVersion() {
        return boxVersion;
    }

    public void setBoxVersion(String boxVersion) {
        this.boxVersion = boxVersion;
    }

    public String getBoxFileName() {
        return boxFileName;
    }

    public void setBoxFileName(String boxFileName) {
        this.boxFileName = boxFileName;
    }

    public String getWatchVersion() {
        return watchVersion;
    }

    public void setWatchVersion(String watchVersion) {
        this.watchVersion = watchVersion;
    }

    public String getWatchFileName() {
        return watchFileName;
    }

    public void setWatchFileName(String watchFileName) {
        this.watchFileName = watchFileName;
    }

    @Override
    public String toString() {
        return "CheckVersionModel{" +
                "appVersion='" + appVersion + '\'' +
                ", appFileName='" + appFileName + '\'' +
                ", boxVersion='" + boxVersion + '\'' +
                ", boxFileName='" + boxFileName + '\'' +
                ", watchVersion='" + watchVersion + '\'' +
                ", watchFileName='" + watchFileName + '\'' +
                '}';
    }
}
