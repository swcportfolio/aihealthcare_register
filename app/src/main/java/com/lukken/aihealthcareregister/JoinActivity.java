package com.lukken.aihealthcareregister;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.lukken.aihealthcareregister.HttpTask.HttpTask;

import org.intellij.lang.annotations.RegExp;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Result;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JoinActivity extends AppCompatActivity {
    EditText editName;
    EditText editPhone;
    EditText editBirth;
    View currentEdit = null;
    RadioButton rbGenderM;
    RadioButton rbGenderF;

    String mUCode;

    boolean mAddFace = false;

    View alertLayout;
    TextView alertTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        mAddFace = false;
        initUI();
        //getUCode();
    }

    //region UI
    @SuppressLint("ClickableViewAccessibility")
    void initUI(){
        editName = findViewById(R.id.name);
        editPhone = findViewById(R.id.phone);
        editBirth = findViewById(R.id.birth);
        rbGenderM = findViewById(R.id.male);
        rbGenderF = findViewById(R.id.female);
        editName.setOnFocusChangeListener(focusChangeListener);
        editPhone.setOnFocusChangeListener(focusChangeListener);
        editBirth.setOnFocusChangeListener(focusChangeListener);

        alertLayout = findViewById(R.id.alertLayout);
        alertTxt = findViewById(R.id.alertTxt);

        //화면영역 터치시  키보드 숨김
        findViewById(R.id.layoutActivity).setOnTouchListener((v, event) -> {
            hideKeyboard();
            return false;
        });
        findViewById(R.id.scroll).setOnTouchListener((v, event) -> {
            hideKeyboard();
            return false;
        });
        rbGenderM.setOnClickListener(v -> hideKeyboard());
        rbGenderF.setOnClickListener(v -> hideKeyboard());

        findViewById(R.id.add_face).setOnClickListener(v -> {
            SetAddFace();
        });
        findViewById(R.id.jo_back).setOnClickListener(v -> {
            JoinActivity.this.finish();
        });

        findViewById(R.id.join).setOnClickListener(v -> {
            hideKeyboard();
            if(checkInputData()){
                checkMobile(true);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
        if(hasFocus)
            currentEdit = v;
    };

    void hideKeyboard(){
        if(currentEdit != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentEdit.getWindowToken(), 0);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    /**
     * 얼굴등록 화면 변경
     */
    void SetAddFace(){
        hideKeyboard();
        if(checkInputData()) {
            checkMobile(false);
        }
    }

    /**
     * 회원가입 정보 체크
     */
    boolean checkInputData(){
        String name = editName.getText().toString();
        if(name.length() == 0){
            showAlert("이름을 입력 하세요.", 1500);
            return false;
        } else if(name.length() > 7){
            showAlert("이름을 7자 이하로 작성해주세요.", 1500);
            return false;
        }


        String mobile = editPhone.getText().toString();
        // 휴대폰 번호 유효성 체크를 위한 정규표현식
        String regex = "^(01[016789])(\\d{3,4})(\\d{4})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mobile);

        if(mobile.length() < 10){
            showAlert("휴대폰 번호를 입력 하세요.", 1500);
            return false;
        } else if (!matcher.matches()) {
            showAlert("휴대번 번호가 유효하지 않습니다.", 1500);
            return false;
        }

        String birth = editBirth.getText().toString();
        String regexBirth = "^(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])$";
        Pattern patternBirth = Pattern.compile(regexBirth);
        Matcher matcherBirth = patternBirth.matcher(birth);



        if(birth.length() < 8){
            showAlert("생년월일을 입력 하세요. ex)19990618", 1500);
            return false;
        } else if(!matcherBirth.matches()){
            showAlert("출생년도가 유효하지 않습니다.", 1500);
            return false;
        }


        if(!rbGenderM.isChecked() && !rbGenderF.isChecked()){
            showAlert("성별을 선택해 주세요.", 1500);
            return false;
        }
        return true;
    }

    public void showAlert(String msg, long delay){
        runOnUiThread(() -> {
            alertTxt.setText(msg);
            alertLayout.setVisibility(View.VISIBLE);
        });

        new Handler().postDelayed(() -> runOnUiThread(() -> {
            alertTxt.setText("");
            alertLayout.setVisibility(View.GONE);
        }), delay);
    }
    //endregion


    ActivityResultLauncher<Intent> mRegistFace = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            mAddFace = true;
        }
    });


    //region - 통신
    /**
     * 신규 유저 코드 생성
     */
    void getUCode(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpTask.URL_DOMAIN).addConverterFactory(GsonConverterFactory.create()).build();
        HttpTask httpTask = retrofit.create(HttpTask.class);
        httpTask.getNewUCode().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mUCode = response.body().get("ucode").getAsString();
                if(mUCode == null || mUCode.length() == 0){
                    new AlertDialog.Builder(JoinActivity.this).setMessage("신규 유저코드 생성에 실패 하였습니다.").setPositiveButton("확인", (dialog, which) -> {
                        hideKeyboard();
                        //JoinActivity.this.finish();
                    }).setCancelable(false).show();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                new AlertDialog.Builder(JoinActivity.this).setMessage("신규 유저코드 생성에 실패 하였습니다.").setPositiveButton("확인", (dialog, which) -> {
                    hideKeyboard();
                    //JoinActivity.this.finish();
                }).setCancelable(false).show();
            }
        });
    }


    /**
     * 가입가능한 휴대폰 번호인지 체크
     * @param isCreateAccount true-신규 생성 false-얼굴등록
     */
    void checkMobile(final boolean isCreateAccount){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpTask.URL_DOMAIN).addConverterFactory(GsonConverterFactory.create()).build();
        HttpTask httpTask = retrofit.create(HttpTask.class);
        httpTask.getCheckMobile(editPhone.getText().toString()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                int code = response.body().get("code").getAsInt();
                if(code == 201){
                    new AlertDialog.Builder(JoinActivity.this).setMessage("이미 가입된 휴대폰번호 입니다..").setPositiveButton("확인", (dialog, which) -> {
                        hideKeyboard();
                    }).setCancelable(false).show();
                }else{
                    if(isCreateAccount){
                        if(!mAddFace){
                            new AlertDialog.Builder(JoinActivity.this).setMessage("안면등록을 해 주세요..").setPositiveButton("확인", null).setCancelable(false).show();
                        }else {
                            getUCode();
                            createAccount();
                        }
                    }else {
                        mAddFace = false;
                        //얼굴등록화면 이동
                        Intent intent = new Intent(JoinActivity.this, AddFaceActivity.class);
                        intent.putExtra("ucode", mUCode);
                        mRegistFace.launch(intent);
                    };
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                new AlertDialog.Builder(JoinActivity.this).setMessage("휴대폰번호 체크 오류.").setPositiveButton("확인", (dialog, which) -> {
                    hideKeyboard();
                }).setCancelable(false).show();
            }
        });
    }

    void createAccount(){
        RequestBody ucode = RequestBody.create(MediaType.parse("multipart/form-data"), mUCode);
        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), editName.getText().toString());
        RequestBody mobile = RequestBody.create(MediaType.parse("multipart/form-data"), editPhone.getText().toString());
        RequestBody birth_y = RequestBody.create(MediaType.parse("multipart/form-data"), editBirth.getText().toString());
        RequestBody gender = RequestBody.create(MediaType.parse("multipart/form-data"), rbGenderM.isChecked() ? "M" : "F");

        String fileName = mUCode + ".dat";
        File file = new File(getExternalFilesDir("").getPath() + "/" + fileName);
        MultipartBody.Part biodata = MultipartBody.Part.createFormData("biodata", fileName, RequestBody.create(MediaType.parse("application/octet-stream"), file));

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("ucode", ucode);
        map.put("name", name);
        map.put("mobile", mobile);
        map.put("birth_y", birth_y);
        map.put("gender", gender);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpTask.URL_DOMAIN).addConverterFactory(GsonConverterFactory.create()).build();
        HttpTask httpTask = retrofit.create(HttpTask.class);
        httpTask.postCreateAccount(map, biodata).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                new AlertDialog.Builder(JoinActivity.this).setMessage("회원가입이 완료되었습니다.").setPositiveButton("확인", (dialog, which) -> {
                    hideKeyboard();

                    String savePath = getExternalFilesDir("").getPath();
                    File n = new File(savePath);
                    File[] files = n.listFiles(pathname -> pathname.getName().toLowerCase(Locale.US).endsWith(".dat"));
                    for(File f : files)
                        f.delete();
                    JoinActivity.this.finish();

                    Intent intent = new Intent(JoinActivity.this, ResultActivity.class);
                    intent.putExtra("ucode", mUCode);
                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(intent);

                }).setCancelable(false).show();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                new AlertDialog.Builder(JoinActivity.this).setMessage("회원가입 오류.\n"+t.getMessage()).setPositiveButton("확인", null).setCancelable(false).show();
            }
        });
    }
    //endregion
}