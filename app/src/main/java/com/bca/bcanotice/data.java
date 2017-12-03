package com.bca.bcanotice;

/**
 * Created by dipanker on 23/08/17.
 */

public class data {
    public String url;
    public Long time;
    public String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public data(String url, Long time,String filename) {
        this.setUrl(url);
        this.setTime(time);
        this.setFilename(filename);
    }

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
