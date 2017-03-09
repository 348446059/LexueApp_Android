package com.libo.lexue.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ObbInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.baoyz.actionsheet.ActionSheet;
import com.libo.lexue.MainActivity;
import com.libo.lexue.R;
import com.libo.lexue.utils.AppConstants;
import com.libo.lexue.utils.FileUtils;
import com.libo.lexue.utils.PictureUtil;
import com.libo.lexue.utils.SpUtils;
import com.libo.lexue.utils.ToastUtils;
import com.libo.lexue.utils.Utils;
import com.libo.lexue.views.ProgressBarCircularIndeterminate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by libo on 2017/2/28.
 */

public class WebActivity extends FragmentActivity implements ActionSheet.ActionSheetListener {
    @Bind(R.id.webView)
    WebView webView;
    @Bind(R.id.progressBarCircularIndetermininate)
    ProgressBarCircularIndeterminate progressBarCircularIndetermininate;
    public final static int  ALBUM_REQUEST_CODE = 1;
    public final static int CROP_REQUEST = 2;
    public final static int CAMERA_REQUEST_CODE = 3;
    public static String SAVED_IMAGE_DIR_PATH =  Environment.getExternalStorageDirectory().getPath()

            + "/AppName/camera/";// 拍照路径
    private File imgFile;
    private String imgPath;
    String cameraPath;



    private String fileName;
    private File mCurrentPhotoFile;// 照相机拍照得到的图片
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/CameraCache");
    private File mCacheFile;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){

                webView.post(new Runnable() {
                    @Override
                    public void run() {

                       // webView.loadUrl("javascript:wave('" + mCurrentPhotoFile.getAbsolutePath() + "')");
                         webView.loadUrl("javascript:window.localStorage('aa','bb')");
                    }
                });
            }
        }
    };





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch (requestCode){
                   case CAMERA_REQUEST_CODE:
                       int length = grantResults.length;
                       final boolean isGranted = length >= 1 && PackageManager.PERMISSION_GRANTED == grantResults[length - 1];
                       if (isGranted){
                           startCamera();
                       }
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        boolean isMain =  getIntent().getBooleanExtra("Main",false);
        setTheme(R.style.ActionSheetStyleiOS7);
        String url="";
        if (isMain){
            String params = "username=" + getIntent().getStringExtra("username") +
                    "&" + "password=" + getIntent().getStringExtra("password");
            url =  getIntent().getStringExtra("url")+"?"+params;

        }else {
            url = getIntent().getStringExtra("url");
        }

        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);


        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        String userAgent = "shixinzhang";
        String js = "window.localStorage.setItem('userAgent','" + userAgent + "');";
        String jsUrl = "javascript:(function({ var localStorage = window.localStorage; localStorage.setItem('userAgent','" + userAgent + "')})()";

//        ValueCallback<String> resultCallback = new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//                Log.i("aa",value);
//            }
//        };
//        //2.根据不同版本，使用不同的 API 执行 Js
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            webView.evaluateJavascript(js, resultCallback);
//        } else {
//            webView.loadUrl(jsUrl);
//            webView.reload();
//        }
        class JsObject {

            @JavascriptInterface
            public void test() {

//                Message message = new Message();
//                message.what = 1;
//
//                handler.sendMessage(message);
                finish();
            }
            @JavascriptInterface
            public void  openCamera(){
                ActionSheet.createBuilder(WebActivity.this,getSupportFragmentManager())
                               .setCancelButtonTitle("取消")
                                .setOtherButtonTitles("相册","相机")
                                .setCancelableOnTouchOutside(true)
                                .setListener(WebActivity.this).show();

            }
            @JavascriptInterface
            public void uploadImg(){

            }
        }

        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.addJavascriptInterface(new JsObject(), "javatojs");
        webView.loadUrl(url);
        final View videoView;
        WebChromeClient.CustomViewCallback mCallBack = null;
        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {



                webView.setVisibility(View.GONE);

            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBarCircularIndetermininate.setVisibility(View.GONE);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
            }



        });


    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet,final int index) {

        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(WebActivity.this, new String[]{
                        Manifest.permission.CAMERA,
                      Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        if (index == 0){
                            showPhotoAlbum();
                        } else if(index == 1){
                            startCamera();
                        }
                    }

                    @Override
                    public void onDenied(String permission) {

                    }
                });


    }

    public void startCamera(){

     // 指定相机拍摄照片保存地址
     String state = Environment.getExternalStorageState();

     if (state.equals(Environment.MEDIA_MOUNTED)) {
         if (!PHOTO_DIR.exists()) {
             PHOTO_DIR.mkdirs();// 创建照片的存储目录
         }
         fileName = System.currentTimeMillis()+".jpg";
         mCurrentPhotoFile = new File(PHOTO_DIR,fileName);
          Intent intent = new Intent();
         intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
             Uri imageUri = FileProvider.getUriForFile(WebActivity.this, "com.libo.fileprovider", mCurrentPhotoFile);
             intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
             intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

         } else {
             intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
         }

         startActivityForResult(intent, CAMERA_REQUEST_CODE);
         }
           else {
              ToastUtils.showShort(this,"请确认已经插入SD卡");
         }
    }

    private void showPhotoAlbum(){
            //跳转到系统相册
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }
        startActivityForResult(intent, ALBUM_REQUEST_CODE);
    }


    private String compressImgPath;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){

             if (requestCode == CAMERA_REQUEST_CODE){//相机

                 if (mCurrentPhotoFile == null || !mCurrentPhotoFile.exists()) {
                     mCurrentPhotoFile = new File(PHOTO_DIR, fileName);
                     imgPath = mCurrentPhotoFile.getAbsolutePath();
                     compressImgPath = PictureUtil.compressImage(imgPath,imgPath,30);
                 }

             }else if (requestCode == ALBUM_REQUEST_CODE){//相册

                 Uri uri = data.getData();
                 String selectedImagePath = FileUtils.getPath(WebActivity.this,uri);
                 mCurrentPhotoFile = new File(selectedImagePath);

                 imgPath = mCurrentPhotoFile.getAbsolutePath();
                 String compress =  imgPath.replace(imgPath.substring(imgPath.length()-9,imgPath.length()-6),"compress") ;
                 compressImgPath = PictureUtil.compressImage(imgPath, compress, 30);
             }
            Message message = new Message();

            message.what = 1;
            handler.sendEmptyMessage(1);

        }

    }

    private long exitTime = 1000;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
            webView.goBack();
            return true;
        }else{
//            if((System.currentTimeMillis()-exitTime) > 2000){
//                ToastUtils.makeShortText("再按一次退出程序",WebActivity.this);
//                exitTime = System.currentTimeMillis();
//            } else {
                //  finish();
            //}
            return false;
        }

    }
}
