package com.sid.cameraplayground.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.sid.cameraplayground.R;
import com.sid.cameraplayground.ui.activity.MainActivity;
import com.sid.cameraplayground.ui.adapter.ReviewImagesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment {

    @BindView(R.id.cropImage)
    ImageView croppedImage;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    ReviewImagesAdapter adapter;
    List<Bitmap> list = new ArrayList<>();

    public ReviewFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        ButterKnife.bind(this, view);

        if (((MainActivity) getActivity()).croppedBitmaps.size() > 0) {
            croppedImage.setImageBitmap(((MainActivity) getActivity()).croppedBitmaps.get(((MainActivity) getActivity()).croppedBitmaps.size() - 1));
        }

        adapter = new ReviewImagesAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        list.clear();

        list.addAll(((MainActivity) getActivity()).croppedBitmaps);
        list.add(null);
        adapter.swapData(list);
        adapter.setReviewFragment(this);

        return view;
    }

    @OnClick(R.id.back)
    public void finishFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @OnClick(R.id.done)
    public void done() {
        Toast.makeText(getActivity(), "That's all folks", Toast.LENGTH_SHORT).show();
    }

    public void setCroppedImage(Bitmap bitmap) {
        croppedImage.setImageBitmap(bitmap);
    }

}
