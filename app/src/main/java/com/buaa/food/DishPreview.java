package com.buaa.food;

public class DishPreview {
    String dishName;
    String dishPrice;

    // TODO: implement dishImage
    // String dishImage;

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
}
