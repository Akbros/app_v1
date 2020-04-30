package com.oadevelopers.winbazi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oadevelopers.winbazi.R;
import com.oadevelopers.winbazi.common.Config;
import com.oadevelopers.winbazi.model.SliderPojo;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.ViewHolder> {

    private Context context;
    private List<SliderPojo> sliderPojoList;

    private SliderAdapter.OnItemClickListener mOnItemClickListener;

    public SliderAdapter(List<SliderPojo> sliderPojoList, Context context) {
        super();
        this.sliderPojoList = sliderPojoList;
        this.context = context;
    }

    public void setOnItemClickListener(final SliderAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public SliderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_slider, parent, false);
        SliderAdapter.ViewHolder viewHolder = new SliderAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SliderAdapter.ViewHolder holder, final int position) {
        final SliderPojo sliderPojo = sliderPojoList.get(position);
        //Tools.displayImageOriginal(context, holder.imageIv, Config.FILE_PATH_URL+sliderPojo.getProd_img());
        Picasso.get().load(Config.FILE_PATH_URL + sliderPojo.getProd_img().replace(" ", "%20")).into(holder.imageIv);
        holder.imageIv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, sliderPojoList.get(position), position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return sliderPojoList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, SliderPojo obj, int pos);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageIv;

        public ViewHolder(View itemView) {
            super(itemView);

            imageIv = (ImageView) itemView.findViewById(R.id.imageIv);
        }

    }

}

