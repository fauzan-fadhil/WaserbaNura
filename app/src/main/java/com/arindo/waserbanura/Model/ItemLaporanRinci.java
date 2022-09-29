package com.arindo.waserbanura.Model;

import java.io.Serializable;

public class ItemLaporanRinci implements Serializable {
    int nomer;
    String menu, menu_id, qty, txt_price, places_id, trx_total;
    public ItemLaporanRinci(int nomer, String menu, String menu_id, String qty, String txt_price, String places_id, String trx_total)
    {
        this.nomer = nomer;
        this.menu = menu;
        this.menu_id = menu_id;
        this.qty = qty;
        this.txt_price = txt_price;
        this.places_id = places_id;
        this.trx_total = trx_total;
    }

    public int getNomer() {
        return nomer;
    }
    public String getMenu(){
        return menu;
    }
    public String getMenu_id() {
        return menu_id;
    }
    public String getQty() {
        return qty;
    }
    public String getTxt_price(){
        return txt_price;
    }
    public String getPlaces_id() {
        return places_id;
    }
    public String getTrx_total() {
        return trx_total;
    }
}
