package com.arindo.waserbanura;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arindo.waserbanura.Adapter.ListAdapterLaporanRinci;
import com.arindo.waserbanura.Constanta.BluetoothHandler;
import com.arindo.waserbanura.Constanta.Constanta;
import com.arindo.waserbanura.Constanta.DokumenPDFLaporanRinci;
import com.arindo.waserbanura.Constanta.PrinterCommands;
import com.arindo.waserbanura.Model.ItemLaporanRinci;
import com.google.android.material.snackbar.Snackbar;
import com.zj.btsdk.BluetoothService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.arindo.waserbanura.ActivityLaporan.bulaan;
import static com.arindo.waserbanura.ActivityLaporan.harri;
import static com.arindo.waserbanura.ActivityLaporan.tahunn;


public class ActivityLaporanRinci extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, BluetoothHandler.HandlerInterface {

    public static String kdhost;
    public static TextView codetrx, total, tglhsl, txstatus, placeidlaporanrinci;
    private List<ItemLaporanRinci> itemRincis;
    ListView listvieww;
    String transaksi, induk, placeidstr;
    JSONArray playerArray, playerArray2;
    JSONObject obj, obj2;
    JSONObject playerObject, playerObject2;
    private NumberFormat formatRupiah;
    private RequestQueue requestQueue;
    public static final int RC_BLUETOOTH = 0;
    public static final int RC_CONNECT_DEVICE = 1;
    public static final int RC_ENABLE_BLUETOOTH = 2;
    private BluetoothService mService = null;
    private boolean isPrinterReady = false;
    private final String TAG = MainActivity.class.getSimpleName();
    Snackbar snackBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_rinci);
        setupBluetooth();
        requestQueue = Volley.newRequestQueue(this);
        codetrx = (TextView) findViewById(R.id.kodetrxlaporanrinci);
        total = (TextView) findViewById(R.id.totallaporanrinci);
        tglhsl = (TextView) findViewById(R.id.tanggallaporanrinci);
        placeidlaporanrinci = (TextView) findViewById(R.id.placeidlaporanrinci);
        txstatus = (TextView) findViewById(R.id.txstatus);
        tahunn = getTahun();
        bulaan = getBulan();
        harri = getTanggal();
        listvieww =  findViewById(R.id.listview);
        toolbar = findViewById(R.id.tolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLaporanRinci.this.finish();
            }
        });

        snackBar = Snackbar.make(toolbar, "", Snackbar.LENGTH_INDEFINITE);
        Locale localeID = new Locale("in", "ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        getSupportActionBar().setTitle("Rincian Laporan");
        itemRincis = new ArrayList<>();
        String trxcode = getIntent().getStringExtra("trx_code");
        String totaltrx = getIntent().getStringExtra("total_price");
        String datetimetrx = getIntent().getStringExtra("datetime");
        codetrx.setText(trxcode);
        total.setText(formatRupiah.format(Integer.parseInt(totaltrx)));
        tglhsl.setText(datetimetrx);
        loadrincianlaporan();
        loadpembayaran();

    }

    private void loadrincianlaporan() {
        final String ardkdhost = Constanta.Setting.host_laporan_rinci;
        kdhost = ardkdhost;
        final String codetrxx = getIntent().getStringExtra("trx_code");
        final String URL_JSON = kdhost + codetrxx;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_JSON,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        transaksi = response;
                        try {
                            itemRincis.clear();
                            obj = new JSONObject(response);
                            playerArray = obj.getJSONArray("values");
                            for (int j = 0; j < playerArray.length(); j++) {
                                playerObject = playerArray.getJSONObject(j);
                                ItemLaporanRinci itemRinci = new ItemLaporanRinci(
                                        j + 1,
                                        playerObject.getString("menu"),
                                        playerObject.getString("menu_id"),
                                        playerObject.getString("qty"),
                                        playerObject.getString("trx_price"),
                                        playerObject.getString("places_id"),
                                        playerObject.getString("trx_total"));
                                placeidstr =  playerObject.getString("places_id");
                                placeidlaporanrinci.setText(placeidstr);
                                itemRincis.add(itemRinci);
                            }
                            ListAdapterLaporanRinci adapterr = new ListAdapterLaporanRinci(itemRincis, getApplicationContext() );
                            listvieww.setAdapter(adapterr);
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

    private void loadpembayaran() {
        final String ardkdhost = Constanta.Setting.host_load_pembayaran;
        kdhost = ardkdhost;
        final String trxxcode = getIntent().getStringExtra("trx_code");
        final String JSON_URL = kdhost + trxxcode;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        induk = response;
                        obj2 = null;
                        try {
                            obj2 = new JSONObject(response);
                            playerArray2 = null;
                            playerArray2 = obj2.getJSONArray("values");
                            for (int i = 0; i < playerArray2.length(); i++) {
                                playerObject2 = null;
                                playerObject2 = playerArray2.getJSONObject(i);

                                Log.e("Total", playerObject2.getString("Total"));
                                Log.e("Tunai", playerObject2.getString("Tunai"));
                                Log.e("Kembalian", playerObject2.getString("Kembalian"));
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

    @AfterPermissionGranted(RC_BLUETOOTH)
    private void setupBluetooth() {
        String[] params = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};
        if (!EasyPermissions.hasPermissions(this, params)) {
            EasyPermissions.requestPermissions(this, "You need bluetooth permission",
                    RC_BLUETOOTH, params);
            return;
        }
        mService = new BluetoothService(this, new BluetoothHandler(this));
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        // TODO: 10/11/17 do something
    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // TODO: 10/11/17 do something
    }
    @Override
    public void onDeviceConnected() {
        isPrinterReady = true;
        txstatus.setText("Terhubung dengan perangkat");
        Toast.makeText(ActivityLaporanRinci.this, "Perangkat Terhubung dengan Print", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDeviceConnecting() {
        txstatus.setText("Sedang menghubungkan...");
        Toast.makeText(ActivityLaporanRinci.this, "Menghubungkan. . . ", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDeviceConnectionLost() {
        isPrinterReady = false;
        txstatus.setText("Koneksi perangkat terputus");
        Toast.makeText(ActivityLaporanRinci.this, "Koneksi Terputus", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDeviceUnableToConnect() {
        txstatus.setText("Tidak dapat terhubung ke perangkat");
        Toast.makeText(ActivityLaporanRinci.this, "Tidak Tersambung Periksa Print Bluethooth", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_ENABLE_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "onActivityResult: bluetooth aktif");
                } else {
                    Log.i(TAG, "onActivityResult: bluetooth harus aktif untuk menggunakan fitur ini");
                }
                break;
            case RC_CONNECT_DEVICE:
                if (resultCode == RESULT_OK) {
                    String address = data.getExtras().getString(DeviceActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice mDevice = mService.getDevByMac(address);
                    mService.connect(mDevice);
                }
                break;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_report:
                DokumenPDFLaporanRinci dokumenPDF = new DokumenPDFLaporanRinci();
                dokumenPDF.createdokumenPDFRinci(playerArray);
                SnackBarMsg("Laporan Rincian Transaksi Berhasil Tersimpan");
                // Toast.makeText(ActivityLaporanRinci.this, "Laporan Tersimpan", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_print:
                if (!mService.isAvailable()) {
                    Log.i(TAG, "printText: perangkat tidak support bluetooth");
                    break;
                }
                if (isPrinterReady) {
                    String BILL = "";
                    BILL = BILL + "\n";
                    BILL = BILL + "PT. ARINDO PRATAMA" + "\n";
                    BILL = BILL + "Jl. Arhanudri II No.20, Batununggal, Kec. Bandung Kidul, Kota Bandung, Jawa Barat 40266" + "\n";

                    BILL = BILL
                            + "--------------------------------\n";
                    mService.write(PrinterCommands.ESC_ALIGN_CENTER);
                    mService.write(BILL.getBytes());
                    BILL = String.format("%1$1s", getNamaHari(harri, bulaan, tahunn) + "," + harri + " " + getNamaBulan(bulaan) + " "  + tahunn + "\n");
                    BILL = BILL + "--------------------------------";

                    try {
                        for (int k = 0; k < playerArray.length(); k++) {
                            playerObject = playerArray.getJSONObject(k);
                            playerArray = obj.getJSONArray("values");
                            BILL = BILL + "\n" + playerObject.getString("menu");
                            BILL = BILL + "\n" + String.format("%1$1s %2$1s %3$1s %4$13s", playerObject.getString("qty"), "x",formatRupiah.format(Integer.parseInt(playerObject.getString("trx_price"))) ,formatRupiah.format(Integer.parseInt(playerObject.getString("trx_total")))) + "\n";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    BILL = BILL
                            + "--------------------------------\n";

                    try {
                        for (int i = 0; i < playerArray2.length(); i++) {
                            BILL = BILL + String.format("%1$1s %2$25s", "Total",formatRupiah.format(Integer.parseInt(playerObject2.getString("Total")))  + "\n");
                            BILL = BILL + String.format("%1$1s %2$25s", "Tunai", formatRupiah.format(Integer.parseInt(playerObject2.getString("Tunai"))) + "\n");
                            BILL = BILL + String.format("%1$1s %2$23s", "Kembali", formatRupiah.format(Integer.parseInt(playerObject2.getString("Kembalian"))) + "\n");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    BILL = BILL
                            + "--------------------------------\n";
                    BILL = BILL + "\n\n ";
                    Log.e("Error", BILL);
                    mService.write(PrinterCommands.ESC_ALIGN_LEFT);
                    mService.write(BILL.getBytes());
                    finish();
                } else {
                    if (mService.isBTopen())
                        startActivityForResult(new Intent(this, DeviceActivity.class), RC_CONNECT_DEVICE);
                    else
                        requestBluetooth();
                }
                /*
                Intent intent = new Intent(ActivityLaporanRinci.this, ActivityPrint.class);
                intent.putExtra("transaksi", transaksi);
                intent.putExtra("induk", induk);
                startActivity(intent);
                break;
                 */
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_report, menu);
        return super.onCreateOptionsMenu(menu);
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
    private String getTanggal() {
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

        snackBar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }
}


