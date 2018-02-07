package com.sid.cameraplayground.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.sid.cameraplayground.ui.util.CameraPreview;
import com.sid.cameraplayground.ui.fragment.EditImageFragment;
import com.sid.cameraplayground.R;
import com.sid.cameraplayground.ui.fragment.ReviewFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.camera_view)
    FrameLayout camera_view;

    @BindView(R.id.imgClose)
    ImageView imgClose;

    @BindView(R.id.capture)
    RelativeLayout capture;

    @BindView(R.id.captureImage)
    ImageView captureImage;

    @BindView(R.id.previewImage)
    ImageView previewImage;

    @BindView(R.id.refresh)
    ImageView refresh;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Camera mCamera = null;
    private CameraPreview mCameraView = null;
    Context mContext;

    byte[] imageByteData = null;
    public Bitmap bitmap = null;
    public List<Bitmap> croppedBitmaps = new ArrayList<>();

    boolean isPictureTaken = false;

    EditImageFragment editImageFragment = new EditImageFragment();
    ReviewFragment reviewFragment = new ReviewFragment();
    FragmentManager manager = getSupportFragmentManager();

    String TAG = "MainActivity";
    public static final int MEDIA_TYPE_IMAGE = 1;

    private final int CAMERA_REQUEST_CODE = 50;
    private final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 51;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
        loadCameraPreview();

    }

    public void loadCameraPreview() {
        mCamera = getCameraInstance();
        Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        params.set("zsl", "on");
        params.setRotation(90);

        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size size = sizes.get(0);
        for (int i = 0; i < sizes.size(); i++) {
            if (sizes.get(i).width > size.width)
                size = sizes.get(i);
        }

        params.setPictureSize(size.width, size.height);

        mCamera.setParameters(params);

        if (mCamera != null) {
            mCameraView = new CameraPreview(this, mCamera);//create a SurfaceView to show camera data
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
        }
    }

    @OnClick(R.id.capture)
    public void captureImage() {
        if (isPictureTaken) {
            //new SaveImageTask().execute(imageByteData);
            editImageFragment.setBitmap(bitmap);
            manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(android.R.id.content, editImageFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            capture.setBackground(ContextCompat.getDrawable(mContext, R.drawable.white_circle));
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            } else {
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        isPictureTaken = true;
                        refresh.setVisibility(View.VISIBLE);
                        imageByteData = null;
                        imageByteData = data;

                        progressBar.setVisibility(View.INVISIBLE);
                        capture.setBackground(ContextCompat.getDrawable(mContext, R.drawable.red_circle));
                        captureImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_check_white_30dp));

                        new LoadBitmapTask().execute(imageByteData);
                    }
                });
            }
        }
    }

    @OnClick(R.id.refresh)
    public void refreshCamera() {
        refresh.setVisibility(View.INVISIBLE);
        previewImage.setVisibility(View.INVISIBLE);
        captureImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_camera_alt_white_30dp));
        isPictureTaken = false;
    }

    /**
     * Create a File for saving an image
     */
    private static File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraApp");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("CameraApp", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            Log.e("ERROR", "Failed to get camera: " + e.getMessage());
            // Camera is not available (in use or does not exist)
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            }
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.e(TAG, ":" + requestCode);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadCameraPreview();
                }
                return;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @OnClick(R.id.imgClose)
    public void exitApplication() {
        finish();
    }

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.e(TAG, "Error creating media file, check storage permissions: ");
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(imageByteData);
                fos.close();
                Log.e(TAG, "Success ");
            } catch (FileNotFoundException e) {
                Log.e(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "Error accessing file: " + e.getMessage());
            }
            return null;
        }

    }

    private class LoadBitmapTask extends AsyncTask<byte[], Void, Void> {

        Bitmap bmp = null;

        @Override
        protected Void doInBackground(byte[]... data) {
            bmp = BitmapFactory.decodeByteArray(imageByteData, 0, imageByteData.length);
            bitmap = bmp;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (bmp != null) {
                previewImage.setVisibility(View.VISIBLE);
                previewImage.setImageBitmap(bmp);
            }
        }
    }

    public void openReviewFragment(){
        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(android.R.id.content, reviewFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void closeFragments(){

        if(editImageFragment!=null){
            manager.beginTransaction().remove(editImageFragment).commit();
        }

        if(reviewFragment!=null){
            manager.beginTransaction().remove(reviewFragment).commit();
        }



    }
}
