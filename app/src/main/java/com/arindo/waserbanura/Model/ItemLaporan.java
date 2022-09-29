package com.arindo.waserbanura.Model;

import java.io.Serializable;

public class ItemLaporan implements Serializable {

    int nomer;
    String trx_code, trx_datetime, total_price, Jumlah;
    public ItemLaporan(int nomer,  String trx_code, String trx_datetime, String total_price, String Jumlah)
    {
        this.nomer = nomer;
        this.trx_code = trx_code;
        this.trx_datetime = trx_datetime;
        this.total_price = total_price;
        this.Jumlah = Jumlah;
    }
    public int getNomer() {
        return  nomer;
    }
    public String getTrx_code() {
        return trx_code;
    }
    public String getTrx_datetime() {
        return trx_datetime;
    }
    public String getTotal_price(){
        return total_price;
    }
    public String getJumlah () {
        return Jumlah;
    }

}

