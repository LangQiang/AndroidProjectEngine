package com.lazylite.mod.http.okhttp.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.lazylite.mod.http.mgr.model.IResponseInfo;
import com.lazylite.mod.log.LogMgr;
import com.lazylite.mod.utils.StringCodec;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;


public class OkResponseInfo implements IResponseInfo {

    public int code;

    public byte[] data;

    public String errorMsg;

    public Map<String, List<String>> responseHeaders;

    public String finalRequestUrl;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

    @NonNull
    @Override
    public String dataToString() {
        if(isSuccessful() && null != data){
            StringCodec codec = new StringCodec();
            try{
                return codec.decode(data, "UTF-8");
            }catch(UnsupportedEncodingException uee){
                LogMgr.e(uee.getMessage() + "");
            }catch(Throwable e){
                LogMgr.e(e.getMessage() + "");
            }
        }
        return "";
    }

    @Override
    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    @Override
    public boolean isServeSuccess() {
        String data = dataToString();
        if(!TextUtils.isEmpty(dataToString())){
            try {
                JSONObject jsonObject = new JSONObject(data);
                return jsonObject.optInt("code") == 200;
            } catch (JSONException e) {
                return false;
            }
        }
        return false;
    }


    @Override
    public String getServeMsg() {
        String data = dataToString();
        if(!TextUtils.isEmpty(dataToString())){
            try {
                JSONObject jsonObject = new JSONObject(data);
                return jsonObject.optString("msg") ;
            } catch (JSONException e) {
                return "数据错误";
            }
        }
        return "数据错误";
    }

    @Override
    public String getFinalRequestUrl() {
        return finalRequestUrl;
    }
}
