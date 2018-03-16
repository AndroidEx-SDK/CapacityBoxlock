package com.androidex.capbox.module;

/**
 * Created by cts on 17/10/13.
 */

public class GetFingerInfoModel extends BaseModel {
    public Data data;

    public class Data {
//        possessorFinger1: ‘fffffff’，   //箱体所有人的指纹信息或指纹id
//        possessorFinger2:’fffffff’,     //箱体所有人的指纹信息或id
//        possessorFinger3:’fffffff’,     //箱体所有人的指纹信息或id
//        becomeFinger1:’fffffff’,      //静默功能的指纹信息或id
//        becomeFinger2:’fffffff’’      //静默功能的指纹信息或id
//        becomeFinger3:’fffffff’’      //静默功能的指纹信息或id

        public String possessorFinger1;
        public String possessorFinger2;
        public String possessorFinger3;
        public String becomeFinger1;
        public String becomeFinger2;
        public String becomeFinger3;

        @Override
        public String toString() {
            return "Data{" +
                    "possessorFinger1='" + possessorFinger1 + '\'' +
                    ", possessorFinger2='" + possessorFinger2 + '\'' +
                    ", possessorFinger3='" + possessorFinger3 + '\'' +
                    ", becomeFinger1='" + becomeFinger1 + '\'' +
                    ", becomeFinger2='" + becomeFinger2 + '\'' +
                    ", becomeFinger3='" + becomeFinger3 + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BoxDetailModel{" +
                "data=" + data +
                '}';
    }
}
