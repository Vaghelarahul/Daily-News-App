package com.example.android.newsstand.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class NewsContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.newsstand";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_NEWSTABLE = "newsTable";


    public static final class NewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NEWSTABLE);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_AUTHORITY + "\n" + PATH_NEWSTABLE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_AUTHORITY + "\n" + PATH_NEWSTABLE;

        public static final String TABLE_NAME = "newsTable";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NEWS_AUTHOR = "author";
        public static final String COLUMN_NEWS_TITLE = "newsTitle";
        public static final String COLUMN_NEWS_DESCRIPTION = "description";
        public static final String COLUMN_NEWS_URL = "url";
        public static final String COLUMN_NEWS_URL_IMAGE = "urlToImage";
        public static final String COLUMN_NEWS_IMAGE = "image";
        public static final String COLUMN_NEWS_PUBLISHED = "publishedAt";


    }
}
