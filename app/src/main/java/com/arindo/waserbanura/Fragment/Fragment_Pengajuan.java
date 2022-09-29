package com.arindo.waserbanura.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arindo.waserbanura.Constanta.Constanta;
import com.arindo.waserbanura.Model.ItemPengajuan;
import com.arindo.waserbanura.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment_Pengajuan extends Fragment {

    //String URL = "http://1.1.50.203:3000/pengajuan/NUA00012";
    String URL_JSON = Constanta.Setting.host_pengajuan;
    private List<ItemPengajuan> itemPengajuans;
    private Spinner spinner;
    ArrayAdapter<String> adapter;
    int indexmenu;
    Toolbar toolbar;
    TextView desc;
    EditText Jumlahbrgpngj, ketbrgpngj;
    Button btnKirim;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public Fragment_Pengajuan() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pengajuan, container, false);
        spinner = view.findViewById(R.id.spinerp);
        desc = view.findViewById(R.id.desc);
        toolbar = view.findViewById(R.id.tolbar);
        toolbar.setTitle("PENGAJUAN");
        ketbrgpngj = view.findViewById(R.id.ketbrgpngj);
        Jumlahbrgpngj = view.findViewById(R.id.Jumlahbrgpngj);
        preferences = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        itemPengajuans = new ArrayList<>();
        indexmenu = 0;
        btnKirim = view.findViewById(R.id.btn_kirim);

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Jumlahbrgpngj.getText().toString().equals(" ") || ketbrgpngj.getText().toString().equals(" ")) {

                    Toast.makeText(getContext(), "Tidak boleh ada kolom yang kosong, Isi kolom terlebih dahulu", Toast.LENGTH_SHORT).show();
                } else {
                    final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_JSON,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                                    showdialog();
                                    requestQueue.stop();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Error ...", Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                            requestQueue.stop();
                        }
                    }) {
                        final String Plcd = itemPengajuans.get(indexmenu).getPlacesId();
                        final String menuid = itemPengajuans.get(indexmenu).getMenuId();
                        final String qty = Jumlahbrgpngj.getText().toString();
                        final String note = ketbrgpngj.getText().toString();

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("places_id", Plcd);
                            params.put("menu_id", menuid);
                            params.put("qty", qty);
                            params.put("note", note);
                            return params;
                        }
                    };
                    requestQueue.add(stringRequest);
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                indexmenu = position;
                desc.setText(itemPengajuans.get(indexmenu).getMenudesc());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        loadPlayer();
        return view;
    }

    private void loadPlayer() {
        final String ardkdhost = Constanta.Setting.host_tampil_pengajuan;
        final String kode = preferences.getString("placeid", null);
        final String URL = ardkdhost + kode;
        Log.e("JSONnya", URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray playerArray = obj.getJSONArray("values");
                            String[] list = new String[ playerArray.length()];
                            for (int i = 0; i < playerArray.length(); i++) {
                                JSONObject playerObject = playerArray.getJSONObject(i);
                                list[i]=playerObject.getString("menu");
                                ItemPengajuan itemPengajuan = new
                                        ItemPengajuan(playerObject.getString("menu"),
                                        playerObject.getString("menu_id"),
                                        playerObject.getString("places_id"),
                                        playerObject.getString("menu_desc"));
                                itemPengajuans.add(itemPengajuan);
                            }
                            adapter = new ArrayAdapter<>(getContext(),
                                    android.R.layout.simple_spinner_dropdown_item,list);
                            //Memasukan Adapter pada Spinner
                            spinner.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void showdialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext()
        );
        alertDialogBuilder.setTitle("Terkirim");
        alertDialogBuilder
                .setMessage("Pengajuan Terkirim")
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Jumlahbrgpngj.setText("");
                        ketbrgpngj.setText("");
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
