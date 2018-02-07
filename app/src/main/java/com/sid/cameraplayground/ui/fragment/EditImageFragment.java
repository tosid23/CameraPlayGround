package com.sid.cameraplayground.ui.fragment;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.sid.cameraplayground.R;
import com.sid.cameraplayground.ui.activity.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditImageFragment extends Fragment {

    @BindView(R.id.back)
    ImageView back;

    @BindView(R.id.cropImage)
    ImageView cropImage;

    @BindView(R.id.footerLayout)
    RelativeLayout footerLayout;

    @BindView(R.id.done)
    RelativeLayout done;

    Bitmap bitmap = null;

    public EditImageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_image, container, false);
        ButterKnife.bind(this, view);

        cropImage.setImageBitmap(bitmap);
        cropImage.setOnTouchListener(new ImageMatrixTouchHandler(view.getContext()));

        return view;
    }

    @OnClick(R.id.back)
    public void finishFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @OnClick(R.id.done)
    public void done(){
        new CreateBitmapTask().execute();
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private class CreateBitmapTask extends AsyncTask<Void, Void, Void> {

        Bitmap bmp = null;

        @Override
        protected Void doInBackground(Void... voids) {
            Bitmap result = Bitmap.createBitmap(cropImage.getWidth(), cropImage.getHeight(), Bitmap.Config.RGB_565);
            Canvas c = new Canvas(result);
            cropImage.draw(c);
            bmp = result;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ((MainActivity)getActivity()).croppedBitmaps.add(bmp);
            ((MainActivity)getActivity()).openReviewFragment();
        }
    }
}
