package com.lazylite.mod.http.mgr;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.lazylite.mod.config.ConfMgr;
import com.lazylite.mod.config.IConfDef;
import com.lazylite.mod.http.okhttp.OkHttpCreator;
import com.lazylite.mod.receiver.network.NetworkStateUtil;
import com.lazylite.mod.utils.AppInfo;
import com.lazylite.mod.utils.LRSign;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;


public class KwHttpConfig {

    private ICommHeaderPolicy commExtraHeaderPolicy;

    private List<IHttpResultCheckPolicy> resultCheckPolicies;

    private IKwHttpFetcher iKwHttpFetcher;

    private Map<String, String> commonHeaders;

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

    Map<String, String> getCommonHeaders() {
        if (commonHeaders == null) {
            commonHeaders = new HashMap<>();
        }
        if(null != commExtraHeaderPolicy && commExtraHeaderPolicy.isCanUse()){
            commExtraHeaderPolicy.configParams(commonHeaders);
        }
        return commonHeaders;
    }

    List<IHttpResultCheckPolicy> getResultCheckPolicies(){
        return resultCheckPolicies;
    }

    public static Builder newOkHttpBuilder(Context context, Handler handler) {
        Builder builder = new Builder();
        builder.setContext(context);
        builder.setHandler(handler);
        builder.setCommonHeaders(buildCommonHeaders());
        builder.setCommExtraHeaderPolicy(new AccountHeaderPolicy());
        return builder;
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

    private static Map<String, String> buildCommonHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Auth-Platform", "Android");
        return headers;
    }

    public Map<String, String> getCommonParams() {
        Map<String, String> params = new HashMap<>();
        params.put("loginUid",ConfMgr.getLongValue(IConfDef.SEC_LR_LOGIN, IConfDef.KEY_LOGIN_UID, 0) + "");
        params.put("appuid", AppInfo.getAppUid());
        params.put("nonceStr", LRSign.getRandomString(6));
        params.put("timestamp", System.currentTimeMillis() + "");
        params.put("appPkg", "com.tencent.metarare");
        params.put("appVer", AppInfo.VERSION_CODE);
        params.put("appStore", AppInfo.INSTALL_CHANNEL);
        params.put("osName", "Android");
        params.put("netType", NetworkStateUtil.getNetworkTypeName());

        return params;
    }

    public static class Builder {

        @NonNull private final IKwHttpFetcher iKwHttpFetcher = new EmptyHttpFetcher();

        private ICommHeaderPolicy commExtraHeaderPolicy;
        private List<IHttpResultCheckPolicy> resultCheckPolicies;
        private final Map<String, String> commonHeaders = new HashMap<>();
        private HostnameVerifier hostnameVerifier;
        private X509TrustManager trustManager;
        private SSLSocketFactory sslSocketFactory;
        private Context context;
        private Handler handler;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setCommExtraHeaderPolicy(ICommHeaderPolicy commExtraHeaderPolicy) {
            this.commExtraHeaderPolicy = commExtraHeaderPolicy;
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

        public Builder setCommonHeaders(Map<String, String> headers) {
            commonHeaders.putAll(headers);
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
            kwHttpConfig.commonHeaders = this.commonHeaders;
            kwHttpConfig.hostnameVerifier = this.hostnameVerifier;
            kwHttpConfig.context = this.context;
            kwHttpConfig.handler = this.handler;
            kwHttpConfig.trustManager = this.trustManager;
            kwHttpConfig.sslSocketFactory = this.sslSocketFactory;
            kwHttpConfig.commExtraHeaderPolicy = this.commExtraHeaderPolicy;
            kwHttpConfig.resultCheckPolicies = this.resultCheckPolicies;
            if(null == kwHttpConfig.resultCheckPolicies){
                kwHttpConfig.resultCheckPolicies = new LinkedList<>();
            }
            kwHttpConfig.iKwHttpFetcher = OkHttpCreator.create(kwHttpConfig); //这个赋值要放在最后一个
            return kwHttpConfig;
        }

    }

}
