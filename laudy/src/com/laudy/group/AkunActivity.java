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
import android.util.Log;
import android.widget.*;
import android.view.*;
import android.net.*;
import android.content.*;
import android.provider.*;
import android.graphics.*;
import android.database.Cursor;

public class AkunActivity extends Activity 
{
    private static int LOAD_IMAGE_RESULT = 1;
    private File dir;
    private EditText edtName;
    private EditText edtPass;
    private EditText edtSignNama;
    private EditText edtSignPass;
    private EditText edtSignAlam;
    private TextView title;
    private TextView txtmember;
    private TextView titTitle;
    private TextView titPass;
    private Button btnLogin;
    private Button btnSign;
    private MainActivity mainEx;
    private ImageView imgProfil;
    private ImageView imgViewFoto;
    private LinearLayout mainLayout;
    private SharedPreferences settings;
    private static boolean cekPass = false;
    private static boolean yoshFoto = false;
    private static boolean circleImg = true;
    private static String imagesrc, inPathProfil = "";
    private static String saveName, savePass, saveAlam = "";
    private String[] datMemName = null;
    private String[] datMemAlam = null;
    private String[] datMemTroll = null;

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
        
        ImageView akun = (ImageView)menu.findItem(R.id.mylayout).getActionView().findViewById(R.id.akun);
        akun.setBackgroundColor(Color.parseColor("#FF7500"));

