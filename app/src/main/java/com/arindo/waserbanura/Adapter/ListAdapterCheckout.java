package com.arindo.waserbanura.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arindo.waserbanura.R;
import com.arindo.waserbanura.Sqlite.DBHelper;
import com.arindo.waserbanura.Model.ItemDataSqlite;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ListAdapterCheckout extends BaseAdapter {
    private Activity activity;
    private DBHelper db;
    private LayoutInflater inflater;
    private List<ItemDataSqlite> items;
    private NumberFormat formatRupiah;


    public ListAdapterCheckout(Activity activity, List<ItemDataSqlite> items) {
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
            convertView = inflater.inflate(R.layout.list_checkout, null);
            db = new DBHelper(activity.getApplicationContext());
        }
        final TextView namabarg = (TextView) convertView.findViewById(R.id.namabarg);
        final TextView quantybarg = (TextView) convertView.findViewById(R.id.quantybarg);
        final TextView hargabarang = (TextView) convertView.findViewById(R.id.hargabarang);
        final TextView totalharga = (TextView) convertView.findViewById(R.id.totalharga);

        ItemDataSqlite itemDataSqlite = items.get(position);
        namabarg.setText(itemDataSqlite.getNama());
        quantybarg.setText(itemDataSqlite.getJumlah());
        hargabarang.setText(formatRupiah.format(Integer.parseInt(itemDataSqlite.getHarga())));
        totalharga.setText(formatRupiah.format(Integer.parseInt(itemDataSqlite.getTotal())));
        return convertView;
    }
}

