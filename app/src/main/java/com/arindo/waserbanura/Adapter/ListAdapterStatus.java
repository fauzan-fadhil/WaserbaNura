package com.arindo.waserbanura.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.RequiresApi;

import com.arindo.waserbanura.Model.ItemStatus;
import com.arindo.waserbanura.R;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListAdapterStatus extends ArrayAdapter<ItemStatus> implements Filterable {

    private List<ItemStatus>itemApproveds;
    private List<ItemStatus>itemsModelListFiltered;
    private Context context;
    private NumberFormat formatRupiah;
    TextView txtapproved;
    SimpleDateFormat input, tanggal, waktu;
    public static ImageView iconapproved, iconrejected, iconproses;

    @SuppressLint("SimpleDateFormat")
    public ListAdapterStatus(List<ItemStatus> itemApproveds, Context context) {
        super(context, R.layout.activity_status, itemApproveds);
        this.itemApproveds = itemApproveds;
        this.itemsModelListFiltered = itemApproveds;
        this.context = context;

        input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        tanggal = new SimpleDateFormat("dd-MM-yyyy");
        waktu = new SimpleDateFormat("HH:mm");

    }
    @Override
    public int getCount() {
        return itemsModelListFiltered.size();
    }

    @Override
    public ItemStatus getItem(int position) {
        return itemsModelListFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint({"ViewHolder", "InflateParams"}) final View listViewItem = inflater.inflate(R.layout.activity_status, null, true);
        iconapproved = listViewItem.findViewById(R.id.iconapprove);
        iconrejected = listViewItem.findViewById(R.id.iconreject);
        iconproses = listViewItem.findViewById(R.id.iconproses);
        final TextView txtwaktu = listViewItem.findViewById(R.id.waktu);
        txtapproved = listViewItem.findViewById(R.id.totalharga);
        final TextView txttanggal = listViewItem.findViewById(R.id.tanggal);
        final TextView txtket = listViewItem.findViewById(R.id.keterngan);
        ItemStatus playerItem = itemsModelListFiltered.get(position);
        Locale localeID = new Locale("in", "ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        txtapproved.setText(formatRupiah.format(Integer.parseInt(playerItem.getNominal())));
        txtket.setText(playerItem.getStatus());
        if (txtket.getText().toString().equals("Proses")) {
            txtapproved.setTextColor(Color.RED);
            iconapproved.setVisibility(View.GONE);
            iconrejected.setVisibility(View.GONE);
            iconproses.setVisibility(View.VISIBLE);
        } else if (txtket.getText().toString().equals("Rejected")) {
            txtapproved.setTextColor(Color.RED);
            iconapproved.setVisibility(View.GONE);
            iconrejected.setVisibility(View.VISIBLE);
            iconproses.setVisibility(View.GONE);
        } else if (txtket.getText().equals("Approved")) {
            iconapproved.setVisibility(View.VISIBLE);
            iconrejected.setVisibility(View.GONE);
            iconproses.setVisibility(View.GONE);
        }
        txtwaktu.setText("");
        final String parse = playerItem.getTanggal();
        Date date = null;
        try {
            date = input.parse(parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formatted = tanggal.format(date);
        String waktuuu = waktu.format(date);
        txttanggal.setText(formatted);
        txtwaktu.setText(waktuuu);
        return listViewItem;
    }
}
