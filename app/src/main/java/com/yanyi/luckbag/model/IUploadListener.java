package com.yanyi.luckbag.model;

/**
 * Created by amayababy
 * 2015-07-20
 * 下午2:45
 */
public interface IUploadListener {
    void uploadOk(String data);

    void uploadError(int code, String message);
}
