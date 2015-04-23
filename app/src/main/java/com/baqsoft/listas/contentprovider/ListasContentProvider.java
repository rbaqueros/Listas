/*
 * Copyright (c) 2015 Rafael Baquero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baqsoft.listas.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.baqsoft.listas.database.ListasDBOpenHelper;

public class ListasContentProvider extends ContentProvider {
    // Database
    private ListasDBOpenHelper mOpenHelper;

    // URI Identifiers.
    private static final int CATEGORY = 1;
    private static final int CATEGORY_ID = 2;
    private static final int CHECKLIST = 3;
    private static final int CHECKLIST_ID = 4;
    private static final int ITEM = 5;
    private static final int ITEM_ID = 6;
    private static final int NAV_LIST = 7;
    private static final int CAT_CHECK = 8;


    // URI matcher.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ListasContract.AUTHORITY, ListasContract.CategoryContract.PATH, CATEGORY);
        sUriMatcher.addURI(ListasContract.AUTHORITY, ListasContract.CategoryContract.PATH + "/#", CATEGORY_ID);
        sUriMatcher.addURI(ListasContract.AUTHORITY, ListasContract.ChecklistContract.PATH, CHECKLIST);
        sUriMatcher.addURI(ListasContract.AUTHORITY, ListasContract.ChecklistContract.PATH + "/#", CHECKLIST_ID);
        sUriMatcher.addURI(ListasContract.AUTHORITY, ListasContract.ItemContract.PATH, ITEM);
        sUriMatcher.addURI(ListasContract.AUTHORITY, ListasContract.ItemContract.PATH + "/#", ITEM_ID);
        sUriMatcher.addURI(ListasContract.AUTHORITY, ListasContract.NavListContract.PATH, NAV_LIST);
        sUriMatcher.addURI(ListasContract.AUTHORITY, ListasContract.CatCheckContract.PATH, CAT_CHECK);
    }

    // Nav list query.
    /*
    select _id, type, case when type=0 then cat_title else check_title end as title from
    (select 0 as type, _id, title as cat_title, '' as check_title from category where _id in
    (select category_id from checklist) union select 1 as type, checklist._id as _id,
    category.title as cat_title, checklist.title as check_title from category inner join checklist
    on category._id = checklist.category_id order by cat_title, type, check_title);
     */
    private static final String CAT_TITLE_TAG = "cat_title";
    private static final String CHECK_TITLE_TAG = "check_title";
    private static final String NAV_ID_TAG = ListasContract.NavListContract.COLUMN_ID;
    private static final String NAV_TYPE_TAG = ListasContract.NavListContract.COLUMN_TYPE;
    private static final String NAV_TITLE_TAG = ListasContract.NavListContract.COLUMN_TITLE;
    private static final String CATEGORY_TAG = ListasContract.CategoryContract.PATH;
    private static final String CATEGORY_ID_TAG = ListasContract.CategoryContract.COLUMN_ID;
    private static final String CATEGORY_TITLE_TAG = ListasContract.CategoryContract.COLUMN_TITLE;
    private static final String CHECKLIST_TAG = ListasContract.ChecklistContract.PATH;
    private static final String CHECKLIST_ID_TAG = ListasContract.ChecklistContract.COLUMN_ID;
    private static final String CHECKLIST_TITLE_TAG = ListasContract.ChecklistContract.COLUMN_TITLE;
    private static final String CHECKLIST_CATEGORY_ID_TAG = ListasContract.ChecklistContract.COLUMN_CATEGORY_ID;

    public static String NAV_QUERY = "select " + NAV_ID_TAG + ", " + NAV_TYPE_TAG + ", "
            + "case when " + NAV_TYPE_TAG + "=0 then " + CAT_TITLE_TAG + " else "
            + CHECK_TITLE_TAG + " end as " + NAV_TITLE_TAG + " from (select 0 as "
            + NAV_TYPE_TAG + ", " + CATEGORY_ID_TAG + ", " + CATEGORY_TITLE_TAG + " as "
            + CAT_TITLE_TAG + ", '' as " + CHECK_TITLE_TAG + " from " + CATEGORY_TAG + " where "
            + CATEGORY_ID_TAG + " in (select " + CHECKLIST_CATEGORY_ID_TAG + " from "
            + CHECKLIST_TAG + ") union select 1 as " + NAV_TYPE_TAG + ", " + CHECKLIST_TAG + "."
            + CHECKLIST_ID_TAG + " as " + NAV_ID_TAG + ", " + CATEGORY_TAG + "."
            + CATEGORY_TITLE_TAG + " as " + CAT_TITLE_TAG + ", " + CHECKLIST_TAG + "."
            + CHECKLIST_TITLE_TAG + " as " + CHECK_TITLE_TAG + " from " + CATEGORY_TAG + " inner join "
            + CHECKLIST_TAG + " on " + CATEGORY_TAG + "." + CATEGORY_ID_TAG + "="
            + CHECKLIST_TAG + "." + CHECKLIST_CATEGORY_ID_TAG + " order by " + CAT_TITLE_TAG + ", "
            + NAV_TYPE_TAG + ", " + CHECK_TITLE_TAG +")";

    // Category - checklist query.
    /*
    select checklist._id as _id, category.title || ' - ' || checklist.title as title from
    category inner join checklist on category._id=checklist.category_id order by title;
    */
    private static final String CAT_CHECK_ID_TAG = ListasContract.CatCheckContract.COLUMN_ID;
    private static final String CAT_CHECK_TITLE_TAG = ListasContract.CatCheckContract.COLUMN_TITLE;

    private static final String CAT_CHECK_QUERY = "select " + CHECKLIST_TAG + "." + CHECKLIST_ID_TAG
            + " as " + CAT_CHECK_ID_TAG + ", " + CATEGORY_TAG + "." + CATEGORY_TITLE_TAG
            + " || ' - ' || " + CHECKLIST_TAG + "." + CHECKLIST_TITLE_TAG + " as "
            + CAT_CHECK_TITLE_TAG + " from " + CATEGORY_TAG + " inner join " + CHECKLIST_TAG
            + " on " + CATEGORY_TAG + "." + CATEGORY_ID_TAG + "=" + CHECKLIST_TAG + "."
            + CHECKLIST_CATEGORY_ID_TAG + " order by " + CAT_CHECK_TITLE_TAG;

    /**
     * Provider startup.
     * @return Allways true.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new ListasDBOpenHelper(getContext());
        return true;
    }

    /**
     * Perform a search and return the result to the caller
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor result;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setStrict(true);
        sortOrder = TextUtils.isEmpty(sortOrder) ? "title ASC" : sortOrder;

        // Choose the table to query and the query type based on the given URI.
        switch (sUriMatcher.match(uri)) {
            case CATEGORY:
                queryBuilder.setTables(ListasContract.CategoryContract.PATH);
                result = queryBuilder.query(mOpenHelper.getWritableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CATEGORY_ID:
                queryBuilder.setTables(ListasContract.CategoryContract.PATH);
                String idSelection = ListasContract.CategoryContract._ID + "=" + uri.getLastPathSegment();
                result = queryBuilder.query(mOpenHelper.getWritableDatabase(), projection, idSelection, null, null, null, null);
                break;
            case CHECKLIST:
                queryBuilder.setTables(ListasContract.ChecklistContract.PATH);
                result = queryBuilder.query(mOpenHelper.getWritableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CHECKLIST_ID:
                queryBuilder.setTables(ListasContract.ChecklistContract.PATH);
                idSelection = ListasContract.ChecklistContract._ID + "=" + uri.getLastPathSegment();
                result = queryBuilder.query(mOpenHelper.getWritableDatabase(), projection, idSelection, null, null, null, null);
                break;
            case ITEM:
                queryBuilder.setTables(ListasContract.ItemContract.PATH);
                result = queryBuilder.query(mOpenHelper.getWritableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM_ID:
                queryBuilder.setTables(ListasContract.ItemContract.PATH);
                idSelection = ListasContract.ItemContract.COLUMN_ID + "=" + uri.getLastPathSegment();
                result = queryBuilder.query(mOpenHelper.getWritableDatabase(), projection, idSelection, null, null, null, null);
                break;
            case NAV_LIST:
                result = mOpenHelper.getWritableDatabase().rawQuery(NAV_QUERY, null);
                break;
            case CAT_CHECK:
                result = mOpenHelper.getWritableDatabase().rawQuery(CAT_CHECK_QUERY, null);
                break;
            default:
                result = null;
                break;
        }

        return result;
    }

    /**
     * Return the MIME type for the given URI.
     * @param uri    URI
     * @return  MIME type
     */
    @Override
    public String getType(Uri uri) {
        // Choose the table to query and the query type based on the given URI.
        switch (sUriMatcher.match(uri)) {
            case CATEGORY:
                return ListasContract.CategoryContract.CATEGORY_TABLE_MIME_TYPE;
            case CATEGORY_ID:
                return ListasContract.CategoryContract.CATEGORY_ROW_MIME_TYPE;
            case CHECKLIST:
                return ListasContract.ChecklistContract.CHECKLIST_TABLE_MIME_TYPE;
            case CHECKLIST_ID:
                return ListasContract.ChecklistContract.CHECKLIST_ROW_MIME_TYPE;
            case ITEM:
                return ListasContract.ItemContract.ITEM_TABLE_MIME_TYPE;
            case ITEM_ID:
                return ListasContract.ItemContract.ITEM_ROW_MIME_TYPE;
            case NAV_LIST:
                return ListasContract.NavListContract.NAV_LIST_TABLE_MIME_TYPE;
            case CAT_CHECK:
                return ListasContract.CatCheckContract.CAT_CHECK_LIST_MIME_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri resultUri;
        long insertResult;
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();

        // Choose the table to query and the query type based on the given URI.
        switch (sUriMatcher.match(uri)) {
            case CATEGORY:
                // Check if values contain id. If they do throw exception.
                if(values.containsKey(ListasContract.CategoryContract.COLUMN_ID)) {
                    throw new IllegalArgumentException("Id cannot be assigned explicitly");
                }

                // Insert category.
                insertResult = database.insert(ListasContract.CategoryContract.PATH, null, values);
                resultUri = insertResult >= 0 ? Uri.parse(ListasContract.CategoryContract.URI_STRING + "/" + insertResult) : null;
                break;
            case CATEGORY_ID:
                resultUri = null;
                break;
            case CHECKLIST:// Check if values contain id. If they do throw exception.
                if(values.containsKey(ListasContract.ChecklistContract.COLUMN_ID)) {
                    throw new IllegalArgumentException("Id cannot be assigned explicitly");
                }

                insertResult = database.insert(ListasContract.ChecklistContract.PATH, null, values);
                resultUri = insertResult >= 0 ? Uri.parse(ListasContract.ChecklistContract.URI_STRING + "/" + insertResult) : null;
                break;
            case CHECKLIST_ID:
                resultUri = null;
                break;
            case ITEM:
                // Check if values contain id. If they do throw exception.
                if(values.containsKey(ListasContract.ItemContract.COLUMN_ID)) {
                    throw new IllegalArgumentException("Id cannot be assigned explicitly");
                }
                insertResult = database.insert(ListasContract.ItemContract.PATH, null, values);
                resultUri = insertResult >= 0 ? Uri.parse(ListasContract.ItemContract.URI_STRING + "/" + insertResult) : null;
                break;
            case ITEM_ID:
                resultUri = null;
                break;
            default:
                resultUri = null;
                break;
        }

        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleteResult;
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        String idSelection;
        Cursor result;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Choose the table to query and the query type based on the given URI.
        switch (sUriMatcher.match(uri)) {
            case CATEGORY:
                deleteResult = 0;
                break;
            case CATEGORY_ID:
                // Check if any checklists are using the category.
                queryBuilder.setTables(ListasContract.ChecklistContract.PATH);
                idSelection = ListasContract.ChecklistContract.COLUMN_CATEGORY_ID + "=" + uri.getLastPathSegment();
                result = queryBuilder.query(mOpenHelper.getWritableDatabase(),
                        new String[] {ListasContract.ChecklistContract.COLUMN_ID},
                        idSelection, null, null, null, null);
                if(result.getCount() == 0) {
                    idSelection = ListasContract.CategoryContract.COLUMN_ID + "=" + uri.getLastPathSegment();
                    deleteResult = database.delete(ListasContract.CategoryContract.PATH, idSelection, null);
                } else {
                    deleteResult = 0;
                }
                break;
            case CHECKLIST:
                deleteResult = 0;
                break;
            case CHECKLIST_ID:
                // Check if any items are using the checklist.
                queryBuilder.setTables(ListasContract.ItemContract.PATH);
                idSelection = ListasContract.ItemContract.COLUMN_CHECKLIST_ID + "=" + uri.getLastPathSegment();
                result = queryBuilder.query(mOpenHelper.getWritableDatabase(),
                        new String[] {ListasContract.ItemContract.COLUMN_ID},
                        idSelection, null, null, null, null);
                if(result.getCount() == 0) {
                    idSelection = ListasContract.ChecklistContract.COLUMN_ID + "=" + uri.getLastPathSegment();
                    deleteResult = database.delete(ListasContract.ChecklistContract.PATH, idSelection, null);
                } else {
                    deleteResult = 0;
                }
                break;
            case ITEM:
                deleteResult = 0;
                break;
            case ITEM_ID:
                idSelection = ListasContract.ItemContract._ID + "=" + uri.getLastPathSegment();
                deleteResult = database.delete(ListasContract.ItemContract.PATH, idSelection, null);
                break;
            default:
                deleteResult = 0;
                break;
        }
        return deleteResult;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String idSelection;
        int insertResult;
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();

        // Choose the table to query and the query type based on the given URI.
        switch (sUriMatcher.match(uri)) {
            case CATEGORY:
                insertResult = 0;
                break;
            case CATEGORY_ID:
                // Check if values contain id. If they do throw exception.
                if(values.containsKey(ListasContract.CategoryContract.COLUMN_ID)) {
                    throw new IllegalArgumentException("Id cannot be assigned explicitly");
                }

                // Update category.
                idSelection = ListasContract.CategoryContract.COLUMN_ID + "=" + uri.getLastPathSegment();
                insertResult = database.update(ListasContract.CategoryContract.PATH, values, idSelection, null);
                break;
            case CHECKLIST:
                insertResult = 0;
                break;
            case CHECKLIST_ID:
                // Check if values contain id. If they do throw exception.
                if(values.containsKey(ListasContract.ChecklistContract.COLUMN_ID)) {
                    throw new IllegalArgumentException("Id cannot be assigned explicitly");
                }

                // Update checklist.
                idSelection = ListasContract.CategoryContract.COLUMN_ID + "=" + uri.getLastPathSegment();
                insertResult = database.update(ListasContract.ChecklistContract.PATH, values, idSelection, null);
                break;
            case ITEM:
                insertResult = 0;
                break;
            case ITEM_ID:
                // Check if values contain id. If they do throw exception.
                if(values.containsKey(ListasContract.ItemContract.COLUMN_ID)) {
                    throw new IllegalArgumentException("Id cannot be assigned explicitly");
                }

                // Update item.
                idSelection = ListasContract.ItemContract.COLUMN_ID + "=" + uri.getLastPathSegment();
                insertResult = database.update(ListasContract.ItemContract.PATH, values, idSelection, null);
                break;
            default:
                insertResult = 0;
                break;
        }

        return insertResult;
    }
}
