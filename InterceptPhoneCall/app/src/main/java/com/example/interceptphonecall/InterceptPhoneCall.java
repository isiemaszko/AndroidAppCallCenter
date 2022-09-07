package com.example.interceptphonecall;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class InterceptPhoneCall extends BroadcastReceiver {

    private SharedPreferences sharedPreferences;

    private final OkHttpClient client=new OkHttpClient();
    private String json;
    private String text,date, val, price;
    private String errorResponse;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

       try{
           String state=intent.getStringExtra(TelephonyManager.EXTRA_STATE);

           String number=intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
           if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                if(number!=null){
                    sharedPreferences=context.getSharedPreferences("state", MODE_PRIVATE);

                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putBoolean("previousState",true);
                    editor.commit();

                    SharedPreferences sharedPreferences=context.getSharedPreferences("preferences",MODE_PRIVATE);
                    String codeQR= sharedPreferences.getString("codeQR",null);
                    String[] resultCode=codeQR.split(";");
                    String testPhoneNumber="797561231";
                    MediaType mediaType= MediaType.get("application/json; charset=utf-8");
                    json="{\n\t\"telephone\": \""+number+"\",\n\t\"posQrCode\": \""+codeQR+"\"\n}";
                    Log.e("LOGGGGG", " "+json);
                    String url=resultCode[1]+"/phoneReport";
                    // Toast.makeText(getApplicationContext(),""+json, Toast.LENGTH_SHORT).show();

                    RequestBody body = RequestBody.create(json, mediaType);
                    Request request = new Request.Builder()
                            //  .url("https://test.forgastro.pl/java/call_center/notification")
                            .url(url)
                            .post(body)
                            .build();
                    final CountDownLatch latch = new CountDownLatch(1);
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try  {
                                Response response = client.newCall(request).execute();
                                errorResponse=response.body().string();

                                Log.e("LOGGGGG", "response "+errorResponse);
                                latch.countDown();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    try{
                        thread.start();
                        latch.await();
                    } catch (Exception e){
                    }
                    String[] error=errorResponse.split(",");
                    Log.e("LOGGGGG", "ok "+error[0].length());

                    if(error[0].length()<20){
                        String[] value=error[2].split(":");
                        String[] count=error[3].split(":");
                        String[] lastClosingOrder=error[4].split(":");
                        lastClosingOrder[3]=lastClosingOrder[3].substring(0,lastClosingOrder[3].length()-1);

                        String data=lastClosingOrder[1];
                        String[] dat=data.split("T");
                        String hour=dat[1]+":"+lastClosingOrder[2]+":"+lastClosingOrder[3];


                    text="ostatnie zamówienie:\n "+dat[0]+" "+hour+"\n\n ilość: "+count[1]+"\n\n cena: "+value[1]+"zł";
                    date="ostatnie zamówienie: "+dat[0]+" "+hour;
                    val="ilość: "+count[1];
                    price="cena: "+value[1]+"zł";
                    Log.e("LOGGGGG", "text "+text);


                    }
                    else{
                        //error
                        text="Nieznany klient";

                    }

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Thread pageTimer = new Thread(){
                                @SuppressLint("WrongConstant")
                                public void run(){
                                    try{
                                        sleep(700);
                                    } catch (InterruptedException e){
                                        e.printStackTrace();
                                    } finally {
                                        Intent i = new Intent();
                                        i.setClass(context, PopupWindowActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        i.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                                        i.putExtra("INFO_CLIENT", text);
                                        i.setAction(Intent.ACTION_MAIN);
                                        i.addCategory(Intent.CATEGORY_LAUNCHER);
                                        context.startService(i);

                                    }
                                }
                            };
                            pageTimer.start();
                       }
                   }, 100);

                }

           }
           if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)){
               sharedPreferences=context.getSharedPreferences("state", MODE_PRIVATE);
               Boolean st= sharedPreferences.getBoolean("previousState",false);

               if(number!=null && st==true){
                  Toast.makeText(context, "Answered " + number, Toast.LENGTH_SHORT).show();

                   Intent i = new Intent(context, AppServices.class);
                   i.putExtra("updatedString",number);
                   i.setFlags(Intent.FLAG_FROM_BACKGROUND);
                   i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                   context.startService(i);

               }
           }
           if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)){
               if(number!=null){
                   sharedPreferences=context.getSharedPreferences("state", MODE_PRIVATE);

                   SharedPreferences.Editor editor=sharedPreferences.edit();
                   editor.putBoolean("previousState",false);
                   editor.commit();
              //     Toast.makeText(context, "Idle "+ st, Toast.LENGTH_SHORT).show();
               }

           }
       }catch (Exception  e){
           e.printStackTrace();
       }
    }
}

