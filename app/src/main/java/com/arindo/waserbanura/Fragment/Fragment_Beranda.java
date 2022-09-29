package com.arindo.waserbanura.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.arindo.waserbanura.ActivityBelanja;
import com.arindo.waserbanura.ActivityLaporan;
import com.arindo.waserbanura.ActivitySetor;
import com.arindo.waserbanura.Constanta.Constanta;
import com.arindo.waserbanura.Constanta.HttpHandler;
import com.arindo.waserbanura.Constanta.SliderImageAdapter;
import com.arindo.waserbanura.MainActivity;
import com.arindo.waserbanura.R;
import com.arindo.waserbanura.Sqlite.DBHelper;
import com.nex3z.notificationbadge.NotificationBadge;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_Beranda extends Fragment {
    SliderView sliderMyshop;
    private ImageView imageViewactivity;
    ArrayList<HashMap<String, String>> list_data;
    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView listvieberanda;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static String kdhost;
    private TextView fullname, alamatberanda;
    private ArrayList<HashMap<String, String>> stokList;
    public static NotificationBadge notificationBadge;
    DBHelper SQLite = new DBHelper(getContext());
    public Fragment_Beranda() {
        stokList = new ArrayList<>();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beranda, container, false);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listvieberanda = (ListView) view.findViewById(R.id.listvieberanda);
        SQLite = new DBHelper(getActivity().getApplicationContext());
        Toolbar toolbar = view.findViewById(R.id.tolbar);
        notificationBadge = view.findViewById(R.id.badge);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        preferences = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
        alamatberanda = view.findViewById(R.id.alamatberanda);
        alamatberanda.setText(preferences.getString("alamat", null));
        new GetStock().execute();
        imageViewactivity = (ImageView) view.findViewById(R.id.btnbelanja);
        imageViewactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityBelanja.class);
                getActivity().startActivity(intent);
            }
        });
        imageViewactivity = (ImageView) view.findViewById(R.id.BtnPPOB);
        imageViewactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = null;
                try {
                    launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.onlinepayment");
                } catch (Exception ignored) {
                }
                if (launchIntent == null) {
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=com.onlinepayment")));
                } else {
                    startActivity(launchIntent);
                }
            }
        });

        imageViewactivity = (ImageView) view.findViewById(R.id.BtnLaporan);
        imageViewactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityLaporan.class);
                getActivity().startActivity(intent);
            }
        });
        imageViewactivity = (ImageView) view.findViewById(R.id.btnSetor);
        imageViewactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivitySetor.class);
                getActivity().startActivity(intent);
            }
        });

        sliderMyshop = view.findViewById(R.id.imageSlider);
        final SliderImageAdapter sliderImageAdapter = new SliderImageAdapter(getContext());
        sliderImageAdapter.setCount(4);
        sliderMyshop.setSliderAdapter(sliderImageAdapter);
        sliderMyshop.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderMyshop.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderMyshop.setIndicatorSelectedColor(Color.WHITE);
        sliderMyshop.setIndicatorUnselectedColor(Color.GRAY);
        sliderMyshop.startAutoCycle();
        sliderMyshop.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                sliderMyshop.setCurrentPagePosition(position);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private class GetStock extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Please check your internet connection");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            final String ardkdhost = Constanta.Setting.host_stok_agen;
            kdhost = ardkdhost;
            @SuppressLint("WrongThread")
            final String JSON_URL = kdhost + preferences.getString("placeid", null);
            HttpHandler sh = new HttpHandler();
            // membuat request dari url untuk mendapatkan response
            String jsonStr = sh.makeServiceCall(JSON_URL);
            Log.e("Response from url: ", jsonStr);
            editor.putString("responseurl", jsonStr);
            editor.apply();
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Merubah ke mode JSON Array node
                    JSONArray stock = jsonObj.getJSONArray("values");
                    // perulangan
                    for (int i = 0; i < stock.length(); i++) {
                        JSONObject c = stock.getJSONObject(i);
                        String id = c.getString("id");
                        String grup = c.getString("grup");
                        String menu = c.getString("menu");
                        String stockawal = c.getString("stockawal");
                        String stockkeluar = c.getString("stockkeluar");
                        String stockakhir = c.getString("stockakhir");
                        // peta hash tmp untuk stok tunggal
                        HashMap<String, String> stok = new HashMap<>();
                        // menambahkan setiap child node ke HashMap key => value
                        stok.put("id", id);
                        stok.put("grup", grup);
                        stok.put("menu", menu);
                        stok.put("stockawal", stockawal);
                        stok.put("stockkeluar", stockkeluar);
                        stok.put("stockakhir", stockakhir);
                        // menambahkan stok ke stok list
                        stokList.add(stok);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    equals(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                equals(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    getContext(), stokList,
                    R.layout.list_stok, new String[]{
                    "menu", "stockawal", "stockkeluar", "stockakhir"}, new int[]
                    {R.id.menu, R.id.stokawal, R.id.stokkeluar, R.id.stokakhir});
            listvieberanda.setAdapter(adapter);
        }
    }
}

