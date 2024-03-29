package com.example.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyProvider extends ContentProvider {
    private static final UriMatcher mMatcher;

    static {
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // 注册 uri
        mMatcher.addURI(Constant.AUTOHORITY, Constant.TABLE_NAME, Constant.ITEM);
        mMatcher.addURI(Constant.AUTOHORITY, Constant.TABLE_NAME + "/#", Constant.ITEM_ID);
    }

    DBHelper mDbHelper = null;
    SQLiteDatabase db = null;

    @Override
    public boolean onCreate() {
        mDbHelper = new DBHelper(getContext());
        db = mDbHelper.getReadableDatabase();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor c = null;
        switch (mMatcher.match(uri)) {
            case Constant.ITEM:
                c = db.query(Constant.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case Constant.ITEM_ID:
                c = db.query(Constant.TABLE_NAME, projection, Constant.COLUMN_ID + "=" + uri.getLastPathSegment(), selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mMatcher.match(uri)) {
            case Constant.ITEM:
                return Constant.CONTENT_TYPE;
            case Constant.ITEM_ID:
                return Constant.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long rowId;
        if (mMatcher.match(uri) != Constant.ITEM) {
            throw new IllegalArgumentException("Unknown URI" + uri);
        }
        rowId = db.insert(Constant.TABLE_NAME, null, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(Constant.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
