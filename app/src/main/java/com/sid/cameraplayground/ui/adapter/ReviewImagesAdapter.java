package com.sid.cameraplayground.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sid.cameraplayground.R;
import com.sid.cameraplayground.ui.fragment.ReviewFragment;
import com.sid.cameraplayground.ui.activity.MainActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sid on 2/7/18.
 */

public class ReviewImagesAdapter extends RecyclerView.Adapter<ReviewImagesAdapter.ViewH> {
    private final int BITMAP_IMAGE = 0;
    private final int ADD_IMAGE = 1;
    Context context;
    ReviewFragment reviewFragment;

    List<Bitmap> bitmapList;

    @Override
    public int getItemViewType(int position) {
        if (bitmapList.get(position) == null) {
            return ADD_IMAGE;
        } else {
            return BITMAP_IMAGE;
        }
    }

    @Override
    public ViewH onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case ADD_IMAGE:
                view = inflater.inflate(R.layout.view_add_image, parent, false);
                return new AddImageVH(view);
            case BITMAP_IMAGE:
                view = inflater.inflate(R.layout.view_image, parent, false);
                return new ImageVH(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ViewH holder, int position) {
        final Bitmap bitmap = bitmapList.get(position);
        if (bitmap == null) {
            AddImageVH vh = (AddImageVH) holder;
            vh.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) context).refreshCamera();
                    ((MainActivity) context).closeFragments();
                }
            });
        } else {
            ImageVH vh = (ImageVH) holder;
            vh.image.setImageBitmap(bitmap);
            vh.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reviewFragment.setCroppedImage(bitmap);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return bitmapList != null ? bitmapList.size() : 0;
    }

    class ViewH extends RecyclerView.ViewHolder {
        ViewH(View itemView) {
            super(itemView);
        }
    }

    class AddImageVH extends ViewH {
        @BindView(R.id.image)
        ImageView image;

        AddImageVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ImageVH extends ViewH {
        @BindView(R.id.image)
        ImageView image;

        ImageVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void swapData(List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
    }

    public void setReviewFragment(ReviewFragment reviewFragment) {
        this.reviewFragment = reviewFragment;
    }
}
