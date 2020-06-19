package com.example.wallpaper_manager;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "com.example.wallpaper_manager/wallpaper";
    private int status = -1;

    private class GetBitmapFromURLTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;

        protected Integer doInBackground(String... urls) {
            try {
                InputStream stream = new URL(urls[0]).openStream();
                if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                    try {
                        WallpaperManager wpm = WallpaperManager.getInstance(getApplicationContext());
                        wpm.setStream(stream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return 1;
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }

        protected void onPostExecute(Integer status) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler((call, result) -> {
                    // Note: this method is invoked on the main thread.
                    if (call.method.equals("setWallpaper")) {
                        try {
                            status = new GetBitmapFromURLTask().execute("https://www.ft.com/__origami/service/image/v2/images/raw/http%3A%2F%2Fcom.ft.imagepublish.upp-prod-us.s3.amazonaws.com%2F5ecccf40-b7e5-11e9-96bd-8e884d3ea203?fit=scale-down&source=next&width=700").get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (status != -1) {
                            result.success(status);
                        } else {
                            result.error("UNAVAILABLE", "Battery level not available.", null);
                        }
                    } else {
                        result.notImplemented();
                    }
                });
    }
}
