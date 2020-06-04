package com.laudy.group;

import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.text.*;
import java.lang.reflect.*;
import android.app.*;
import android.os.*;
import android.view.View;
import android.widget.ActionMenuView.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;
import android.widget.*;
import android.view.*;
import android.content.pm.*;
import android.net.*;
import android.content.*;
import android.provider.*;
import android.graphics.*;

public class MainCall extends Activity 
{
    private SharedPreferences settings;
    private String[] dataSer = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!settings.getString("siapa", "").equals("")) {
            String[] cekLogin = settings.getString("siapa", "").split("-jin-");

            if (cekLogin[0].equals("admin")) {
                new MenuInflater(this).inflate(R.menu.actions, menu);
            }
            else {
                new MenuInflater(this).inflate(R.menu.actions_member, menu);
            }
        }
        else {
            new MenuInflater(this).inflate(R.menu.actions_member, menu);
        }

        ImageView call = (ImageView)menu.findItem(R.id.mylayout).getActionView().findViewById(R.id.pesan);
        call.setBackgroundColor(Color.parseColor("#FF7500"));

        ImageView akun = (ImageView)menu.findItem(R.id.mylayout).getActionView().findViewById(R.id.akun);
        akun.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                MainCall.this.startActivity(new Intent(MainCall.this, AkunActivity.class));
                MainCall.this.finish();
            }
        });
        ImageView beranda = (ImageView)menu.findItem(R.id.mylayout).getActionView().findViewById(R.id.beranda);
        beranda.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                MainCall.this.startActivity(new Intent(MainCall.this, MainActivity.class));
                MainCall.this.finish();
            }
        });
        ImageView troll = (ImageView)menu.findItem(R.id.mylayout).getActionView().findViewById(R.id.troll);
        troll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                MainCall.this.startActivity(new Intent(MainCall.this, MainTroll.class));
                MainCall.this.finish();
            }
        });
        
        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public void onBackPressed()
    {
        MainCall.this.startActivity(new Intent(MainCall.this, MainActivity.class));
        MainCall.this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            Toast.makeText(this, "Klik beranda untuk aksi", Toast.LENGTH_LONG).show();
            return(true);
        }
        if (item.getItemId() == R.id.addKat) {
            Toast.makeText(this, "Klik beranda untuk aksi", Toast.LENGTH_LONG).show();
            return(true);
        }
        if (item.getItemId() == R.id.hapusKat) {
            Toast.makeText(this, "Klik beranda untuk aksi", Toast.LENGTH_LONG).show();
            return(true);
        }
        if (item.getItemId() == R.id.menprofile) {
            Toast.makeText(this, "Klik beranda untuk aksi", Toast.LENGTH_LONG).show();
            return(true);
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main_call);

        ImageView imgWhat = (ImageView)findViewById(R.id.callWhats);
        ImageView imgEmail = (ImageView)findViewById(R.id.callEmail);
        ImageView imgFbuk = (ImageView)findViewById(R.id.callFb);
        ImageView imgIg = (ImageView)findViewById(R.id.callIG);

        imgWhat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                PackageManager pm=getPackageManager();
                try {
                    String mobile = "6282234194456";
                    String msg = "hallo using laudy apps";
                    MainCall.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + mobile + "&text=" + msg)));

                } catch (Exception e) {
                    Toast.makeText(MainCall.this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
                }  
            }
        });

        imgFbuk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,"hallo using laudy apps");
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.facebook.orca");
                try {
                    MainCall.this.startActivity(sendIntent);
                }
                catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainCall.this,"Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
                }
            }
        });

        imgEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent email = new Intent(Intent.ACTION_SEND);  
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "sunjangyo12@gmail.com" });  
                email.putExtra(Intent.EXTRA_SUBJECT, "Tes");  
                email.putExtra(Intent.EXTRA_TEXT, "hallo using laudy apps");  
           
                //need this to prompts email client only  
                email.setType("message/rfc822");  
      
                MainCall.this.startActivity(Intent.createChooser(email, "Choose an Email client :"));  
            }
        });


        settings = getSharedPreferences("Settings", 0);
        getActionBar().setTitle("About Us");
        getActionBar().setSubtitle(null);
    }

}