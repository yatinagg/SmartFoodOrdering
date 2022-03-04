package com.foodorder.smartfoodordering;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<Food> foodList;
    private List<String> itemTitle;
    private List<String> itemQuantity;
    private List<String> itemPrice;
    private int totalPrice;
    private String paymentStatus;

    public Order(List<Food> foodList) {
        this.foodList = foodList;
    }

    public List<Food> getFoodList() {
        return foodList;
    }

    public void setData(){
        itemTitle = new ArrayList<>();
        itemPrice = new ArrayList<>();
        itemQuantity = new ArrayList<>();
        totalPrice = 0;
        for(int i=0;i<foodList.size();i++){
            itemTitle.add(foodList.get(i).title);
            itemPrice.add(String.valueOf(foodList.get(i).price*foodList.get(i).quantity));
            itemQuantity.add(String.valueOf(foodList.get(i).quantity));
            totalPrice += foodList.get(i).price*foodList.get(i).quantity;
        }
        paymentStatus = "Cash";
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    @Override
    public String toString() {
        String displayOrder = "";
        for(int i=0;i<foodList.size();i++){
            displayOrder += foodList.get(i).getTitle() + " " + foodList.get(i).getQuantity() + " " + foodList.get(i).getPrice() + "\n";
        }
        return displayOrder;
    }

    public List<String> getItemTitle() {
        return itemTitle;
    }

    public List<String> getItemQuantity() {
        return itemQuantity;
    }

    public List<String> getItemPrice() {
        return itemPrice;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
