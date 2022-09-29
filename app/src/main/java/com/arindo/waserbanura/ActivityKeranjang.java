package com.arindo.waserbanura;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.arindo.waserbanura.Sqlite.DBHelper;
import com.arindo.waserbanura.Model.ItemDataSqlite;
import com.arindo.waserbanura.Adapter.ListAdapterKeranjang;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.arindo.waserbanura.Fragment.Fragment_Beranda.notificationBadge;
import static com.arindo.waserbanura.MainActivity.mCartItemCount;
import static com.arindo.waserbanura.MainActivity.setupBadge;
import static com.arindo.waserbanura.MainActivity.textCartItemCount;

public class ActivityKeranjang extends AppCompatActivity {
    ListView listView;
    TextView TotalTrx, placeid;
    private ImageView noNotesView;
    List<ItemDataSqlite> itemList = new ArrayList<ItemDataSqlite>();
    ListAdapterKeranjang adapter;
    Button BayarTrx;
    int hitung;
    private NumberFormat formatRupiah;
    private SharedPreferences preferences;
    DBHelper SQLite = new DBHelper(this);
    ArrayList<HashMap<String, String>> list_data;
    public static final String TAG_ID = "id";
    public static final String TAG_MENUID = "menuid";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_TOTAL = "total";
    public static final String TAG_JUMLAH = "jumlah";
    public static final String TAG_HARGA = "harga";
    public static final String TAG_URL = "url";
    public static final String TAG_PLACEID = "placeid";
    public static String placeidstr;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);
        SQLite = new DBHelper(getApplicationContext());
        listView = (ListView) findViewById(R.id.list_view);
        TotalTrx = (TextView) findViewById(R.id.TotalTrx);
        placeid = (TextView) findViewById(R.id.placeid);
        Locale localeID = new Locale("in", "ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        noNotesView = findViewById(R.id.empty_notes_view);
        Toolbar toolbar = findViewById(R.id.tol);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityKeranjang.this, MainActivity.class));
                finish();
            }
        });

        getSupportActionBar().setTitle("Keranjang Barang");
        adapter = new ListAdapterKeranjang(ActivityKeranjang.this, itemList);
        listView.setAdapter(adapter);
        BayarTrx = (Button) findViewById(R.id.BayarTrx);
        BayarTrx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityKeranjang.this, KonfirmasiPembayaranActivity.class));
            }
        });

        getAllData();
        placeidstr = preferences.getString("placeid",null);
    }

    private void getAllData() {
        hitung = 0;
        ArrayList<HashMap<String, String>> row = SQLite.getAllDataPlaceid(placeidstr);
        for (int i = 0; i < row.size(); i++) {
            String id = row.get(i).get(TAG_ID);
            String menuid = row.get(i).get(TAG_MENUID);
            String nama = row.get(i).get(TAG_NAMA);
            String total = row.get(i).get(TAG_TOTAL);
            hitung = hitung + Integer.parseInt(total);
            String harga = row.get(i).get(TAG_HARGA);
            String jumlah = row.get(i).get(TAG_JUMLAH);
            String url = row.get(i).get(TAG_URL);
            String placeid = row.get(i).get(TAG_PLACEID);
            ItemDataSqlite itemDataSqlite = new ItemDataSqlite();
            itemDataSqlite.setId(id);
            itemDataSqlite.setMenuid(menuid);
            itemDataSqlite.setNama(nama);
            itemDataSqlite.setTotal(total);
            itemDataSqlite.setHarga(harga);
            itemDataSqlite.setJumlah(jumlah);
            itemDataSqlite.setUrl(url);
            itemDataSqlite.setPlaceid(placeid);
            itemList.add(itemDataSqlite);

        }
        adapter.notifyDataSetChanged();
        toggleEmptyNotes();
        TotalTrx.setText(formatRupiah.format(hitung));

    }

    @Override
    protected void onResume() {
        super.onResume();
        itemList.clear();
        getAllData();
    }

    private void toggleEmptyNotes() {
        // you can check notesList.size()>0
        if (SQLite.getNotesCount2(placeidstr) > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
            BayarTrx.setVisibility(View.GONE);
            setupBadge();
            mCartItemCount = 0;
            notificationBadge.setNumber(mCartItemCount);
            textCartItemCount.setVisibility(View.GONE);
        }
    }

    private void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title dialog
        alertDialogBuilder.setTitle("Bayar Pesanan");
        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Klik Ya untuk melanjutkan")
                .setIcon(R.drawable.waserba)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /*
                        String JSON_URl = Constanta.Setting.purchase_trx;
                        ArrayList<HashMap<String, String>> row = SQLite.getAllData();
                        for (int J = 0; J < row.size(); J++) {
                            final int finalJ = J;
                            final RequestQueue requestQueue = Volley.newRequestQueue(ActivityKeranjang.this);
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URl,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Toast.makeText(ActivityKeranjang.this, response, Toast.LENGTH_LONG).show();
                                            requestQueue.stop();
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(ActivityKeranjang.this, "Error ...", Toast.LENGTH_LONG).show();
                                    error.printStackTrace();
                                    requestQueue.stop();
                                }
                            }) {
                                final String Plcd = placeTrx.getText().toString();
                                final String menuid = itemList.get(finalJ).getMenuid();
                                final String quantity = itemList.get(finalJ).getJumlah();
                                final String kodet = KodeTrx.getText().toString();
                                final String date = DateTrx.getText().toString();
                                final String time = TimeTrx.getText().toString();
                                final String harga = itemList.get(finalJ).getHarga();
                                final String adminps = AdminPosTrx.getText().toString();

                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("places_id", Plcd);
                                    params.put("menu_id", menuid);
                                    params.put("qty", quantity);
                                    params.put("trx_code", kodet);
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
                        final RequestQueue requestQueue2 = Volley.newRequestQueue(ActivityKeranjang.this);
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
                                Toast.makeText(ActivityKeranjang.this, "Error. . .", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                                requestQueue2.stop();
                            }
                        }) {
                            final String plcidd = placeTrx.getText().toString();
                            final String kdtrxx = KodeTrx.getText().toString();
                            final String datatime = WaktuTanggalTrx.getText().toString();
                            final String total = TotalTrx.getText().toString();
                            final String adminpos = AdminPosTrx.getText().toString();

                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("places_id", plcidd);
                                params.put("trx_code", kdtrxx);
                                params.put("trx_datetime", datatime);
                                params.put("total_price", total);
                                params.put("adminpos_id", adminpos);
                                return params;
                            }
                        };
                        requestQueue2.add(stringRequest2);

                        ArrayList<HashMap<String, String>> roks = SQLite.getAllData();
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
                         */
                        //  ActivityKeranjang.this.finish();
                        //startActivity(new Intent(ActivityKeranjang.this, ActivityPrint.class));
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // jika tombol ini diklik, akan menutup dialog
                        // dan tidak terjadi apa2
                        dialog.cancel();
                    }
                });
        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();
        // menampilkan alert dialog
        alertDialog.show();
    }

}

