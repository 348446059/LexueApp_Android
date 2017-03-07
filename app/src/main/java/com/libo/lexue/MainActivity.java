package com.libo.lexue;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.libo.lexue.Activity.CustomScanAct;
import com.libo.lexue.Activity.WebActivity;
import com.libo.lexue.utils.AppConstants;
import com.libo.lexue.utils.CommonCallback;
import com.libo.lexue.utils.SpUtils;
import com.libo.lexue.utils.ToastUtils;
import com.libo.lexue.utils.User;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.et_username)
    EditText etUsername;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.scan)
    Button scan;
    @Bind(R.id.rember_btn)
    Button remberBtn;
    @Bind(R.id.auto_btn)
    Button autoBtn;
    @Bind(R.id.login_btn)
    Button loginBtn;
    @Bind(R.id.activity_main)
    LinearLayout activityMain;
    @Bind(R.id.user_icon)
    ImageView userIcon;
    @Bind(R.id.company_name)
    TextView companyName;
    @Bind(R.id.back_btn)
    Button backBtn;

    private String idString = "";
    private boolean is_remeber = false;
    private boolean is_auto_login = false;
    private User user;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                Map<String, Object> stringObjectMap = (Map<String, Object>) msg.obj;
                SpUtils.putString(MainActivity.this, AppConstants.ICON, idString);
                ToastUtils.showShort(MainActivity.this, (String) stringObjectMap.get("msg"));
                Glide.with(MainActivity.this).load(stringObjectMap.get("siteImg")).into(userIcon);
                companyName.setText((String) stringObjectMap.get("siteTitle"));
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initUI();
        idString = SpUtils.getString(MainActivity.this, AppConstants.ICON);

        if (!idString.isEmpty()) {
            getIcon();
        }

    }


    @Override
    protected void onRestart() {
        super.onRestart();
       User _user = (User) SpUtils.readObject(MainActivity.this,AppConstants.USER);
        if (_user != null){
            autoBtn.setSelected(_user.isAutoLogin());
            if (_user.isAutoLogin()){
                etUsername.setText(_user.getName());
                etPassword.setText(_user.getPassword());

            }
        }

    }

    private void initUI() {
        scan.setOnClickListener(this);
        autoBtn.setOnClickListener(this);
        remberBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

         user = (User) SpUtils.readObject(MainActivity.this,AppConstants.USER);

        if (user != null){
            if (user.isAutoLogin()){
                goMainPage(user.getName(),user.getPassword());
                return;
            }

            if (user.isRemeber()){
                etUsername.setText(user.getName());
                etPassword.setText(user.getPassword());

            }
            remberBtn.setSelected(user.isRemeber());
            autoBtn.setSelected(user.isAutoLogin());
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scan) {

            IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
            intentIntegrator
                    .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                    .setPrompt("将二维码/条码放入框内，即可自动扫描")
                    .setOrientationLocked(false)
                    .setCaptureActivity(CustomScanAct.class)
                    .initiateScan();
        } else if (v.getId() == R.id.auto_btn) {
            autoBtn.setSelected(!autoBtn.isSelected());
        } else if (v.getId() == R.id.rember_btn) {
            remberBtn.setSelected(!remberBtn.isSelected());
        } else if (v.getId() == R.id.login_btn) {

            if (etUsername.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
                ToastUtils.showShort(MainActivity.this, "账号或密码不能为空!");
                return;
            }
            loginAction();




        } else if (v.getId() == R.id.back_btn){

            Intent intent = new Intent(MainActivity.this,WebActivity.class);
            intent.putExtra("url", AppConstants.PAGE_ADMIN);
            startActivity(intent);
        }
    }

    private void loginAction(){
        OkHttpUtils.
                post()
                .addParams("company_id",idString)
                .addParams("login_name",etUsername.getText().toString().trim())
                .addParams("password",etPassword.getText().toString().trim())
                .url(AppConstants.LOGIN_URL).build()
                .execute(new CommonCallback() {

                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(Map<String, Object> stringObjectMap) {
                       if (stringObjectMap.get("code").equals("1")){

                           if (autoBtn.isSelected()){
                               User user = new User();
                               user.setAutoLogin(autoBtn.isSelected());
                               user.setPassword(etPassword.getText().toString());
                               user.setName(etUsername.getText().toString());
                               user.setRemeber(remberBtn.isSelected());
                               SpUtils.saveObject(MainActivity.this,AppConstants.USER,user);
                           }else {
                               
                               User user = (User) SpUtils.readObject(MainActivity.this,AppConstants.USER);
                               if (user != null){
                                   SpUtils.clearData(MainActivity.this,AppConstants.USER);
                               }
                           }


                           goMainPage(etUsername.getText().toString(),etPassword.getText().toString());
                       }
                        ToastUtils.showShort(MainActivity.this, (String) stringObjectMap.get("msg"));
                    }
                });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {

            } else {
                // ScanResult 为获取到的字符串
                String scanResult = intentResult.getContents();
                idString = scanResult;

                if (!idString.isEmpty() && Integer.parseInt(idString) > 0) {

                    getIcon();
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    //跳往主页
    private void goMainPage(String username,String password){
        Intent intent = new Intent(MainActivity.this, WebActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("url", AppConstants.PAGE_MAIN);
        intent.putExtra("Main",true);
        etUsername.setText("");
        etPassword.setText("");
        startActivity(intent);
    }
    private void getIcon() {
        OkHttpUtils
                .post()
                .url(AppConstants.LOGO_URL)
                .addParams("CusCode", idString)
                .build()
                .execute(new CommonCallback() {
                    @Override
                    public void onError(Call call, Exception e) {


                    }

                    @Override
                    public void onResponse(Map<String, Object> stringObjectMap) {

                        if (stringObjectMap.get("code").equals("0")) {

                            Message message = new Message();
                            message.what = 1;
                            message.obj = stringObjectMap;
                            handler.sendMessage(message);

                        } else if (stringObjectMap.get("code").equals("1")) {
                            ToastUtils.showShort(MainActivity.this, (String) stringObjectMap.get("msg"));
                        }
                    }
                });
    }
}
