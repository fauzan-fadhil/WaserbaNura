package com.arindo.waserbanura.Model;

public class ItemDataSqlite {
    private String id, menuid, placeid, nama, total, harga, jumlah, ket, url;

    public ItemDataSqlite() {
    }

    public ItemDataSqlite(String id, String menuid, String placeid, String nama, String total, String harga, String jumlah, String ket, String url) {
        this.id = id;
        this.menuid = menuid;
        this.placeid = placeid;
        this.nama = nama;
        this.total = total;
        this.harga = harga;
        this.jumlah = jumlah;
        this.ket = ket;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenuid() {
        return menuid;
    }

    public void setMenuid(String menuid) {
        this.menuid = menuid;
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getHarga(){
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}