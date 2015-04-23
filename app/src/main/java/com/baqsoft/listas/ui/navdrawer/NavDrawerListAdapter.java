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

package com.baqsoft.listas.ui.navdrawer;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baqsoft.listas.ui.click_processor.ChecklistClickData;
import com.baqsoft.listas.R;
import com.baqsoft.listas.contentprovider.ListasContract;
import com.baqsoft.listas.ui.click_processor.RecyclerViewClickProcessor;

public class NavDrawerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Cursor mCursor;
    private static RecyclerViewClickProcessor.ClickProcessor clickProcessor;

    /**
     * Checklist view holder.
     */
    public static class ChecklistViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public TextView mTitleTextView;
        public ImageView mIcon;
        public long mChecklistId;

        public ChecklistViewHolder(View v) {
            super(v);
            mTitleTextView = (TextView) v.findViewById(R.id.checklist_title);

            mIcon = (ImageView) v.findViewById(R.id.checklist_icon);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            processViewClick(this, v);
        }
    }

    /**
     * Category view holder.
     */
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitleTextView;

        public CategoryViewHolder(View v) {
            super(v);
            mTitleTextView = (TextView) v.findViewById(R.id.category_title);
        }
    }

    public void setClickProcessor(RecyclerViewClickProcessor.ClickProcessor processor) {
        clickProcessor = processor;
    }

    public void changeCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        Log.v("Listas", "Binding item at position " + position + " with view holder of type " + holder.getItemViewType());
        mCursor.moveToPosition(position);
        int idIndex = mCursor.getColumnIndex(ListasContract.NavListContract.COLUMN_ID);
        int titleIndex = mCursor.getColumnIndex(ListasContract.NavListContract.COLUMN_TITLE);
        switch (holder.getItemViewType()) {
            case ListasContract.TYPE_CATEGORY:
                CategoryViewHolder catViewHolder = (CategoryViewHolder) holder;
                catViewHolder.mTitleTextView.setText(mCursor.getString(titleIndex));
                break;
            default:
                ChecklistViewHolder checkViewHolder = (ChecklistViewHolder) holder;
                checkViewHolder.mTitleTextView.setText(mCursor.getString(titleIndex));
                checkViewHolder.mIcon.setImageResource(R.drawable.ic_list);
                checkViewHolder.mChecklistId = mCursor.getLong(idIndex);
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View itemLayoutView;

        switch (viewType) {
            case ListasContract.TYPE_CATEGORY:
                itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.nd_category_layout, parent, false);
                viewHolder = new CategoryViewHolder(itemLayoutView);
                break;
            default:
                itemLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.nd_checklist_layout, parent, false);
                viewHolder = new ChecklistViewHolder(itemLayoutView);
                break;
        }

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mCursor != null ?  mCursor.getCount() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        mCursor.moveToPosition(position);
        int typeIndex = mCursor.getColumnIndex(ListasContract.NavListContract.COLUMN_TYPE);
        return mCursor.getInt(typeIndex) == ListasContract.TYPE_CATEGORY ? ListasContract.TYPE_CATEGORY : ListasContract.TYPE_CHECKLIST;
    }

    /**
     * Process a click event on a view.
     * @param viewHolder    ViewHolder of the clicked view.
     * @param v             Clicked view.
     */
    private static void processViewClick(RecyclerView.ViewHolder viewHolder, View v) {
        ChecklistViewHolder  vh = (ChecklistViewHolder) viewHolder;
        ChecklistClickData clickData = new ChecklistClickData(vh.mChecklistId, vh.mTitleTextView.getText().toString());
        if(clickProcessor != null) {
            clickProcessor.processClick(new RecyclerViewClickProcessor.ClickParams(vh.getLayoutPosition(), clickData));
        }
    }
}
