package com.arindo.waserbanura.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arindo.waserbanura.Model.ItemLaporanRinci;
import com.arindo.waserbanura.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ListAdapterLaporanRinci extends ArrayAdapter<ItemLaporanRinci> {

    private List<ItemLaporanRinci> itemRincis;
    private Context context;
    private NumberFormat formatRupiah;

    public ListAdapterLaporanRinci(List<ItemLaporanRinci> itemRincis, Context context) {
        super(context, R.layout.list_laporan_rinci, itemRincis);
        this.itemRincis = itemRincis;
        this.context = context;
        Locale localeID = new Locale("in", "ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View listViewItem = inflater.inflate(R.layout.list_laporan_rinci, null, true);
        final TextView menu = listViewItem.findViewById(R.id.namabrglaporanrinci);
        final TextView menuid = listViewItem.findViewById(R.id.menuidlaporanrinci);
        final TextView qty = listViewItem.findViewById(R.id.qtylaporanrinci);
        final TextView trx_price = listViewItem.findViewById(R.id.hargalaporanrinci);
        final TextView totalhsl = listViewItem.findViewById(R.id.totallaporanrinci);
        final TextView nolaporanrinci = listViewItem.findViewById(R.id.nolaporanrinci);
        ItemLaporanRinci rinciItem = itemRincis.get(position);
        nolaporanrinci.setText(String.valueOf(rinciItem.getNomer()));
        menu.setText(rinciItem.getMenu());
        menuid.setText(rinciItem.getMenu_id());
        qty.setText(rinciItem.getQty());
        trx_price.setText(formatRupiah.format(Integer.parseInt(rinciItem.getTxt_price())));
        totalhsl.setText(formatRupiah.format(Integer.parseInt(rinciItem.getTrx_total())));
        return listViewItem;
    }
}