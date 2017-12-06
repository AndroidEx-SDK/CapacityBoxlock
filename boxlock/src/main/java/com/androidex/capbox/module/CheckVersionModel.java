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
    public String appVersion;
    public String appFileName;
    public String boxVersion;
    public String boxFileName;
    public String watchVersion;
    public String watchFileName;

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
