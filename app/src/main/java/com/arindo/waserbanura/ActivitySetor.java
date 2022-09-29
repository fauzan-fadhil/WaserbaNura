package com.arindo.waserbanura;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arindo.waserbanura.Constanta.AppController;
import com.arindo.waserbanura.Constanta.Constanta;
import com.arindo.waserbanura.Model.ItemStatus;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ActivitySetor extends AppCompatActivity {

    NumberFormat formatRupiah;
    Locale localeID;
    Uri filePath;
    Bitmap bitmap, decoded;
    String tag_json_obj = "json_obj_req";
    String nominaljson = "";
    String kode, journalstr, datepayy, nominalproses, sisa, hutang;
    JSONObject objTgh, jsonObjectTgh, objJr, jsonObjectJr, objUpl, objvld, jsonObjectvld;
    JSONArray jsonArrayTgh, jsonArrayJr, jsonArrayvld;
    DateFormat dateFormat;
    Date date;
    Snackbar snackBar;
    Toolbar toolbar;
    MenuItem menuItem;
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView status, nominalprosessetor, tagihansetor, tertundasetor, nominaltxtsetor;
    private EditText edtnominalsetor;
    private Spinner spinerbank;
    private Button btnKirim;
    private ImageView gambarlampiran, btngambar;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    int bitmap_size = 60;
    int max_resolution_image = 800;
    int inttagihansetor, inttertundasetor, intnominaltxtsetor, success;
    private String KEY_JOURNAL = "journal_id";
    private String KEY_AGENT = "agent";
    private String KEY_ADMINPOS = "adminpos_fk";
    private String KEY_BANK = "bank";
    private String KEY_NOMINAL = "nominal";
    private String KEY_DATEPAY = "date_pay";
    private String KEY_FILE = "strfile";
    public static final int REQUEST_CAMERA = 100;
    public final int SELECT_FILE = 2;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private String format;
    private SharedPreferences prefsetor;
    private SharedPreferences preferences;
    long time;
    public List<ItemStatus> itemStatuses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setor);
        itemStatuses = new ArrayList<>();
        localeID = new Locale("in", "ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        requestQueue = Volley.newRequestQueue(this);
        //requestQueueTgh = Volley.newRequestQueue(this);
        long sekarang = System.currentTimeMillis();
        prefsetor = getSharedPreferences("SharedPrefsetor", Context.MODE_PRIVATE);
        preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        kode = preferences.getString("placeid", null);
        time = sekarang - prefsetor.getLong("setor", 0);
        Log.e("waktu setor", String.valueOf(time));
        tagihansetor = (TextView) findViewById(R.id.tagihansetor);
        tertundasetor = (TextView) findViewById(R.id.tertundasetor);
        nominaltxtsetor = (TextView)findViewById(R.id.nominaltxtsetor);
        nominalprosessetor = (TextView) findViewById(R.id.nominalprosessetor);
        edtnominalsetor = (EditText) findViewById(R.id.edtnominalsetor);
        btngambar = (ImageView) findViewById(R.id.btngambar);
        btnKirim = (Button) findViewById(R.id.btnKirim);
        gambarlampiran = (ImageView) findViewById(R.id.gambarlampiran);
        spinerbank = (Spinner) findViewById(R.id.spinerbank);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd" +" "+"HH:mm:ss");
        date = new Date();
        datepayy = dateFormat.format(date);
        inttagihansetor = 0;
        inttertundasetor = 0;
        intnominaltxtsetor = 0;
        tertundasetor.setText(String.valueOf(inttertundasetor));
        nominaltxtsetor.setText(formatRupiah.format(intnominaltxtsetor));
        toolbar = findViewById(R.id.tolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitySetor.this.finish();
            }
        });

        snackBar = Snackbar.make(toolbar, "", Snackbar.LENGTH_INDEFINITE);
        getSupportActionBar().setTitle("Setor Tagihan");
        edtnominalsetor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.equals("")) {
                    try {
                        intnominaltxtsetor = Integer.parseInt(s.toString());
                        nominaltxtsetor.setText(formatRupiah.format(intnominaltxtsetor));
                    } catch (NumberFormatException ex) {
                        intnominaltxtsetor = 0;
                        nominaltxtsetor.setText(formatRupiah.format(intnominaltxtsetor));
                    }
                } else {
                    intnominaltxtsetor = 0;
                    nominaltxtsetor.setText(formatRupiah.format(intnominaltxtsetor));
                }
            }
        });

        getjournalid();
        tertunda();
        getStatus();
        tagihan();
        btngambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(ActivitySetor.this, "Berhasil di pencet", Toast.LENGTH_SHORT).show();
                if (edtnominalsetor.getText().toString().equals("")) {
                    SnackBarMsg("Isi nominal terlebih dahulu...");
                    // IsiNominal();
                    //Toast.makeText(ActivitySetor.this, "Anda belum mengisi nominal", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (decoded == null)
                    SnackBarMsg("Isi lampiran terlebih dahulu...");
                    //Toast.makeText(ActivitySetor.this, "Anda belum mengambil foto", Toast.LENGTH_SHORT).show();
                else
                    validasi();
                //uploadimage();
                // Toast.makeText(ActivitySetor.this, "Anda belum mengambil foto", Toast.LENGTH_SHORT).show();
                //uploadimage();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_status, menu);
        menuItem = menu.findItem(R.id.action_cart);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            Intent intent = new Intent(ActivitySetor.this, ActivityStatus.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void tertunda() {
        final String ardkdhost = Constanta.Setting.nominalhutang;
        final String JSON_TERTUNDA = ardkdhost + kode;
        stringRequest = new StringRequest(Request.Method.GET, JSON_TERTUNDA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        objTgh = null;
                        try {
                            objTgh = new JSONObject(response);
                            jsonArrayTgh = null;
                            jsonArrayTgh = objTgh.getJSONArray("values");
                            for (int j = 0; j < jsonArrayTgh.length(); j++) {
                                jsonObjectTgh = null;
                                jsonObjectTgh = jsonArrayTgh.getJSONObject(j);
                                String places_id = jsonObjectTgh.getString("places_id");
                                String pending = jsonObjectTgh.getString("Pending");
                                String approved = jsonObjectTgh.getString("approved");
                                String rejected = jsonObjectTgh.getString("rejected");
                                nominalproses = jsonObjectTgh.getString("proses");
                                hutang = jsonObjectTgh.getString("hutang");
                                sisa = jsonObjectTgh.getString("sisa");
                                if (hutang.equals("0") ) {
                                    edtnominalsetor.setEnabled(false);
                                    btngambar.setEnabled(false);
                                    btnKirim.setEnabled(false);

                                }
                                if (nominalproses.equals("0")) {
                                }
                                if (approved.equals("0")) {
                                    menuItem.setVisible(false);
                                }
                                if (hutang.equals("null")){
                                    tertundasetor.setText("0" );
                                } else {
                                    tertundasetor.setText(formatRupiah.format(Integer.parseInt(hutang)));
                                    nominalprosessetor.setText(formatRupiah.format(Integer.parseInt(nominalproses)));
                                    Log.e("places_id", places_id);
                                    Log.e("hutang", hutang);
                                    Log.e("pending", pending);
                                    Log.e("approved", approved);
                                    Log.e("rejected", rejected);
                                    Log.e("proses", nominalproses);
                                }
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }

    public void getStatus() {
        final String JSON_VALIDASI = Constanta.Setting.nominal + kode;
        stringRequest = new StringRequest(Request.Method.GET, JSON_VALIDASI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        objvld = null;
                        try {
                            objvld = new JSONObject(response);
                            jsonArrayvld = null;
                            jsonArrayvld = objvld.getJSONArray("values");
                            for (int v = 0; v < jsonArrayvld.length(); v++) {
                                jsonObjectvld = null;
                                jsonObjectvld = jsonArrayvld.getJSONObject(v);
                                nominaljson = jsonObjectvld.getString("nominal");
                                String statusjson = jsonObjectvld.getString("status");
                                Log.e("statusjson", statusjson);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }

    public void getjournalid() {
        String ardkdhost = Constanta.Setting.purchase_max;
        final String JSON_URL = ardkdhost + kode;
        stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        objJr = null;
                        try {
                            objJr = new JSONObject(response);
                            jsonArrayJr = null;
                            jsonArrayJr = objJr.getJSONArray("values");
                            for (int i = 0; i < jsonArrayJr.length(); i++) {
                                jsonObjectJr = null;
                                jsonObjectJr = jsonArrayJr.getJSONObject(i);
                                dateFormat = new SimpleDateFormat("yyyyddMM");
                                date = new Date();
                                journalstr = "pymt" + dateFormat.format(date) +  jsonObjectJr.getString("NO");
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }

    public void tagihan() {
        String ardkdhost = Constanta.Setting.purchase_debit;
        final String JSON_URL = ardkdhost + kode;
        stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        objTgh = null;
                        try {
                            objTgh = new JSONObject(response);
                            jsonArrayTgh = null;
                            jsonArrayTgh = objTgh.getJSONArray("values");
                            for (int j = 0; j < jsonArrayTgh.length(); j++) {
                                jsonObjectTgh = null;
                                jsonObjectTgh = jsonArrayTgh.getJSONObject(j);
                                format = jsonObjectTgh.getString("total_hutang");
                                if (format.equals("null")) {
                                    tagihansetor.setText("0");
                                    edtnominalsetor.setEnabled(false);
                                    btngambar.setEnabled(false);
                                    btnKirim.setEnabled(false);
                                    Toast.makeText(ActivitySetor.this, "Tagihan Tidak Ada", Toast.LENGTH_SHORT).show();
                                } else {
                                    inttagihansetor = Integer.parseInt(format);
                                    tagihansetor.setText(formatRupiah.format(Integer.parseInt(format)));
                                }

                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }

    public void selectImage() {
        gambarlampiran.setImageResource(0);
        final CharSequence[] items = {"Ambil Gambar", "Pilih Dari Galeri",
                "Batal"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySetor.this);
        builder.setTitle("Tambahkan Lampiran");
        builder.setIcon(R.drawable.waserba);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Ambil Gambar")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Ambil Gambar Dari Kamera
                    startActivityForResult(intent, REQUEST_CAMERA);
                    filePath = getOutputMediaFileUri();
                    Log.e("Kamera", String.valueOf(REQUEST_CAMERA));
                } else if (items[which].equals("Pilih Dari Galeri")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_FILE);
                    Log.e("Kameranya", String.valueOf(SELECT_FILE));
                } else if (items[which].equals("Batal")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void uploadimage() {
        String UPLOAD_URL = Constanta.Setting.purchase_payment;
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading.....", "Please wait.....", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SnackBarMsg("Transaksi di proses");
                        // TransaksiProses();
                        Log.e(TAG, "Response :" + response.toString());
                        try {
                            objUpl = new JSONObject(response);
                            success = objUpl.getInt(TAG_SUCCESS);
                            if (success == 1) {
                                Log.e("v Add", objUpl.toString());
                                Toast.makeText(ActivitySetor.this, objUpl.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                                kosong();
                            } else {
                                Toast.makeText(ActivitySetor.this, objUpl.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loading.dismiss();
                        requestQueue.stop();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(ActivitySetor.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_FILE, getStringImage(decoded));
                params.put(KEY_JOURNAL, journalstr);
                params.put(KEY_AGENT, kode);
                params.put(KEY_ADMINPOS, preferences.getString("adminpos", null));
                params.put(KEY_BANK, spinerbank.getSelectedItem().toString().trim());
                params.put(KEY_NOMINAL, edtnominalsetor.getText().toString().trim());
                params.put(KEY_DATEPAY, datepayy);
                Log.e(TAG, "" + params);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", "requestCode " + requestCode + ", resultCode " + resultCode + ", Data " + data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    Log.e("Camera" , Objects.requireNonNull(filePath.getPath()));
                    bitmap = (Bitmap) data.getExtras().get("data");
                    setToImageView(getResizedBitmap(bitmap, max_resolution_image));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE &&
                    resultCode == RESULT_OK &&
                    data != null && data.getData() != null) {
                Log.e("Kamera2", "RequestCode" + requestCode + "resultCode" + resultCode + "data" + data);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(ActivitySetor.this.getContentResolver(), data.getData());
                    setToImageView(getResizedBitmap(bitmap, 800));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setToImageView(Bitmap bmp) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        gambarlampiran.setImageBitmap(decoded);

    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void kosong() {
        gambarlampiran.setImageResource(0);
    }
    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }
    private File getOutputMediaFile() {
        // end of SD card checking
        File docsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Contoh");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
        }
        //path untuk menyimpan file
        String filename = "Image_NURa" + ".jpg";
        File file = new File(docsFolder.getAbsolutePath(), filename);
        if (file.exists()) {
            file.delete();
            file = new File(docsFolder.getAbsolutePath(), filename);
        }

        Log.e("Berhaasil", String.valueOf(file));

        return file;
    }

    public void validasi() {
        try {
            if (nominaljson.equals("")) {
                uploadimage();
            } else {
                nominaljson = jsonObjectvld.getString("nominal");
                Log.e("nominal2", nominaljson);
                if (edtnominalsetor.getText().toString().equals(nominaljson)) {
                    ShowDialog4();
                } else if (intnominaltxtsetor > Integer.parseInt(sisa)) {
                    ShowDialog5();
                } else if (intnominaltxtsetor > Integer.parseInt(hutang)) {
                    ShowDialog6();
                } else {
                    Toast.makeText(ActivitySetor.this, "berhasil validasi bro", Toast.LENGTH_SHORT).show();
                    uploadimage();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ShowDialog6() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this
        );
        alertDialogBuilder.setTitle("Transaksi Ditolak");
        alertDialogBuilder
                .setMessage("Tidak bisa melebihi sisa tagihan sebesar " +" "+ formatRupiah.format(Integer.parseInt(hutang)))
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void ShowDialog5() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this
        );
        alertDialogBuilder.setTitle("Transaksi Ditolak");
        alertDialogBuilder
                .setMessage("Tidak bisa melebihi sisa tagihan sebesar " +" "+ formatRupiah.format(Integer.parseInt(sisa)))
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void ShowDialog4() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this
        );
        alertDialogBuilder.setTitle("Transaksi Ditolak");
        alertDialogBuilder
                .setMessage("Nominal sebesar" +" " + formatRupiah.format(Integer.parseInt(nominaljson)) + " " + "sudah pernah dilakukan silahkan bayar dengan nominal yang berbeda")
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void proses() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this
        );
        alertDialogBuilder.setTitle("Pemberitahuan");
        alertDialogBuilder
                .setMessage("Nominal sedang dalam tahap proses")
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void SnackBarMsg(String msg){
        //final Snackbar snackBar = Snackbar.make(toolbar, msg, Snackbar.LENGTH_INDEFINITE);
        snackBar = Snackbar.make(toolbar, msg, Snackbar.LENGTH_INDEFINITE);
        snackBar.setActionTextColor(getResources().getColor(R.color.colorPrimary));

        View snackbarView = snackBar.getView();
        //snackbarView.setBackgroundColor(Color.WHITE);
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        snackBar.setAction("Close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }
}

