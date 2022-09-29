package com.arindo.waserbanura;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.arindo.waserbanura.Sqlite.DBHelper;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

import static com.arindo.waserbanura.Fragment.Fragment_Beranda.notificationBadge;
import static com.arindo.waserbanura.MainActivity.mCartItemCount;

public class ActivityOrder extends AppCompatActivity {

    private TextView menuidorder, hargabarangorder, stokbarangorder, urlorder, totalhargaorder, quantityView, namabarang, placeidorder;
    private Button Decrement, Increment, btncheckout, btnkeranjang;
    private ImageView imgorder;
    String menuidstr, hargastr, namabarangstr, urlstr, stokbarang, placeidstr;
    private SharedPreferences preferences;
    int stc = 0;
    int desertNumber = 0;
    int hasil = 0;
    Locale localeID;
    NumberFormat formatRupiah;
    DBHelper SQLite = new DBHelper(this);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        getSupportActionBar().setTitle("Checkout");
        getSupportActionBar().setSubtitle("Checkout detail pesanan anda");
        preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        imgorder = findViewById(R.id.imgorder);
        namabarang = findViewById(R.id.namabarang);
        menuidorder = findViewById(R.id.menuidorder);
        hargabarangorder = findViewById(R.id.hargabarangorder);
        stokbarangorder = findViewById(R.id.stokbarangorder);
        urlorder = findViewById(R.id.urlorder);
        totalhargaorder = findViewById(R.id.totalhargaorder);
        quantityView = findViewById(R.id.dessert_number);
        Decrement = findViewById(R.id.Decrement);
        Increment = findViewById(R.id.Increment);
        btncheckout = findViewById(R.id.btncheckout);
        btnkeranjang = findViewById(R.id.btnkeranjang);
        placeidorder = findViewById(R.id.placeidorder);
        localeID = new Locale("in", "ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        stokbarang = preferences.getString("responseurl", null);
        Log.e("ambilresponse", stokbarang );
        menuidstr = getIntent().getStringExtra("menu_id");
        hargastr = getIntent().getStringExtra("price");
        namabarangstr = getIntent().getStringExtra("menu");
        placeidstr = getIntent().getStringExtra("Place_id");
        urlstr = getIntent().getStringExtra("file_name");
        Glide
                .with(this)
                .load(urlstr)
                .into(imgorder);

        menuidorder.setText(menuidstr);
        hargabarangorder.setText(formatRupiah.format(Integer.parseInt(hargastr)));
        namabarang.setText(namabarangstr);
        urlorder.setText(urlstr);
        placeidorder.setText(placeidstr);
        totalhargaorder.setText(String.valueOf(desertNumber));
        String jml = String.valueOf(SQLite.getJumlah(menuidstr));
        hasil = Integer.parseInt(hargastr) * Integer.parseInt(jml);
        quantityView.setText(jml);
        String hsl = (String.valueOf(hasil));
        totalhargaorder.setText(hsl);
        totalhargaorder.setText(formatRupiah.format(Integer.parseInt(hsl)));
        getresponse();

        Decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView quantityView = (TextView) findViewById(R.id.dessert_number);
                String quantityString = quantityView.getText().toString();
                desertNumber = Integer.parseInt(quantityString);
                desertNumber -= 1;
                if (desertNumber < 0) {
                    desertNumber = 0;
                }
                if (desertNumber == 0) {
                    Dialogstokminimal();
                }
                quantityView.setText(String.valueOf(desertNumber));
                desertNumber = Integer.parseInt(hargastr) * desertNumber;
                totalhargaorder.setText(String.valueOf(desertNumber));
                totalhargaorder.setText(formatRupiah.format(desertNumber));
            }
        });

        Increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView quantityView = (TextView) findViewById(R.id.dessert_number);
                String quantityString = quantityView.getText().toString();
                desertNumber = Integer.parseInt(quantityString);
                String stokakhir = stokbarangorder.getText().toString();
                desertNumber += 1;
                if (desertNumber > Integer.parseInt(stokakhir)) {
                    Dialogstokterbatas();
                    desertNumber = Integer.parseInt(stokakhir);
                }
                quantityView.setText(String.valueOf(desertNumber));
                desertNumber = Integer.parseInt(hargastr) * desertNumber;
                totalhargaorder.setText(String.valueOf(desertNumber));
                totalhargaorder.setText(formatRupiah.format(desertNumber));

            }
        });

        btncheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantityView.getText().toString().equals("0")){
                    Dialogstokminimal();
                } else {
                    try {
                        if ( SQLite.cekmenuid(menuidstr) > 0) {
                            edit();
                        } else {
                            save();
                        }
                    } catch (Exception e) {
                        Log.e("Submit", e.toString());
                    }
                }

            }
        });

        btnkeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantityView.getText().toString().equals("0")){
                    Dialogstokminimal();
                } else {
                    try {
                        if (SQLite.cekmenuid(menuidstr) > 0) {
                            editkeranjang();
                        } else {
                            savekeranjang();
                        }
                    } catch (Exception e) {
                        Log.e("Submit", e.toString());
                    }
                }
            }
        });
    }

    private void getresponse() {
        JSONObject obj2 = null;
        try {
            obj2 = new JSONObject(stokbarang);
            JSONArray playerArray2 = null;
            playerArray2 = obj2.getJSONArray("values");
            for (int i = 0; i < playerArray2.length(); i++) {
                JSONObject playerObject = null;
                playerObject = playerArray2.getJSONObject(i);
                String iddd = playerObject.getString("id");
                String stokakhir = playerObject.getString("stockakhir");
                if (iddd.contains(menuidstr)) {
                    if (Integer.parseInt(stokakhir) > 0) {
                        stc = stc + Integer.parseInt(stokakhir);
                    } else if (Integer.parseInt(stokakhir) == 0) {
                        stc = stc + 0;
                    }
                    break;
                }
            }
            if (stc > 0) {
                stokbarangorder.setText(String.valueOf(stc));
            } else {
                Dialogstokbarang();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void Dialogstokbarang() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this
        );
        alertDialogBuilder.setTitle("Pemberitahuan");
        alertDialogBuilder
                .setMessage("Stok Barang Tidak Tersedia")
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void Dialogstokterbatas() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this
        );
        alertDialogBuilder.setTitle("Stok Habis");
        alertDialogBuilder
                .setMessage("Tidak Bisa Melebihi Stok Tersedia")
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void Dialogstokminimal() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this
        );
        alertDialogBuilder.setTitle("Pembelian Kurang");
        alertDialogBuilder
                .setMessage("Minimal Pesan 1 Barang")
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void save() {

        String.valueOf(menuidorder.getText());
        if (String.valueOf(quantityView.getText()).equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Silahkan Lengkapi Pesanan Anda ...", Toast.LENGTH_SHORT).show();
        } else {
            SQLite.insert(menuidstr,
                    namabarangstr,
                    String.valueOf(desertNumber),
                    hargastr,
                    quantityView.getText().toString().trim(),
                    urlorder.getText().toString().trim(),
                    placeidorder.getText().toString().trim());
            startActivity(new Intent(ActivityOrder.this, ActivityKeranjang.class));
            mCartItemCount = mCartItemCount + 1;
            notificationBadge.setNumber(mCartItemCount);
            MainActivity.setupBadge();
            ActivityOrder.this.finish();
           // Toast.makeText(ActivityOrder.this, "Save Berhasil", Toast.LENGTH_SHORT).show();
        }
    }

    private void edit() {
        String.valueOf(menuidorder.getText());
        if (String.valueOf(quantityView.getText()).equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Lengkapi Pesanan Anda ...", Toast.LENGTH_SHORT).show();
        } else {
            SQLite.update(
                    menuidstr,
                    namabarangstr,
                    String.valueOf(desertNumber),
                    hargastr,
                    quantityView.getText().toString().trim(),
                    urlorder.getText().toString().trim(),
                    placeidorder.getText().toString().trim());
            startActivity(new Intent(ActivityOrder.this, ActivityKeranjang.class));
            mCartItemCount = mCartItemCount + 1;
            notificationBadge.setNumber(mCartItemCount);
            MainActivity.setupBadge();
            ActivityOrder.this.finish();
        }
        Toast.makeText(ActivityOrder.this, "Pesanan Berhasil Di Update", Toast.LENGTH_SHORT).show();
    }

    private void savekeranjang() {

        String.valueOf(menuidorder.getText());
        if (String.valueOf(quantityView.getText()).equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Silahkan Lengkapi Pesanan Anda ...", Toast.LENGTH_SHORT).show();
        } else {
            SQLite.insert(menuidstr,
                    namabarangstr,
                    String.valueOf(desertNumber),
                    hargastr,
                    quantityView.getText().toString().trim(),
                    urlorder.getText().toString().trim(),
                    placeidorder.getText().toString().trim());
           // startActivity(new Intent(ActivityOrder.this, ActivityKeranjang.class));
           // mCartItemCount = mCartItemCount + 1;
           // notificationBadge.setNumber(mCartItemCount);
          //  MainActivity.setupBadge();
            Dialogtambahkeranjang();

        }
    }

    private void editkeranjang() {
        String.valueOf(menuidorder.getText());
        if (String.valueOf(quantityView.getText()).equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Lengkapi Pesanan Anda ...", Toast.LENGTH_SHORT).show();
        } else {
            SQLite.update(
                    menuidstr,
                    namabarangstr,
                    String.valueOf(desertNumber),
                    hargastr,
                    quantityView.getText().toString().trim(),
                    urlorder.getText().toString().trim(),
                    placeidorder.getText().toString().trim());
            Dialogupdatekeranjang();
        }
    }

    private void Dialogtambahkeranjang() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this
        );
        alertDialogBuilder.setTitle("Pemberitahuan");
        alertDialogBuilder
                .setMessage("Berhasil Menambahkan Ke keranjang")
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityOrder.this.finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void Dialogupdatekeranjang() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this
        );
        alertDialogBuilder.setTitle("Pemberitahuan");
        alertDialogBuilder
                .setMessage("Keranjang Berhasil Di Update")
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityOrder.this.finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
