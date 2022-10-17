package com.lazylite.mod.http.mgr;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.lazylite.mod.http.mgr.model.CommonParam;
import com.lazylite.mod.http.okhttp.OkHttpCreator;
import com.lazylite.mod.http.okhttp.model.OkResponseInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;


public class KwHttpConfig {

    private final List<ICommonParamProvider> commonParamProviderList = new ArrayList<>();

    private List<IHttpResultCheckPolicy> resultCheckPolicies;

    private IKwHttpFetcher iKwHttpFetcher;

    private HostnameVerifier hostnameVerifier;

    private X509TrustManager trustManager;

    private SSLSocketFactory sslSocketFactory;

    private Context context;
    private Handler handler;

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

    public Context getContext() {
        return context;
    }

    public Handler getHandler() {
        return handler;
    }

    public static class Builder {

        private List<IHttpResultCheckPolicy> resultCheckPolicies;
        private HostnameVerifier hostnameVerifier;
        private X509TrustManager trustManager;
        private SSLSocketFactory sslSocketFactory;
        private Context context;
        private Handler handler;

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


        public KwHttpConfig build() {
            KwHttpConfig kwHttpConfig = new KwHttpConfig();
            kwHttpConfig.hostnameVerifier = this.hostnameVerifier;
            kwHttpConfig.context = this.context;
            kwHttpConfig.handler = this.handler;
            kwHttpConfig.trustManager = this.trustManager;
            kwHttpConfig.sslSocketFactory = this.sslSocketFactory;
            kwHttpConfig.resultCheckPolicies = this.resultCheckPolicies;
            if(null == kwHttpConfig.resultCheckPolicies){
                kwHttpConfig.resultCheckPolicies = new LinkedList<>();
            }
            kwHttpConfig.iKwHttpFetcher = OkHttpCreator.create(kwHttpConfig); //这个赋值要放在最后一个
            return kwHttpConfig;
        }

    }

}
