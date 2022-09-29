package com.arindo.waserbanura.Fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.arindo.waserbanura.ActivityGantiPassword;
import com.arindo.waserbanura.ActivityTentang;
import com.arindo.waserbanura.LoginActivity;
import com.arindo.waserbanura.R;


public class Fragment_Akun extends Fragment {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public Fragment_Akun() {
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_akun, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView nama = (TextView) view.findViewById(R.id.nama);
        TextView address = (TextView) view.findViewById(R.id.address);
        TextView negara = (TextView) view.findViewById(R.id.negara);
        TextView email = (TextView) view.findViewById(R.id.email);
        TextView code = (TextView) view.findViewById(R.id.code);
        TextView phone = (TextView) view.findViewById(R.id.phone);
        ImageView logout = (ImageView) view.findViewById(R.id.btn_logout);
        ImageView change = (ImageView)view.findViewById(R.id.btn_changeP);
        ImageView about = (ImageView) view.findViewById(R.id.about);
        //Toolbar toolbar = view.findViewById(R.id.toolbar);
        // toolbar.setTitle("ACCOUNT");
        preferences = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        nama.setText(preferences.getString("fullname", null));
        address.setText(preferences.getString("alamat", null));
        negara.setText(preferences.getString("negara", null));
        email.setText(preferences.getString("email", null));
        code.setText(preferences.getString("kodetelepon", null));
        phone.setText(preferences.getString("nomertelepon", null));

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ActivityGantiPassword.class));
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ActivityTentang.class));
            }
        });
    }

    private void showDialog(){
        new AlertDialog.Builder(getContext())
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        preferences = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
                        editor = preferences.edit();
                        editor.clear();
                        editor.apply();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                }).create().show();
    }
}
