package com.nalex.mypopularmovies.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Configuration {

    /*  POJO class to get the system wide configuration information
     *  from TheMovieDatabase. Tried but not used.
     */

        @SerializedName("images")
        private Images images;
        @SerializedName("change_keys")
        private List<String> changeKeys = null;

        public Images getImages() {
            return images;
        }

        public void setImages(Images images) {
            this.images = images;
        }

        public List<String> getChangeKeys() {
            return changeKeys;
        }

        public void setChangeKeys(List<String> changeKeys) {
            this.changeKeys = changeKeys;
        }

    }



