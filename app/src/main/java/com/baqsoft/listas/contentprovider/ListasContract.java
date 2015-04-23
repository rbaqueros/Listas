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

import android.content.ContentResolver;
import android.provider.BaseColumns;

public class ListasContract {

    //Category contract class
    public static final class CategoryContract implements BaseColumns {
        // Table name.
        public static final String PATH = "category";

        // Uri String
        public static final String URI_STRING = SCHEME + AUTHORITY +"/" + PATH;

        // MIME Types
        public static final String CATEGORY_TABLE_MIME_TYPE = TABLE_MIME_TYPE_BASE + PATH;
        public static final String CATEGORY_ROW_MIME_TYPE = ROW_MIME_TYPE_BASE + PATH;

        // Columns.
        public static final String COLUMN_ID = _ID;
        public static final String COLUMN_POSITION = "position";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_NOTE = "note";
    }

    // Checklist contract class
    public static final class ChecklistContract implements BaseColumns {
        // Table name.
        public static final String PATH = "checklist";

        // Uri String
        public static final String URI_STRING = SCHEME + AUTHORITY +"/" + PATH;

        // MIME Types
        public static final String CHECKLIST_TABLE_MIME_TYPE = TABLE_MIME_TYPE_BASE + PATH;
        public static final String CHECKLIST_ROW_MIME_TYPE = ROW_MIME_TYPE_BASE + PATH;

        // Columns.
        public static final String COLUMN_ID = _ID;
        public static final String COLUMN_POSITION = "position";
        public static final String COLUMN_CATEGORY_ID = "category_id";
        public static final String COLUMN_PARENT_ID = "parent_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_NOTE = "note";
    }

    // Item contract class
    public static final class ItemContract implements BaseColumns {
        // Table name.
        public static final String PATH = "item";

        // Uri String
        public static final String URI_STRING = SCHEME + AUTHORITY +"/" + PATH;

        // MIME Types
        public static final String ITEM_TABLE_MIME_TYPE = TABLE_MIME_TYPE_BASE + PATH;
        public static final String ITEM_ROW_MIME_TYPE = ROW_MIME_TYPE_BASE + PATH;

        // Columns.
        public static final String COLUMN_ID = _ID;
        public static final String COLUMN_POSITION = "position";
        public static final String COLUMN_CHECKLIST_ID = "checklist_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_NOTE = "note";
        public static final String COLUMN_CHECKED = "checked";
    }

    // Nav drawer list contract class
    public static final class NavListContract implements BaseColumns {
        // Table name.
        public static final String PATH = "nav_list";

        // Uri string
        public static final String URI_STRING = SCHEME + AUTHORITY +"/" + PATH;

        // MIME Types
        public static final String NAV_LIST_TABLE_MIME_TYPE = TABLE_MIME_TYPE_BASE +PATH;

        // Columns.
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_ID = _ID;
        public static final String COLUMN_TITLE = "title";
    }

    // Category-Checklist list contract class
    public static final class CatCheckContract implements BaseColumns {
        // Table name
        public static final String PATH = "cat_check_list";

        // Uri string
        public static final String URI_STRING = SCHEME + AUTHORITY + "/" + PATH;

        // MIME types
        public static final String CAT_CHECK_LIST_MIME_TYPE = TABLE_MIME_TYPE_BASE + PATH;

        // Columns.
        public static final String COLUMN_ID = _ID;
        public static final String COLUMN_TITLE = "title";
    }

    // Authority
    public static final String AUTHORITY = "com.baqsoft.listas.provider";

    // Scheme
    public static final String SCHEME = "content://";

    // MIME types
    private static final String TABLE_MIME_TYPE_BASE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.baqsoft.listas.";
    private static final String ROW_MIME_TYPE_BASE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.baqsoft.listas.";

    /**
     * Type identifiers.
     */
    public static final int TYPE_CATEGORY = 0;
    public static final int TYPE_CHECKLIST = 1;

}
