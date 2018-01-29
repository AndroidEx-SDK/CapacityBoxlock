package com.androidex.capbox.module;

import java.util.List;

/**
 * Created by Administrator on 2018/1/27.
 */

public class BoxMovePathModel extends BaseModel {

    public List<BoxMovePathModel.LatLng> datalist;

    public class LatLng {
        public String longitude;
        public String latitude;
        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public String getLatitude() {
            return latitude;
        }
    }
}
