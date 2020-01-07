/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.google.android.cameraview.demo.camera.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.cameraview.demo.R;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class SubPrefListAdapter extends RecyclerView.Adapter<SubPrefListAdapter.MyViewHolder>{

    private CamListPreference mPref;
    private Context mContext;
    private PrefItemClickListener mListener;
    private int mTextColor;
    private int mHighlightColor;
    private int mHighlightIndex = -1;

    public interface PrefItemClickListener {
        void onItemClick(String key, String value);
    }


    public SubPrefListAdapter(Context context, CamListPreference pref) {
        mContext = context;
        mPref = pref;
        mTextColor = context.getResources().getColor(R.color.menu_text_color);
        mHighlightColor = context.getResources().getColor(R.color.menu_highlight_color);
    }

    public void setClickListener(PrefItemClickListener listener) {
        mListener = listener;
    }

    public void updateDataSet(CamListPreference pref) {
        mPref = pref;
        notifyDataSetChanged();
    }

    public void updateHighlightIndex(int index, boolean notify) {
        int preIndex = mHighlightIndex;
        mHighlightIndex = index;
        if (notify) {
            notifyItemChanged(preIndex);
            notifyItemChanged(mHighlightIndex);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.menu_item_layout, parent,
                false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.itemText.setText(mPref.getEntries()[position]);
        int color = mHighlightIndex == position ? mHighlightColor : mTextColor;
        holder.itemText.setTextColor(color);

        if (mPref.getEntryIcons() != null) {
            holder.itemIcon.setImageResource(mPref.getEntryIcons()[position]);
            holder.itemIcon.setVisibility(View.VISIBLE);
        } else {
            holder.itemIcon.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(mPref.getKey(),
                            mPref.getEntryValues()[position].toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPref.getEntries().length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView itemIcon;
        TextView itemText;
        MyViewHolder(View itemView) {
            super(itemView);
            itemIcon = itemView.findViewById(R.id.item_icon);
            itemText = itemView.findViewById(R.id.item_text);
        }
    }

}
