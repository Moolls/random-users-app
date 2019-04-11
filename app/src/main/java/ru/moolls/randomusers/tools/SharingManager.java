package ru.moolls.randomusers.tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.moolls.randomusers.BuildConfig;
import ru.moolls.randomusers.R;
import ru.moolls.randomusers.service.entity.User;

public class SharingManager {

    public static final String FILE_PROVIDER = ".fileprovider";
    public static final String IMAGES_PATH = "images";
    public static final String TEMP_IMAGE = "/image.png";
    public static final String IMAGE_TYPE = "image/*";

    public static void shareImage(Context context, Bitmap bitmap) {
        try {
            File cachePath = new File(context.getCacheDir(), IMAGES_PATH);
            cachePath.mkdirs();

            FileOutputStream stream = null;
            stream = new FileOutputStream(cachePath + TEMP_IMAGE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            File newFile = new File(cachePath, TEMP_IMAGE);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + FILE_PROVIDER, newFile);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.setType(IMAGE_TYPE);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_title)));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void shareTextInfo(Context context, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share_title)));
    }
}