        ImageView beranda = (ImageView)menu.findItem(R.id.mylayout).getActionView().findViewById(R.id.beranda);
        beranda.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                AkunActivity.this.startActivity(new Intent(AkunActivity.this, MainActivity.class));
            	AkunActivity.this.finish();
            }
        });

        ImageView troll = (ImageView)menu.findItem(R.id.mylayout).getActionView().findViewById(R.id.troll);
        troll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                AkunActivity.this.startActivity(new Intent(AkunActivity.this, MainTroll.class));
                AkunActivity.this.finish();
            }
        });
        ImageView call = (ImageView)menu.findItem(R.id.mylayout).getActionView().findViewById(R.id.pesan);
        call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                AkunActivity.this.startActivity(new Intent(AkunActivity.this, MainCall.class));
                AkunActivity.this.finish();
            }
        });
        
        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public void onBackPressed()
    {
        AkunActivity.this.finish();
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
        setContentView(R.layout.main_akun);

        mainEx = new MainActivity();

        settings = getSharedPreferences("Settings", 0);
        if (!settings.getString("siapa", "").equals("gagal")) 
        {
            cekPass = true;
            String[] cekLogin = settings.getString("siapa", "").split("-jin-");

            try {
                String dataLog = urlEngkoder(cekLogin[0]+"-jin-"+cekLogin[1]);

                MainActivity main = new MainActivity();

                CallWebPageTask task = new CallWebPageTask();
                task.applicationContext = AkunActivity.this;

                task.execute(new String[] { main.server+"/json.php?akun=login-jin-"+dataLog });
            }catch(Exception e) {}            

        }

        edtName = (EditText)findViewById(R.id.edtLoginName);
        edtPass = (EditText)findViewById(R.id.edtLoginPass);

        title = (TextView)findViewById(R.id.titleAkun);
        txtmember = (TextView)findViewById(R.id.akunMember);

        titTitle = (TextView)findViewById(R.id.titNama);
        titPass = (TextView)findViewById(R.id.titPass);

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnSign = (Button)findViewById(R.id.btnSign);

        btnSign.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                alertSign();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String nama = edtName.getText().toString();
                String pass = edtPass.getText().toString();

                nama = urlEngkoder(nama);
                pass = urlEngkoder(pass);

                MainActivity main = new MainActivity();

                CallWebPageTask task = new CallWebPageTask();
                task.applicationContext = AkunActivity.this;

                try {
                    task.execute(new String[] { main.server+"/json.php?akun=login-jin-"+nama+"-jin-"+pass });
                }catch(Exception e) {}
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOAD_IMAGE_RESULT && resultCode == RESULT_OK && data != null){
            Uri pickedImage=data.getData();

            String[] filePath={MediaStore.Images.Media.DATA};
            Cursor cursor=getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();

            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            imgProfil.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            cursor.close();

            inPathProfil = imagePath;
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        }
    }

    public String urlEngkoder(String text) {
        String hash = "";
        String[] pros = { text };
        try {
            for (String s : pros)     
            {
                hash = URLEncoder.encode(s, "UTF-8");       
            }
        }catch (Exception e) {}

        return hash;
    }
    private void alertSign() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AkunActivity.this);
        builder1.setTitle("Daftar member baru");
        builder1.setCancelable(false);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.alert_sign, null);

        edtSignNama = (EditText) layout.findViewById(R.id.edtSignNama);
        edtSignPass = (EditText) layout.findViewById(R.id.edtSignPass);
        edtSignAlam = (EditText) layout.findViewById(R.id.edtSignAlam);

        builder1.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) 
            {
                String nama = edtSignNama.getText().toString();
                String pass = edtSignPass.getText().toString();
                String alam = edtSignAlam.getText().toString();

                saveName = nama;
                savePass = pass;
                saveAlam = alam;

                nama = urlEngkoder(nama);
                pass = urlEngkoder(pass);
                alam = urlEngkoder(alam);

                MainActivity main = new MainActivity();

                CallWebPageTask task = new CallWebPageTask();
                task.applicationContext = AkunActivity.this;

                try {
                    task.execute(new String[] { main.server+"/json.php?akun=sign-jin-"+nama+"-jin-"+pass+"-jin-"+alam });
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

    private void alertEditAkun() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AkunActivity.this);
        builder1.setTitle("Edit Profil");
        builder1.setCancelable(false);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.alert_edit_profil, null);

        final EditText edtName = (EditText)layout.findViewById(R.id.edtProfilNama);
        final EditText edtPass = (EditText)layout.findViewById(R.id.edtProfilPass);
        final Button btnFoto = (Button)layout.findViewById(R.id.btnFotoProfil);
        imgProfil = (ImageView)layout.findViewById(R.id.imageFotoProfil);

        final String[] cekLogin = settings.getString("siapa", "").split("-jin-");

        edtName.setHint("Old Name: "+cekLogin[0]);
        edtPass.setHint("Old Password: "+cekLogin[1]);

        btnFoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                yoshFoto = true;

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                AkunActivity.this.startActivityForResult(i, LOAD_IMAGE_RESULT);
            }
        });

        builder1.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) 
            {
                CallWebPageTask task = new CallWebPageTask();
                task.applicationContext = AkunActivity.this;

                if (!edtName.getText().toString().equals("") || !edtName.getText().toString().equals("")) {

                    //?akun=update-jin-tes-jin-tes-jin-newtes-jin-newtes-jin-http://10.42.0.1/laudy/akunimg/newrohman.jpg

                    String data = "";
                    if (imagesrc.equals(""))
                        data = urlEngkoder(cekLogin[0]+"-jin-"+cekLogin[1]+"-jin-"+edtName.getText().toString()+"-jin-"+edtPass.getText().toString());
                    else
                        data = urlEngkoder(cekLogin[0]+"-jin-"+cekLogin[1]+"-jin-"+edtName.getText().toString()+"-jin-"+edtPass.getText().toString()+"-jin-"+imagesrc);

                    try {
                        task.execute(new String[] { mainEx.server+"/json.php?akun=update-jin-"+data });
                    }catch(Exception e) {}
                }
                
                if (yoshFoto) {
                    mainEx.uploadImage(AkunActivity.this, inPathProfil, dir+"/2.jpg");
                    try {
                        task.execute(new String[] { mainEx.server+"/json.php?akun=imgrename-jin-"+cekLogin[0] });
                    }catch(Exception e) {}
                }
                Toast.makeText(AkunActivity.this, "Update akun sukses", Toast.LENGTH_LONG).show();
                AkunActivity.this.finish();
                AkunActivity.this.startActivity(new Intent(AkunActivity.this, MainActivity.class));
            }
        });
        builder1.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) 
            {}
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

            final String[] dateRes = result.split("-login-");

            if (dateRes[0].equals("login berhasil")) {
                StringBuffer saved = new StringBuffer();

                try {
                    if (edtName.getText().toString().equals("") && edtPass.getText().toString().equals("")) {
                        saved.append(edtSignNama.getText().toString()+"-jin-");
                        saved.append(edtSignPass.getText().toString()+"-jin-");
                        saved.append(dateRes[2]);
                    }
                    else {
                        if (settings.getString("siapa", "").equals("") || settings.getString("siapa", "").equals("gagal")) {
                            saved.append(edtName.getText().toString()+"-jin-");
                            saved.append(edtPass.getText().toString()+"-jin-");
                            saved.append(dateRes[2]);
                        }
                    }
                }catch(Exception e) {
                    saved.append(settings.getString("siapa", ""));
                }

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("siapa", saved.toString());
                editor.commit();
                
                if (!cekPass) {
                    onResume();
                }
                else {
                    cekPass = false;
                    setContentView(R.layout.main_akun_sukses);
                    AkunActivity.this.getActionBar().setTitle("Profil Anda");

                    TextView textSuk = (TextView)findViewById(R.id.titleAkunSukses);
                    TextView textLogout = (TextView)findViewById(R.id.titleAkunLogout);
                    TextView textDate = (TextView)findViewById(R.id.titleAkunDate);
                    Button btnSuk = (Button)findViewById(R.id.btnAkunSukses);
                    ImageView imgPF = (ImageView)findViewById(R.id.fotoprofil);
                    Button btnSukTroll = (Button)findViewById(R.id.btnAkunSuksesTroll);
                    Button btnSukMem = (Button)findViewById(R.id.btnAkunSuksesClient);

                    String[] cekLogin = settings.getString("siapa", "").split("-jin-");

                    new DownloadImageTask(imgPF).execute(mainEx.server+"/akunimg/"+cekLogin[0]+".jpg");

                    textSuk.setText("Nama   : "+cekLogin[0]+"\n"+"Alamat : "+cekLogin[2]);

                    try {
                        textDate.setText("Bergabung pada: "+dateRes[1]);
                    }
                    catch(Exception e) {}

                    if (cekLogin[0].equals("admin")) {
                        btnSukMem.setVisibility(View.VISIBLE);
                        btnSukMem.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v)
                            {
                                AlertDialog.Builder builderIndex = new AlertDialog.Builder(AkunActivity.this);

                                final String[] datMember = dateRes[3].split("-sun-");
                                
                                StringBuffer bufMemName = new StringBuffer();
                                StringBuffer bufMemAlam = new StringBuffer();
                                
                                for (int i=0; i<datMember.length; i++) {
                                    String[] temp1 = datMember[i].split("-jin-");
                                    bufMemName.append(temp1[0]+"\n");
                                    bufMemAlam.append(temp1[2]+"\n");
                                }

                                datMemName = bufMemName.toString().split("\n");
                                datMemAlam = bufMemAlam.toString().split("\n");

                                builderIndex.setTitle("Daftar Member");
                                builderIndex.setItems(datMemName, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) 
                                    {
                                        alertView(datMemAlam[item], datMemName[item]);
                                    }
                                });
                                builderIndex.create().show();
                            }
                        });
                    }

                    textLogout.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v)
                        {
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("siapa", "gagal");
                            editor.commit();

                            AkunActivity.this.finish();

                            Toast.makeText(AkunActivity.this, "Logout berhasil", Toast.LENGTH_LONG).show();
                        }
                    });
                    btnSuk.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v)
                        {
                            alertEditAkun();
                        }
                    });
                    btnSukTroll.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v)
                        {
                            AkunActivity.this.startActivity(new Intent(AkunActivity.this, MainTroll.class));
                        }
                    });

                    Toast.makeText(applicationContext, "Login berhasil", Toast.LENGTH_LONG).show();
                }
            }
            else if (result.equals("sukses sign")) {

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("siapa", saveName+"-jin-"+savePass+"-jin-"+saveAlam);
                editor.commit();

                AlertDialog.Builder builderIndex = new AlertDialog.Builder(AkunActivity.this);
                builderIndex.setTitle("berhasil");
                builderIndex.setMessage("Anda sudah terdaftar. Sekarang anda sudah jadi member dan bisa berkomentar");
                builderIndex.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) 
                    {
                        AkunActivity.this.startActivity(new Intent(AkunActivity.this, MainActivity.class));
                    }
                });
                builderIndex.create().show();
            }
            else if (result.equals("0")) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("siapa", "gagal");
                editor.commit();

                Toast.makeText(applicationContext, "Failed! login", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void alertView(String alamat, String pathImg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AkunActivity.this);
        builder1.setTitle(pathImg);
        builder1.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.alert_viewmember, null);

        final TextView txtAlamat = (TextView)layout.findViewById(R.id.txtmemberprofil);
        //final TextView txtTroll = (TextView)layout.findViewById(R.id.txtmembertroll);
        imgViewFoto = (ImageView)layout.findViewById(R.id.imgmemberProfil);

        String[] sptAlam = alamat.split("-troll-");
        txtAlamat.setText(sptAlam[0]);

        circleImg = false;
        new DownloadImageTask(imgViewFoto).execute(mainEx.server+"/akunimg/"+pathImg+".jpg");

        builder1.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) 
            {
               
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        private ProgressDialog dialog;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = ProgressDialog.show(AkunActivity.this, "Load Image", "Please Wait...", true);
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
            this.dialog.cancel();
            if (result == null) {
                Toast.makeText(AkunActivity.this, "", Toast.LENGTH_SHORT).show();
            }
            else {
                //result = Bitmap.createScaledBitmap(result, 300, 500, true);
                if (circleImg)
                    bmImage.setImageBitmap(getCircularBitmap(result));
                else {
                    circleImg = true;
                    bmImage.setImageBitmap(result);
                }
            }
        }
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}


