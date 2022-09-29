package com.arindo.waserbanura;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arindo.waserbanura.Adapter.ListAdapterCheckout;
import com.arindo.waserbanura.Constanta.Constanta;
import com.arindo.waserbanura.Model.ItemDataSqlite;
import com.arindo.waserbanura.Sqlite.DBHelper;
import com.google.android.material.snackbar.Snackbar;
import com.zj.btsdk.BluetoothService;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.arindo.waserbanura.ActivityLaporan.bulaan;
import static com.arindo.waserbanura.ActivityLaporan.harri;
import static com.arindo.waserbanura.ActivityLaporan.tahunn;
import static com.arindo.waserbanura.Fragment.Fragment_Beranda.notificationBadge;
import static com.arindo.waserbanura.MainActivity.mCartItemCount;
import static com.arindo.waserbanura.MainActivity.setupBadge;
import static com.arindo.waserbanura.MainActivity.textCartItemCount;

@SuppressLint("SetTextI18n")
public class KonfirmasiPembayaranActivity extends AppCompatActivity  {

    int total2, tunai, kemnbalian;
    EditText etText;
    EditText etText2;
    TextView tvStatus;
    EditText bayar2;
    TextView tanggal,totalk2, tunaik2, kembalik2;
    TextView tanggal2;
    Random r = new Random();
    int randomNumber = r.nextInt((int) Math.floor(005000 + Math.random() * 999999));
    ListView listView;
    ListAdapterCheckout adapter;
    List<ItemDataSqlite> itemList = new ArrayList<ItemDataSqlite>();
    DBHelper SQLite = new DBHelper(this);
    Button btn_print_text;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static final String TAG_ID = "id";
    public static final String TAG_MENUID = "menuid";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_TOTAL = "total";
    public static final String TAG_JUMLAH = "jumlah";
    public static final String TAG_HARGA = "harga";
    public static final String TAG_URL = "url";
    public static final String TAG_PLACEID = "placeid";
    private final String TAG = MainActivity.class.getSimpleName();
    public static final int RC_BLUETOOTH = 0;
    public static final int RC_CONNECT_DEVICE = 1;
    public static final int RC_ENABLE_BLUETOOTH = 2;
    private BluetoothService mService = null;
    private boolean isPrinterReady = false;
    private NumberFormat formatRupiah;
    ArrayList<HashMap<String, String>> row;
    Snackbar snackBar;
    Toolbar toolbar;
    public String stringkodet, adminps, placeidstr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_pembayaran);
        preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
        total2 = 0;
        kemnbalian = 0;
        tunai = 0;
        tahunn = getTahun();
        bulaan = getBulan();
        harri = getHari();
        Locale localeID = new Locale("in", "ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        snackBar = Snackbar.make(toolbar, "", Snackbar.LENGTH_INDEFINITE);
        getSupportActionBar().setTitle("Konfirmasi Pembayaran");
        SQLite = new DBHelper(getApplicationContext());
        etText = (EditText) findViewById(R.id.et_text);
        etText2 = (EditText) findViewById(R.id.et_text2);
        //tvStatus = (TextView) findViewById(R.id.tv_status);
        totalk2 = (TextView) findViewById(R.id.totalk2);
        kembalik2 = (TextView) findViewById(R.id.kembalik2);
        tunaik2 = (TextView) findViewById(R.id.tunaik2);
        tanggal2 = (TextView) findViewById(R.id.tanggal2);
        tanggal2.setText(getNamaHari(harri, bulaan, tahunn) + "," + harri + " " + getNamaBulan(bulaan) + " "  + tahunn);
        listView = (ListView) findViewById(R.id.list_view);
        bayar2 = (EditText) findViewById(R.id.bayar2);
        btn_print_text = (Button) findViewById(R.id.btn_print_text);

        stringkodet = preferences.getString("placeid", null) + "TRX" + randomNumber;
        adminps = preferences.getString("adminpos", null);
        editor.putString("trxcodepref", stringkodet);
        editor.putString("trxadmpref", adminps);
        editor.apply();
        btn_print_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (kemnbalian < 0 )  {
                    SnackBarMsg("Pembayaran kurang lunasi pembayaran untuk melanjutkan..");
                    // ShowDialog();
                    return;
                } else if (tunai <=0 ) {
                    SnackBarMsg("Isi nominal pembayaran..");
                    //ShowDialog3();
                    return;
                } else {
                    ShowDialog2();
                }
            }
        });

        adapter = new ListAdapterCheckout (KonfirmasiPembayaranActivity.this, itemList);
        listView.setAdapter(adapter);
        totalk2.setText(String.valueOf(total2));
        tunaik2.setText(String.valueOf(tunai));
        kembalik2.setText(String.valueOf(kemnbalian));
        placeidstr = preferences.getString("placeid", null);
        bayar2.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

            }
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                snackBar.dismiss();
            }

            public void afterTextChanged(Editable arg0) {

                if(!arg0.equals("")) {
                    try {
                        tunai = Integer.parseInt(arg0.toString());
                        kemnbalian = tunai - total2;
                        kembalik2.setText(formatRupiah.format(kemnbalian));
                        tunaik2.setText(formatRupiah.format(tunai));
                        snackBar.dismiss();
                    } catch (NumberFormatException ex) {
                        tunai = 0;
                        kemnbalian = tunai - total2;
                        kembalik2.setText(formatRupiah.format(kemnbalian));
                        tunaik2.setText(formatRupiah.format(tunai));
                        snackBar.dismiss();
                    }
                } else {
                    tunai = 0;
                    kemnbalian = tunai - total2;
                    kembalik2.setText(formatRupiah.format(kemnbalian));
                    tunaik2.setText(formatRupiah.format(tunai));
                    snackBar.dismiss();
                }
                if (bayar2.getText().toString().equals("")){
                    kembalik2.setText("0");
                    tunaik2.setText("0");
                    snackBar.dismiss();
                }
            }

        });
        row = SQLite.getAllDataPlaceid(placeidstr);
        getAllData();
        Log.e("Data", row.toString());
    }

    private void order() {
        String JSON_URl = Constanta.Setting.purchase_trx;
        ArrayList<HashMap<String, String>> row = SQLite.getAllDataPlaceid(placeidstr);
        for (int J = 0; J < row.size(); J++) {
            final int finalJ = J;
            final RequestQueue requestQueue = Volley.newRequestQueue(KonfirmasiPembayaranActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(KonfirmasiPembayaranActivity.this, response, Toast.LENGTH_LONG).show();
                            requestQueue.stop();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(KonfirmasiPembayaranActivity.this, "Error ...", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                    requestQueue.stop();
                }
            }) {

                final String Plcd = preferences.getString("placeid", null);
                final String menuid = itemList.get(finalJ).getMenuid();
                final String quantity = itemList.get(finalJ).getJumlah();
                final String date = getTanggal();
                final String time = getWaktu();
                final String harga = itemList.get(finalJ).getHarga();
                final String adminps = preferences.getString("adminpos", null);

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("places_id", Plcd);
                    params.put("menu_id", menuid);
                    params.put("qty", quantity);
                    params.put("trx_code", stringkodet);
                    params.put("trx_date", date);
                    params.put("trx_time", time);
                    params.put("trx_price", harga);
                    params.put("adminpos_id", adminps);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }

        String JSON_URl2 = Constanta.Setting.purchase_indux;
        final RequestQueue requestQueue2 = Volley.newRequestQueue(KonfirmasiPembayaranActivity.this);
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, JSON_URl2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(ActivityKeranjang.this, response, Toast.LENGTH_SHORT).show();
                        requestQueue2.stop();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(KonfirmasiPembayaranActivity.this, "Error. . .", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                requestQueue2.stop();
            }
        }) {
            final String plcidd =preferences.getString("placeid", null);
            final String datatime = getTanggal() + " " + getWaktu();
            final String adminpos = preferences.getString("adminpos", null);

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("places_id", plcidd);
                params.put("trx_code", stringkodet);
                params.put("trx_datetime", datatime);
                params.put("total_price", String.valueOf(total2));
                params.put("tunai", String.valueOf(tunai));
                params.put("kembalian", String.valueOf(kemnbalian));
                params.put("adminpos_id", adminpos);
                return params;
            }
        };
        requestQueue2.add(stringRequest2);

        ArrayList<HashMap<String, String>> roks = SQLite.getAllDataPlaceid(placeidstr);
        for (int k = 0; k < roks.size(); k++) {
            String idd = roks.get(k).get(TAG_ID);
            SQLite.delete2(Integer.parseInt(idd));
            itemList.clear();
            getAllData();
            //setupBadge();
            mCartItemCount = mCartItemCount - 1;
            notificationBadge.setNumber(mCartItemCount);
            textCartItemCount.setVisibility(View.GONE);
        }
    }

    private void requestBluetooth() {
        if (mService != null) {
            if (!mService.isBTopen()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, RC_ENABLE_BLUETOOTH);
            }
        }
    }

    private void ShowDialog2() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this
        );
        alertDialogBuilder.setTitle("Konfirmasi Pembayaran");
        alertDialogBuilder
                .setMessage("lanjutkan pembayaran")
                .setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(KonfirmasiPembayaranActivity.this, ActivityPembayaranBerhasil.class));
                        order();
                        KonfirmasiPembayaranActivity.this.finish();
                    }
                })
                .setNegativeButton("Selesai", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        order();
                        startActivity(new Intent(KonfirmasiPembayaranActivity.this, MainActivity.class));
                        KonfirmasiPembayaranActivity.this.finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void getAllData() {
        row = SQLite.getAllDataPlaceid(placeidstr);
        for (int i = 0; i < row.size(); i++) {
            String id = row.get(i).get(TAG_ID);
            String menuid = row.get(i).get(TAG_MENUID);
            String nama = row.get(i).get(TAG_NAMA);
            String total = row.get(i).get(TAG_TOTAL);
            total2 = total2 + Integer.parseInt(total);
            String harga = row.get(i).get(TAG_HARGA);
            String jumlah = row.get(i).get(TAG_JUMLAH);
            String url = row.get(i).get(TAG_URL);
            String placeid = row.get(i).get(TAG_PLACEID);
            ItemDataSqlite data = new ItemDataSqlite();
            data.setId(id);
            data.setMenuid(menuid);
            data.setNama(nama);
            data.setTotal(total);
            data.setHarga(harga);
            data.setJumlah(jumlah);
            data.setUrl(url);
            data.setPlaceid(placeid);
            itemList.add(data);
        }
        adapter.notifyDataSetChanged();
        totalk2.setText(formatRupiah.format(total2));
    }

    private String getTanggal() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
    private String getWaktu() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date2 = new Date();
        return dateFormat.format(date2);
    }

    private String getNamaHari(String tanggal, String bulan, String tahun) {
        String dayOfWeek = "";
        try {
            String dateString2 = tanggal + "-" +  bulan + "-" + tahun;
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(dateString2);
            dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);
            if (dayOfWeek.toUpperCase().equals("SUNDAY") ) {
                dayOfWeek = "Minggu";
            } else if (dayOfWeek.toUpperCase().equals("MONDAY")) {
                dayOfWeek = "Senin";
            } else if (dayOfWeek.toUpperCase().equals("TUESDAY")) {
                dayOfWeek = "Selasa";
            } else if (dayOfWeek.toUpperCase().equals("WEDNESDAY")) {
                dayOfWeek = "Rabu";
            } else if (dayOfWeek.toUpperCase().equals("THURSDAY")) {
                dayOfWeek = "Kamis";
            }else if (dayOfWeek.toUpperCase().equals("FRIDAY")) {
                dayOfWeek = "Jumat";
            }else if (dayOfWeek.toUpperCase().equals("SATURDAY")) {
                dayOfWeek = "Sabtu";
            }
            Log.e("tanggal", dayOfWeek);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayOfWeek;
    }

    private String getNamaBulan(String bulan){
        if (bulan.equals("01")) return "Januari";
        else if (bulan.equals("02")) return "Februari";
        else if (bulan.equals("03")) return "Maret";
        else if (bulan.equals("04")) return "April";
        else if (bulan.equals("05")) return "Mei";
        else if (bulan.equals("06")) return "Juni";
        else if (bulan.equals("07")) return "Juli";
        else if (bulan.equals("08")) return "Agustus";
        else if (bulan.equals("09")) return "September";
        else if (bulan.equals("10")) return "Oktober";
        else if (bulan.equals("11")) return "Novemver";
        else if (bulan.equals("12")) return "Desember";
        else return "";
    }

    private String getTahun() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }
    private String getBulan() {
        DateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getHari() {
        DateFormat dateFormat = new SimpleDateFormat("dd");
        Date date = new Date();
        return dateFormat.format(date);
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