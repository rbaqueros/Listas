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

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.baqsoft.listas.contentprovider.ListasContract;

public class CategoryTable {
    // Creation SQL statement.
    public static final String CREATE_TABLE = "create table " + ListasContract.CategoryContract.PATH + "("
            + ListasContract.CategoryContract.COLUMN_ID + " integer primary key, "
            + ListasContract.CategoryContract.COLUMN_POSITION + " integer not null, "
            + ListasContract.CategoryContract.COLUMN_TITLE + " text unique not null, "
            + ListasContract.CategoryContract.COLUMN_NOTE + " text not null"
            + ");";

    // Create table.
    public static void onCreate(SQLiteDatabase database) {
        Log.v("Listas", "Creating table " + ListasContract.CategoryContract.PATH);
        database.execSQL(CREATE_TABLE);
    }

    // Upgrade table
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w("Listas", "Upgrading table " + ListasContract.CategoryContract.PATH + " from version " + oldVersion + " to version " + newVersion);
        Log.w("Listas", "This will destroy all data");
        database.execSQL("DROP TABLE IF EXISTS " + ListasContract.CategoryContract.PATH);
        onCreate(database);
    }
}
