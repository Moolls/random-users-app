package ru.moolls.randomusers.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageDownloader {
    private static OkHttpClient httpClient = new OkHttpClient();

    public static Observable<Bitmap> loadImage(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        return Observable.create(emitter -> {
            httpClient
                    .newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            emitter.onError(e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) {
                            if (response.isSuccessful()) {
                                Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                                response.close();
                                emitter.onNext(bitmap);

                            }
                            emitter.onComplete();
                        }
                    });
        });

    }
}
