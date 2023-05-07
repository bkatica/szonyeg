package com.example.szonyeg;

public class ShopingItem {
    private String id;
    private String nev;
    private String info;
    private String ar;
    private  int imageResource;
    private int cartedCount;

    public ShopingItem() {
    }

    public ShopingItem(String nev, String info, String ar, int imageResource, int cartedCount) {
        this.nev = nev;
        this.info = info;
        this.ar = ar;
        this.imageResource = imageResource;
        this.cartedCount = cartedCount;
    }

    public String getNev() {
        return nev;
    }

    public String getInfo() {
        return info;
    }

    public String getAr() {
        return ar;
    }

    public int getImageResource(){
        return imageResource;
    }
    public int getCartedCount(){
        return cartedCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
