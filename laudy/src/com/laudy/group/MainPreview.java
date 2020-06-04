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
import java.util.*;
import java.text.*;
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

public class MainPreview extends Activity 
{
    private SharedPreferences settings;
    private String imgsrc = "";
    private String kmtsrc = "";
    private String txtsrc = "";
    private String ketera = "";
    private String katagori = "";
    private String harga = "";
    private String pembayaran = "";
    private Button btnBuy;
    private TextView txtKoment;
    private TextView txtKomentti;
    private String[] kmtData = null;
 
    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main_view);

        settings = getSharedPreferences("Settings", 0);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        getActionBar().setTitle(null);
        getActionBar().setSubtitle(null);
        getActionBar().hide();

        ImageView image = (ImageView)findViewById(R.id.imgView);
        TextView txtKete = (TextView)findViewById(R.id.txtView);
        TextView txtHarg = (TextView)findViewById(R.id.hargaView);
        txtKoment = (TextView)findViewById(R.id.komentList);
        txtKomentti = (TextView)findViewById(R.id.komentView);
        Button btnKoment = (Button)findViewById(R.id.btnKoment);
        btnBuy = (Button)findViewById(R.id.btnBuy);

        Myfragment frag = new Myfragment();

        imgsrc = getIntent().getStringExtra("imgsrc");
        ketera = getIntent().getStringExtra("keterangan");
        harga = getIntent().getStringExtra("harga");
        kmtsrc = getIntent().getStringExtra("kmtsrc");
        txtsrc = getIntent().getStringExtra("txtsrc");
        katagori = getIntent().getStringExtra("katagori");

        frag.getViewImage(image, imgsrc);
        txtKete.setText(ketera);
        txtHarg.setText("Rp"+harga);

        btnBuy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                alertBayar();
            }
        });

        if (!settings.getString("siapa", "").equals("")) {
            String[] cekLogin = settings.getString("siapa", "").split("-jin-");

            if (cekLogin[0].equals("admin")) {
                image.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v)
                    {
                        AlertDialog.Builder builderIndex = new AlertDialog.Builder(MainPreview.this);

                        final String[] aksi = {"Hapus produk", "Edit produk", "Hapus komentar"};
                
                        builderIndex.setTitle("Produk manager");
                        builderIndex.setItems(aksi, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) 
                            {
                                if (item == 0) {
                                    AlertDialog.Builder builderKu = new AlertDialog.Builder(MainPreview.this);
                                    builderKu.setTitle("Peringatan!!");
                                    builderKu.setMessage("Yakin anda mau hapus produk ini");
                                    builderKu.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) 
                                        {
                                            MainActivity main = new MainActivity();
                                            CallWebPageTask task = new CallWebPageTask();
                                            task.applicationContext = MainPreview.this;

                                            String[] ximgsrc = imgsrc.split("laudy/");

                                            String outTxt = txtsrc+"-jin-"+ximgsrc[1]+"-jin-"+katagori+"-jin-"+kmtsrc;

                                            outTxt = new AkunActivity().urlEngkoder(outTxt);

                                            try {
                                                task.execute(new String[] { main.server+"/json.php?produk=hapus-jin-"+outTxt });
                                            }catch(Exception e) {}
                                        }
                                    });
                                    builderKu.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) 
                                        {}
                                    });
                                    AlertDialog alert11 = builderKu.create();
                                    alert11.show();
                                }
                                else if (item == 1) {
                                    alertEdit();
                                }
                                else if (item == 2 && kmtData != null) {
                                    AlertDialog.Builder builderIndex = new AlertDialog.Builder(MainPreview.this);
                
                                    builderIndex.setTitle("Kometar manager");
                                    builderIndex.setItems(kmtData, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int item) 
                                        {
                                            MainActivity main = new MainActivity();
                                            CallWebPageTask task = new CallWebPageTask();
                                            task.applicationContext = MainPreview.this;


                                            String outTxt = kmtsrc+"-jin-"+kmtData[item];

                                            outTxt = new AkunActivity().urlEngkoder(outTxt);

                                            try {
                                                //task.execute(new String[] { main.server+"/json.php?komentHapus="+outTxt });
                                            }catch(Exception e) {}
                                    
                                            Toast.makeText(MainPreview.this, kmtData[item], Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    builderIndex.create().show();
                                }
                            }
                        });
                        builderIndex.create().show();
                    }
                });
            }
        }

        CallWebPageTask task = new CallWebPageTask();
        task.applicationContext = this;
        try {
            task.execute(new String[] { new MainActivity().server+"/json.php?komentView="+kmtsrc });
        }catch(Exception e) {}

        btnKoment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                SharedPreferences settings = MainPreview.this.getSharedPreferences("Settings", 0);
                if (settings.getString("siapa", "").equals("") || settings.getString("siapa", "").equals("gagal")) 
                {
                    AlertDialog.Builder builderIndex = new AlertDialog.Builder(MainPreview.this);

                    builderIndex.setTitle("Peringatan!!");
                    builderIndex.setMessage("Untuk berkomentar anda harus jadi member silahkan buat akun dibawah ini");
                    builderIndex.setPositiveButton("Buat akun", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) 
                        {
                            MainPreview.this.startActivity(new Intent(MainPreview.this, AkunActivity.class));
                        }
                    });
                    builderIndex.create().show();
                }
                else {
                    AlertDialog.Builder builderIndex = new AlertDialog.Builder(MainPreview.this);

                    final String[] user = settings.getString("siapa", "").split("-jin-");

                    final EditText editKoment = new EditText(MainPreview.this);
                    editKoment.setEms(80);
                    editKoment.setHint("text disini...");

                    builderIndex.setTitle("Nama anda : "+user[0]);
                    builderIndex.setMessage("Komentar anda akan ditampilkan dipublik jadi yang sopanlah untuk berkomentar");
                    builderIndex.setPositiveButton("Koment", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) 
                        {
                            MainActivity act = new MainActivity();
                            act.mainRequest(MainPreview.this, "web", act.server+"/json.php?koment="+kmtsrc+new AkunActivity().urlEngkoder("-jin-"+user[0]+"-jin-"+editKoment.getText().toString()+"-sun-"+new SimpleDateFormat("HH:mm   dd-MMM-yyy").format(new Date())));

                            Toast.makeText(MainPreview.this, "Komentar berhasil ditambahkan", Toast.LENGTH_LONG).show();
                            
                            CallWebPageTask task = new CallWebPageTask();
                            task.applicationContext = MainPreview.this;
                            try {
                                task.execute(new String[] { new MainActivity().server+"/json.php?komentView="+kmtsrc });
                            }catch(Exception e) {}
                        }
                    });
                    builderIndex.setNegativeButton("Refresh", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) 
                        {
                            Toast.makeText(MainPreview.this, "Refresh komentar...", Toast.LENGTH_LONG).show();
                            
                            CallWebPageTask task = new CallWebPageTask();
                            task.applicationContext = MainPreview.this;
                            try {
                                task.execute(new String[] { new MainActivity().server+"/json.php?komentView="+kmtsrc });
                            }catch(Exception e) {}
                        }
                    });
                    builderIndex.setView(editKoment);
                    builderIndex.create().show();
                }
            }
        });
    }

    private void alertEdit() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainPreview.this);
        builder1.setTitle("Edit produk");
        builder1.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.alert_edit, null);

        final EditText edtAdd = (EditText) layout.findViewById(R.id.edtpdk);
        final EditText edtAddHar = (EditText) layout.findViewById(R.id.edthpdk);
        Button btnFoto = (Button)layout.findViewById(R.id.btnfotopdk);
        ImageView imgFoto = (ImageView)layout.findViewById(R.id.imagepFoto);
        
        edtAdd.setText(ketera);
        edtAddHar.setText(harga);
        btnFoto.setEnabled(false);

        builder1.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) 
            {
                MainActivity main = new MainActivity();

                CallWebPageTask task = new CallWebPageTask();
                task.applicationContext = MainPreview.this;

                String title = edtAdd.getText().toString();
                String xharga = edtAddHar.getText().toString();

                String outTxt = txtsrc+"-jin-"+title+"-jin-"+xharga;
                outTxt = new AkunActivity().urlEngkoder(outTxt);

                try {
                    task.execute(new String[] { main.server+"/json.php?produk=edit-jin-"+outTxt });
                }catch(Exception e) {}
            }
        });
        builder1.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) 
            {
                
            }
        });
        builder1.setView(layout);
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void onClickWhatsApp(String message) {
        PackageManager pm=getPackageManager();
        try {
            String mobile = "6282234194456";
            String msg = message;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + mobile + "&text=" + msg)));

        } catch (Exception e) {
            Toast.makeText(MainPreview.this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
        }  
    }

    private void alertBayar() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainPreview.this);
        builder1.setTitle("Pembelian");
        builder1.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.alert_beli, null);

        final TextView txtNama = (TextView) layout.findViewById(R.id.bayarnama);
        final TextView txtHarga = (TextView) layout.findViewById(R.id.bayarharga);
        final Button btnBayar = (Button)layout.findViewById(R.id.btnPembayaran);
        final EditText edtAlamat = (EditText)layout.findViewById(R.id.alamat);
        final TextView txtAlamat = (TextView)layout.findViewById(R.id.txtAlamat);
        final EditText edtNama = (EditText)layout.findViewById(R.id.edtNama);
        final TextView txtbNama = (TextView)layout.findViewById(R.id.txtNama);
        final TextView txtToko = (TextView)layout.findViewById(R.id.txtToko);

        String ket = ketera;
        if (ketera.length() > 30) ket = ket.substring(0, 30)+"...";

        txtNama.setText(ket);
        txtHarga.setText("Rp"+harga);

        final String[] aksi = {"bayar ditempat (COD)", "Di toko"};

        btnBayar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                AlertDialog.Builder builderIndex = new AlertDialog.Builder(MainPreview.this);

                builderIndex.setTitle("Metode");
                builderIndex.setItems(aksi, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) 
                    {
                        if (item == 0) {
                            txtToko.setVisibility(TextView.INVISIBLE);
                            txtAlamat.setVisibility(TextView.VISIBLE);
                            edtAlamat.setVisibility(EditText.VISIBLE);
                            txtbNama.setVisibility(TextView.VISIBLE);
                            edtNama.setVisibility(EditText.VISIBLE);
                        }
                        else if (item == 1) {
                            txtAlamat.setVisibility(TextView.INVISIBLE);
                            edtAlamat.setVisibility(EditText.INVISIBLE);
                            txtbNama.setVisibility(TextView.INVISIBLE);
                            edtNama.setVisibility(EditText.INVISIBLE);
                            txtToko.setVisibility(TextView.VISIBLE);
                        }
                        btnBayar.setText(aksi[item]);
                        pembayaran = aksi[item];
                    }
                });
                builderIndex.create().show();
            }
        });

        builder1.setPositiveButton("Kirim ke Whatsapp", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) 
            {
                if (pembayaran.equals("")) {
                    Toast.makeText(MainPreview.this, "Harap pilih salah metode pembayaran", Toast.LENGTH_LONG).show();
                }
                else {
                    if (settings.getString("siapa", "").equals("") || settings.getString("siapa", "").equals("gagal")) 
                    {
                        if (pembayaran.equals("Di toko"))
                            onClickWhatsApp(pembayaran+"\n\n"+"Produk : "+ketera+"\n\nHarga : "+harga);
                        else
                            onClickWhatsApp(pembayaran+"\n\n"+"Produk : "+ketera+"\n\nHarga : "+harga+"\nAlamat : "+edtAlamat.getText().toString()+"\nNama : "+edtNama.getText().toString());
                    }
                    else {
                        String[] cekLogin = settings.getString("siapa", "").split("-jin-");
                        String nama = new AkunActivity().urlEngkoder(cekLogin[0]);
                        String pass = new AkunActivity().urlEngkoder(cekLogin[1]);

                        MainActivity main = new MainActivity();

                        CallWebPageTask task = new CallWebPageTask();
                        task.applicationContext = MainPreview.this;

                        String trurl = new AkunActivity().urlEngkoder("-jin-"+cekLogin[0]+"-jin-"+ketera+"-jin-"+harga+"-jin-"+imgsrc);

                        try {
                            task.execute(new String[] { main.server+"/json.php?akun=troll"+trurl });
                        }catch(Exception e) {}
                        
                        if (pembayaran.equals("Di toko"))
                            onClickWhatsApp(pembayaran+"\n\n"+"Produk : "+ketera+"\n\nHarga : "+harga);
                        else
                            onClickWhatsApp(pembayaran+"\n\n"+"Produk : "+ketera+"\n\nHarga : "+harga+"\nAlamat : "+edtAlamat.getText().toString()+"\nNama : "+edtNama.getText().toString());
                    }
                }
            }
        });
        
        builder1.setView(layout);
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private class CallWebPageTask extends AsyncTask<String, Void, String> 
    {
        protected Context applicationContext;
        protected String main = "";

        // connecting...
        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... data) {
            String sret = "";
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 50000);
            HttpConnectionParams.setSoTimeout(httpParams, 50000);

            HttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpGet request = new HttpGet(data[0]);
            try{
                HttpResponse response = httpClient.execute(request);

                try { // split result
                    InputStream in = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder str = new StringBuilder();
                    String line = null;
                    while((line = reader.readLine()) != null){
                        str.append(line);
                    }
                    in.close();
                    sret = str.toString();
                } catch(Exception ex) {}
            }
            catch(Exception ex){
            }
            return sret;
        }

        // berhasil
        @Override
        protected void onPostExecute(String result) {
            Log.i("mikusan", result);
            
            if (result.equals("sukses troll")) {
                Toast.makeText(MainPreview.this, "Sukses menambah ke database", Toast.LENGTH_LONG).show();
            }
            else if (result.equals("sukses edit")) {
                Toast.makeText(MainPreview.this, "Sukses edit produk refresh aplikasi untuk melihat perubahan", Toast.LENGTH_LONG).show();
            }
            else if (result.equals("hapus oke")) {
                Toast.makeText(MainPreview.this, "Sukses hapus produk refresh aplikasi untuk melihat perubahan", Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    if (!result.equals("0")) {
                        kmtData = result.split("-jin-");
                        txtKomentti.setText(""+kmtData.length+" Komentar");

                        StringBuffer out = new StringBuffer();

                        String[] kom = null;
                        for (int i=0; i<kmtData.length; i++) 
                        {
                            kom = kmtData[i].split("-sun-");
                            out.append("<font color='blue'>"+kom[0]+"</font><br>"+kom[1]+"<br><br><font color='red'><tt>"+kom[2]+"</tt></font><br><br><br>");
                        }

                        Spanned htmlAsSpanned = Html.fromHtml(out.toString());

                        txtKoment.setText(htmlAsSpanned);
                    }
                }
                catch(Exception e) {}
            }
        }
    }
}