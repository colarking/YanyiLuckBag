package com.yanyi.luckbag.model;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.yanyi.luckbag.activity.MatrixApplication;
import com.yanyi.luckbag.util.AmayaConstants;
import com.yanyi.luckbag.util.AmayaLog;
import com.yanyi.luckbag.util.AmayaUrls;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxuewen on 2015-06-22 23:02.
 * 六月
 */
public class AmayaRequest<T> extends Request<T> {

    private static final int CODE_CUCCESS = 200;
    private static final String TAG = AmayaRequest.class.getSimpleName();
    private static boolean addBaseParams = true;
    private final IAmayaListener<T> listener;
    private HashMap<String, String> mParams = new HashMap<>();
    private boolean parseCode = true;

    public AmayaRequest(String url, final IAmayaListener<T> listener) {
        this(false, true, true, url, listener);
    }

    public AmayaRequest(boolean getMethod, String url, final IAmayaListener<T> listener) {
        this(getMethod, true, true, url, listener);
    }

    public AmayaRequest(boolean getMethod, boolean addBaseUrl, String url, final IAmayaListener<T> listener) {
        this(getMethod, addBaseUrl, true, url, listener);
    }

    public AmayaRequest(boolean getMethod, boolean addBaseUrl, boolean addToken, String url, final IAmayaListener<T> listener) {
        this(getMethod ? Method.GET : Method.POST, initUrl(getMethod, addBaseUrl, addToken, url), listener);
    }

    private AmayaRequest(int method, String url, final IAmayaListener<T> listener) {
        super(method, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (listener != null) {
                    if (volleyError instanceof AmayaError) {
                        listener.onErrorResponse(((AmayaError) volleyError).code, volleyError.getMessage());
                    } else {
//                        AmayaLog.e(TAG,"onErrorResponse()..."+volleyError.getCause().toString());
//                        AmayaLog.e(TAG,"onErrorResponse()..."+volleyError.getCause().getStackTrace().toString());
                        listener.onErrorResponse(AmayaConstants.CODE_ERROR_SYSTEM, "网络异常,请重试");
                    }
                }
            }
        });
        this.listener = listener;
    }

    private static String initUrl(boolean getMethod, boolean addBaseUrl, boolean addToken, String url) {
        if (addBaseUrl) {
            url = String.format("%1$s%2$s", AmayaUrls.BASE_URL, url);
        }
        if (getMethod && addToken) {
            url = String.format("%1$s&tocken=%2$s", url, MatrixApplication.user.getToken());
        } else {
            addBaseParams = addToken;
        }
        AmayaLog.e("amaya", "initUrl()...url=" + url);
        return url;
    }

    /**
     * 增加必选参数
     */
    private void addBaseParams() {
        if (addBaseParams) {
            if (TextUtils.isEmpty(MatrixApplication.user.getToken())) {
                MatrixApplication.user.setToken("");
            }
            mParams.put("tocken", MatrixApplication.user.getToken());
        }
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        addBaseParams();
        return mParams;
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse networkResponse) {
        if (isCanceled() || listener == null) return null;
        String parsed;
        try {
            parsed = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(networkResponse.data);
        }
        if (parseCode) {
            try {
                if (!TextUtils.isEmpty(parsed)) {
                    final JSONObject jsonObject = JSONObject.parseObject(parsed);
                    int code = jsonObject.getIntValue("code");
                    if (code == CODE_CUCCESS) {
                        final String data = jsonObject.getString("data");
                        T t = listener.parseData(data);
                        return Response.success(t, HttpHeaderParser.parseCacheHeaders(networkResponse));
                    } else {
                        String msg = jsonObject.getString("msg");
                        if (TextUtils.isEmpty(msg)) msg = "请求失败,请重试...";
                        return Response.error(new AmayaError(code, msg));
                    }
                } else {
                    return Response.error(new AmayaError(AmayaConstants.CODE_ERROR_PARSE, "[amaya:body is null]"));
                }
            } catch (Exception e) {
                return Response.error(new AmayaError(AmayaConstants.CODE_ERROR_PARSE, e.getLocalizedMessage()));
            }
        } else {
            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(networkResponse));
        }
    }

    public void addParam(String name, String value) {
        if (!TextUtils.isEmpty(name)) {
            mParams.put(name, value == null ? "" : value);
        }
    }

    public void addParam(String name, int value) {
        if (!TextUtils.isEmpty(name)) {
            mParams.put(name, String.valueOf(value));
        }
    }

    public void addParam(String name, long value) {
        if (!TextUtils.isEmpty(name)) {
            mParams.put(name, String.valueOf(value));
        }
    }

    public void addParam(String name, double value) {
        if (!TextUtils.isEmpty(name)) {
            mParams.put(name, String.valueOf(value));
        }
    }

    public void addParam(String name, float value) {
        if (!TextUtils.isEmpty(name)) {
            mParams.put(name, String.valueOf(value));
        }
    }


    public void setParseCode(boolean parseCode) {
        this.parseCode = parseCode;
    }

    /**
     * 网络请求回调成功
     *
     * @param response 返回数据
     */
    @Override
    protected void deliverResponse(T response) {

        if (listener != null) {
            listener.onResponse(response);
        }
    }
}
