package com.arindo.waserbanura;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arindo.waserbanura.Constanta.BluetoothHandler;
import com.arindo.waserbanura.Constanta.Constanta;
import com.arindo.waserbanura.Constanta.PrinterCommands;
import com.arindo.waserbanura.Model.ItemDataSqlite;
import com.arindo.waserbanura.Model.ItemLaporanRinci;
import com.arindo.waserbanura.Sqlite.DBHelper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.zj.btsdk.BluetoothService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.arindo.waserbanura.Fragment.Fragment_Beranda.notificationBadge;
import static com.arindo.waserbanura.MainActivity.mCartItemCount;
import static com.arindo.waserbanura.MainActivity.setupBadge;
import static com.arindo.waserbanura.MainActivity.textCartItemCount;

public class ActivityPembayaranBerhasil extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, BluetoothHandler.HandlerInterface {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    DBHelper SQLite = new DBHelper(this);
    public static final String TAG_ID = "id";
    public static final String TAG_MENUID = "menuid";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_TOTAL = "total";
    public static final String TAG_JUMLAH = "jumlah";
    public static final String TAG_HARGA = "harga";
    public static final String TAG_URL = "url";
    private ArrayList<HashMap<String, String>> row;
    private TextView tanggal, PtArindo, jlarhanudri, totaltxt, tunaitxt, kembaliantxt, placeidtxt, trxcodetxt;
    private Button cetakbukti, btnkirimbukti;
    public static final int RC_BLUETOOTH = 0;
    public static final int RC_CONNECT_DEVICE = 1;
    public static final int RC_ENABLE_BLUETOOTH = 2;
    private BluetoothService mService = null;
    private boolean isPrinterReady = false;
    private final String TAG = ActivityPembayaranBerhasil.class.getSimpleName();
    private NumberFormat formatRupiah;
    private ImageView printdisable, printeneble;
    public static String kdhost, induk;
    List<ItemDataSqlite> itemList = new ArrayList<ItemDataSqlite>();
    JSONObject jsonObject, playerObject2, obj, playerObject;
    JSONArray jsonArray, playerArray;
    private RequestQueue requestQueue;
    private List<ItemLaporanRinci> itemLaporanRincis;
    ItemLaporanRinci itemLaporanRinci;
    String adminpos, nomorHp, totalstr, tunaistr, kembalianstr, placesidstr;
    EditText edtnotelefon;
    public static final int PICK_CONTACT = 100;
    public static String tahunn, bulaan, harri;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_berhasil);

        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        SQLite = new DBHelper(getApplicationContext());
        requestQueue = Volley.newRequestQueue(this);
        Locale localeID = new Locale("in", "ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        printdisable = findViewById(R.id.printdisable);
        printeneble = findViewById(R.id.printeneble);
        cetakbukti = findViewById(R.id.btncetakbukti);
        btnkirimbukti = findViewById(R.id.btnkirimbukti);
        tanggal = findViewById(R.id.tanggaltxt);
        tahunn = gettahun();
        bulaan = getbulan();
        harri = gethari();
        tanggal.setText(getNamaHari(harri, bulaan, tahunn) + "," + harri + " " + getNamaBulan(bulaan) + " "  + tahunn);
        PtArindo = findViewById(R.id.PtArindo);
        jlarhanudri = findViewById(R.id.jlarhanudri);
        placeidtxt = findViewById(R.id.placeidtxt);
        trxcodetxt = findViewById(R.id.trxcodetxt);
        totaltxt = findViewById(R.id.totaltxt);
        tunaitxt = findViewById(R.id.tunaitxt);
        kembaliantxt = findViewById(R.id.kembaliantxt);
        itemLaporanRincis = new ArrayList<>();
        printeneble.setVisibility(View.GONE);
        preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        trxcodetxt.setText(preferences.getString("trxcodepref", null));
        adminpos = preferences.getString("trxadmpref", null);
        placesidstr = preferences.getString("placeid",null);
        editor = preferences.edit();
        setupBluetooth();

        printdisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mService.isBTopen()) {
                        startActivityForResult(new Intent(ActivityPembayaranBerhasil.this, DeviceActivity.class), RC_CONNECT_DEVICE);
                    } else {
                        requestBluetooth();
                    }
                } catch (Exception e) {
                    Toast.makeText(ActivityPembayaranBerhasil.this, " Tidak ada Bluetooth tersedia ", Toast.LENGTH_LONG).show();
                    Log.e("try", String.valueOf(e));
                }
            }
        });

        btnkirimbukti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityPembayaranBerhasil.this, ActivityKirimWa.class);
                intent.putExtra("totalkirimwa", totalstr);
                startActivity(intent);
               // startActivity(new Intent(ActivityPembayaranBerhasil.this, ActivityKirimWa.class));
               // showCustomDialog();
            }
        });

        cetakbukti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mService.isAvailable()) {
                    Log.i(TAG, "printText: perangkat tidak support bluetooth");
                    return;
                }
                if (isPrinterReady) {
                    if (PtArindo.getText().toString().isEmpty() || jlarhanudri.getText().toString().isEmpty() ) {
                        Toast.makeText(ActivityPembayaranBerhasil.this, "Cant print null text", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String BILL = "";
                    BILL = BILL + "\n";
                    BILL = BILL + PtArindo.getText().toString() + "\n";
                    BILL = BILL + jlarhanudri.getText().toString() + "\n";

                    BILL = BILL
                            + "--------------------------------\n";
                    mService.write(PrinterCommands.ESC_ALIGN_CENTER);
                    mService.write(BILL.getBytes());
                    BILL = tanggal.getText().toString() + "\n";
                    BILL = BILL + "--------------------------------";

                    for (int j = 0; j < row.size(); j++) {
                        BILL = BILL + "\n" + row.get(j).get(TAG_NAMA);
                        BILL = BILL + "\n" + String.format(" %1$1s %2$1s %3$1s  %4$13s", row.get(j).get(TAG_JUMLAH), "x", formatRupiah.format(Integer.parseInt(row.get(j).get(TAG_HARGA))), formatRupiah.format(Integer.parseInt(row.get(j).get(TAG_TOTAL)))) + "\n";
                    }
                    BILL = BILL
                            + "--------------------------------\n";

                    try {
                        BILL = BILL + String.format("%1$1s %2$25s", "Total", playerObject2.getString("Total") + "\n");
                        BILL = BILL + String.format("%1$1s %2$25s", "Tunai", playerObject2.getString("Tunai") + "\n");
                        BILL = BILL + String.format("%1$1s %2$23s", "Kembali", playerObject2.getString("Kembalian") + "\n");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    BILL = BILL
                            + "--------------------------------\n";
                    BILL = BILL + "\n\n ";
                    Log.e("Error", BILL);
                    mService.write(PrinterCommands.ESC_ALIGN_LEFT);
                    mService.write(BILL.getBytes());
                    //order();
                    finish();
                } else {
                    if (mService.isBTopen()) {
                        Toast.makeText(ActivityPembayaranBerhasil.this, "Scan Print Bluethooth Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        requestBluetooth();
                    }
                }
            }
        });

        loadrincianlaporan();

        loadpembayaran();

    }

    private String gettahun() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getbulan() {
        DateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String gethari() {
        DateFormat dateFormat = new SimpleDateFormat("dd");
        Date date2 = new Date();
        return dateFormat.format(date2);
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
    public void onDeviceConnected() {
        isPrinterReady = true;
        // btn_print_scan.setText("Terhubung dengan perangkat");
        printeneble.setVisibility(View.VISIBLE);
        printdisable.setVisibility(View.GONE);
        Toast.makeText(ActivityPembayaranBerhasil.this, "Perangkat Terhubung dengan Print", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceConnecting() {
        //  btn_print_scan.setText("Sedang menghubungkan...");
        printeneble.setVisibility(View.VISIBLE);
        printdisable.setVisibility(View.GONE);
    }

    @Override
    public void onDeviceConnectionLost() {
        isPrinterReady = false;
        //  btn_print_scan.setText("Koneksi perangkat terputus");
        printdisable.setVisibility(View.VISIBLE);
        printeneble.setVisibility(View.GONE);
        Toast.makeText(ActivityPembayaranBerhasil.this, "Koneksi Terputus", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceUnableToConnect() {
        //  btn_print_scan.setText("Tidak dapat terhubung ke perangkat");
        printdisable.setVisibility(View.VISIBLE);
        printeneble.setVisibility(View.GONE);
        Toast.makeText(ActivityPembayaranBerhasil.this, "Tidak Tersambung, Coba Lagi", Toast.LENGTH_SHORT).show();

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

            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cur =  ActivityPembayaranBerhasil.this.getContentResolver().query(contactData, null, null, null, null);
                    if (cur == null) return;
                    try {
                        if (cur.moveToFirst()) {
                            int phoneIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            edtnotelefon.setText(cur.getString(phoneIndex));
                        }
                    }
                    finally {
                        cur.close();
                    }
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

    private void loadrincianlaporan() {
        final String ardkdhost = Constanta.Setting.host_laporan_rinci;
        kdhost = ardkdhost;
        final String codetrxx = preferences.getString("trxcodepref", null);
        final String URL_JSON = kdhost + codetrxx;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_JSON,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //transaksi = response;
                        try {
                            itemLaporanRincis.clear();
                            obj = new JSONObject(response);
                            playerArray = null;
                            playerArray = obj.getJSONArray("values");
                            for (int j = 0; j < playerArray.length(); j++) {

                                playerObject = null;
                                playerObject = playerArray.getJSONObject(j);
                                itemLaporanRinci = new ItemLaporanRinci(
                                        j + 1,
                                        playerObject.getString("menu"),
                                        playerObject.getString("menu_id"),
                                        playerObject.getString("qty"),
                                        playerObject.getString("trx_price"),
                                        playerObject.getString("places_id"),
                                        playerObject.getString("trx_total"));
                                itemLaporanRincis.add(itemLaporanRinci);
                                editor.putString("preflaporanrinci", response);
                                editor.apply();
                            }
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
        final String trxxcode = preferences.getString("trxcodepref", null);
        final String JSON_URL = kdhost + trxxcode;
        Log.e("trxcode", trxxcode + " " + JSON_URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        induk = response;
                        jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            jsonArray = null;
                            jsonArray = jsonObject.getJSONArray("values");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                playerObject2 = null;
                                playerObject2 = jsonArray.getJSONObject(i);

                                totalstr = playerObject2.getString("Total");
                                tunaistr = playerObject2.getString("Tunai");
                                kembalianstr = playerObject2.getString("Kembalian");

                                Log.e("Total", totalstr);
                                Log.e("Tunai", tunaistr);
                                Log.e("Kembalian", kembalianstr);

                                totaltxt.setText(formatRupiah.format(Integer.parseInt(totalstr)));
                                tunaitxt.setText(formatRupiah.format(Integer.parseInt(tunaistr)));
                                kembaliantxt.setText(formatRupiah.format(Integer.parseInt(kembalianstr)));
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
        switch (bulan) {
            case "01":
                return "Januari";
            case "02":
                return "Februari";
            case "03":
                return "Maret";
            case "04":
                return "April";
            case "05":
                return "Mei";
            case "06":
                return "Juni";
            case "07":
                return "Juli";
            case "08":
                return "Agustus";
            case "09":
                return "September";
            case "10":
                return "Oktober";
            case "11":
                return "Novemver";
            case "12":
                return "Desember";
            default:
                return "";
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ArrayList<HashMap<String, String>> roks = SQLite.getAllDataPlaceid(placesidstr);
                        for (int k = 0; k < roks.size(); k++) {
                            String idd = roks.get(k).get(TAG_ID);
                            SQLite.delete2(Integer.parseInt(idd));
                            itemList.clear();
                            //getAllData();
                            setupBadge();
                            mCartItemCount = mCartItemCount - 1;
                            notificationBadge.setNumber(mCartItemCount);
                            textCartItemCount.setVisibility(View.GONE);
                        }
                        finish();
                    }
                }).create().show();
        }
    }

