package com.buaa.food;

public class DishPreview implements Comparable<DishPreview> {
    int dishId;
    int ordered;
    int viewed;
    String dishName;
    String dishPrice;

    // TODO: implement dishImage
    // String dishImage;
    byte[] image;

    public DishPreview(int dishId, String dishName, String dishPrice, byte[] image,
                       int ordered, int viewed) {
        this.dishId = dishId;
        this.dishName = dishName;
        this.dishPrice = dishPrice;
        this.image = image;
        this.ordered = ordered;
        this.viewed = viewed;
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

    public void setDishPrice(String dishPrice) {
        this.dishPrice = dishPrice;
    }

    public byte[] getImage() {
        return image;
    }

    public int getDishId() {
        return dishId;
    }

    @Override
    public int compareTo(DishPreview other) {
        // 比较规则：首先按照ordered属性升序排列，如果相同则按照viewed属性升序排列
        if (this.ordered + this.viewed != other.ordered + other.viewed) {
            return Integer.compare(other.ordered + other.viewed, this.ordered + this.viewed);
        } else {
            return Integer.compare(this.dishId, other.dishId);
        }
    }
}
