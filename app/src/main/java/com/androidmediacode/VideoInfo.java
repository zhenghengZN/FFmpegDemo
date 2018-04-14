package com.androidmediacode;

/**
 * Created by zhengheng on 18/4/12.
 */
public class VideoInfo {
    private String displayName;
    private String path;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "displayName='" + displayName + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
