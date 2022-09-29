package com.arindo.waserbanura.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arindo.waserbanura.Adapter.ListAdapterStatus;
import com.arindo.waserbanura.MainActivity;
import com.arindo.waserbanura.Model.ItemStatus;
import com.arindo.waserbanura.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class Fragment_History extends Fragment {

    Toolbar toolbar;
    public List<ItemStatus> itemStatuses;
    private ListAdapterStatus adapter;
    private SharedPreferences preferences;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    public static ItemStatus itemStatus;
    JSONObject playerObject;
    JSONArray playerArray;
    private ImageView noNotesView;

    public Fragment_History() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Riwayat Transaksi");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lv = (ListView) view.findViewById(R.id.list);
        itemStatuses = new ArrayList<>();
        preferences = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);

        loadApproved();
    }

    private void loadApproved() {
        final String JSON_URL = "http://1.1.50.202:3000/purchase/status/" +  preferences.getString("placeid", null);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            playerArray = obj.getJSONArray("values");
                            for (int i = 0; i < playerArray.length(); i++) {
                                playerObject = playerArray.getJSONObject(i);
                                String status = playerObject.getString("status");
                                if (status.equals("Approved")) {
                                    itemStatus = new ItemStatus(
                                            playerObject.getString("status"),
                                            playerObject.getString("nominal"),
                                            playerObject.getString("tanggal"));
                                    itemStatuses.add(itemStatus);
                                } else if (status.equals("Rejected")) {
                                    itemStatus = new ItemStatus(
                                            playerObject.getString("status"),
                                            playerObject.getString("nominal"),
                                            playerObject.getString("tanggal"));
                                    itemStatuses.add(itemStatus);
                                }
                            }
                            adapter = new ListAdapterStatus(itemStatuses, getContext());
                            lv.setAdapter(adapter);
                           // toggleEmptyNotes();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


}



