package com.afx.afx_preface_tool;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class HttpService extends NanoHTTPD {
    public static final String TAG = HttpService.class.getSimpleName();

    String upload_dir = "/data/local/tmp";
    public HttpService(String upload_dir, int port) {
        super(port);

        this.upload_dir = upload_dir;
    }

    public HttpService(String upload_dir, String hostname, int port) {
        super(hostname, port);

        this.upload_dir = upload_dir;
    }

    //重写Serve方法，每次请求时会调用该方法
    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        try {
            if (Method.POST.equals(method)) {
                Map<String, String> files = new HashMap<>();
                try {
                    if (session.getUri().endsWith("/upload")) {
                        session.parseBody(files);

                        Map<String, String> params = session.getParms();
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            final String paramsKey = entry.getKey();
                            //"file"是上传文件参数的key值
                            if (paramsKey.contains("file")) {
                                final String tmpFilePath = files.get(paramsKey);
                                //可以直接拿上传的文件名保存，也可以解析一下然后自己命名保存
                                final String fileName = entry.getValue();
                                final File tmpFile = new File(tmpFilePath);
                                //targetFile是你要保存的file，这里是保存在SD卡的根目录（需要获取文件读写权限）

                                {
                                    File t = new File(upload_dir + "/preface_tool_tmp");
                                    if(t.isFile()) {
                                        t.delete();
                                    }
                                    if(!t.exists()) {
                                        t.mkdirs();
                                    }
                                }
                                final File targetFile = new File(upload_dir + "/preface_tool_tmp", fileName);

                                {
                                    if(!targetFile.exists()) {
                                        targetFile.createNewFile();
                                    }
                                }
                                Log.d(TAG, "copy file now, source file path<>target file path === " + tmpFile.getAbsoluteFile() + "<>" + targetFile.getAbsoluteFile());
                                //a copy file methoed just what you like
                                copyFile(tmpFile, targetFile);
                                //maybe you should put the follow code out
                                return getResponse("Success");
                            }
                        }
                    }
                } catch (IOException ioe) {
                    return getResponse("Internal Error IO Exception: " + ioe.getMessage());
                } catch (ResponseException re) {
                    return newFixedLengthResponse(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
                }
            } else if (Method.GET.equals(method)) {
                if (session.getUri().endsWith("/start")) {
                    return NanoHTTPD.newFixedLengthResponse("<form action=\"/upload\"  enctype=\"multipart/form-data\" method=\"post\">\n" +
                            "    \n" +
                            "    <input type=\"file\" name=\"filename\"  value=\"选择文件\"/>\n" +
                            " \n" +
                            "    <button type=\"submit\">\n" +
                            " \n" +
                            "       点击上传\n" +
                            "    </button>\n" +
                            " \n" +
                            "</form>");

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return response404(session, null);
    }

    //页面不存在，或者文件不存在时
    public Response response404(IHTTPSession session, String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html>body>");
        builder.append("404 Not Found" + url + " !");
        builder.append("</body></html>\n");
        return NanoHTTPD.newFixedLengthResponse(builder.toString());
    }

    //成功请求
    public Response getResponse(String success) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html>body>");
        builder.append(success+ " !");
        builder.append("</body></html>\n");
        return NanoHTTPD.newFixedLengthResponse(builder.toString());
    }

    public boolean copyFile(File file, File targetfile) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        if (!file.exists()) {
            Log.e(TAG, "File not exists!");
            return false;
        }
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(targetfile);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                fos.flush();
            }
            return true;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (fis != null) {

                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

}
