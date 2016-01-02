package com.yanyi.luckbag.model;

import com.android.volley.VolleyError;

/**
 * Created by huxuewen on 2015-06-23 15:40.
 * 六月
 */
public class AmayaError extends VolleyError {

    public int code;
    public String msg;

    public AmayaError(int code, String exceptionMessage) {
        super(exceptionMessage);
        this.code = code;
        this.msg = exceptionMessage;
    }
}
