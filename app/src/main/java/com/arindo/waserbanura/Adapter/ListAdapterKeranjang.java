package com.arindo.waserbanura.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.arindo.waserbanura.Constanta.PicassoClient;
import com.arindo.waserbanura.R;
import com.arindo.waserbanura.Sqlite.DBHelper;
import com.arindo.waserbanura.Model.ItemDataSqlite;
import com.arindo.waserbanura.ActivityDelete;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


public class ListAdapterKeranjang extends BaseAdapter {
    private Activity activity;
    private DBHelper db;
    private LayoutInflater inflater;
    private List<ItemDataSqlite> items;
    private NumberFormat formatRupiah;

    public ListAdapterKeranjang(Activity activity, List<ItemDataSqlite> items) {
        this.activity = activity;
        this.items = items;
        Locale localeID = new Locale("in", "ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int location) {
        return items.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            assert inflater != null;
            convertView = inflater.inflate(R.layout.list_keranjang, null);
            db = new DBHelper(activity.getApplicationContext());
        }

        final TextView id = (TextView) convertView.findViewById(R.id.id);
        final TextView menuid = (TextView) convertView.findViewById(R.id.menuid);
        final TextView placeid = (TextView) convertView.findViewById(R.id.placeid);
        final TextView nama = (TextView) convertView.findViewById(R.id.nama);
        final TextView total = (TextView) convertView.findViewById(R.id.total);
        final TextView harga = (TextView) convertView.findViewById(R.id.harga);
        final TextView jumlah = (TextView) convertView.findViewById(R.id.jmlh);
        final ImageView movieImage = (ImageView) convertView.findViewById(R.id.imageView);
        final ImageView imgdelete = (ImageView) convertView.findViewById(R.id.imgdelete);
        imgdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), ActivityDelete.class);
                ItemDataSqlite itemDataSqlite = items.get(position);
                intent.putExtra("id", itemDataSqlite.getId() );
                intent.putExtra("menuid", itemDataSqlite.getMenuid());
                intent.putExtra("placeid", itemDataSqlite.getPlaceid());
                intent.putExtra("total", itemDataSqlite.getTotal());
                intent.putExtra("harga", itemDataSqlite.getHarga());
                intent.putExtra("jumlah", itemDataSqlite.getJumlah());
                intent.putExtra("nama", itemDataSqlite.getNama());
                intent.putExtra("movieImage", itemDataSqlite.getUrl());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });
        ItemDataSqlite itemDataSqlite = items.get(position);
        id.setText(itemDataSqlite.getId());
        menuid.setText(itemDataSqlite.getMenuid());
        //placeid.setText(itemDataSqlite.getPlaceid());
        nama.setText(itemDataSqlite.getNama());
        total.setText(formatRupiah.format(Integer.parseInt(itemDataSqlite.getTotal())));
        harga.setText(formatRupiah.format(Integer.parseInt(itemDataSqlite.getHarga())));
        jumlah.setText(itemDataSqlite.getJumlah());
        PicassoClient.loadImage(activity, items.get(position).getUrl(), movieImage);
        return convertView;
    }
}

