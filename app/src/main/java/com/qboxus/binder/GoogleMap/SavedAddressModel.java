package com.qboxus.binder.GoogleMap;

import java.io.Serializable;

/**
 * Created by qboxus on 3/13/2018.
 */

public class SavedAddressModel implements Serializable {
    String Latitude, Longitude;
        public String getLatitude()
    { return Latitude; }
        public void setLatitude(String latitude) {
        Latitude = latitude; }
        public String getLongitude() {
        return Longitude; }
        public void setLongitude(String longitude) {
        Longitude = longitude; }
}
