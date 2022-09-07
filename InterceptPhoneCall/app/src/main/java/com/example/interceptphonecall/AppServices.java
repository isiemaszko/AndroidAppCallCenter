package com.example.interceptphonecall;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.security.spec.ECField;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppServices extends Service implements LifecycleOwner {

    private final OkHttpClient client=new OkHttpClient();
    private String json;

    private String errorResponse;

    public AppServices(){

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        onTaskRemoved(intent);
        Bundle extras = intent.getExtras();
        if(extras!=null){
            SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("preferences",MODE_PRIVATE);
            String codeQR= sharedPreferences.getString("codeQR",null);
            //  Toast.makeText(getApplicationContext(),"codeQR in service "+ code, Toast.LENGTH_LONG).show();
            //  Toast.makeText(getApplicationContext(),"answer phone number "+extras.getString("updatedString"), Toast.LENGTH_SHORT).show();
            Log.e("LOGGGGG", "codeQR in service"+codeQR);

            String[] resultCode=codeQR.split(";");
            MediaType mediaType= MediaType.get("application/json; charset=utf-8");
            json="{\n\t\"telephone\": \""+extras.getString("updatedString")+"\",\n\t\"posQrCode\": \""+codeQR+"\"\n}";
            Log.e("LOGGGGG", " "+json);
            String url=resultCode[1]+"/notification";
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

                if(errorResponse.length()<24){
                    Toast.makeText(getApplicationContext(),getString(R.string.success_sending), Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(getApplicationContext(),errorResponse, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e){
                Toast.makeText(getApplicationContext(),getString(R.string.not_found), Toast.LENGTH_SHORT).show();
            }

        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }
}
