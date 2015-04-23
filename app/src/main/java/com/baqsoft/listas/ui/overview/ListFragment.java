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

package com.baqsoft.listas.ui.overview;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;

import com.baqsoft.listas.R;
import com.baqsoft.listas.contentprovider.ListasContract;
import com.baqsoft.listas.ui.click_processor.ChecklistItemClickData;
import com.baqsoft.listas.ui.click_processor.RecyclerViewClickProcessor;
import com.baqsoft.listas.ui.edit.EditItemActivity;

public class ListFragment extends Fragment implements RecyclerViewClickProcessor.ClickProcessor,
        LoaderManager.LoaderCallbacks<Cursor> {
    private ChecklistItemListAdapter mAdapter;

    // Projections.
    private static final String[] ItemProjection = {ListasContract.ItemContract._ID,
            ListasContract.ItemContract.COLUMN_TITLE, ListasContract.ItemContract.COLUMN_NOTE,
            ListasContract.ItemContract.COLUMN_CHECKED};

    // Selected checklist id.
    private long mChecklistId;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        // Setup recyclerview.
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ChecklistItemListAdapter(getActivity());
        mAdapter.setClickProcessor(this);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mChecklistId = 1;
        getLoaderManager().initLoader(0, null, this);
    }
    @Override
    public void processClick(RecyclerViewClickProcessor.ClickParams clickParams) {
        ChecklistItemClickData clickData = (ChecklistItemClickData) clickParams.clickData;
        if (clickData.eventType == ChecklistItemClickData.typeToggle) {
            String query = ListasContract.ItemContract.URI_STRING + "/" + clickData.itemId;
            String[] projection = {ListasContract.ItemContract.COLUMN_CHECKED};
            Cursor cursor = getActivity().getContentResolver().query(Uri.parse(query),projection,null,null,null);
            cursor.moveToFirst();
            ContentValues values = new ContentValues();
            if(cursor.getInt(0) > 0) {
                values.put(ListasContract.ItemContract.COLUMN_CHECKED, 0);
            } else {
                values.put(ListasContract.ItemContract.COLUMN_CHECKED, 1);
            }
            cursor.close();
            getActivity().getContentResolver().update(Uri.parse(query), values, null, null);
            reloadChecklist();
        } else {
            Intent i = new Intent(getActivity(), EditItemActivity.class);
            i.putExtra("type", "item");
            i.putExtra("dataId", clickData.itemId);
            i.putExtra("edit", true);
            startActivity(i);
        }
    }

    public void setChecklist(long checklist) {
        mChecklistId = checklist;
        reloadChecklist();
    }

    private void reloadChecklist() {
        Loader<Cursor> loader = getLoaderManager().getLoader(0);
        CursorLoader cursorLoader = (CursorLoader) loader;
        cursorLoader.setSelectionArgs(new String[]{Long.toString(mChecklistId)});
        if(cursorLoader.isStarted()) {
            cursorLoader.forceLoad();
        }
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = ListasContract.ItemContract.COLUMN_CHECKLIST_ID + "=?";
        String[] selectionArgs = {Long.toString(mChecklistId)};
        return new CursorLoader(getActivity(), Uri.parse(ListasContract.ItemContract.URI_STRING),
                ItemProjection, selection, selectionArgs, null);
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }
}
