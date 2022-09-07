package com.example.interceptphonecall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    public SharedPreferences sharedPreferences;
    TextView textConnection;
    ImageView imageScanner;
    private String code;
    private String codeName;
    private String error;

    private String name;

    private final OkHttpClient client=new OkHttpClient();

    private CodeQRViewModel codeQRViewModel;
    private List<CodeQR> codes;

    private IntentIntegrator scanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        win.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
*/
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE) +ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_PHONE_STATE) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CALL_LOG)) {
                AlertDialog.Builder builder=new AlertDialog.Builder((MainActivity.this));
                builder.setTitle("Pozwolenie na wykorzystanie");
                builder.setMessage("Rejest połączeń, przechwytywanie połączeń");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CALL_LOG},
                                1
                        );
                    }
                });
                builder.setNegativeButton("Cancel",null);
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
            } else {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CALL_LOG},  1 );
            }
        }
        textConnection = (TextView) findViewById(R.id.connection);
        initDatabase();
        initView();

    }

    private void initDatabase(){

        codeQRViewModel= ViewModelProviders.of(this).get(CodeQRViewModel.class);
        codeQRViewModel.findAll().observe(this, new Observer<List<CodeQR>>() {
            @Override
            public void onChanged(List<CodeQR> codeQRS) {
                   codes=codeQRS;

                    if(codes.size()!=0){
                        for(CodeQR c:codes){
                            if(c.getId()==1){
                                if(c.getCode()!=null){
                                    statusConnection(c.getCode(),c.getName());
                                }
                                else{
                                    textConnection.setTextColor(Color.RED);
                                    textConnection.setText(getString(R.string.notconnection));

                                }


                                Log.e("LOGGGGG", "codeQR in database: "+c.getCode()+" "+c.getName());
                                sharedPreferences=getApplicationContext().getSharedPreferences("preferences",Context.MODE_PRIVATE);

                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("codeQR",c.getCode());
                                editor.commit();

                                code=c.getCode();
                                codeName=c.getName();
                            }
                        }
                    }
                    else{
                        textConnection.setTextColor(Color.RED);
                        textConnection.setText(getString(R.string.notconnection));
                    }
            }
        });
    }


    private void initView() {

        imageScanner=findViewById(R.id.img);

        scanner=new IntentIntegrator(this);
        scanner.setCaptureActivity(AnyOrientationCaptureActivity.class);
        scanner.setPrompt(getString(R.string.scanner_qr));
        scanner.setOrientationLocked(true);
        scanner.setCameraId(0);
        scanner.setBeepEnabled(true);
        imageScanner.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
               scanner.initiateScan();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void statusConnection(String codeQR, String nameCo){
        MediaType mediaType= MediaType.get("application/json; charset=utf-8");
        String json="{\"posQrCode\":\""+codeQR+"\"}";

        String[] resultPosQrCode=codeQR.split(";");
        //     Log.e("LOGGGGG", "posQRCode "+json);
        String url=resultPosQrCode[1]+"/test";
        //   Log.e("LOGGGGG", "Address "+url);
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                //.url("https://test.forgastro.pl/java/call_center/test")
                .url(url)
                .post(body)
                .build();

        final CountDownLatch latch = new CountDownLatch(1);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    Response response = client.newCall(request).execute();
                    error=response.body().string();

                    Log.e("LOGGGGG", "Error "+error);
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try{
            thread.start();
            latch.await();

            String[] er=error.split(",");
            String[] status=er[0].split(":");
            if(status[1]=="0"){
                textConnection.setTextColor(Color.RED);
                textConnection.setText(R.string.expiredConnection);
            }
            else {
                String connection=getString(R.string.connection)+" "+nameCo;
                textConnection.setTextColor(getResources().getColor(R.color.niagarra));
                textConnection.setText(connection);
            }
            //   Toast.makeText(getApplicationContext(),"successful sending request", Toast.LENGTH_SHORT).show();

        }
        catch (Exception e){
            //  Toast.makeText(getApplicationContext(),"request not found", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode,@Nullable Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, getString(R.string.qrcodenotfound), Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    MediaType mediaType= MediaType.get("application/json; charset=utf-8");
                    String json="{\"posQrCode\":\""+result.getContents()+"\"}";

                    String[] resultPosQrCode=result.getContents().split(";");
                    //     Log.e("LOGGGGG", "posQRCode "+json);
                    String url=resultPosQrCode[1]+"/test";
                    //   Log.e("LOGGGGG", "Address "+url);
                    RequestBody body = RequestBody.create(json, mediaType);
                    Request request = new Request.Builder()
                            //.url("https://test.forgastro.pl/java/call_center/test")
                            .url(url)
                            .post(body)
                            .build();

                    final CountDownLatch latch = new CountDownLatch(1);
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try  {
                                Response response = client.newCall(request).execute();
                                name=response.body().string();

                                Log.e("LOGGGGG", "Name "+name);
                                latch.countDown();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    try{
                        thread.start();
                        latch.await();
                        //   Toast.makeText(getApplicationContext(),"successful sending request", Toast.LENGTH_SHORT).show();

                    }
                    catch (Exception e){
                        //  Toast.makeText(getApplicationContext(),"request not found", Toast.LENGTH_SHORT).show();

                    }
                    String[] error=name.split(",");
                    char status=error[0].charAt(error[0].length()-1);
                    Log.e("LOGGGGG", "ok "+status);

                    if(status=='1'){
                        String[] tab= name.split("posName");
                        String[] tab1=tab[1].split(":");
                        name=tab1[1];
                        name = name.substring(0, name.length()-1);
                        name = name.substring(0, name.length()-1);
                        name = name.substring(1);


                        if(codes.size()!=0){
                            for(CodeQR c:codes){
                                if(c.getId()==1){
                                    //  Toast.makeText(getApplicationContext(),"CodeQR changes ", Toast.LENGTH_LONG).show();
                                    c.setCode(result.getContents());
                                    c.setName(name);
                                    codeQRViewModel.update(c);
                                    Log.e("LOGGGGG", "Changes codeQR "+c.getCode()+" "+c.getName());

                                }
                            }

                        }
                        else{
                            CodeQR newCodeQR=new CodeQR(1,result.getContents(),name);
                            codes.add(newCodeQR);
                            codeQRViewModel.insert(newCodeQR);
                            Log.e("LOGGGGG", "New codeQR "+newCodeQR.getCode()+" "+newCodeQR.getName());
                            // Toast.makeText(this, "New codeQR", Toast.LENGTH_LONG).show();
                        }
                        code=result.getContents();
                        codeName=name;
                        Log.e("LOGGGGG", "code "+code);
                        initDatabase();
                    }
                    else{

                        for(CodeQR c:codes){
                            if(c.getId()==1){
                                //  Toast.makeText(getApplicationContext(),"CodeQR changes ", Toast.LENGTH_LONG).show();
                                c.setCode(null);
                                c.setName(null);
                                codeQRViewModel.update(c);
                                Log.e("LOGGGGG", "Changes codeQR "+c.getCode()+" "+c.getName());

                            }
                        }
                        code=null;
                        codeName=null;
                        initView();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, ""+result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }




    @Override
    protected void onPause(){
        super.onPause();
        sharedPreferences=this.getSharedPreferences("preferences",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("codeQR",code);
        editor.commit();

        Log.e("LOGGGGG", "codeSP "+code);
    }

    @Override
    protected void onResume(){
        super.onResume();
        initView();
        initDatabase();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if(grantResults.length>0 && (grantResults[0]+grantResults[1]==PackageManager.PERMISSION_GRANTED)){
                Toast.makeText(this, getString(R.string.permisionGranded), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this,  getString(R.string.permisionDenied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }





 }