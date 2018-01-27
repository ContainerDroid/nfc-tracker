package com.eden.apps.nfctracker;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CategoriesProvider extends ContentProvider{

    private CategoriesDatabaseHelper mCategoriesDatabaseHelper;

    public static final int TABLE = 1;
    public static final int ROW = 2;

    public static final String uriRoot = "com.eden.apps.nfctracker";
    public static final String tableUri = "content://" + uriRoot + "/categories";

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        uriMatcher.addURI(uriRoot, "categories", TABLE);
        uriMatcher.addURI(uriRoot, "categories/#", TABLE);
    }

    @Override
    public boolean onCreate() {
        mCategoriesDatabaseHelper = new CategoriesDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables("categories");

        switch (uriMatcher.match(uri)) {
            case TABLE:
                break;
            case ROW:
                queryBuilder.appendWhereEscapeString("id = " + uri.getLastPathSegment());
                break;
            default:
                throw  new IllegalArgumentException("Wrong URI: " + uri);
        }

        SQLiteDatabase db = mCategoriesDatabaseHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if (uriMatcher.match(uri) != TABLE) {
            throw new UnsupportedOperationException("Should insert to categories table");
        }

        SQLiteDatabase db = mCategoriesDatabaseHelper.getWritableDatabase();
        long rowId = db.insert("categories", null, contentValues);
        if (rowId >= 0) {

            Uri noteUri = ContentUris.withAppendedId(Uri.parse(tableUri), rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        } else {
            throw new UnsupportedOperationException("Failed to insert row into " + uri);
        }
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
