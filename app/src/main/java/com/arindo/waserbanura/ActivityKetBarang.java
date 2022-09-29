package com.arindo.waserbanura;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import java.text.NumberFormat;
import java.util.Locale;

public class ActivityKetBarang extends AppCompatActivity {
    ImageView gambar;
    TextView Menu, Des, price;
    String menudesc;
    String menu;
    String harga;
    String gambarf;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ketbrng);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityKetBarang.this.finish();
            }
        });

        getSupportActionBar().setTitle("Deskripsi");
        getSupportActionBar().setSubtitle("deskripsi barang");
        // casting id
        gambar = (ImageView) findViewById(R.id.image_produk);
        Menu = (TextView)findViewById(R.id.menu);
        Des = (TextView)findViewById(R.id.deskripsi);
        price = (TextView)findViewById(R.id.price);
        // ambil nilai yang di kirim pada saat di klik
        menudesc = getIntent().getStringExtra("menu_desc");
        menu = getIntent().getStringExtra("menu");
        gambarf = getIntent().getStringExtra("file_name");
        harga = getIntent().getStringExtra("price");
        // tampilkan di widged
        Glide
                .with(this)
                .load(gambarf)
                .into(gambar);
        // gambar.setImageResource(gambarf);
        Menu.setText(menu);
        Des.setText(menudesc);
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        price.setText(formatRupiah.format(Integer.parseInt(harga)));
    }
}
