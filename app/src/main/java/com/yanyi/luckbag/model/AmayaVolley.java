package com.yanyi.luckbag.model;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by huxuewen on 2015-06-22 23:12.
 * 六月
 */
public class AmayaVolley {
    private static RequestQueue sRequestQueue;


    /**
     * 必须要在Application里调用
     *
     * @param context
     */
    public static void init(Context context) {
        sRequestQueue = Volley.newRequestQueue(context);
    }

    public static void submit(AmayaRequest request) {
        submit(request, request.toString());
    }

    public static void submit(Request req, String tag) {
        req.setTag(tag);
        sRequestQueue.add(req);
        sRequestQueue.start();
    }

    public static void cancel(AmayaRequest request) {
        sRequestQueue.cancelAll(request.toString());
    }

    public void cancelPendingRequests(String tag) {
        sRequestQueue.cancelAll(tag);
    }
}
