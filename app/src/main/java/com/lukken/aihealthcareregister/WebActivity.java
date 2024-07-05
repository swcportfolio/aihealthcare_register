package com.lukken.aihealthcareregister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WebActivity extends AppCompatActivity {

    private WebView mwv;
    // private String URL = "http://onwards.iptime.org:50003/console";
    //private String URL = "http://lifelogop.ghealth.or.kr/console";

    //private String URL = "http://192.168.0.9:50104/console";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

       // 플로팅 홈버튼
        FloatingActionButton fb = findViewById(R.id.fab);
        fb.setBackgroundColor(Color.parseColor("#FFFFFF"));
        fb.setOnClickListener(view -> finish());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 가로 고정

        mwv = (WebView)findViewById(R.id.webView);

        WebSettings mws = mwv.getSettings(); // Mobile Web Setting
        mws.setBuiltInZoomControls(false);   // 확대 축소
        mws.setSupportZoom(false);           // 확대 축소 지원
        mws.setJavaScriptEnabled(true);      // 자바스크립트 허용
        mws.setUseWideViewPort(true);
        mws.setLoadWithOverviewMode(false);  // 컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정

        mwv.setVerticalScrollBarEnabled(false);   //세로 스크롤

        // 컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정
        mwv.setInitialScale(100);
        mwv.setWebViewClient(new WebViewClient() {
            //                super.onReceivedError(view, request, error);
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebReso
//
//                Intent intent = new Intent(MainActivity.this, ErrorHandlingActivity.class);
//                startActivity(intent);
//                finish();
//            }
        });
        mwv.loadUrl("URL");
        mwv.setWebChromeClient(new WebChromeClient(){

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mwv.canGoBack()) {
                mwv.goBack();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}