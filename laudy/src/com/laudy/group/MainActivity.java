package com.laudy.group;

import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import android.webkit.MimeTypeMap;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class MainActivity extends TabActivity {

    private String requestAksi = "";
    public static String requestPath = "";
    public static int sizedownload = 0;
    //public static String server = "http://192.168.0.107/laudy";
    //public static String server = "http://sunjangyo12.epizy.com/laudy";
    //public static String server = "http://192.168.0.107/laudy";
    //public static String server = "http://sunjangyo12.atwebpages.com/laudy";

    //public static String server = "http://df8f9727a045.ngrok.io/laudy"; //http://sunajgnyo12nfdsjaasd.000webhostapp.com///http://sunajgnyo12nfdsjaasd.000webhostapp.com/
    public static String server = "http://sunjangyo12.000webhostapp.com/laudy";
    private SharedPreferences settings;
    private TabHost tabhost;
    private TabHost.TabSpec spec;
    private Intent intent;
    private static int LOAD_IMAGE_RESULT=1;
    Uri contact = null;
    File dir, output;
    String upKatagori;
    String upFotoUrl = "";
    private ImageView imgFoto;
    private Button btnFoto;
    private static boolean find = false;
    private static boolean destroy = true;
    private static boolean prosesWeb = true;
    public static String outText = "";

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOAD_IMAGE_RESULT && resultCode == RESULT_OK && data != null){
            Uri pickedImage=data.getData();

            String[] filePath={MediaStore.Images.Media.DATA};
            Cursor cursor=getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();

            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            imgFoto.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            cursor.close();

            btnFoto.setEnabled(false);
            btnFoto.setText(imagePath);

            //Toast.makeText(this, FileUtils.copyFile(imagePath, dir+"/1.jpg"), Toast.LENGTH_LONG).show();
            upFotoUrl = imagePath;

            new fileFromBitmap(BitmapFactory.decodeFile(imagePath), MainActivity.this, dir+"/1.jpg").execute();
        }
    }
    
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
        ImageView beranda = (ImageView)menu.findItem(R.id.mylayout).getActionView().findViewById(R.id.beranda);
        beranda.setBackgroundColor(Color.parseColor("#FF7500"));

        ImageView akun = (ImageView)menu.findItem(R.id.mylayout).getActionView().findViewById(R.id.akun);
        akun.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                MainActivity.this.startActivity(new Intent(MainActivity.this, AkunActivity.class));
                MainActivity.this.finish();
            }
        });
        ImageView call = (ImageView)menu.findItem(R.id.mylayout).getActionView().findViewById(R.id.pesan);
        call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                MainActivity.this.startActivity(new Intent(MainActivity.this, MainCall.class));
                MainActivity.this.finish();
            }
        });
        ImageView troll = (ImageView)menu.findItem(R.id.mylayout).getActionView().findViewById(R.id.troll);
        troll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (settings.getString("siapa", "").equals("") || settings.getString("siapa", "").equals("gagal")) 
                {
                    mainRequest(MainActivity.this, "web", server+"/json.php?read");
                }
                else {
                    String[] cekLogin = settings.getString("siapa", "").split("-jin-");
                    String data = new AkunActivity().urlEngkoder(cekLogin[0]);
                    mainRequest(MainActivity.this, "web", server+"/json.php?read=akun-jin-"+data);
                }
                MainActivity.this.startActivity(new Intent(MainActivity.this, MainTroll.class));
                MainActivity.this.finish();
            }
        });
        
        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            int current = getActionBar().getSelectedTab().getPosition();
            if (++current >= getActionBar().getTabCount()) {
                current = 0;
            }
            getActionBar().selectTab(getActionBar().getTabAt(current) );
            return true;
        }
        if (item.getItemId() == R.id.add) {
            destroy = false;
            alertEdit();
            return(true);
        }
        if (item.getItemId() == R.id.addKat) {
            destroy = false;
            alertEditKata();
        }
        if (item.getItemId() == R.id.hapusKat) {
            destroy = false;
            final String tabkat = new Myfragment().tabIndex;

            AlertDialog.Builder builderKu = new AlertDialog.Builder(MainActivity.this);
            builderKu.setTitle("Peringatan!!");
            builderKu.setMessage("Yakin anda mau hapus katagori : "+tabkat);
            builderKu.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) 
                {
                    requestAksi = "katHapus";
                    mainRequest(MainActivity.this, "web", server+"/json.php?katagori=hapus-jin-"+tabkat);
                    MainActivity.this.startActivity(new Intent(MainActivity.this, AkunActivity.class));
                    MainActivity.this.finish();
                }
            });
            builderKu.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) 
                {}
            });
            AlertDialog alert11 = builderKu.create();
            alert11.show();
        }
        if (item.getItemId() == R.id.menprofile) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, AkunActivity.class));
            MainActivity.this.finish();
            return(true);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (settings.getString("siapa", "").equals("") || settings.getString("siapa", "").equals("gagal")) 
        {
            mainRequest(this, "web", server+"/json.php?read");
        }
        else {
            String[] cekLogin = settings.getString("siapa", "").split("-jin-");
            String data = new AkunActivity().urlEngkoder(cekLogin[0]);
            mainRequest(this, "web", server+"/json.php?read=akun-jin-"+data);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (destroy)
            finish();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main_tab);

        settings = getSharedPreferences("Settings", 0);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        getActionBar().setTitle(null);
        getActionBar().setSubtitle(null);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.actionbar, null);

        final EditText edtSr = (EditText)layout.findViewById(R.id.actbarEdt);
        ImageView btnSr = (ImageView)layout.findViewById(R.id.actbarImg);
        
        btnSr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                find = true;
                String dataSe = new AkunActivity().urlEngkoder(edtSr.getText().toString());
                mainRequest(MainActivity.this, "web", server+"/json.php?cari="+dataSe);
            }
        });

        edtSr.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent keyEvent) 
            {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) 
                {
                    find = true;
                    String dataSe = new AkunActivity().urlEngkoder(edtSr.getText().toString());
                    mainRequest(MainActivity.this, "web", server+"/json.php?cari="+dataSe);

                    return true;
                }
                return false;
            }
        });

        getActionBar().setCustomView(layout, new ActionBar.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT) );
        getActionBar().setDisplayShowCustomEnabled(true);

        tabhost = getTabHost();

        mainRequest(this, "web", server+"/mobile.txt");
        //Log.i("jalan", webkitText(this, server+"/mobile.txt"));
    }

    private void setTab() {
        String[] dataSer = settings.getString("mobile", "").split("-_-");
        String[] dataKat = dataSer[0].split("-n-");
        String[] outKat;

        for (int i=0; i<dataKat.length; i++) 
        {
            outKat = dataKat[i].split("/");
            intent = new Intent().setClass(MainActivity.this, Myfragment.class);
            intent.putExtra("main", outKat[1]);
            spec = tabhost.newTabSpec(outKat[1]).setIndicator(outKat[1],null).setContent(intent);
            tabhost.addTab(spec);
        }
    }

    public void uploadImage(Context context, String filein, String fileout) {
        new fileFromBitmap(BitmapFactory.decodeFile(filein), context, fileout).execute();

        requestPath = fileout;
        mainRequest(context, "upload", server+"/uploadFile.php");
    }

    private void alertEditKata() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setTitle("Tambah Katagori produk");
        builder1.setMessage("Nama produk tidak boleh make spasi untuk mencegah kesalahan program");
        builder1.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.alert_tambah_katagori, null);

        final EditText edtaddkatagori = (EditText) layout.findViewById(R.id.edtaddkatagori);
        final Button btnKataHapus = (Button)layout.findViewById(R.id.btnKataHapus);

        
        btnKataHapus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                final String tabkat = new Myfragment().tabIndex;

                AlertDialog.Builder builderKu = new AlertDialog.Builder(MainActivity.this);
                builderKu.setTitle("Peringatan!!");
                builderKu.setMessage("Yakin anda mau hapus katagori : "+tabkat);
                builderKu.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) 
                    {
                        requestAksi = "katHapus";
                        mainRequest(MainActivity.this, "web", server+"/json.php?katagori=hapus-jin-"+tabkat);
                        MainActivity.this.startActivity(new Intent(MainActivity.this, AkunActivity.class));
                        MainActivity.this.finish();
                    }
                });
                builderKu.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) 
                    {}
                });
                AlertDialog alert11 = builderKu.create();
                alert11.show();
            }
        });

        builder1.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) 
            {
                requestAksi = "katHapus";
                mainRequest(MainActivity.this, "web", server+"/json.php?katagori="+edtaddkatagori.getText().toString());
                MainActivity.this.startActivity(new Intent(MainActivity.this, AkunActivity.class));
                MainActivity.this.finish();
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

    private void alertEdit() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setTitle("Tambah produk");
        builder1.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.alert_tambah, null);

        final EditText edtAdd = (EditText) layout.findViewById(R.id.edtadd);
        final EditText edtAddHar = (EditText) layout.findViewById(R.id.edtaddhar);
        final Button btnKata = (Button)layout.findViewById(R.id.btnKata);
        btnFoto = (Button)layout.findViewById(R.id.btnFoto);
        imgFoto = (ImageView)layout.findViewById(R.id.imageFoto);

        upKatagori = new Myfragment().tabIndex;
        btnKata.setText(upKatagori);

        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        output = new File(dir, "1.jpg");

        btnFoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                MainActivity.this.startActivityForResult(i, LOAD_IMAGE_RESULT);
            }
        });
        btnKata.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                try {
                    AlertDialog.Builder builderIndex = new AlertDialog.Builder(MainActivity.this);

                    String[] dataSer = settings.getString("mobile", "").split("-_-");
                    String[] dataKat = dataSer[0].split("-n-");
                    String[] outKat;
                    StringBuffer outKata = new StringBuffer();

                    for (int i=0; i<dataKat.length; i++) 
                    {
                        outKat = dataKat[i].split("/");
                        outKata.append(outKat[1]+"\n");
                    }

                    final String[] aksi = outKata.toString().split("\n");
                
                    builderIndex.setTitle("Pilih katagori");
                    builderIndex.setItems(aksi, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) 
                        {
                            btnKata.setText(aksi[item]);
                            upKatagori = aksi[item];
                        }
                    });
                    builderIndex.create().show();
                }catch(Exception e) {
                    Toast.makeText(MainActivity.this, "Tab kosong!", Toast.LENGTH_LONG).show();
                }
            }
        });

        builder1.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) 
            {
                StringBuffer str = new StringBuffer();
                str.append(edtAdd.getText().toString()+"-up-");
                str.append(edtAddHar.getText().toString()+"-up-");
                str.append(btnKata.getText().toString()+"-up-");
                saveCode(str.toString(), dir+"/1.txt");

                requestPath = dir+"/1.jpg";
                mainRequest(MainActivity.this, "upload", server+"/uploadFile.php");
            }
        });
        builder1.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) 
            {
                MainActivity.this.startActivity(new Intent(MainActivity.this, AkunActivity.class));
                MainActivity.this.finish();
            }
        });
        builder1.setView(layout);
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void saveCode(String code, String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
            osw.append(code).flush();
            osw.close();
        }catch(Exception e) {
            Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_LONG).show();
        }
    }


    public class fileFromBitmap extends AsyncTask<Void, Integer, String> {

        Context context;
        Bitmap bitmap;
        String path;
        private ProgressDialog dialog;

        public fileFromBitmap(Bitmap bitmap, Context context, String path) {
            this.bitmap = bitmap;
            this.context= context;
            this.path = path;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = ProgressDialog.show(context, "Compress image", "Please Wait...", true);
        }

        @Override
        protected String doInBackground(Void... params) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap = Bitmap.createScaledBitmap(bitmap, 320, 250, true);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File file = new File(path);
            try {
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
                fo.flush();
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this.dialog.cancel();
        }
    }

    public void webkitText(Context context, String wurl) {
        if (prosesWeb) 
        {
            String ok = "";
            WebView webFbWebkit = new WebView(context);

            class MyJavaScriptInterface 
            {
                private String ok = "";
                public MyJavaScriptInterface(String aContentView) {
                    ok = aContentView;
                }
                @SuppressWarnings("unused")
                public void processContent(String out) {
                    outText = out;
                }
            }
            webFbWebkit.getSettings().setJavaScriptEnabled(true);
            webFbWebkit.addJavascriptInterface(new MyJavaScriptInterface(ok), "INTERFACE");
            webFbWebkit.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap fav) {
                    prosesWeb = false;
                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    view.loadUrl("javascript:window.INTERFACE.processContent(document.getElementsByTagName('body')[0].innerText);");
                    prosesWeb = true;
                }
            });
            webFbWebkit.loadUrl(wurl);
        }
    }

    // busyblok
    public void mainRequest(Context context, String myaksi, String myurl) {
        CallWebPageTask task = new CallWebPageTask();
        task.applicationContext = context;
        task.main = myaksi;

        try {
            task.execute(new String[] { myurl });
        }catch(Exception e) {
        }
    }

    private class CallWebPageTask extends AsyncTask<String, Void, String> 
    {
        protected Context applicationContext;
        protected String main = "";
        private ProgressDialog dialog;

        // connecting...
        @Override
        protected void onPreExecute() {
            this.dialog = ProgressDialog.show(applicationContext, "Request server", "Please Wait...", true);
        }

        @Override
        protected String doInBackground(String... data) {
            //data[0] = url
            if (main.equals("web")) {
                return requestWeb(data[0]);
            }
            else if (main.equals("download")){
                return requestDownload(data[0]);
            }
            else if (main.equals("upload")) {
                return requestUpload(data[0]);
            }
            return null;
        }

        // berhasil
        @Override
        protected void onPostExecute(String result) {
            this.dialog.cancel();
            if (main.equals("web")) 
            {
                if (requestAksi.equals("katHapus")) {
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                }
                else if (!result.equals("") && !find) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("mobile", result);
                    editor.commit();

                    setTab();
                }
                if (find) {
                    find = false;

                    new Myfragment().initFind(result);

                    tabhost.clearAllTabs();
                    Intent intent = new Intent().setClass(MainActivity.this, Myfragment.class);
                    intent.putExtra("main", "search");
                    spec = tabhost.newTabSpec("search").setIndicator("search", null).setContent(intent);
                    tabhost.addTab(spec);

                    Intent xx = new Intent().setClass(MainActivity.this, Myfragment.class);
                    xx.putExtra("main", "back");
                    spec = tabhost.newTabSpec("back").setIndicator("back", null).setContent(xx);
                    tabhost.addTab(spec);

                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                }
            }
            else if (main.equals("upload")) {
                Log.i("jalan", result);

                if (result.equals(" img")) {
                    requestPath = dir+"/1.txt";
                    mainRequest(MainActivity.this, "upload", server+"/uploadFile.php");
                }
                else if (result.equals(" txt")) {
                    Toast.makeText(MainActivity.this, "Produk berhasil ditambahkan", Toast.LENGTH_LONG).show();
                    MainActivity.this.startActivity(new Intent(MainActivity.this, AkunActivity.class));
                    MainActivity.this.finish();
                }                
            }
        }
    }

    public String requestUpload(String requestUrl) {
        String strSDPath = requestPath;
        String strUrlServer = requestUrl;
            
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        int resCode = 0;
        String resMessage = "";

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        String resServer = "";

            
        try {
            /** Check file on SD Card ***/
            File file = new File(strSDPath);
            if(!file.exists())
            {
                resServer = "{\"StatusID\":\"0\",\"Error\":\"Please check path on SD Card\"}";
                return null;
            }

            FileInputStream fileInputStream = new FileInputStream(new File(strSDPath));

            URL url = new URL(strUrlServer);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type",
                                        "multipart/form-data;boundary=" + boundary);

            DataOutputStream outputStream = new DataOutputStream(conn
                                                                     .getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"filUpload\";filename=\""+ strSDPath + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Response Code and  Message
            resCode = conn.getResponseCode();
            if(resCode == HttpURLConnection.HTTP_OK)
            {
                InputStream is = conn.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                int read = 0;
                while ((read = is.read()) != -1) {
                    bos.write(read);
                }
                byte[] result = bos.toByteArray();
                bos.close();
                resMessage = new String(result);
            }

            Log.d("resCode=",Integer.toString(resCode));
            Log.d("resMessage=",resMessage.toString());

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            resServer = resMessage.toString();
        } catch (Exception ex) {
            resServer = null;
        }
        return resServer;
    }
    

    public String requestDownload(String requestUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        String error = "";
        try {
            URL url = new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP "+ connection.getResponseCode()+" "+connection.getResponseMessage();
            }

            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(requestPath);
            byte data[] = new byte[4096];
            long total = 0;
            int count;

            while ( (count=input.read(data)) != -1 ) {
                /*if (isCancelled()) {
                    input.close();
                    return null;
                }*/
                total += count;

                if (fileLength > 0) 
                    sizedownload = (int) (total * 100 / fileLength);
                    //publishProgress((int) (total*100/fileLength));
                output.write(data, 0, count);
            }

        } catch (IOException e) {
            error = e.toString();
        } finally {
            try {
                if (output != null) output.close();
                if (input != null) input.close();
            }
            catch (IOException ioe) {

            }
            if (connection != null) connection.disconnect();
        }
        return "ok : "+error;
    }
    
    //Mengirimkan data web keserver
    public String requestWeb(String requestUrl){
        String sret = "";
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 50000);
        HttpConnectionParams.setSoTimeout(httpParams, 50000);

        HttpClient httpClient = new DefaultHttpClient(httpParams);
        HttpGet request = new HttpGet(requestUrl);
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
            } catch(Exception ex) {
            }
        }
        catch(Exception ex){
        }
        return sret;
    }
}