package com.foodorder.smartfoodordering;

public class Food {
    String title;
    int icon;
    String description;
    int price;
    int quantity;
    String category;

    public Food(String title, int icon, String description, int price, int quantity, String category) {
        this.title = title;
        this.icon = icon;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public Food(String title, int quantity) {
        this.title = title;
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void incrementQuantity() {
        this.quantity++;
    }

    public void decrementQuantity() {
        this.quantity--;
    }

}
