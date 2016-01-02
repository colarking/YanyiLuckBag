package com.yanyi.luckbag.model;

import org.json.JSONException;

/**
 * Created by huxuewen on 2015-06-22 19:26.
 * 六月
 */
public interface IAmayaListener<T> {
    void onResponse(T data);

    void onErrorResponse(int code, String errorMsg);

    T parseData(String data) throws JSONException;

}
