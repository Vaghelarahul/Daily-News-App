package com.example.android.newsstand.utilities;


import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.newsstand.data.NewsContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonResponse {

    private static final String LOG_TAG = JsonResponse.class.getSimpleName();

    public static ContentValues[] getJsonResponse(Uri uri){

        String jsonResponse = NetworkUtils.getHttpJsonResponse(uri);
        return parseJson(jsonResponse);
    }


    private static ContentValues[] parseJson(String jsonResponse) {

        ContentValues[] valuesInArray = null;
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray articlesArray = root.getJSONArray("articles");

            if(articlesArray.length() == 0){
                return null;
            }

            valuesInArray = new ContentValues[articlesArray.length()];

            for (int i = 0; i < articlesArray.length(); i++) {

                JSONObject arrayObject = articlesArray.getJSONObject(i);

                String author = arrayObject.getString("author");
                String title = arrayObject.getString("title");
                String description = arrayObject.getString("description");
                String url = arrayObject.getString("url");
                String urlToImage = arrayObject.getString("urlToImage");
                String publishedAt = arrayObject.getString("publishedAt");

                ContentValues values = new ContentValues();
                values.put(NewsContract.NewsEntry.COLUMN_NEWS_AUTHOR, author);
                values.put(NewsContract.NewsEntry.COLUMN_NEWS_TITLE, title);
                values.put(NewsContract.NewsEntry.COLUMN_NEWS_DESCRIPTION, description);
                values.put(NewsContract.NewsEntry.COLUMN_NEWS_URL, url);
                values.put(NewsContract.NewsEntry.COLUMN_NEWS_URL_IMAGE, urlToImage);
                values.put(NewsContract.NewsEntry.COLUMN_NEWS_PUBLISHED, publishedAt);

                valuesInArray[i] = values;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return valuesInArray;
    }
}
