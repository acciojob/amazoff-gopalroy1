package com.driver;


public class Order {

    private String id;
    private int deliveryTime;

    public Order(String id, String deliveryTime) {
        // The deliveryTime has to converted from string to int and then stored in the attribute
        //deliveryTime  = HH*60 + MM
        this.deliveryTime = TimeUtil.convertTime(deliveryTime);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getDeliveryTime() {return deliveryTime;}

    public String getDeliveryTimeAsString() {
        return TimeUtil.convertTime(this.deliveryTime);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = TimeUtil.convertTime(deliveryTime);
    }
}