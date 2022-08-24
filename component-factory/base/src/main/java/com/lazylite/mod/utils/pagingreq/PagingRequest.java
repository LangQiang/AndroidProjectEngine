package com.lazylite.mod.utils.pagingreq;

/**
 * Created by tiancheng :)
 */
public abstract class PagingRequest {

    Cache mCache;
    private int mPage;// 页码
    private int mPageForRefresh;//下拉刷新的页码
    private int mPageSize; // 单页加载条目数，固定
    private int mTotal; // 总数，固定
    private boolean isLoadMoreEnable = true; // 是否能loadmore
    private boolean isPullToRefreshEnable;//是否能下拉刷新

    /**
     * 适用于明确标准的total
     */
    public PagingRequest(int page, int pageSize, int total) {
        this.mPage = page;
        this.mPageForRefresh = page - 1;
        this.mPageSize = pageSize;
        this.mTotal = total;
    }

    /**
     * 适用于初始外层接口返回的total不准，需要依赖列表接口返回total在设置setTotal的情况下，比如歌单，主播电台
     */
    public PagingRequest(int page, int pageSize) {
        this.mPage = page;
        this.mPageForRefresh = page - 1;
        this.mPageSize = pageSize;
        this.mTotal = Integer.MAX_VALUE;
    }

    /**
     * 请求完调用，自增页数
     * @param size 请求回来的条目，主要是数据为0的时候置一下LoadMore false结束加载更多
     */
    public void increment(int size) {
        mPage = mPage + 1;
        if (size == 0 || mPage * mPageSize >= mTotal) {
            setLoadMoreEnable(false);
        }
    }

    // page 从1开始情况
    public void incrementNew(int size){
        if (size == 0 || mPage * mPageSize >= mTotal) {
            setLoadMoreEnable(false);
        }
    }

    public void decrement(){
        if (mPageForRefresh >= 0){
            mPageForRefresh = mPageForRefresh - 1;
        }
    }

    /**
     * 重置分页起始index
     */
    public void reset(int page) {
        mPage = page;
        setLoadMoreEnable(mPage * mPageSize < mTotal);
    }

    public boolean isLoadMoreEnable() {
        return isLoadMoreEnable;
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        isLoadMoreEnable = loadMoreEnable;
    }

    public boolean isPullToRefreshEnable() {
        return mPageForRefresh >= 0;
    }

    public Cache getCache() {
        return mCache;
    }

    public void setCache(Cache cache) {
        this.mCache = cache;
    }

    public void setTotal(int total) {
        this.mTotal = total;
    }

    public int getTotal(){
        return mTotal;
    }

    public String getRequestUrl() {
        // 根据页数再url里面再转成起始
        return requestUrl(mPage, mPageSize);
    }

    public String getPullToRefreshUrl(){
        return requestUrl(mPageForRefresh, mPageSize);
    }

    public abstract String requestUrl(int page, int size);

    public int getRefreshPage(){
        return mPageForRefresh;
    }
}
