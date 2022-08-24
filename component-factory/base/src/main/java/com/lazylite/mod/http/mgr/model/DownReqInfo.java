package com.lazylite.mod.http.mgr.model;

public class DownReqInfo implements IDownloadInfo {

    private String url;

    private String path;

    private long startPos;

    public DownReqInfo(String url, String path, long startPos) {
        this.url = url;
        this.path = path;
        this.startPos = startPos;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public long getStartPos() {
        return startPos;
    }
}
