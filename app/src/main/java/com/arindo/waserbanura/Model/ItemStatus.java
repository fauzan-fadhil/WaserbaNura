package com.arindo.waserbanura.Model;

import java.io.Serializable;

public class ItemStatus implements Serializable {

    String status, nominal, tanggal;

    public ItemStatus( String status, String nominal, String tanggal ) {

        this.status = status;
        this.nominal = nominal;
        this.tanggal = tanggal;

    }
    public String getStatus() {
        return status;
    }
    public String getNominal() {
        return nominal;
    }
    public String getTanggal(){
        return tanggal;
    }
}
