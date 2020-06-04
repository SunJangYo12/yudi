package com.laudy.group;

import android.util.Log;
import android.app.*;
import android.content.*;
import android.os.*;
import android.net.*;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toolbar.LayoutParams;
import android.widget.AbsListView;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.net.URL;
import java.io.*;
 
public class Myfragment extends Activity {

    private String[] imgid = null;
    private String[] keterangan = null;
    private String[] harga = null;
    private String[] komid = null;
    private String[] komSt = null;

    public static String tabIndex = "";
    public static String findResult = "";

    private MainActivity activity;
    private SharedPreferences settings;

    @Override
    public void onResume() {
        super.onResume();

        if (getIntent().getStringExtra("main").equals("search")) {
            imgid = null;
            keterangan = null;
            harga = null;
            komid = null;
            komSt = null;

            String[] dataSer = settings.getString("find", "").split("-_-");

            StringBuffer bufKet = new StringBuffer();
            for (int i=0; i<dataSer.length; i++) {
                bufKet.append(dataSer[i]+"\n");
            }
            keterangan = bufKet.toString().split("\n");

            for (int i=0; i<keterangan.length; i++) {
                Log.i("mikusan", keterangan[i]);
            }
        }
        else if (getIntent().getStringExtra("main").equals("back")) {
            startActivity(new Intent(this, MainActivity.class));
        }
        else {
            init();

            GridView gridview =(GridView)findViewById(R.id.gridview);
            gridview.setAdapter(new ImageAdapter(this));
            gridview.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(getBaseContext(), " pic selected "+position, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Myfragment.this, MainPreview.class);
                    intent.putExtra("imgsrc", ""+imgid[position]);
                    intent.putExtra("keterangan", ""+keterangan[position]);
                    intent.putExtra("harga", ""+harga[position]);

                    String[] kmtsrc1 = imgid[position].split("_images/");
                    String[] kmtsrc2 = kmtsrc1[1].split("/");
                    String kmtsrc = kmtsrc2[1]+".txt";

                    String[] txtsrc = kmtsrc.split(".jpg"); 

                    String outKmt = "_images/"+kmtsrc2[0]+"/koment/"+kmtsrc;
                    String outTxt = "_images/"+kmtsrc2[0]+"/txt/"+txtsrc[0]+txtsrc[1];

                    intent.putExtra("kmtsrc", outKmt);
                    intent.putExtra("txtsrc", outTxt);
                    intent.putExtra("katagori", tabIndex);

                    Myfragment.this.startActivity(intent);
                }
            });
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmentku);

        settings = getSharedPreferences("Settings", 0);
        activity = new MainActivity();
    }

    public void initFind(String result) {
        String[] dataSer = result.split("-_-");

        StringBuffer outKete = new StringBuffer();

        for (int i=0; i<dataSer.length; i++) {
            Log.i("mikusan", dataSer[i]);
            outKete.append(dataSer[i]+"\n");
        }
        keterangan = outKete.toString().split("\n");
    }

    public void init() {
        String[] dataSer = settings.getString("mobile", "").split("-_-");
        String[] dataKat = dataSer[0].split("-n-");
        String[] outKat = null;
        String[] dataImg = dataSer[1].split("-n-");
        String[] dataTxt = dataSer[2].split("-n-");

        for (int i=0; i<dataKat.length; i++) 
        {
            outKat = dataKat[i].split("/");
            if (outKat[1].equals(getIntent().getStringExtra("main"))) 
            {
                tabIndex = outKat[1];
            }
        }
        String[] exl = null;
        StringBuffer outImg = new StringBuffer();
        for (int i=0; i<dataImg.length; i++) 
        {
            exl = dataImg[i].split(tabIndex);
            try {
                Log.i("zzz", exl[1]);
                outImg.append(activity.server+"/"+exl[0]+tabIndex+exl[1]+"\n");
            }catch(Exception e) {}
        }
        imgid = outImg.toString().split("\n");

        StringBuffer outTxt = new StringBuffer();
        StringBuffer outTxtHar = new StringBuffer();
        for (int i=0; i<dataTxt.length; i++) 
        {
            exl = dataImg[i].split(tabIndex);
            try {
                Log.i("zzz", exl[1]);
                keterangan = dataTxt[i].split("-jin-");
                outTxt.append(keterangan[0]+"\n");
                outTxtHar.append(keterangan[1]+"\n");
            }catch(Exception e) {}
        }
        keterangan = outTxt.toString().split("\n");
        harga = outTxtHar.toString().split("\n");

        try {
            String[] dataKom = dataSer[3].split("-n-");
            String[] outKomKatagori = null;
            String[] tmpkom1 = null;
            StringBuffer outKom = new StringBuffer();
            StringBuffer outKomID = new StringBuffer();

            for (int i=0; i<dataKom.length; i++) {
                outKomKatagori = dataKom[i].split("-jin-");
                tmpkom1 = outKomKatagori[0].split("/");

                if (tmpkom1[1].equals(tabIndex)) {
                    //outKom.append(dataKom[i]+"\n");
                    String[] temp1 = dataKom[i].split(".jpg");
                    String[] temp2 = temp1[0].split("-jin-");
                    outKom.append(temp2[1]+"\n");

                    String[] temp3 = dataKom[i].split("-jin-");

                    for (int a=0; a<temp3.length; a++) 
                    {
                        try {
                            String[] komSplit = temp3[a].split("-sun-");
                            if (!komSplit[1].equals("")) {
                                outKomID.append(temp2[1]+"\n");
                            }
                        }catch(Exception e) {}
                    }
                }
            }
            komid = outKom.toString().split("\n");
            komSt = outKomID.toString().split("\n");
        }catch(Exception e) {}

        try {
            final String[] dataUp = dataSer[4].split("-n-");
            Log.i("mikusan", dataUp[0]);

            if (dataUp[0].equals("update")) {
                AlertDialog.Builder builderIndex = new AlertDialog.Builder(Myfragment.this);

                builderIndex.setTitle("Update");
                builderIndex.setMessage("Aplikasi terbaru sudah tersedia dengan fitur yang lebih lengkap silahkan klik tombol update dibawah ini");
                builderIndex.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) 
                    {
                        Uri uri = Uri.parse("market://details?id=com.laudy.group");
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        Myfragment.this.startActivity(goToMarket);
                    }
                });
                builderIndex.setNegativeButton("Nanti", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) 
                    {
                    }
                });
                builderIndex.create().show();
            }
        }catch(Exception e) {}
        
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
            return imgid.length;
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

            String ket = keterangan[position];
            if (keterangan[position].length() > 30) ket = ket.substring(0, 30)+"..."; 

            new DownloadImageTask(image).execute(imgid[position]);
            txtKete.setText(ket);
            txtHarg.setText("Rp"+harga[position]);

            try {
                for (int i=0; i<komid.length; i++) 
                {
                    if (Integer.parseInt(komid[i]) == (position+1)) 
                    {
                        int jum = 0;
                        for (int x=0; x<komSt.length; x++) {
                            if (komSt[x].equals(komid[i])) {
                                jum += 1;
                            }
                        }
                        txtKome.setText(""+jum+" Komentar");
                    }
                }
            }catch(Exception e) {}

            final int xposition = position;
            imageBuy.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v)
                {
                    Intent intent = new Intent(Myfragment.this, MainPreview.class);
                    intent.putExtra("imgsrc", ""+imgid[xposition]);
                    intent.putExtra("keterangan", ""+keterangan[xposition]);
                    intent.putExtra("harga", ""+harga[xposition]);

                    String[] kmtsrc1 = imgid[xposition].split("_images/");
                    String[] kmtsrc2 = kmtsrc1[1].split("/");
                    String kmtsrc = kmtsrc2[1]+".txt";

                    String outKmt = "_images/"+kmtsrc2[0]+"/koment/"+kmtsrc;

                    intent.putExtra("kmtsrc", outKmt);

                    Myfragment.this.startActivity(intent);
                }
            });

            return view;
        }
    }

    public void getViewImage(ImageView image, String source) {
        new DownloadImageTask(image).execute(source);
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
                Toast.makeText(Myfragment.this, "", Toast.LENGTH_SHORT).show();
            }
            else {
                result = Bitmap.createScaledBitmap(result, 320, 250, true);
                bmImage.setImageBitmap(result);
            }
        }
    }
}

