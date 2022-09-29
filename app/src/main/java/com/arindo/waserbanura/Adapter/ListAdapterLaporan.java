package com.arindo.waserbanura.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arindo.waserbanura.Model.ItemLaporan;
import com.arindo.waserbanura.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


public class ListAdapterLaporan extends ArrayAdapter<ItemLaporan> {

    private List<ItemLaporan> itemLaporans;
    private Context context;
    private NumberFormat formatRupiah;

    public ListAdapterLaporan(List<ItemLaporan> itemLaporans, Context context) {
        super(context, R.layout.list_laporan, itemLaporans);
        this.itemLaporans = itemLaporans;
        this.context = context;
        Locale localeID = new Locale("in", "ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View listViewItem = inflater.inflate(R.layout.list_laporan, null, true);
        final TextView txtkodetrx = listViewItem.findViewById(R.id.kodetrxlaporan);
        final TextView txtnomer = listViewItem.findViewById(R.id.nomerlaporan);
        final TextView texttanggal = listViewItem.findViewById(R.id.tanggallaporan);
        final TextView textpricr = listViewItem.findViewById(R.id.totallaporan);
        final TextView textjumlah = listViewItem.findViewById(R.id.jumlahlaporan);
        ItemLaporan laporanItem = itemLaporans.get(position);
        texttanggal.setText(laporanItem.getTrx_datetime());
        textjumlah.setText(laporanItem.getJumlah());
        txtkodetrx.setText(laporanItem.getTrx_code());
        textpricr.setText(formatRupiah.format(Integer.parseInt(laporanItem.getTotal_price())));
        txtnomer.setText(String.valueOf(laporanItem.getNomer() + 1));
        return listViewItem;
    }
}