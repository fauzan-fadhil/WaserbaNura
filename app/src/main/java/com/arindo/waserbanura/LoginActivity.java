package com.arindo.waserbanura;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arindo.waserbanura.Constanta.Constanta;
import com.arindo.waserbanura.Constanta.HttpHandler;
import com.arindo.waserbanura.Model.ItemDataSqlite;
import com.arindo.waserbanura.Sqlite.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.arindo.waserbanura.ActivityKeranjang.TAG_ID;
import static com.arindo.waserbanura.ActivityKeranjang.placeidstr;
import static com.arindo.waserbanura.Fragment.Fragment_Beranda.notificationBadge;
import static com.arindo.waserbanura.MainActivity.mCartItemCount;
import static com.arindo.waserbanura.MainActivity.setupBadge;
import static com.arindo.waserbanura.MainActivity.textCartItemCount;

public class LoginActivity extends AppCompatActivity {
    public EditText edtusername, edtpassword;
    public Button ButtonLogin;
    public ProgressBar progressBar;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefsetor;
    private SharedPreferences.Editor editsetor;
    String username;
    DBHelper SQLite = new DBHelper(this);
    List<ItemDataSqlite> itemList = new ArrayList<ItemDataSqlite>();
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtusername = findViewById(R.id.edtusername);
        edtpassword = findViewById(R.id.edtpassword);
        ButtonLogin = findViewById(R.id.ButtonLogin);
        progressBar = findViewById(R.id.progressBar);

        SQLite = new DBHelper(getApplicationContext());
        progressBar.setVisibility(View.GONE);
        preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        prefsetor = getSharedPreferences("SharedPrefsetor", Context.MODE_PRIVATE);
        username = preferences.getString("placeid", "");

        if (!username.equals("")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

        editor = preferences.edit();
        editsetor = prefsetor.edit();

        ButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Logindata();
              //  new RequestAsync().execute();
            }
        });
    }

    @Override
    public void onBackPressed() {
        LoginActivity.this.finish();
    }

    private void Logindata(){
        String url_json = Constanta.Setting.Login;
        final RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_json,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                           JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("200")) {
                                String placesid = jsonObject.getString("places_id");
                                String nama = jsonObject.getString("full_name");
                                String adminpos = jsonObject.getString("adminpos_id");
                                String alamat = jsonObject.getString("address");
                                String negara = jsonObject.getString("country");
                                String email = jsonObject.getString("email");
                                String kodetelfn = jsonObject.getString("phone_code");
                                String number = jsonObject.getString("phone_number");
                                long login = System.currentTimeMillis();
                                long setor = System.currentTimeMillis();
                                editor.putString("placeid", placesid);
                                editor.putString("fullname", nama);
                                editor.putString("adminpos", adminpos);
                                editor.putString("alamat", alamat);
                                editor.putString("negara", negara);
                                editor.putString("email", email);
                                editor.putString("kodetelepon", kodetelfn);
                                editor.putString("nomertelepon", number);
                                editor.putLong("waktu", login);
                                editsetor.putLong("setor", setor);
                                editor.apply();
                                editsetor.apply();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else {
                                jsonObject.getString("status").equals("909");
                                Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                        requestQueue.stop();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Toast.makeText(LoginActivity.this,
                            "Oops. request ke server gagal coba lagi",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(LoginActivity.this,
                            String.valueOf(error),
                            Toast.LENGTH_LONG).show();
                }
                Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG);
                Log.e("pos trx Error", error.toString());
                progressBar.setVisibility(View.GONE);
                requestQueue.stop();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("adminpos_id", edtusername.getText().toString());
                params.put("adminpos_pwd", edtpassword.getText().toString());
                // Log.e("Json", params.toString());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
}

/*
    //@SuppressLint("StaticFieldLeak")
    public class RequestAsync extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        final String Username = edtusername.getText().toString();
        final String Password = edtpassword.getText().toString();
        String JSON_URL = Constanta.Setting.Login;
        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected String doInBackground(String... arg0) {
            try {
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("adminpos_id", Username);
                postDataParams.put("adminpos_pwd", Password);
                return HttpHandler.sendPost(JSON_URL, postDataParams);

            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // p.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("status").equals("200")) {
                    String placesid = jsonObject.getString("places_id");
                    String nama = jsonObject.getString("full_name");
                    String adminpos = jsonObject.getString("adminpos_id");
                    String alamat = jsonObject.getString("address");
                    String negara = jsonObject.getString("country");
                    String email = jsonObject.getString("email");
                    String kodetelfn = jsonObject.getString("phone_code");
                    String number = jsonObject.getString("phone_number");
                    long login = System.currentTimeMillis();
                    long setor = System.currentTimeMillis();
                    editor.putString("placeid", placesid);
                    editor.putString("fullname", nama);
                    editor.putString("adminpos", adminpos);
                    editor.putString("alamat", alamat);
                    editor.putString("negara", negara);
                    editor.putString("email", email);
                    editor.putString("kodetelepon", kodetelfn);
                    editor.putString("nomertelepon", number);
                    editor.putLong("waktu", login);
                    editsetor.putLong("setor", setor);
                    editor.apply();
                    editsetor.apply();
//                  p.show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    progressBar.setVisibility(View.GONE);
                } else {
                    jsonObject.getString("status").equals("909");
                    Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

    }
 */

