package com.arindo.waserbanura;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arindo.waserbanura.Adapter.ListAdapterLaporan;
import com.arindo.waserbanura.Constanta.Constanta;
import com.arindo.waserbanura.Constanta.DokumenPDF;
import com.arindo.waserbanura.Model.ItemLaporan;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityLaporan extends AppCompatActivity {

    CoordinatorLayout coordinator;
    public static Spinner tahun, bulan, hari ;
    public static TextView adminpshsl, tanggallpr;
    public static String kdhost;
    private List<ItemLaporan> itemLaporans;
    ListView listvieww;
    Button carilaporan;
    private SharedPreferences preferences;
    ListAdapterLaporan adapterr;
    JSONArray playerArray;
    private String[] mounth = {"01","02","03","04",
            "05","06","07","08", "09", "10", "11", "12"};
    private String[] years = {"2019","2020","2021","2022","2023"};
    private String[] days = {" ","01","02","03","04",
            "05","06","07","08", "09", "10", "11", "12", "13", "14", "15", "16","17","18","19",
            "20","21","22","23","24","25","26","27","28","29","30","31"};
    public static String tahunn, bulaan, harri;
    Snackbar snackBar;
    Toolbar toolbar;
    View snackbarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        toolbar = findViewById(R.id.tolbar);
        snackBar = Snackbar.make(toolbar, "", Snackbar.LENGTH_INDEFINITE);
        //Inisialiasi Array Adapter dengan memasukkan String Array
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,mounth);

        final ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,years);

        final ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,days);
        requestStoragePermission();
        coordinator = (CoordinatorLayout) findViewById(R.id.coordinator);
        coordinator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });

        tahun = (Spinner) findViewById(R.id.tahunan);
        bulan = (Spinner) findViewById(R.id.bulanan);
        hari = (Spinner) findViewById(R.id.harian);
        adminpshsl = (TextView) findViewById(R.id.admposhsl);
        tanggallpr = (TextView) findViewById(R.id.tanggallpr);
        listvieww =  findViewById(R.id.listview);
        //Memasukan Adapter pada Spinner
        tahun.setAdapter(adapter2);
        bulan.setAdapter(adapter);
        hari.setAdapter(adapter3);
        tahun.setSelection(Arrays.asList(years).indexOf(getTahun()));
        bulan.setSelection(Arrays.asList(mounth).indexOf(getBulan()));
        hari.setSelection(Arrays.asList(days).indexOf(getTanggal()));
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportActionBar().setTitle("Laporan Transaksi");
        preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        final String admps = preferences.getString("adminpos", null);
        adminpshsl.setText(admps);
        itemLaporans = new ArrayList<>();
        carilaporan = (Button) findViewById(R.id.carilaporan);
        carilaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tahunn = tahun.getSelectedItem().toString().trim();
                bulaan = bulan.getSelectedItem().toString().trim();
                harri = hari.getSelectedItem().toString().trim();
                if (!tahunn.equals("") && !bulaan.equals("") && !harri.equals("")){
                    tanggallpr.setText(getNamaHari(harri, bulaan, tahunn) + ", " + harri + " " + getNamaBulan(bulaan) + " "  + tahunn);
                    loadrincianhari();
                } else if (!tahunn.equals("") && !bulaan.equals("")) {
                    tanggallpr.setText( getNamaBulan(bulaan) + " "  + tahunn);
                    loadrincianbulan();
                } else {
                    Log.e("Laporan", "Lainnya");
                    SnackBarMsg("Laporan tidak tersedia silahkan periksa kembali tanggal bulan dan tahun");
                    // ShowDialog();
                }
            }
        });

        listvieww.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),ActivityLaporanRinci.class);
                ItemLaporan itemLaporan = itemLaporans.get(position);
                intent.putExtra("trx_code", (itemLaporan.getTrx_code()));
                intent.putExtra("total_price", (itemLaporan.getTotal_price()));
                intent.putExtra("datetime", (itemLaporan.getTrx_datetime()));
                startActivity(intent);
            }
        });
    }

    private void loadrincianbulan() {
        final String ardkdhost = Constanta.Setting.host_laporan_bulanan;
        final String place = preferences.getString("placeid", null);
        kdhost = ardkdhost;
        final String tahunn = tahun.getSelectedItem().toString();
        final String bulaan = bulan.getSelectedItem().toString();
        final String URL_JSON = kdhost + place + "/" + tahunn + "/" + bulaan;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_JSON,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            itemLaporans.clear();
                            JSONObject obj = new JSONObject(response);
                            playerArray = obj.getJSONArray("values");
                            for (int i = 0; i < playerArray.length(); i++) {
                                JSONObject playerObject = playerArray.getJSONObject(i);
                                ItemLaporan itemLaporan = new ItemLaporan(
                                        i,
                                        playerObject.getString("trx_code"),
                                        playerObject.getString("datetime"),
                                        playerObject.getString("total_price"),
                                        playerObject.getString("jumlah"));
                                itemLaporans.add(itemLaporan);
                            }
                            adapterr = new ListAdapterLaporan(itemLaporans, getApplicationContext());
                            listvieww.setAdapter(adapterr);
                            if (itemLaporans.size() <= 0){
                                SnackBarMsg("Laporan tidak tersedia silahkan periksa kembali tanggal bulan dan tahun");
                                // ShowDialog();
                            }
                            Log.e("Arindo", playerArray.toString());
                            Log.e("response", response);
                            //buat dokumentPDF
                            //DokumenPDF dokumenPDF = new DokumenPDF();
                            //dokumenPDF.createdokumenPDF(playerArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void loadrincianhari() {
        final String ardkdhost = Constanta.Setting.host_laporan_harian;
        final String place = preferences.getString("placeid", null);
        kdhost = ardkdhost;
        final String tahunn = tahun.getSelectedItem().toString();
        final String bulaan = bulan.getSelectedItem().toString();
        final String harii = hari.getSelectedItem().toString();
        final String URL_JSON = kdhost + place + "/" + tahunn + "/" + bulaan + "/" + harii;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_JSON,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            itemLaporans.clear();
                            JSONObject obj = new JSONObject(response);
                            playerArray = obj.getJSONArray("values");
                            for (int i = 0; i < playerArray.length(); i++) {
                                JSONObject playerObject = playerArray.getJSONObject(i);
                                ItemLaporan itemLaporan = new ItemLaporan(
                                        i,
                                        playerObject.getString("trx_code"),
                                        playerObject.getString("datetime"),
                                        playerObject.getString("total_price"),
                                        playerObject.getString("jumlah"));
                                itemLaporans.add(itemLaporan);
                            }
                            ListAdapterLaporan adapterr = new ListAdapterLaporan(itemLaporans, getApplicationContext() );
                            listvieww.setAdapter(adapterr);
                            if (itemLaporans.size() <= 0){
                                SnackBarMsg("Laporan tidak tersedia silahkan periksa kembali tanggal bulan dan tahun");
                            }
                            Log.e("Arindo", playerArray.toString());
                            //DokumenPDF dokumenPDF = new DokumenPDF();
                            //dokumenPDF.createdokumenPDF(playerArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Toast.makeText(ActivityLaporan.this, "Permission denied", Toast.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(),"Error occurred! ",Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Memanggil atau memasang menu item pada toolbar dari layout menu_bar.xml
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_report, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_report:
                if (itemLaporans.size() <= 0){
                    SnackBarMsg("Laporan tidak tersedia silahkan periksa kembali tanggal bulan dan tahun");
                } else  {
                    if (playerArray == null) {
                        SnackBarMsg("Laporan tidak tersedia silahkan periksa kembali tanggal bulan dan tahun");

                    } else {
                        DokumenPDF dokumenPDF = new DokumenPDF();
                        dokumenPDF.createdokumenPDF(playerArray);
                        SnackBarMsg("Laporan Transaksi Berhasil Tersimpan");
                        //Toast.makeText(ActivityLaporan.this, "Dokumen berhasil tersimpan ", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.action_print:
                //Kode disini akan di eksekusi saat tombol search di klik
                Toast.makeText(this, "PDF Print", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    private String getTanggal() {
        DateFormat dateFormat = new SimpleDateFormat("dd");
        Date date = new Date();
        return dateFormat.format(date);
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

    private String getNamaBulan(String bulan) {
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

    private void SnackBarMsg(String msg){
        //final Snackbar snackBar = Snackbar.make(toolbar, msg, Snackbar.LENGTH_INDEFINITE);
        snackBar = Snackbar.make(toolbar, msg, Snackbar.LENGTH_INDEFINITE);
        snackBar.setActionTextColor(getResources().getColor(R.color.colorPrimary));

        snackbarView = snackBar.getView();
        //snackbarView.setBackgroundColor(Color.WHITE);
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackBar.dismiss();
        snackBar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
        // snackBar.dismiss();
    }
}