package com.tec.zhang.prv;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tec.zhang.prv.databaseUtil.AccountData;
import com.tec.zhang.prv.databaseUtil.Accounts;
import org.litepal.crud.DataSupport;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;


public class LoginActivity extends BaseActivity {
    //private OkHttpClient client = new OkHttpClient();
    private CircleImageView header;
    private EditText userName;
    private EditText password;
    private TextView notice;
    private CheckBox checkBox;
    private String realName;
    private FloatingActionButton login;
    private ProgressBar progressBar;
    /*private String[] permission = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.BODY_SENSORS
    };*/
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.ic_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(0xffffffff));
        SpannableString ss = new SpannableString("泄压阀");
        ss.setSpan(new ForegroundColorSpan(Color.BLACK),0,3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        actionBar.setTitle(new SpannableString(ss));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        header = (CircleImageView) findViewById(R.id.circleImageView);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        userName = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);
        autoFillBlank();
        userName.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        login = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        if (userName.getText().toString().equals("")||password.getText().toString().equals("")) {
            login.setEnabled(false);
        }
        notice = (TextView) findViewById(R.id.notice);
        /*if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
            requestNeedPermission();
        }*/
        login.setOnClickListener(v -> {
            String account = userName.getText().toString();
            String pass = password.getText().toString();
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            Log.d(TAG, "onCreate: " + account + "    pass:" + pass);
            Accounts accounts = DataSupport.where("userName=?",account).findFirst(Accounts.class);
            Log.d(TAG, "onCreate: account长度"  + DataSupport.findAll(Accounts.class).size());
            if (accounts!= null && accounts.getPassWord().equals(pass)){
                AccountData ad = new AccountData();
                ad.setUserName(account);
                ad.setPassWord(pass);
                ad.setRemenber(checkBox.isChecked());
                ad.save();
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeScaleUpAnimation(login,login.getWidth()/2,login.getHeight()/2,0,0);
                ActivityCompat.startActivity(LoginActivity.this,new Intent(LoginActivity.this,Search.class),compat.toBundle());
            }else {
                progressBar.setVisibility(View.GONE);
                Toasty.warning(this,"账户名或者密码错误",Toast.LENGTH_LONG).show();
            }
            //new ConnectServer().execute();
        });
    }
    private void autoFillBlank() {
        try {
            AccountData ad = DataSupport.findLast(AccountData.class);
            if (ad == null ) return ;
            if (ad.isRemenber()){
                userName.setText(ad.getUserName());
                password.setText(ad.getPassWord());
                checkBox.setChecked(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>20){
                    notice.setText("够了够了！这么长的名字你自己记得住么！(ง •_•)ง");
                }
                if (userName.getText().toString().equals("")||password.getText().toString().equals("")){
                    if (login.isEnabled()){
                        login.setEnabled(false);
                    }
                }
                if ((!userName.getText().toString().equals("")) && (!password.getText().toString().equals(""))){
                    if (!login.isEnabled()){
                        login.setEnabled(true);
                    }
                }
            }

        @Override
        public void afterTextChanged(Editable s) {}
    };
    private void requestNeedPermission() {
            if (ContextCompat.checkSelfPermission(LoginActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED||
                    ContextCompat.checkSelfPermission(LoginActivity.this,
                            Manifest.permission.BODY_SENSORS )!= PackageManager.PERMISSION_GRANTED
                    ){
                //ActivityCompat.requestPermissions(LoginActivity.this,permission,1);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode){
                case 1:
                    if (grantResults.length>0){
                        for (int i = 0;i<grantResults.length;i++){
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                                notice.setText("对不起，本应用无授权不能运行！！(•́へ•́ ╬)");
                                notice.setVisibility(View.VISIBLE);
                                login.setVisibility(View.GONE);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
    }
    /*public class ConnectServer extends AsyncTask<Void,Void,Boolean>{
        String account;
        String pass;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            account = userName.getText().toString();
            pass = password.getText().toString();
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            final boolean[] loginResult = {false};
            Request request = new Request.Builder().get().url(BaseActivity.SERVER_ADDRESS +
                    "login?userName=" + account + "&password=" + pass).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        JSONObject jo= new JSONObject(URLDecoder.decode(response.body().string(),"GBK"));
                        if (jo.getString("state").equals("1")){
                            Log.d(TAG, jo.getString("realName")+"登录成功");
                            AccountData lastOne = DataSupport.findLast(AccountData.class);
                            if ((lastOne ==null) || !(lastOne.getName().equals(account)&&lastOne.getPassword().
                                    equals(pass)&&lastOne.
                                    getAccountRight() == Integer.parseInt(jo.getString("right") ))){
                                AccountData ad = new AccountData();
                                ad.setName(account);
                                ad.setPassword(pass);
                                ad.setRemember(checkBox.isChecked());
                                ad.setAccountRight(Integer.parseInt(jo.getString("right")));
                                ad.setRealName(jo.getString("realName"));
                                realName = jo.getString("realName");
                                ad.save();
                            }
                            loginResult[0] = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return loginResult[0];
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                progressBar.setVisibility(View.GONE);
                notice.setText("登录成功٩(๑^o^๑)۶");
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeScaleUpAnimation(login,login.getWidth()/2,login.getHeight()/2,0,0);
                ActivityCompat.startActivity(LoginActivity.this,new Intent(LoginActivity.this,Transaction.class),compat.toBundle());
                //Intent intent = new Intent(LoginActivity.this,Transaction.class);
                //startActivity(intent);
            }else{
                progressBar.setVisibility(View.GONE);
                notice.setText("登录失败，用户名或者密码错误，或者服务器大姨妈来了");
            }
        }
    }
*/
}

