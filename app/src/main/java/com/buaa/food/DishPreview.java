package com.buaa.food;

public class DishPreview {
    int dishId;
    String dishName;
    String dishPrice;

    // TODO: implement dishImage
    // String dishImage;
    byte[] image;

    public DishPreview(int dishId, String dishName, String dishPrice, byte[] image) {
        this.dishId = dishId;
        this.dishName = dishName;
        this.dishPrice = dishPrice;
        this.image = image;
    }

    public DishPreview(String dishName, String dishPrice) {
        this.dishName = dishName;
        this.dishPrice = dishPrice;
    }

    public String getDishName() {
        return dishName;
    }

    public String getDishPrice() {
        return dishPrice;
    }

    public byte[] getImage() {
        return image;
    }

    public int getDishId() {
        return dishId;
    }
}
