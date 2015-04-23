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

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baqsoft.listas.R;
import com.baqsoft.listas.contentprovider.ListasContract;
import com.baqsoft.listas.ui.click_processor.ChecklistItemClickData;
import com.baqsoft.listas.ui.click_processor.RecyclerViewClickProcessor;

public class ChecklistItemListAdapter extends RecyclerView.Adapter<ChecklistItemListAdapter.ViewHolder>  {
    private Context context;
    private Cursor mCursor;
    private static RecyclerViewClickProcessor.ClickProcessor clickProcessor;

    public static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public TextView mTitleTextView;
        public TextView mNotesTextView;
        public ImageView mAvatar;
        public long checklistItemId;

        public ViewHolder(View v) {
            super(v);

            // Fill view data
            mTitleTextView = (TextView) v.findViewById(R.id.title_line_text);
            mNotesTextView = (TextView) v.findViewById(R.id.notes_line_text);
            mAvatar = (ImageView) v.findViewById(R.id.item_avatar);

            // Set click listener.
            v.findViewById(R.id.text_layout).setOnClickListener(this);
            mAvatar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            processViewClick(this, v);
        }
    }

    public ChecklistItemListAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setClickProcessor(RecyclerViewClickProcessor.ClickProcessor processor) {
        clickProcessor = processor;
    }

    public void changeCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int idIndex = mCursor.getColumnIndex(ListasContract.ItemContract.COLUMN_ID);
        int titleIndex = mCursor.getColumnIndex(ListasContract.ItemContract.COLUMN_TITLE);
        int noteIndex = mCursor.getColumnIndex(ListasContract.ItemContract.COLUMN_NOTE);
        int checkedIndex = mCursor.getColumnIndex(ListasContract.ItemContract.COLUMN_CHECKED);
        mCursor.moveToPosition(position);
        holder.mTitleTextView.setText(mCursor.getString(titleIndex));
        holder.mNotesTextView.setText(mCursor.getString(noteIndex));
        holder.checklistItemId = mCursor.getLong(idIndex);
        if(mCursor.getInt(checkedIndex) > 0) {
            holder.mAvatar.setImageResource(R.drawable.checkmark_avatar);
            holder.mTitleTextView.setTextColor(context.getResources().getColor(R.color.black_secondary_text));
            holder.mTitleTextView.setPaintFlags(holder.mTitleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mNotesTextView.setPaintFlags(holder.mNotesTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.mAvatar.setImageResource(R.drawable.checkbox_avatar);
            holder.mTitleTextView.setTextColor(context.getResources().getColor(R.color.black_primary_text));
            holder.mTitleTextView.setPaintFlags(holder.mTitleTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.mNotesTextView.setPaintFlags(holder.mNotesTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    // Create new view
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);

//        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
//        return viewHolder;
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public int getItemCount() {
        return mCursor != null ?  mCursor.getCount() : 0;
    }

    private static void processViewClick(RecyclerView.ViewHolder viewHolder, View v) {
        ViewHolder vh = (ViewHolder) viewHolder;
        ChecklistItemClickData checklistItemClickData =
                new ChecklistItemClickData(vh.checklistItemId, ChecklistItemClickData.typeEdit);

        if(v == vh.mAvatar) {
            checklistItemClickData.eventType = ChecklistItemClickData.typeToggle;
        } else {
            checklistItemClickData.eventType = ChecklistItemClickData.typeEdit;
        }

        if(clickProcessor != null) {
            clickProcessor.processClick(
                    new RecyclerViewClickProcessor.ClickParams(vh.getLayoutPosition(), checklistItemClickData));
        }
    }
}

