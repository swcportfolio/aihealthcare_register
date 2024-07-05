package com.lukken.aihealthcareregister;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lukken.aihealthcareregister.HttpTask.DisableSSLCertificateValidation;
import com.lukken.aihealthcareregister.HttpTask.SocketClient;
import com.lukken.aihealthcareregister.recognition.id3Credentials;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 회원등록 화면
        findViewById(R.id.btn_join).setOnClickListener(v -> {
            Intent intent = new Intent(this, JoinActivity.class);
            joinResult.launch(intent);
        });


        //얼굴인식 라이센스 체크
//        boolean isLicenseOk = id3Credentials.registerSdkLicense(getFilesDir().getAbsolutePath() + "/id3FaceLicense.lic");
//        if (!isLicenseOk) {
//            new AlertDialog.Builder(MainActivity.this).setMessage("라이센스 확인 필요").setPositiveButton("확인", (dialog, which) -> {
//                finish();
//                System.exit(-1);
//            }).setCancelable(false).show();
//        }

        requestPermissions();
    }

    ActivityResultLauncher<Intent> joinResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();
            String ucode = intent.getStringExtra("ucode");
            //키오스크에 가입완료 전송
            SocketClient.getInstance(MainActivity.this).sendJoinComplete(ucode);
        }
    });

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    //region 퍼미션
    static final int RC_VIDEO_APP_PERM = 124;
    int percount = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if(percount >= 2) {
            MainActivity.this.finish();
        } else {
            //if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            String msg = "퍼미션 에러: ";
            for(int idx=0; idx<perms.size(); ++idx){
                if(idx==0)
                    msg = msg+perms.get(idx);
                else
                    msg = msg + " / " + perms.get(idx);
            }
            new AppSettingsDialog.Builder(this).setRationale(msg/*getString(R.string.permission01)*/)
                    .setPositiveButton("설정").setNegativeButton("닫기")
                    .setRequestCode(RC_VIDEO_APP_PERM).build().show();
            //}
            percount++;
        }
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = { Manifest.permission.INTERNET, android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE };
        if (EasyPermissions.hasPermissions(this, perms)) {

        } else {
            EasyPermissions.requestPermissions(this, "카메라 및 저장공간 사용 권한 필요.", RC_VIDEO_APP_PERM, perms);
        }
    }
    //endregion
}
