package com.arindo.waserbanura;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arindo.waserbanura.Constanta.Constanta;
import com.arindo.waserbanura.Constanta.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ActivityGantiPassword extends AppCompatActivity {

    private Button Ganti;
    private SharedPreferences preferences;
    public ProgressDialog p;
    private EditText passwordLama, passwordBaru, KonfirmasiPassword;
    JSONObject jsonObject;
    JSONObject postDataParams;
    boolean pwd_status = true;
    String Username, Password, PassBaru, Konfirmasipass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganti_password);
        preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportActionBar().setTitle("Ganti Kata Sandi");
        passwordLama = (EditText) findViewById(R.id.passwordLama);
        passwordBaru = (EditText) findViewById(R.id.passwordBaru);
        KonfirmasiPassword = (EditText) findViewById(R.id.KonfirmasiPassword);
        Username = preferences.getString("data3", null);
        Password = passwordLama.getText().toString();
        PassBaru = passwordBaru.getText().toString();
        Konfirmasipass = KonfirmasiPassword.getText().toString();
        Ganti = (Button) findViewById(R.id.Ganti);
        Ganti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Password = passwordLama.getText().toString();
                PassBaru = passwordBaru.getText().toString();
                Konfirmasipass = KonfirmasiPassword.getText().toString();
                boolean valid = true;
                if (TextUtils.isEmpty(Password)) {
                    passwordLama.setError("Harus Diisi... Tidak Boleh Kosong!!!");
                    passwordLama.requestFocus();
                } else if (TextUtils.isEmpty(PassBaru)) {
                    passwordBaru.setError("Harus Diisi...Tidak Boleh Kosong!!!");
                    passwordBaru.requestFocus();
                } else if (TextUtils.isEmpty(Konfirmasipass)) {
                    KonfirmasiPassword.setError("Harus Diisi... Tidak Boleh Kosong!!!");
                    KonfirmasiPassword.requestFocus();
                } else {
                    new RequestAsync().execute();
                }
            }
        });
    }

    public class RequestAsync extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(ActivityGantiPassword.this);
            p.setMessage("\tLoading...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }
        String JSON_URL = Constanta.Setting.Update;
        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected String doInBackground(String... arg0) {
            try {
                postDataParams = new JSONObject();
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
                jsonObject = new JSONObject(result);
                if (jsonObject.getString("status").equals("200")) {
                    Log.e("cek", Password);
                    String pwdbaru = passwordBaru.getText().toString();
                    if (pwdbaru.equals(Password)) {
                        Toast.makeText(ActivityGantiPassword.this, "Password Sudah Digunakan", Toast.LENGTH_SHORT).show();
                    } else if (!KonfirmasiPassword.getText().toString().equals(pwdbaru)){
                        Toast.makeText(ActivityGantiPassword.this, "Konfirmasi Password Berbeda Dengan Password Baru", Toast.LENGTH_SHORT).show();
                    } else {
                        update();
                    }
                    p.hide();
                } else {
                    jsonObject.getString("status").equals("909");
                    Toast.makeText(ActivityGantiPassword.this, "Password Lama Salah", Toast.LENGTH_SHORT).show();
                    p.hide();
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
    private void update(){
        String url_json = Constanta.Setting.UpdateData + preferences.getString("data3", null);
        final RequestQueue requestQueue = Volley.newRequestQueue(ActivityGantiPassword.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_json,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ActivityGantiPassword.this, "Password Berhasil Di Update", Toast.LENGTH_LONG).show();
                        requestQueue.stop();
                        ActivityGantiPassword.this.finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ActivityGantiPassword.this, "Error ...", Toast.LENGTH_LONG).show();
                error.printStackTrace();
                Log.e("pos trx Error", error.toString());
                requestQueue.stop();
            }
        }) {
            final String passwordd = passwordBaru.getText().toString();
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("adminpos_pwd", passwordd);
                Log.e("data pos trx Error", params.toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}