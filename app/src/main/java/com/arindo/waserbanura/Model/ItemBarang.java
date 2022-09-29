package com.arindo.waserbanura.Model;

import java.io.Serializable;

public class ItemBarang implements Serializable {
    int id;
    String menu_id, group_id, places_id, menu, menu_desc, price, fee, unit, favorite, file_name, status;
    public ItemBarang(int id, String menu_id, String group_id, String places_id, String menu, String menu_desc, String price,
                       String unit, String favorite, String file_name, String status) {

        this.id = id;
        this.menu_id = menu_id;
        this.group_id = group_id;
        this.places_id = places_id;
        this.menu = menu;
        this.menu_desc = menu_desc;
        this.price = price;
        this.unit = unit;
        this.favorite = favorite;
        this.file_name = file_name;
        this.status = status;
    }

    public int getId() {
        return id;
    }
    public String getMenu_id() {
        return menu_id;
    }
    public String getGroup_id() {
        return group_id;
    }
    public String getPlaces_id(){
        return places_id;
    }
    public String getMenu() {
        return menu;
    }
    public String getMenu_desc(){
        return menu_desc;
    }
    public String getPrice () {
        return price;
    }
    public String getUnit(){
        return unit;
    }
    public String getFavorite(){
        return favorite;
    }
    public String getFile_name(){
        return file_name;
    }
    public String getStatus(){
        return status;
    }
}


