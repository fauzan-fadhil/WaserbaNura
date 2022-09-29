package com.arindo.waserbanura.Model;

public class ItemPengajuan {


    String menu, menuId, placesId, menudesc;
    public ItemPengajuan(String menu, String menuId, String placesId, String menudesc) {
        this.menu = menu;
        this.menuId = menuId;
        this.placesId = placesId;
        this.menudesc = menudesc;
    }
    public String getMenu() {
        return menu;
    }
    public String getMenuId() {
        return menuId;
    }
    public String getPlacesId(){
        return placesId;
    }
    public String getMenudesc(){
        return menudesc;
    }
}