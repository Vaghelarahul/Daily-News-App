package com.example.android.newsstand.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.newsstand.data.NewsContract.NewsEntry;

public class NewsProvider extends ContentProvider {

    private static final String LOG_TAG = NewsProvider.class.getSimpleName();

    private NewsDbOpener mDbOpener = null;
    private SQLiteDatabase mDatabase;

    private static final int NEWS_URI = 200;
    private static final int NEWS_URI_WITH_ID = 201;


//    static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//
//    static {
//        sUriMatcher.addURI(NewsContract.CONTENT_AUTHORITY, NewsContract.PATH_NEWSTABLE, NEWS_URI);
//        sUriMatcher.addURI(NewsContract.CONTENT_AUTHORITY, NewsContract.PATH_NEWSTABLE + "/#", NEWS_URI_WITH_ID);
//    }


    private UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        String authority = NewsContract.CONTENT_AUTHORITY;
        String path = NewsContract.PATH_NEWSTABLE;

        matcher.addURI(authority, path, NEWS_URI);
        matcher.addURI(authority, path + "/#", NEWS_URI_WITH_ID);

        return matcher;
    }


    public NewsProvider() {

    }

    @Override
    public boolean onCreate() {
        mDbOpener = new NewsDbOpener(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mDbOpener.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case NEWS_URI:
                cursor = db.query(NewsEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);
                break;

            case NEWS_URI_WITH_ID:
                String select = NewsEntry._ID + "=?";
                String[] selectionArguments = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(NewsEntry.TABLE_NAME, projection, select, selectionArguments, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)) {
            case NEWS_URI:
                return NewsEntry.CONTENT_LIST_TYPE;

            case NEWS_URI_WITH_ID:
                return NewsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);

        }
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        mDatabase = mDbOpener.getWritableDatabase();

        if (values == null) {
            return 0;
        }
        switch (sUriMatcher.match(uri)) {
            case NEWS_URI:

                mDatabase.beginTransaction();
                int rowsInserted = 0;

                for (ContentValues value : values) {

                    long id = mDatabase.insert(NewsEntry.TABLE_NAME, null, value);
                    if (id != -1) {
                        rowsInserted++;
                    }
                }

                mDatabase.setTransactionSuccessful();
                mDatabase.endTransaction();

                getContext().getContentResolver().notifyChange(uri, null);
                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        mDatabase = mDbOpener.getWritableDatabase();

        int rowsDeleted;
        switch (sUriMatcher.match(uri)) {
            case NEWS_URI:
                rowsDeleted =  mDatabase.delete(NewsEntry.TABLE_NAME, null, null);
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;

            default:
                throw new IllegalArgumentException("unknown uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
