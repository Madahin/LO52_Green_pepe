package com.example.madahin.pepe_media;

/**
 * Created by Madahin on 15/12/2016.
 */

public class MediaInfo {
    public final String path;
    public final String filename;
    public String title = "";

    public MediaInfo(String _path, String _filename){
        path = _path;
        filename = _filename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MediaInfo mediaInfo = (MediaInfo) o;

        if (path != null ? !path.equals(mediaInfo.path) : mediaInfo.path != null) return false;
        if (filename != null ? !filename.equals(mediaInfo.filename) : mediaInfo.filename != null)
            return false;
        return title != null ? title.equals(mediaInfo.title) : mediaInfo.title == null;

    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
