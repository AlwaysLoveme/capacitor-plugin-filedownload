package com.capacitor.filedownload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@CapacitorPlugin(name = "FileDownload", permissions = {
        // SDK VERSIONS 32 AND BELOW
        @Permission(
                alias = FileDownloadPlugin.STORAGE,
                strings = {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
        ),
        /*
        SDK VERSIONS 33 AND ABOVE
        This alias is a placeholder and the PHOTOS alias will be updated to use these permissions
        so that the end user does not need to explicitly use separate aliases depending
        on the SDK version.
         */
        //@Permission(strings = { Manifest.permission.READ_MEDIA_IMAGES }, alias = CameraPlugin.MEDIA)
})
public class FileDownloadPlugin extends Plugin {

    static final String STORAGE = "storage";
    static final String MEDIA = "media";

    private FileDownload implementation = new FileDownload();

    private OkHttpClient okHttpClient;
    private String pathStr;

    private Call downloadInstance;


    @Override
    public void load() {
        okHttpClient = new OkHttpClient.Builder().build();
    }

    @PluginMethod
    public void download(PluginCall call) throws JSONException {
        downloadFile(call);
//        Log.d("权限", getPermissionState(STORAGE) + "");
//        if(getPermissionState(STORAGE) != PermissionState.GRANTED && getPermissionState(STORAGE) != PermissionState.DENIED) {
//            requestPermissionForAlias(STORAGE, call, "permissionsCallback");
//        } else {
//
//        }
    }

    @PluginMethod
    public void cancel(PluginCall call) {
        if (downloadInstance != null && !downloadInstance.isCanceled()) {
            downloadInstance.cancel();
        }
        call.resolve();
    }

    @PluginMethod
    public void isCanceled(PluginCall call) {
        JSObject ret = new JSObject();
        if (downloadInstance != null) {
            ret.put("isCanceled", downloadInstance.isCanceled());
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void checkPermissions(PluginCall call) {
        if (getPermissionState(STORAGE) == PermissionState.GRANTED) {
            JSObject permissionsResultJSON = new JSObject();
            permissionsResultJSON.put(STORAGE, "granted");
            call.resolve(permissionsResultJSON);
        } else {
            super.checkPermissions(call);
        }
    }

    @PluginMethod
    public void requestPermissions(PluginCall call) {
        if (getPermissionState(STORAGE) == PermissionState.GRANTED) {
            JSObject permissionsResultJSON = new JSObject();
            permissionsResultJSON.put(STORAGE, "granted");
            call.resolve(permissionsResultJSON);
        } else {
            requestPermissionForAlias(STORAGE, call, "permissionsCallback");
        }
    }

    @PermissionCallback
    private void permissionsCallback(PluginCall call) {
        this.checkPermissions(call);
    }

    @PluginMethod
    public void openSetting(PluginCall call) {
        Context context = getContext();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
        call.resolve();
    }

    private void downloadFile(final PluginCall call) throws JSONException {
        String url = call.getString("url", "");
        final String fileName = call.getString("fileName", "");
        String destination = call.getString("destination", "DOCUMENT");

        assert url != null;
        if (url.isEmpty()) {
            call.reject("URL is required");
            return;
        }


        assert fileName != null;
        if (fileName.isEmpty()) {
            call.reject("File name is required");
            return;
        }

        Request.Builder requestBuilder = new Request.Builder().url(url);

        //设置请求头
        JSObject headers = call.getObject("headers");
        if (headers != null) {
            for (Iterator<String> it = headers.keys(); it.hasNext(); ) {
                String key = it.next();
                String value = headers.getString(key);
                assert value != null;
                requestBuilder.addHeader(key, value);
            }
        }

        // 设置请求体
        JSObject requestBodyObject = call.getObject("body", null);
        if (requestBodyObject != null) {
            JsonObject jsonObject = new JsonObject();
            for (Iterator<String> it = requestBodyObject.keys(); it.hasNext(); ) {
                String key = it.next();
                Object value = requestBodyObject.get(key);
                if (value instanceof String) {
                    jsonObject.addProperty(key, (String) value);
                } else if (value instanceof Number) {
                    jsonObject.addProperty(key, (Number) value);
                } else if (value instanceof Boolean) {
                    jsonObject.addProperty(key, (Boolean) value);
                } else if (value instanceof Character) {
                    jsonObject.addProperty(key, (Character) value);
                }
            }

            Gson gson = new Gson();
            String jsonBody = gson.toJson(jsonObject);

            RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json"));
            requestBuilder.method("POST", requestBody);
        }

        Request request = requestBuilder.build();

        downloadInstance = okHttpClient.newCall(request);
        downloadInstance.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call __call, @NonNull IOException e) {
                call.reject("download fail: " + e.getMessage());
                downloadInstance.cancel();
            }

            @Override
            public void onResponse(@NonNull Call __call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    call.reject("Unexpected code " + response);
                    return;
                }

                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    call.reject("Download failed: Response body is null");
                    return;
                }

                if (downloadInstance.isCanceled()) {
                    return;
                }

                try {
                    assert destination != null;
                    File downloadDestination = getDefaultDownloadDestination(destination);
                    File file = new File(downloadDestination, fileName);
                    pathStr = file.getAbsolutePath();
                    File parentDir = file.getParentFile();
                    if (parentDir != null && !parentDir.exists()) {
                        if (!parentDir.mkdirs()) {
                            call.reject("Failed to create parent directory");
                            return;
                        }
                    }


                    assert response.body() != null;
                    InputStream inputStream = response.body().byteStream();
                    FileOutputStream outputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int length;
                    long downloadedBytes = 0;
                    long totalBytes = response.body().contentLength();
                    while ((length = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                        downloadedBytes += length;
                        int progress = (int) (downloadedBytes * 100 / totalBytes);
                        notifyProgress(progress);
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();

                    call.resolve(createSuccessResponse());
                } catch (IOException e) {
                    call.reject("download fail: " + e.getMessage());
                    __call.cancel();
                }
            }
        });
    }

    private File getDefaultDownloadDestination(String destination) {
        File downloadDestination;
        switch (destination) {
            case "DOCUMENT":
                downloadDestination = getContext().getExternalFilesDir("");
                break;
            case "EXTERNAL":
            case "EXTERNAL_STORAGE":
                downloadDestination = Environment.getExternalStorageDirectory();
                break;
            case "DATA":
                downloadDestination = getContext().getFilesDir();
                break;
            case "CACHE":
                downloadDestination = getContext().getCacheDir();
                break;
            default:
                downloadDestination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                break;
        }

        if (!downloadDestination.exists()) {
            downloadDestination.mkdirs();
        }

        return downloadDestination;
    }

    private void notifyProgress(int progress) {
        JSObject progressObj = new JSObject();
        progressObj.put("progress", progress);
        notifyListeners("downloadProgress", progressObj);
    }

    private JSObject createSuccessResponse() {
        JSObject response = new JSObject();
        response.put("success", true);
        response.put("path", "file://" + pathStr);
        return response;
    }
}
