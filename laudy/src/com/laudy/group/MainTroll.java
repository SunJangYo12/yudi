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
import android.net.*;
import android.content.*;
import android.provider.*;
import android.graphics.*;

public class MainTroll extends Activity 
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

        ImageView troll = (ImageView)menu.findItem(R.id.mylayout).getActionView().findViewById(R.id.troll);
        troll.setBackgroundColor(Color.parseColor("#FF7500"));

        ImageView akun = (ImageView)menu.findItem(R.id.mylayout).getActionView().findViewById(R.id.akun);
        akun.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                MainTroll.this.startActivity(new Intent(MainTroll.this, AkunActivity.class));
                MainTroll.this.finish();
            }
        });
        ImageView beranda = (ImageView)menu.findItem(R.id.mylayout).getActionView().findViewById(R.id.beranda);
        beranda.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                MainTroll.this.startActivity(new Intent(MainTroll.this, MainActivity.class));
                MainTroll.this.finish();
            }
        });
        ImageView call = (ImageView)menu.findItem(R.id.mylayout).getActionView().findViewById(R.id.pesan);
        call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                MainTroll.this.startActivity(new Intent(MainTroll.this, MainCall.class));
                MainTroll.this.finish();
            }
        });
        
        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public void onBackPressed()
    {
        MainTroll.this.startActivity(new Intent(MainTroll.this, MainActivity.class));
        MainTroll.this.finish();
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
        setContentView(R.layout.main_troll);

        settings = getSharedPreferences("Settings", 0);
        getActionBar().setTitle("Produk yg terbeli");
        getActionBar().setSubtitle(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.main_troll);

        settings = getSharedPreferences("Settings", 0);
        GridView gridview =(GridView)findViewById(R.id.gridtroll);

        if (settings.getString("siapa", "").equals("") || settings.getString("siapa", "").equals("gagal")) 
        {
            TextView txt = new TextView(this);
            txt.setText("Login untuk melihat daftar belanja");
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(15);

            setContentView(txt);
            Toast.makeText(this, "Klik menu akun untuk jadi member", Toast.LENGTH_LONG).show();
        }
        else {
            dataSer = settings.getString("mobile", "").split("-troll-");

            gridview.setVisibility(GridView.VISIBLE);
            gridview.setAdapter(new ImageAdapter(this));
            gridview.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    
                    String[] data = dataSer[position].split("-jin-");

                    String[] kmtsrc1 = data[2].split("_images/");
                    String[] kmtsrc2 = kmtsrc1[1].split("/");
                    String kmtsrc = kmtsrc2[1]+".txt";

                    String outKmt = "_images/"+kmtsrc2[0]+"/koment/"+kmtsrc;

                    Intent intent = new Intent(MainTroll.this, MainPreview.class);
                    intent.putExtra("imgsrc", ""+data[2]);
                    intent.putExtra("keterangan", ""+data[0]);
                    intent.putExtra("harga", ""+data[1]);
                    intent.putExtra("kmtsrc", outKmt);

                    MainTroll.this.startActivity(intent);
                }
            });
        }
    }

    public class ImageAdapter extends BaseAdapter
    {
        private Context context;
        private View view;

        public ImageAdapter(Context c)
        {
            context = c;
        }
        public int getCount() {
            return dataSer.length;
        }
        public Object getItem(int position) {
            return position;
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag, parent, false);
            ImageView image = (ImageView)view.findViewById(R.id.imgFrag);
            ImageView imageBuy = (ImageView)view.findViewById(R.id.imgFBeli);
            TextView txtKete = (TextView)view.findViewById(R.id.keterangan);
            TextView txtHarg = (TextView)view.findViewById(R.id.harga);
            TextView txtKome = (TextView)view.findViewById(R.id.koment);

            txtKete.setVisibility(View.INVISIBLE);
            txtHarg.setVisibility(View.INVISIBLE);
            txtKome.setVisibility(View.INVISIBLE);
            image.setVisibility(View.INVISIBLE);
            imageBuy.setVisibility(View.INVISIBLE);

            if (position != 0) {
                String[] data = dataSer[position].split("-jin-");

                txtKete.setVisibility(View.VISIBLE);
                txtHarg.setVisibility(View.VISIBLE);
                txtKome.setVisibility(View.VISIBLE);
                image.setVisibility(View.VISIBLE);
                imageBuy.setVisibility(View.VISIBLE);

                txtKete.setText(data[0]);
                txtHarg.setText(data[1]);


                try {
                    String[] imgurl = data[2].split("-n-");
                    new DownloadImageTask(image).execute(imgurl[0]);
                }
                catch(Exception e) {
                    new DownloadImageTask(image).execute(data[2]);
                }
            }

            return view;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                Toast.makeText(MainTroll.this, "", Toast.LENGTH_SHORT).show();
            }
            else {
                result = Bitmap.createScaledBitmap(result, 320, 250, true);
                bmImage.setImageBitmap(result);
            }
        }
    }

}