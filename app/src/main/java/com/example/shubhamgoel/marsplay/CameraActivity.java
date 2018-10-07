package com.example.shubhamgoel.marsplay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.shubhamgoel.marsplay.utils.Control;
import com.example.shubhamgoel.marsplay.utils.ControlView;
import com.example.shubhamgoel.marsplay.utils.ImageFilePath;
import com.example.shubhamgoel.marsplay.utils.Util;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;
import com.otaliastudios.cameraview.SessionType;
import com.otaliastudios.cameraview.Size;
import com.otaliastudios.cameraview.VideoQuality;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


public class CameraActivity extends AppCompatActivity implements View.OnClickListener, ControlView.Callback {

    private CameraView camera;
    private ViewGroup controlPanel;
    private static final int IMAGE_PICKER_SELECT = 1212;

    private boolean mCapturingPicture;
    private boolean mCapturingVideo;

    // To show stuff in the callback
    private Size mCaptureNativeSize;
    private long mCaptureTime;
    private Bitmap bmp;
    private String finalPath = "";
    private File f = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_camera);
        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE);

        camera = findViewById(R.id.camera);
        camera.setLifecycleOwner(this);
        camera.addCameraListener(new CameraListener() {
            public void onCameraOpened(CameraOptions options) {
                try {
                    onOpened();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onPictureTaken(byte[] jpeg) {
                onPicture(jpeg);
            }

            @Override
            public void onVideoTaken(File video) {
                super.onVideoTaken(video);
                onVideo(video);
            }
        });
        camera.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
        findViewById(R.id.edit).setOnClickListener(this);
        findViewById(R.id.capturePhoto).setOnClickListener(this);
        findViewById(R.id.captureVideo).setOnClickListener(this);
        findViewById(R.id.toggleCamera).setOnClickListener(this);
        findViewById(R.id.gallery).setOnClickListener(this);
        findViewById(R.id.toggleflash).setOnClickListener(this);

        controlPanel = findViewById(R.id.controls);
        ViewGroup group = (ViewGroup) controlPanel.getChildAt(0);
        Control[] controls = Control.values();
        for (Control control : controls) {
            ControlView view = new ControlView(this, control, this);
            group.addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        controlPanel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
                b.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    private void message(String content, boolean important) {
        int length = important ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast.makeText(this, content, length).show();
    }

    private void onOpened() {
        ViewGroup group = (ViewGroup) controlPanel.getChildAt(0);
        for (int i = 0; i < group.getChildCount(); i++) {
            ControlView view = (ControlView) group.getChildAt(i);
            view.onCameraOpened(camera);
        }
    }

    private void onPicture(byte[] jpeg) {
        mCapturingPicture = false;
        long callbackTime = System.currentTimeMillis();
        if (mCapturingVideo) {
            message("Captured while taking video. Size=" + mCaptureNativeSize, false);
            return;
        }

        // This can happen if picture was taken with a gesture.
        if (mCaptureTime == 0) mCaptureTime = callbackTime - 300;
        if (mCaptureNativeSize == null) mCaptureNativeSize = camera.getPictureSize();

        final ProgressDialog progressDialog = new ProgressDialog(CameraActivity.this);
        progressDialog.setMessage("Processing image. Please wait...");
        progressDialog.show();

        CameraUtils.decodeBitmap(jpeg, new CameraUtils.BitmapCallback() {
            @Override
            public void onBitmapReady(Bitmap bitmap) {

                bmp = bitmap;
                bmp = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * 0.5), (int) (bmp.getHeight() * 0.5), true);

                f = saveToFile(bmp);
                if (finalPath == null) {
                    Util.showToast("Some error with media selected", CameraActivity.this);
                    return;
                }
                progressDialog.dismiss();
                Intent intent = new Intent(CameraActivity.this, AddPostActivity.class);
                intent.putExtra("file", finalPath);
                startActivity(intent);

            }
        });

        mCaptureTime = 0;
        mCaptureNativeSize = null;
    }

    private void onVideo(File video) {
        mCapturingVideo = false;
        finalPath = video.getAbsolutePath();
//        Intent intent = new Intent(CameraActivity.this, VideoPreviewActivity.class);
//        intent.putExtra("video", Uri.fromFile(video));
//        startActivity(intent);

        if (finalPath == null) {
            Util.showToast("Some error with media selected", CameraActivity.this);
            return;
        }
        Intent intent = new Intent(CameraActivity.this, AddPostActivity.class);
        intent.putExtra("file", finalPath);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit:
                edit();
                break;
            case R.id.capturePhoto:
                capturePhoto();
                break;
            case R.id.captureVideo:
                //captureVideo();
                captureVideoDeviceCamera();
                break;
            case R.id.toggleCamera:
                toggleCamera();
                break;
            case R.id.gallery:
                pickFromGallery();
                break;
            case R.id.toggleflash:
                toogleFlash();
                break;
        }
    }

    private void toogleFlash() {
        if (camera.getFlash() == Flash.TORCH)
            camera.setFlash(Flash.OFF);
        else
            camera.setFlash(Flash.TORCH);
    }

    private void pickFromGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/jpeg");
        startActivityForResult(pickIntent, IMAGE_PICKER_SELECT);
    }

    static final int REQUEST_VIDEO_CAPTURE = 1;

    private void captureVideoDeviceCamera() {

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }

    }

    @Override
    public void onBackPressed() {
        BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
        if (b.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            b.setState(BottomSheetBehavior.STATE_HIDDEN);
            return;
        }
        super.onBackPressed();
    }

    private void edit() {
        BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
        b.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void capturePhoto() {
        if (mCapturingPicture) return;
        mCapturingPicture = true;
        mCaptureTime = System.currentTimeMillis();
        mCaptureNativeSize = camera.getPictureSize();
        message("Capturing picture...", false);
        camera.capturePicture();
    }

    private void captureVideo() {
        if (camera.getSessionType() != SessionType.VIDEO) {
            camera.setSessionType(SessionType.VIDEO);
            //message("Can't record video while session type is 'picture'.", false);
            //return;
        }
        camera.setVideoQuality(VideoQuality.MAX_720P);
        if (mCapturingPicture || mCapturingVideo) return;
        mCapturingVideo = true;
        message("Recording for 8 seconds...", true);
        camera.startCapturingVideo(null, 8000);
    }

    private void toggleCamera() {
        if (mCapturingPicture) return;
        switch (camera.toggleFacing()) {
            case BACK:
                message("Switched to back camera!", false);
                break;

            case FRONT:
                message("Switched to front camera!", false);
                break;
        }
    }

    @Override
    public boolean onValueChanged(Control control, Object value, String name) {
        if (!camera.isHardwareAccelerated() && (control == Control.WIDTH || control == Control.HEIGHT)) {
            if ((Integer) value > 0) {
                message("This device does not support hardware acceleration. " +
                        "In this case you can not change width or height. " +
                        "The view will act as WRAP_CONTENT by default.", true);
                return false;
            }
        }
        control.applyValue(camera, value);
        BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
        b.setState(BottomSheetBehavior.STATE_HIDDEN);
        //message("Changed " + control.getName() + " to " + name, false);
        return true;
    }

    //region Permissions

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean valid = true;
        for (int grantResult : grantResults) {
            valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (valid && !camera.isStarted()) {
            camera.start();
        }
    }

    private File saveToFile(Bitmap bmp) {
        long time = new Date().getTime();
        File f = new File(getCacheDir(), "filename_" + time + ".png");
        try {
            f.createNewFile();


            Bitmap bitmap = bmp;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finalPath = f.getAbsolutePath();
        return f;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            finalPath = ImageFilePath.getPath(CameraActivity.this, intent.getData());
            Intent i = new Intent(CameraActivity.this, AddPostActivity.class);
            i.putExtra("file", finalPath);
            startActivity(i);
        } else if (resultCode == RESULT_OK) {
            Uri selectedMediaUri = intent.getData();
            finalPath = ImageFilePath.getPath(CameraActivity.this, intent.getData());
            Intent i = new Intent(CameraActivity.this, AddPostActivity.class);
            i.putExtra("file", finalPath);
            startActivity(i);
        }
    }
}