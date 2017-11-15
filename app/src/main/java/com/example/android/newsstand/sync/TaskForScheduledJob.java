package com.example.android.newsstand.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.newsstand.data.NewsContract;
import com.example.android.newsstand.utilities.ImageUtils;
import com.example.android.newsstand.utilities.JsonResponse;
import com.example.android.newsstand.utilities.NetworkUtils;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class TaskForScheduledJob {

    private static final String LOG_TAG = TaskForScheduledJob.class.getSimpleName();

    private static Uri API_URI_1;
    private static Uri API_URI_2;
    private static final String API_BASE_URI_STRING = "https://newsapi.org/v1/articles?";
    private static final String SOURCE_PARAM = "source";
    private static final String SOURCE_1 = "the-times-of-india";
    private static final String SOURCE_2 = "the-hindu";
    private static final String SORT_BY_PARAM = "sortBy";
    private static final String SORT_BY = "top";
    private static final String KEY_PARAM = "apiKey";
    private static final String KEY = "fb338d9c04b14656964c8f90ccdafa40";   //// NewsAPI.org key

    public static final String RELOAD_ACTION = "reload-action";

    public static ContentValues[] executeTask(Context context, String action) {

        if (RELOAD_ACTION.equals(action)) {
            return reloadTask(context);
        }
        return null;
    }

    public static ContentValues[] reloadTask(Context context) {

        ContentValues[] values = null;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            API_URI_1 = uriTOI();
            API_URI_2 = uriTH();

            try {
                ContentValues[] valuesFirst = JsonResponse.getJsonResponse(API_URI_1);
                ContentValues[] valuesSecond = JsonResponse.getJsonResponse(API_URI_2);

                values = new ContentValues[valuesFirst.length + valuesSecond.length];

                System.arraycopy(valuesFirst, 0, values, 0, valuesFirst.length);
                System.arraycopy(valuesSecond, 0, values, valuesFirst.length, valuesSecond.length);

                for (int i = 0; i < values.length; i++) {
                    String link = values[i].getAsString(NewsContract.NewsEntry.COLUMN_NEWS_URL_IMAGE);
                    if (link != null) {
                        Bitmap bitmap = NetworkUtils.downloadImage(link);
                        byte[] imageByte = ImageUtils.getImageByte(bitmap);

                        values[i].put(NewsContract.NewsEntry.COLUMN_NEWS_IMAGE, imageByte);
                    }
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            return values;

        } else {
            return null;
        }
    }


    public static Uri uriTOI() {
        return Uri.parse(API_BASE_URI_STRING).buildUpon()
                .appendQueryParameter(SOURCE_PARAM, SOURCE_1)
                .appendQueryParameter(SORT_BY_PARAM, SORT_BY)
                .appendQueryParameter(KEY_PARAM, KEY)
                .build();
    }

    public static Uri uriTH() {
        return Uri.parse(API_BASE_URI_STRING).buildUpon()
                .appendQueryParameter(SOURCE_PARAM, SOURCE_2)
                .appendQueryParameter(SORT_BY_PARAM, SORT_BY)
                .appendQueryParameter(KEY_PARAM, KEY)
                .build();
    }


    ///////  AsyncTask //////////

    public static class backgroundTask extends AsyncTask<Context, Void, ContentValues[]> {

        Context sContext;

        @Override
        protected ContentValues[] doInBackground(Context... contexts) {

            sContext = contexts[0];

            ContentValues[] values = executeTask(sContext, RELOAD_ACTION);
            return values;
        }

        @Override
        protected void onPostExecute(ContentValues[] contentValues) {

            if (contentValues == null) {
                return;
            }

            if (sContext == null) {
                return;
            }

            ContentResolver resolver = sContext.getContentResolver();
            resolver.delete(NewsContract.NewsEntry.CONTENT_URI, null, null);
            resolver.bulkInsert(NewsContract.NewsEntry.CONTENT_URI, contentValues);
        }
    }
}
