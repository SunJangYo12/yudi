package com.laudy.group;

import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.*;
import android.app.*;
import android.content.pm.*;
import android.os.*;
import android.view.View;
import android.widget.ActionMenuView.LayoutParams;
import android.util.Log;
import android.widget.*;
import android.view.*;
import android.net.*;
import android.content.*;
import android.provider.*;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;

public class MainWelcome extends Activity 
{
    private SharedPreferences settings;
    private String imgsrc = "";
    private String kmtsrc = "";
    private String ketera = "";
    private String harga = "";
    private String pembayaran = "";
    private Button btnBuy;
    private TextView txtKoment;
    private TextView txtKomentti;

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.main_welcome);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        getActionBar().setTitle(null);
        getActionBar().setSubtitle(null);
        getActionBar().hide();

        Button btnWel = (Button)findViewById(R.id.btnWelcome);
        btnWel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                MainWelcome.this.startActivity(new Intent(MainWelcome.this, MainActivity.class));
                MainWelcome.this.finish();
            }
        });
        
    }
}