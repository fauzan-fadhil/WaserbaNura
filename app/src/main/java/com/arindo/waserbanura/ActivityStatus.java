package com.arindo.waserbanura;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arindo.waserbanura.Adapter.ListAdapterStatus;
import com.arindo.waserbanura.Model.ItemStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityStatus extends AppCompatActivity {
    public List<ItemStatus> itemStatuses;
    private ListAdapterStatus adapter;
    JSONObject playerObject;
    ItemStatus itemStatus;
    SharedPreferences preferences;
    JSONArray playerArray;
    ListView listVie;
    JSONObject obj;
    private ArrayList<HashMap<String, String>> stokList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_status);
        listVie = findViewById(R.id.listv);
        preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        itemStatuses = new ArrayList<>();
        stokList = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityStatus.this.finish();
            }
        });
        getSupportActionBar().setTitle("Approved");
        loadApproved();
    }

    private void loadApproved() {
        final String JSON_URL = "http://1.1.50.202:3000/purchase/status/" + preferences.getString("placeid", null);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            obj = new JSONObject(response);
                            playerArray = obj.getJSONArray("values");
                            for (int i = 0; i < playerArray.length(); i++) {
                                playerObject = playerArray.getJSONObject(i);
                                String status = playerObject.getString("status");
                                Log.e("Jsonapproved", status);
                                if (status.equals("Approved")) {
                                    itemStatus = new ItemStatus(
                                            playerObject.getString("status"),
                                            playerObject.getString("nominal"),
                                            playerObject.getString("tanggal"));
                                    itemStatuses.add(itemStatus);
                                } else if (status.equals("Proses")) {
                                    itemStatus = new ItemStatus(
                                            playerObject.getString("status"),
                                            playerObject.getString("nominal"),
                                            playerObject.getString("tanggal"));
                                    itemStatuses.add(itemStatus);
                                }
                            }
                            adapter = new ListAdapterStatus(itemStatuses, getApplicationContext());
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
}
