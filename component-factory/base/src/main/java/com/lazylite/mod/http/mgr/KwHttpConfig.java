package com.lazylite.mod.http.mgr;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.lazylite.mod.http.mgr.model.CommonParam;
import com.lazylite.mod.http.okhttp.OkHttpCreator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;


public class KwHttpConfig {

    private final List<ICommonParamProvider> commonParamProviderList = new ArrayList<>();

    private List<IHttpResultCheckPolicy> resultCheckPolicies;

    private IKwHttpFetcher iKwHttpFetcher;

    private ArrayList<Interceptor> interceptors = new ArrayList<>();
    private ArrayList<Interceptor> networkInterceptors = new ArrayList<>();

    private HostnameVerifier hostnameVerifier;

    private X509TrustManager trustManager;

    private SSLSocketFactory sslSocketFactory;

    private Context context;
    private Handler handler;
    private int connectTimeout = 30;
    private int readTimeout = 60;
    private int writeTimeout = 60;

    private KwHttpConfig() {}

    @NonNull
    IKwHttpFetcher getKwHttpFetch() {
        if (iKwHttpFetcher == null) {
            iKwHttpFetcher = new EmptyHttpFetcher();
        }
        return iKwHttpFetcher;
    }

    @NonNull
    public synchronized CommonParam getCommonHeaders() {
        StringBuilder opt = new StringBuilder();
        Map<String, String> headersMap = new HashMap<>();
        for (ICommonParamProvider iCommonParamProvider : commonParamProviderList) {
            Map<String, String> map = iCommonParamProvider.getCommonHeads();
            if (map != null) {
                opt.append("[").append(iCommonParamProvider.providerName()).append("] ");
                headersMap.putAll(map);
            }
        }
        return new CommonParam(headersMap, opt.toString());
    }

    @NonNull
    public CommonParam getCommonQueryParams() {
        StringBuilder opt = new StringBuilder();
        Map<String, String> params = new HashMap<>();
        for (ICommonParamProvider iCommonParamProvider : commonParamProviderList) {
            Map<String, String> map = iCommonParamProvider.getCommonQueryParams();
            if (map != null) {
                opt.append("[").append(iCommonParamProvider.providerName()).append("] ");
                params.putAll(map);
            }
        }
        return new CommonParam(params, opt.toString());
    }

    synchronized void addCommonParamProvider(ICommonParamProvider commonParamProvider) {
        commonParamProviderList.add(commonParamProvider);
    }

    public List<IHttpResultCheckPolicy> getResultCheckPolicies(){
        return resultCheckPolicies;
    }

    public X509TrustManager getTrustManager() {
        return trustManager;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public ArrayList<Interceptor> getInterceptors() {
        return interceptors;
    }
    public ArrayList<Interceptor> getNetworkInterceptors() {
        return networkInterceptors;
    }
    public int getConnectTimeout() {
        return connectTimeout;
    }
    public int getReadTimeout() {
        return readTimeout;
    }
    public int getWriteTimeout() {
        return writeTimeout;
    }

    public Context getContext() {
        return context;
    }

    public Handler getHandler() {
        return handler;
    }

    public synchronized String printCommonParamInfo() {
        JSONObject paramObj = new JSONObject();
        JSONObject headerObj = new JSONObject();
        JSONObject queryObj = new JSONObject();
        try {
            paramObj.putOpt("headerParam", headerObj);
            paramObj.putOpt("queryParam", queryObj);
            for (ICommonParamProvider iCommonParamProvider : commonParamProviderList) {
                JSONArray headerArr = new JSONArray();
                headerObj.putOpt(iCommonParamProvider.providerName() + "#" + iCommonParamProvider.getClass().getSimpleName(), headerArr);
                Map<String, String> headerMap = iCommonParamProvider.getCommonHeads();
                if (headerMap != null) {
                    for (Map.Entry<String, String> stringStringEntry : headerMap.entrySet()) {
                        headerArr.put(stringStringEntry);
                    }
                }
                JSONArray queryArr = new JSONArray();
                queryObj.putOpt(iCommonParamProvider.providerName() + "#" + iCommonParamProvider.getClass().getSimpleName(), queryArr);
                Map<String, String> queryMap = iCommonParamProvider.getCommonQueryParams();
                if (queryMap != null) {
                    for (Map.Entry<String, String> stringStringEntry : queryMap.entrySet()) {
                        queryArr.put(stringStringEntry);
                    }
                }
            }
        } catch (Exception ignore) {

        }

        return paramObj.toString();
    }

    public static class Builder {

        private List<IHttpResultCheckPolicy> resultCheckPolicies;
        private HostnameVerifier hostnameVerifier;
        private X509TrustManager trustManager;
        private SSLSocketFactory sslSocketFactory;
        private Context context;
        private Handler handler;
        public int connectTimeout = 30;
        public int readTimeout = 60;
        public int writeTimeout = 60;
        private ArrayList<Interceptor> interceptors;
        private ArrayList<Interceptor> networkInterceptors;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }


        public Builder setHttpResultCheckPolicy(List<IHttpResultCheckPolicy> resultCheckPolicies) {
            this.resultCheckPolicies = resultCheckPolicies;
            return this;
        }

        public Builder setHandler(Handler handler) {
            this.handler = handler;
            return this;
        }
        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }
        public Builder setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }
        public Builder setWriteTimeout(int writeTimeout) {
            this.writeTimeout = writeTimeout;
            return this;
        }

        public Builder setHostnameVerifier(HostnameVerifier hostnameVerifier){
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        public Builder setTrustManager(X509TrustManager trustManager) {
            this.trustManager = trustManager;
            return this;
        }

        public Builder setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        public void setInterceptors(ArrayList<Interceptor> interceptors) {
            this.interceptors = interceptors;
        }

        public void setNetworkInterceptors(ArrayList<Interceptor> interceptors) {
            this.networkInterceptors = interceptors;
        }


        public KwHttpConfig build() {
            KwHttpConfig kwHttpConfig = new KwHttpConfig();
            kwHttpConfig.hostnameVerifier = this.hostnameVerifier;
            kwHttpConfig.context = this.context;
            kwHttpConfig.handler = this.handler;
            kwHttpConfig.trustManager = this.trustManager;
            kwHttpConfig.sslSocketFactory = this.sslSocketFactory;
            kwHttpConfig.resultCheckPolicies = this.resultCheckPolicies;
            kwHttpConfig.connectTimeout = this.connectTimeout;
            kwHttpConfig.readTimeout = this.readTimeout;
            kwHttpConfig.writeTimeout = this.writeTimeout;
            if (this.interceptors != null) {
                kwHttpConfig.interceptors.addAll(this.interceptors);
            }
            if (this.networkInterceptors != null) {
                kwHttpConfig.networkInterceptors.addAll(this.networkInterceptors);
            }
            if(null == kwHttpConfig.resultCheckPolicies){
                kwHttpConfig.resultCheckPolicies = new LinkedList<>();
            }
            kwHttpConfig.iKwHttpFetcher = OkHttpCreator.create(kwHttpConfig); //这个赋值要放在最后一个
            return kwHttpConfig;
        }
    }

}
