package com.arindo.waserbanura;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.arindo.waserbanura.Fragment.Fragment_Akun;
import com.arindo.waserbanura.Fragment.Fragment_Beranda;
import com.arindo.waserbanura.Fragment.Fragment_History;
import com.arindo.waserbanura.Fragment.Fragment_Pengajuan;
import com.arindo.waserbanura.Model.ItemDataSqlite;
import com.arindo.waserbanura.Sqlite.DBHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.arindo.waserbanura.ActivityKeranjang.TAG_ID;
import static com.arindo.waserbanura.ActivityKeranjang.placeidstr;
import static com.arindo.waserbanura.Fragment.Fragment_Beranda.notificationBadge;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks{
    private BottomNavigationView menu_bawah;
    Fragment fragment;
    FragmentManager fragmentManager;
    public static TextView textCartItemCount;
    public static int mCartItemCount = 0;
    ArrayList<HashMap<String, String>> list_data;
    private static DBHelper SQLite;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    public static final int RC_BLUETOOTH = 0;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothDevice mBluetoothDevice;
    BluetoothAdapter mBluetoothAdapter;
    public static String username;
    List<ItemDataSqlite> itemList = new ArrayList<ItemDataSqlite>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBluetooth();
        // ActivitySetor.showNotif();
        SQLite = new DBHelper(getApplicationContext());
        menu_bawah = findViewById(R.id.menu_bawah);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_container, new Fragment_Beranda()).commitNow();
        menu_bawah.setOnNavigationItemSelectedListener(this);
        menu_bawah.setItemHorizontalTranslationEnabled(true);
        preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        preferences.getLong("waktu", 0);
        username = preferences.getString("placeid", null);
        long sekarang = System.currentTimeMillis();
        long time = sekarang - preferences.getLong("waktu", 0);
        Log.e("waktu1", String.valueOf(time));

        if (time <= 172800000) {
        } else {
            preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
            editor = preferences.edit();
            editor.clear();
            editor.apply();
            ShowDialog();
            return;
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(MainActivity.this, "Gagal Terhubung Ke Bluethooth", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,
                        REQUEST_ENABLE_BT);
            }
        }
        //validasi();
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {

            /*
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;
             */
            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {

                } else {
                    Toast.makeText(MainActivity.this, "Tidak Terhubung Ke Bluethooth", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
                        editor = preferences.edit();
                        editor.clear();
                        editor.apply();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.pengajuan:
                fragment = new Fragment_Pengajuan();
                break;
            case R.id.home:
                fragment = new Fragment_Beranda();
                break;
            case R.id.akun:
                fragment = new Fragment_Akun();
                break;
            case R.id.History:
                fragment = new Fragment_History();
        }

        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_container, fragment).commit();
        preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        preferences.getLong("waktu", 0);
        long sekarang = System.currentTimeMillis();
        long time = sekarang - preferences.getLong("waktu", 0);
        Log.e("waktu2", String.valueOf(time));
        if (time <= 172800000 ) {

        } else {
            preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
            editor = preferences.edit();
            editor.clear();
            editor.apply();
            ShowDialog();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);
        View actionView = menuItem.getActionView();
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);
        setupBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupBadge();
        Log.e("Main", "Resume");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            Intent intent = new Intent(MainActivity.this, ActivityKeranjang.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void setupBadge() {
        if (textCartItemCount == null) {
            return;
        }
        ArrayList<HashMap<String, String>> row = SQLite.getAllDataPlaceid(username);
        for (int i = 0; i < row.size(); i++) {
            mCartItemCount =  i + 1;
            notificationBadge.setNumber(mCartItemCount);
        }
        if (mCartItemCount == 0) {
            if (textCartItemCount.getVisibility()!= View.GONE) {
                textCartItemCount.setVisibility(View.GONE);
            }
        } else {
            textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
            if (textCartItemCount.getVisibility() != View.VISIBLE) {
                textCartItemCount.setVisibility(View.VISIBLE);
            }
        }
    }

    private void ShowDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this
        );
        alertDialogBuilder.setTitle("Session Sudah Berakhir");
        alertDialogBuilder
                .setMessage("Login kembali untuk melanjutkan")
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @AfterPermissionGranted(RC_BLUETOOTH)
    private void setupBluetooth() {
        String[] params = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};
        if (!EasyPermissions.hasPermissions(this, params)) {
            EasyPermissions.requestPermissions(this, "You need bluetooth permission",
                    RC_BLUETOOTH, params);
            return;
        }
        //   mService = new BluetoothService(this, new BluetoothHandler(this));
    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // TODO: 10/11/17 do something
        Log.e("Permissiomn", "gagal");
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        // TODO: 10/11/17 do something
        Log.e("Permissiomn", "berhasil");
    }

    private void validasi() {
        if (!username.equals(placeidstr)) {
            ArrayList<HashMap<String, String>> roks = SQLite.getAllDataPlaceid(username);
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
        }
    }
}



