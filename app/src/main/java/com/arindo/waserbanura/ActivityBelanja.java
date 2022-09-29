package com.arindo.waserbanura;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arindo.waserbanura.Adapter.ListAdapterBelanja;
import com.arindo.waserbanura.Constanta.Constanta;
import com.arindo.waserbanura.Model.ItemBarang;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityBelanja extends AppCompatActivity {
    private TextView place;
    private ListView listVie;
    private ListAdapterBelanja adapter;
    private List<ItemBarang> itemBarangs;
    public static String kdhost;
    private SharedPreferences preferences;
    private EditText etnamalengkap;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> stokList;
    JSONArray playerArray;
    JSONObject playerObject;
    JSONObject obj;
    TextView textpopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_belanja);
        listVie =  findViewById(R.id.listviewbelanja);
        textpopup = findViewById(R.id.textpopup);
        textpopup.setVisibility(View.GONE);
        etnamalengkap = findViewById(R.id.etnamalengkap);
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);
        preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        stokList = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityBelanja.this.finish();
            }
        });

        getSupportActionBar().setTitle("Daftar Barang");
        getSupportActionBar().setSubtitle("daftar belanja barang");
        itemBarangs = new ArrayList<>();

        etnamalengkap.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }
            public void afterTextChanged(Editable arg0) {
                adapter.getFilter().filter(arg0);
                // Log.e("adapternyanih", String.valueOf(adapter));

            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to make your refresh action
                // CallYourRefreshingMethod();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            loadPlayer();
                        }
                    }
                }, 3000);
            }
        });
        loadPlayer();
    }

    private void loadPlayer() {
        final String ardkdhost = Constanta.Setting.host_list_item;
        kdhost = ardkdhost;
        final String kode = preferences.getString("placeid", null);
        final String JSON_URL = kdhost + kode;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            obj = new JSONObject(response);
                            playerArray = obj.getJSONArray("values");
                            for (int i = 0; i < playerArray.length(); i++) {
                                playerObject = playerArray.getJSONObject(i);
                                ItemBarang itemBarang = new ItemBarang(playerObject.getInt("id"),
                                        playerObject.getString("menu_id"),
                                        playerObject.getString("group_id"),
                                        playerObject.getString("places_id"),
                                        playerObject.getString("menu"),
                                        playerObject.getString("menu_desc"),
                                        playerObject.getString("price"),
                                        playerObject.getString("unit"),
                                        playerObject.getString("favorite"),
                                        playerObject.getString("file_name"),
                                        playerObject.getString("status"));
                                itemBarangs.add(itemBarang);
                                Log.e("file name", playerObject.getString("file_name"));
                            }
                            adapter = new ListAdapterBelanja(itemBarangs, getApplicationContext() );
                            listVie.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}

