package com.lukken.aihealthcareregister;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lukken.aihealthcareregister.recognition.FaceProcessor;

import eu.id3.face.DetectedFace;
import eu.id3.face.FaceTemplate;
import eu.id3.face.Image;
import eu.id3.face.Rectangle;

public class AddFaceActivity extends AppCompatActivity implements FrameListener{
    private CameraFragment captureFragment = new CameraFragment();

    ScanBG scanBG;

    View txtExplain;
    View btnRegist;

    FaceProcessor faceProcessor = null;
    DetectedFace detectedFace;
    eu.id3.face.Image processingImage;
    String mUcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addface);

        Intent intent = getIntent();
        mUcode = intent.getStringExtra("ucode");
        if(mUcode == null || mUcode.length() == 0){
            new AlertDialog.Builder(AddFaceActivity.this).setMessage("신규 유저코드 오류.").setPositiveButton("확인", (dialog, which) -> {
                AddFaceActivity.this.finish();
            }).setCancelable(false).show();
        }else {
            scanBG = findViewById(R.id.scan_bg);
            scanBG.setLayerType(View.LAYER_TYPE_HARDWARE, null);

            txtExplain = findViewById(R.id.exptxt);
            btnRegist = findViewById(R.id.takepic);
            findViewById(R.id.fd_back).setOnClickListener(v -> {
                AddFaceActivity.this.finish();
            });
            findViewById(R.id.takepic).setOnClickListener(v -> {
                registFace();
            });

            faceProcessor = new FaceProcessor(this);
            captureFragment = (CameraFragment) getSupportFragmentManager().findFragmentById(R.id.cameraFragment);
            if(captureFragment != null)
                captureFragment.setFrameListener(this);
        }
    }

    @Override
    protected  void onResume(){
        super.onResume();
        if(captureFragment != null)
            captureFragment.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    void registFace(){
        try{
            FaceTemplate template = faceProcessor.enrollLargestFace(processingImage, detectedFace);
            if (template != null) {
                faceProcessor.saveTemplate(template, mUcode);
                new AlertDialog.Builder(AddFaceActivity.this).setMessage("얼굴등록 완료.").setPositiveButton("확인", (dialog, which) -> {
                    Intent resultIntent = new Intent(AddFaceActivity.this, JoinActivity.class);
                    setResult(Activity.RESULT_OK, resultIntent);
                    AddFaceActivity.this.finish();
                }).setCancelable(false).show();
            }
        }catch (Exception e){
            showAlert("너무 멀리 있습니다.");
        }
    }

    void showAlert(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFrame(eu.id3.face.Image frame) {
        processingImage = frame;
        //얼굴 인식
        detectedFace = faceProcessor.detectLargestFace(frame);
        if(detectedFace != null) {
            Rectangle rectangle = detectedFace.getBounds();
            boolean center = scanBG.setBounding(rectangle, frame.getWidth(), frame.getHeight());
            setRegistUI(center);
        }
    }

    void setRegistUI(boolean v){
        runOnUiThread(() ->{
            if(v){
                txtExplain.setVisibility(View.GONE);
                btnRegist.setVisibility(View.VISIBLE);
            }else{
                txtExplain.setVisibility(View.VISIBLE);
                btnRegist.setVisibility(View.GONE);
            }
        });
    }
}
