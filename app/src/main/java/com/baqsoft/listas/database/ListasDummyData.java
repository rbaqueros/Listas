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

package com.baqsoft.listas.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.baqsoft.listas.contentprovider.ListasContract;

public class ListasDummyData {
    public static void initializeDB(Context context) {
        ListasDBOpenHelper listasDB = new ListasDBOpenHelper(context);
        SQLiteDatabase sqlDB = listasDB.getWritableDatabase();
        listasDB.onUpgrade(sqlDB, 1, 1);
        ContentValues values;
        Uri result;

        // Create some categories.
        for(int count = 1; count < 4; count++) {
            values = new ContentValues();
            values.put(ListasContract.CategoryContract.COLUMN_POSITION, count);
            values.put(ListasContract.CategoryContract.COLUMN_TITLE, "Category " + count);
            values.put(ListasContract.CategoryContract.COLUMN_NOTE, "Notes for category " + count);
            result = context.getContentResolver().insert(Uri.parse(ListasContract.CategoryContract.URI_STRING), values);
            if (result != null) {
                Log.v("Listas", "Result: " + result.toString());
                Log.v("Listas", "Type: " + context.getContentResolver().getType(result));
            } else {
                Log.v("Listas", "Failed to create category");
            }
        }


        // Create some checklists.
        for(int count = 1; count < 3; count++) {
            values = new ContentValues();
            values.put(ListasContract.ChecklistContract.COLUMN_POSITION, 3 - count);
            values.put(ListasContract.ChecklistContract.COLUMN_CATEGORY_ID, 1);
            values.put(ListasContract.ChecklistContract.COLUMN_TITLE, "Checklist " + count + ", Category 1");
            values.put(ListasContract.CategoryContract.COLUMN_NOTE, "Notes for checklist " + count);
            result = context.getContentResolver().insert(Uri.parse(ListasContract.ChecklistContract.URI_STRING), values);
            if (result != null) {
                Log.v("Listas", "Result: " + result.toString());
            } else {
                Log.v("Listas", "Failed to create checklist");
            }
        }

        for(int count = 1; count < 5; count++) {
            values = new ContentValues();
            values.put(ListasContract.ChecklistContract.COLUMN_POSITION, count);
            values.put(ListasContract.ChecklistContract.COLUMN_CATEGORY_ID, 2);
            values.put(ListasContract.ChecklistContract.COLUMN_TITLE, "Checklist " + count + ", Category 2");
            values.put(ListasContract.CategoryContract.COLUMN_NOTE, "Notes for checklist " + count);
            result = context.getContentResolver().insert(Uri.parse(ListasContract.ChecklistContract.URI_STRING), values);
            if (result != null) {
                Log.v("Listas", "Result: " + result.toString());
            } else {
                Log.v("Listas", "Failed to create checklist");
            }
        }

        for(int count = 1; count < 4; count++) {
            values = new ContentValues();
            values.put(ListasContract.ChecklistContract.COLUMN_POSITION, count);
            values.put(ListasContract.ChecklistContract.COLUMN_CATEGORY_ID, 3);
            values.put(ListasContract.ChecklistContract.COLUMN_TITLE, "Checklist " + count + ", Category 3");
            values.put(ListasContract.CategoryContract.COLUMN_NOTE, "Notes for checklist " + count);
            result = context.getContentResolver().insert(Uri.parse(ListasContract.ChecklistContract.URI_STRING), values);
            if (result != null) {
                Log.v("Listas", "Result: " + result.toString());
            } else {
                Log.v("Listas", "Failed to create checklist");
            }
        }

        // Create some items.
        for(int count = 1; count < 10; count++) {
            for(int count2 = 1; count2< 7; count2++) {
                values = new ContentValues();
                values.put(ListasContract.ItemContract.COLUMN_POSITION, 1);
                values.put(ListasContract.ItemContract.COLUMN_CHECKLIST_ID, count);
                values.put(ListasContract.ItemContract.COLUMN_TITLE, "Checklist " + count + ", Item " + count2);
                values.put(ListasContract.ItemContract.COLUMN_NOTE, "Notes for item " + count2);
                values.put(ListasContract.ItemContract.COLUMN_CHECKED, 0);
                result = context.getContentResolver().insert(Uri.parse(ListasContract.ItemContract.URI_STRING), values);
                if (result != null) {
                    Log.v("Listas", "Result: " + result.toString());
                } else {
                    Log.v("Listas", "Failed to create item");
                }
            }
        }

        // Test update
        values = new ContentValues();
        values.put(ListasContract.ItemContract.COLUMN_POSITION, 1);
        values.put(ListasContract.ItemContract.COLUMN_TITLE, "Renamed item");
        values.put(ListasContract.ItemContract.COLUMN_NOTE, "Notes for renamed item");
        values.put(ListasContract.ItemContract.COLUMN_CHECKED, 1);
        if(context.getContentResolver().update(Uri.parse("content://com.baqsoft.listas.provider/item/47"), values, null, null) > 0) {
            Log.v("Listas", "Item updated");
        } else {
            Log.v("Listas", "Failed to update item");
        }

        values = new ContentValues();
        values.put(ListasContract.ItemContract.COLUMN_CHECKED, 1);
        if(context.getContentResolver().update(Uri.parse("content://com.baqsoft.listas.provider/item/46"), values, null, null) > 0) {
            Log.v("Listas", "Item updated");
        } else {
            Log.v("Listas", "Failed to update item");
        }
    }
}
