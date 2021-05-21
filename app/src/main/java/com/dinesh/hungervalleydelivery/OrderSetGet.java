package com.dinesh.hungervalleydelivery;

public class OrderSetGet {
    String pName,price,quantity,res;

    public OrderSetGet() {

    }

    public OrderSetGet(String pName, String price, String quantity, String res) {
        this.pName = pName;
        this.price = price;
        this.quantity = quantity;
        this.res = res;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }
}

