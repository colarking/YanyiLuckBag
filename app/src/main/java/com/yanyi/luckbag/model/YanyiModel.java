package com.yanyi.luckbag.model;

import com.yanyi.luckbag.util.AmayaConstants;
import com.yanyi.luckbag.util.AmayaEvent;
import com.yanyi.luckbag.util.AmayaSPUtil;
import com.yanyi.luckbag.util.AmayaUrls;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.greenrobot.event.EventBus;

/**
 * Created by huxuewen
 * 2015-12-19
 * 下午12:46
 */
public class YanyiModel {
    private static EventBus eventBus = EventBus.getDefault();
    private static ExecutorService executor = Executors.newCachedThreadPool();

    public void post(Object event) {
        eventBus.post(event);
    }

    public void postSticky(Object event) {
        eventBus.postSticky(event);
    }

    public void submit(AmayaRequest request) {
        AmayaVolley.submit(request);
    }

    public void cancel(AmayaRequest request) {
        AmayaVolley.cancel(request);
    }

    public void submit(Runnable runnable) {
        executor.submit(runnable);
    }

    public void submit(final String url, final Map<String, String> files, final Map<String, String> params, final IUploadListener listener) {
        submit(url, "images", files, params, listener);
    }

    public void submit(final String url, final String imageKey, final Map<String, String> files, final Map<String, String> params, final IUploadListener listener) {
        submit(url, false, imageKey, files, params, listener);
    }

    public void submit(final String url, final boolean headPic, final String imageKey, final Map<String, String> files, final Map<String, String> params, final IUploadListener listener) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HttpPostUploadUtil.formUploadFile(AmayaUrls.BASE_URL + url, headPic, imageKey, params, files, listener);
            }
        };
        submit(runnable);
    }


    /**
     * 检查是否有系统级别错误
     *
     * @param code
     * @return
     */
    public boolean checkErrorCode(int code) {
        if (code == AmayaConstants.CODE_USER_ERROR) {
            AmayaSPUtil.clearUser();
            post(new AmayaEvent.UserErrorEvent());
            return false;
        }
        return true;
    }
}
