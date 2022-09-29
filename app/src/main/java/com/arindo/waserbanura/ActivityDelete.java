package com.arindo.waserbanura;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arindo.waserbanura.Sqlite.DBHelper;

import java.text.NumberFormat;
import java.util.Locale;

import static com.arindo.waserbanura.Fragment.Fragment_Beranda.notificationBadge;
import static com.arindo.waserbanura.MainActivity.mCartItemCount;
import static com.arindo.waserbanura.MainActivity.setupBadge;


public class ActivityDelete extends AppCompatActivity {
    private RequestQueue requestQueue;
    TextView iddelete, menuiddelete, namabarangdelete, totalhargadelete, qtydelete, hargadelete, placeiddelete;
    Button btndelete;
    DBHelper SQLite = new DBHelper(this);
    String id,menuid,namabrg,hargabrg,quantity,totalhrg,url,placeidbrg;
    private NumberFormat formatRupiah;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        Locale localeID = new Locale("in", "ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        Toolbar toolbar = findViewById(R.id.tolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportActionBar().setTitle("Delete");
        iddelete = (TextView) findViewById(R.id.iddelete);
        requestQueue = Volley.newRequestQueue(this);
        menuiddelete = (TextView) findViewById(R.id.menuiddelete);
        namabarangdelete = (TextView) findViewById(R.id.namabarangdelete);
        hargadelete = (TextView) findViewById(R.id.hargadelete);
        qtydelete = (TextView) findViewById(R.id.qtydelete);
        totalhargadelete = (TextView) findViewById(R.id.totalhargadelete);
        placeiddelete = (TextView) findViewById(R.id.placeiddelete);
        btndelete = (Button) findViewById(R.id.btndelete);
        id = getIntent().getStringExtra(ActivityKeranjang.TAG_ID);
        menuid = getIntent().getStringExtra(ActivityKeranjang.TAG_MENUID);
        namabrg = getIntent().getStringExtra(ActivityKeranjang.TAG_NAMA);
        hargabrg = getIntent().getStringExtra(ActivityKeranjang.TAG_HARGA);
        quantity = getIntent().getStringExtra(ActivityKeranjang.TAG_JUMLAH);
        totalhrg = getIntent().getStringExtra(ActivityKeranjang.TAG_TOTAL);
        url = getIntent().getStringExtra(ActivityKeranjang.TAG_URL);
        placeidbrg = getIntent().getStringExtra(ActivityKeranjang.TAG_PLACEID);
        iddelete.setText(id);
        menuiddelete.setText(menuid);
        placeiddelete.setText(placeidbrg);
        namabarangdelete.setText(namabrg);
        hargadelete.setText(formatRupiah.format(Integer.parseInt(hargabrg)));
        qtydelete.setText(quantity);
        totalhargadelete.setText(formatRupiah.format(Integer.parseInt(totalhrg)));
        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
                if (mCartItemCount == 0) {
                    return;
                } else {
                    mCartItemCount = mCartItemCount - 1;
                    notificationBadge.setNumber(mCartItemCount);
                    setupBadge();
                }
            }
        });
    }

    public void delete() {
        String id = getIntent().getStringExtra(ActivityKeranjang.TAG_ID);
        int success = SQLite.delete(id);
        String message = "Keranjang gagal di hapus";
        if (success != 0) {
            message = "Keranjang berhasil di hapus";
            onBackPressed();
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}

