package com.arindo.waserbanura.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.arindo.waserbanura.ActivityKetBarang;
import com.arindo.waserbanura.ActivityOrder;
import com.arindo.waserbanura.MainActivity;
import com.arindo.waserbanura.Model.ItemBarang;
import com.arindo.waserbanura.R;
import com.arindo.waserbanura.Sqlite.DBHelper;
import com.bumptech.glide.Glide;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListAdapterBelanja extends ArrayAdapter<ItemBarang>  implements Filterable {
    private List<ItemBarang> itemBarangs;
    private List<ItemBarang> itemsModelListFiltered;
    String imageUrl = "http://backend.waserbanura.com/apipaycon/3111/asset/food/favorite/";
    private Context context;
    private String TAG = MainActivity.class.getSimpleName();
    DBHelper SQLite = new DBHelper(context);
    public ListAdapterBelanja(List<ItemBarang> itemBarangs, Context context) {
        super(context, R.layout.list_belanja, itemBarangs);
        this.itemBarangs = itemBarangs;
        this.itemsModelListFiltered = itemBarangs;
        this.context = context;
        SQLite = new DBHelper(getContext().getApplicationContext());
    }

    @Override
    public int getCount() {
        return itemsModelListFiltered.size();
    }

    @Override
    public ItemBarang getItem(int position) {
        return itemsModelListFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View listViewItem = inflater.inflate(R.layout.list_belanja, null, true);
        final TextView textmenu = listViewItem.findViewById(R.id.menu);
        final TextView textprice = listViewItem.findViewById(R.id.price);
        final TextView textstok = listViewItem.findViewById(R.id.stok);
        final Toolbar toolbar = listViewItem.findViewById(R.id.toolbar);
        Button button = listViewItem.findViewById(R.id.pesan);
        try {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ActivityOrder.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ItemBarang itemBarang = itemsModelListFiltered.get(position);
                    intent.putExtra("menu_id", (itemBarang.getMenu_id()));
                    intent.putExtra("price", (itemBarang.getPrice()));
                    intent.putExtra("menu", (itemBarang.getMenu()));
                    intent.putExtra("file_name", (imageUrl + itemBarang.getFile_name()));
                    intent.putExtra("Place_id", (itemBarang.getPlaces_id()));
                    getContext().startActivity(intent);
                   // ((Activity)getContext()).finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        final ImageView imgVIew = listViewItem.findViewById(R.id.image_produk);
        imgVIew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(context.getApplicationContext(), ActivityKetBarang.class);
                ItemBarang playerItem = itemsModelListFiltered.get(position);
                a.putExtra("menu_desc", (playerItem.getMenu_desc()));
                a.putExtra("price", (playerItem.getPrice()));
                a.putExtra("menu", (playerItem.getMenu()));
                a.putExtra("file_name", (imageUrl + playerItem.getFile_name()));
                a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(a);
            }
        });

        ItemBarang playerItem = itemsModelListFiltered.get(position);
        textmenu.setText(playerItem.getMenu());
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        textprice.setText(formatRupiah.format(Integer.parseInt(playerItem.getPrice())));
        Glide.with(context)
                .load(imageUrl + playerItem.getFile_name())
                .into(imgVIew);
        return listViewItem;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.e(TAG, "Response from url: " + constraint);
                FilterResults filterResults = new FilterResults();
                if(constraint == null || constraint.length() == 0){
                    //   Log.e(TAG, "6 " +  filterResults);
                    filterResults.count = itemBarangs.size();
                    filterResults.values = itemBarangs;
                }else{
                    List<ItemBarang> resultsModel = new ArrayList<>();
                    //  Log.e(TAG, "7 " + resultsModel );
                    String searchStr = constraint.toString().toUpperCase();
                    // Log.e(TAG, "3 " + searchStr);
                    for(ItemBarang itemBarang:itemBarangs){
                        //  Log.e(TAG, "4 " + itemBarang.getMenu());
                        if(itemBarang.getMenu().contains(searchStr)){
                            Log.e(TAG, "8 " + searchStr);
                            resultsModel.add(itemBarang);
                        }
                        filterResults.count = resultsModel.size();
                        //  Log.e(TAG, "1 " +  filterResults.count);
                        filterResults.values = resultsModel;
                        Log.e(TAG, "2 " +  filterResults.values);
                    }
                }
                //  Log.e(TAG, "5 " + filterResults.count);
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                itemsModelListFiltered = (List<ItemBarang>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}

