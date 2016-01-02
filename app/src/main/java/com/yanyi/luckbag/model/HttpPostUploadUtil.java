package com.yanyi.luckbag.model;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.yanyi.luckbag.activity.MatrixApplication;
import com.yanyi.luckbag.util.AmayaConstants;
import com.yanyi.luckbag.util.AmayaImageUtil;
import com.yanyi.luckbag.util.AmayaLog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;

public class HttpPostUploadUtil {

    private static final int DEFAULT_MAX_SIZE = 1024 * 600; //200KB
    private static final int DEFAULT_HEADER_SIZE = 1024 * 100; //20KB

    /**
     * @param args
     */
    public static void main(String[] args) {
        String filepath = "d:\\《政治学》.jpg";
        String urlStr = "http://121.199.31.3/lvYou/rs/user/userAddUserImg";
        //获取我的信息：http://121.199.31.3/lvYou/rs/user/userGetUserImg/1001
        Map<String, String> textMap = new HashMap<String, String>();

        textMap.put("userToken", "1001@@-1s5gd5st64epk5gih05i4riv7h");

        Map<String, String> fileMap = new HashMap<String, String>();
        String path = "";
        try {
            byte[] bytes = filepath.getBytes("utf-8");
            path = new String((bytes));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        fileMap.put("userImg", path);

        formUploadFile(urlStr, false, "images", textMap, fileMap, null);


    }


    private static void addMustParams(Map<String, String> maps) {
//        maps.put("userId", String.valueOf(XApplication.user.getId()));
        maps.put("tocken", MatrixApplication.user.getToken());
    }

    public static void formUploadFile(String urlStr, boolean headerPic, String imageKey, Map<String, String> textMap,
                                      Map<String, String> fileMap, IUploadListener listener) {
        formUploadFile(urlStr, imageKey, textMap, fileMap, headerPic ? DEFAULT_HEADER_SIZE : DEFAULT_MAX_SIZE, listener);
    }

    /**
     * 上传图片
     *
     * @param urlStr
     * @param textMap
     * @param fileMap
     * @param listener
     * @return
     */
    public static void formUploadFile(String urlStr, String imageKey, Map<String, String> textMap,
                                      Map<String, String> fileMap, int maxSize, IUploadListener listener) {
        String res = "";
        HttpURLConnection conn = null;
        String BOUNDARY = "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符
        try {
            if (textMap == null) textMap = new HashMap<>();
            addMustParams(textMap);
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);

            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // text
            if (textMap != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator iter = textMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append(
                            "\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes("utf-8"));
            }

            // file
            if (fileMap != null) {
                Iterator iter = fileMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (TextUtils.isEmpty(inputValue)) {
                        continue;
                    }
                    InputStream is = null;
                    if (inputValue.startsWith(AmayaConstants.PREFIX_HTTP)) {
                        StringBuffer strBuf = new StringBuffer();
                        strBuf.append("\r\n").append("--").append(BOUNDARY).append(
                                "\r\n");
                        strBuf.append("Content-Disposition: form-data; name=\"")
                                .append(imageKey).append("\"; filename=\"")
                                .append(inputName).append("\"\r\n");
                        strBuf.append("Content-Type:image/png").append(
                                "\r\n\r\n");
                        out.write(strBuf.toString().getBytes("utf-8"));
                        is = new URL(inputValue).openStream();
                    } else {
                        is = getFileStream(imageKey, textMap, maxSize, BOUNDARY, out, inputValue);
                    }
                    DataInputStream in = new DataInputStream(is);
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    in.close();
                }
            }

            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            // 读取返回数据
            int code = conn.getResponseCode();
            AmayaLog.e("amaya", "formUpload()...code=" + code);
            if (code == 200) {
                StringBuilder strBuf = new StringBuilder();
                InputStream inputStream = conn.getInputStream();
                int len = 0;
                byte[] buf = new byte[2048];
                while ((len = inputStream.read(buf)) != -1) {
                    strBuf.append(new String(buf, 0, len));
                }
                res = strBuf.toString();
                inputStream.close();
                inputStream = null;
                if (listener != null) {
                    listener.uploadOk(res);
                }
            } else {
                InputStream errorStream = conn.getErrorStream();
                StringBuffer sb = new StringBuffer();
                byte[] buf = new byte[1024];
                int len = 0;
                while ((len = errorStream.read(buf)) != -1) {
                    sb.append(new String(buf, 0, len));
                }
                errorStream.close();
                errorStream = null;
                if (listener != null) {
                    listener.uploadError(code, sb.toString());
                }
                AmayaLog.e("amaya", "formUpload()...code=" + code + "--" + sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.uploadError(AmayaConstants.CODE_ERROR_PARSE, "error occured...");
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
    }

    @Nullable
    public static FileInputStream getFileStream(String imageKey, Map<String, String> textMap, int maxSize, String BOUNDARY, OutputStream out, String inputValue) throws IOException {
        File file = new File(inputValue);
        if (!file.exists()) {
            AmayaLog.e("amaya", "formUpload()...file NOT exists...path=" + inputValue);
            return null;
        }
        if (file.length() > maxSize) {//1M=1048576 200K=204800
//                        int suitLength = 300 * 1024 * 100;
//                        long compress = suitLength / file.length();
            long compress = file.length() / maxSize;
            if (compress > 100) compress = 90;
            else if (compress < 70) compress = 70;
            AmayaLog.i("amaya", "formUpload()...file.length=" + file.length() + "--maxSize=" + maxSize + "--compress=" + compress + "--path=" + inputValue);
            File path = AmayaImageUtil.createUploadSuitPic(inputValue, (int) compress, textMap, inputValue.endsWith(".png"), false);
            AmayaLog.i("amaya", "formUpload()...file.length=" + file.length() + "--path=" + path + "--path.length=" + path.length());
            if (path != null) {
//                            AmayaLog.i("amaya","formUpload()...compress success--size="+ Formatter.formatFileSize(MatrixApplication.mContext,path.length()));
                file = path;
            }
//                        String path = AmayaImageUtil.createUploadSuitPic(inputValue);
//                        if(!TextUtils.isEmpty(path)) {
//                            String size = Formatter.formatShortFileSize(MatrixApplication.mContext, new File(path).length());
//                        }

        }
        String filename = file.getName();
        String contentType = "";//new MimetypesFileTypeMap().getContentType(file);
        if (filename.endsWith(".png")) {
            contentType = "image/png";
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        }
        if (TextUtils.isEmpty(contentType)) {
            contentType = "application/octet-stream";
        }

        StringBuffer strBuf = new StringBuffer();
        strBuf.append("\r\n").append("--").append(BOUNDARY).append(
                "\r\n");
        strBuf.append("Content-Disposition: form-data; name=\"")
                .append(imageKey).append("\"; filename=\"")
                .append(filename).append("\"\r\n");
        strBuf.append("Content-Type:").append(contentType).append(
                "\r\n\r\n");

        out.write(strBuf.toString().getBytes("utf-8"));
        return new FileInputStream(file);
    }

}
