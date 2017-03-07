package com.libo.lexue.Activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.libo.lexue.R;


/**
 * Created by libo on 2017/2/28.
 */

public class CustomScanAct extends AppCompatActivity implements DecoratedBarcodeView.TorchListener{

    Button swichLight;
    DecoratedBarcodeView mDBV;
    private CaptureManager captureManager;
    private boolean isLightOn = false;

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }
    @Override
    public void onTorchOn() {

    }

    @Override
    public void onTorchOff() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        captureManager.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        swichLight = (Button) findViewById(R.id.btn_switch);
        mDBV= (DecoratedBarcodeView) findViewById(R.id.dbv_custom);

        mDBV.setTorchListener(this);

        // 如果没有闪光灯功能，就去掉相关按钮
        if (!hasFlash()) {
            swichLight.setVisibility(View.GONE);
        }
        //重要代码，初始化捕获
        captureManager = new CaptureManager(this, mDBV);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();
        //选择闪关灯
        swichLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (isLightOn) {
//                    mDBV.setTorchOff();
//                } else {
//                    mDBV.setTorchOn();
//                }
                finish();
            }
        });
    }
    // 判断是否有闪光灯功能
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

}
