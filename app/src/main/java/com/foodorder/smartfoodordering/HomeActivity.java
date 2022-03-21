package com.foodorder.smartfoodordering;

import static java.lang.Math.max;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodorder.smartfoodordering.adapter.FoodListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements PaymentStatusListener {

    private String email;
    private RecyclerView recyclerView;
    private FoodListAdapter foodListAdapter;
    private Button btnViewCart;
    private Order order;
    private Button payButton;
    private Button btnUsePreviousOrder;
    private Button btnModifyCart;
    private final String TAG = "HomeActivity";
    private int timeEst=0;
    private TextView tvTotalPrice;
    private Order previousOrder;
    private TextView orderPlacedItemTitle;
    private TextView orderPlacedItemQuantity;
    private TextView orderPlacedItemPrice;
    private TextView orderPlacedTitle;
    private TextView orderPlacedQuantity;
    private TextView orderPlacedPrice;
    private TextView menuItems;
    private Button btnLogout;
    private Button btnPaymentMode;
    private List<Food> foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        getPreviousOrder();

        setupView();

        setupListeners();

        foodList = new ArrayList<>();
        foodList.add(new Food("Dal Makhni", R.drawable.dal_makhni, "Dal Makhni Description", 10, 0,"MainCourse"));
        foodList.add(new Food("Paneer", R.drawable.paneer, "Paneer Description", 210, 0,"MainCourse"));
        foodList.add(new Food("Chole Bhature", R.drawable.chole_bhature, "2 pcs Bhature with Chole", 80, 0,"MainCourse"));
        foodList.add(new Food("Idli", R.drawable.idli_sambhar, "2 pcs Idli with Sambhar", 70, 0,"SouthIndian"));
        foodList.add(new Food("Malai Kofta", R.drawable.malai_kofta, "Malai Kofta description", 190, 0,"MainCourse"));
        foodList.add(new Food("Masala Dosa", R.drawable.masala_dosa, "Masala dosa with sambhar and chutney", 100, 0,"SouthIndian"));
        foodList.add(new Food("Mix Veg", R.drawable.mix_veg, "Mix Veg Description", 160, 0,"MainCourse"));
        foodList.add(new Food("Pav Bhaji", R.drawable.pav_bhaji, "2 pcs Pav with bhaji", 150, 0,"FastFood"));
        foodList.add(new Food("Sambhar Vada", R.drawable.sambhar_vada, "2 pcs vada with sambhar and chutney", 80, 0,"SouthIndian"));

        foodListAdapter = new FoodListAdapter(foodList);
        recyclerView.setAdapter(foodListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

    }

    private void setupView() {
        orderPlacedTitle = findViewById(R.id.order_placed_title);
        orderPlacedQuantity = findViewById(R.id.order_placed_quantity);
        orderPlacedPrice = findViewById(R.id.order_placed_price);
        orderPlacedItemTitle = findViewById(R.id.order_placed_item_title);
        orderPlacedItemQuantity = findViewById(R.id.order_placed_item_quantity);
        orderPlacedItemPrice = findViewById(R.id.order_placed_item_price);
        recyclerView = findViewById(R.id.recycler_view_food);
        btnViewCart = findViewById(R.id.btn_view_cart);
        payButton = findViewById(R.id.btn_pay);
        menuItems = findViewById(R.id.tv_menu_items);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnUsePreviousOrder = findViewById(R.id.btn_use_previous_order);
        btnModifyCart = findViewById(R.id.btn_modify_cart);
        btnLogout = findViewById(R.id.btn_logout);
        btnPaymentMode = findViewById(R.id.payment_mode);
    }


    private void setupListeners() {
        btnUsePreviousOrder.setOnClickListener(view -> {
            if(btnUsePreviousOrder.getText().equals("Use Previous Order")) {
                for (int i = 0; i < previousOrder.getFoodList().size(); i++) {
                    for (int j = 0; j < foodList.size(); j++) {
                        if (previousOrder.getFoodList().get(i).title.equals(foodList.get(j).title)) {
                            foodList.get(j).quantity = previousOrder.getFoodList().get(i).quantity;
                        }
                    }
                }
                btnUsePreviousOrder.setText("Don't use previous order");
                foodListAdapter = new FoodListAdapter(foodList);
                recyclerView.setAdapter(foodListAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
            }
            else{
                for (int j = 0; j < foodList.size(); j++) {
                    foodList.get(j).quantity = 0;
                }
                btnUsePreviousOrder.setText("Use Previous Order");
                foodListAdapter = new FoodListAdapter(foodList);
                recyclerView.setAdapter(foodListAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
            }
        });

        btnPaymentMode.setOnClickListener(view -> {
            if(btnPaymentMode.getText() == "Cash") {
                btnPaymentMode.setText("Online");
                payButton.setText("Pay and place order");
            }
            else {
                btnPaymentMode.setText("Cash");
                payButton.setText("Place Order in cash mode");
            }
        });

        payButton.setOnClickListener(view -> {
            if(btnPaymentMode.getText() == "Cash"){
                order.setPaymentMode("Cash");
                order.setPaymentStatus("Incomplete");
                orderPlaced();
            }
            else {
                order.setPaymentMode("Online");
                makePayment(order.getTotalPrice() + ".00", "9891439925@paytm", "SmartFoodOrdering", order.getTotalPrice() + " " + email, email);
            }
        });

        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        });

        btnViewCart.setOnClickListener(view -> {
            btnViewCart.setVisibility(View.INVISIBLE);
            payButton.setVisibility(View.VISIBLE);
            btnUsePreviousOrder.setVisibility(View.INVISIBLE);
            btnViewCart.setText("Order Placed");
            btnModifyCart.setVisibility(View.VISIBLE);
            menuItems.setText("Cart");
            recyclerView.setVisibility(View.INVISIBLE);
            orderPlacedItemTitle.setVisibility(View.VISIBLE);
            orderPlacedItemQuantity.setVisibility(View.VISIBLE);
            orderPlacedItemPrice.setVisibility(View.VISIBLE);

            orderPlacedTitle.setVisibility(View.VISIBLE);
            orderPlacedQuantity.setVisibility(View.VISIBLE);
            orderPlacedPrice.setVisibility(View.VISIBLE);
            tvTotalPrice.setVisibility(View.VISIBLE);
            btnPaymentMode.setVisibility(View.VISIBLE);

            final List<Food> foodList1 = foodListAdapter.getFoodList();
            List<Food> orderFoodList = new ArrayList<>();
            for(int i=0;i<foodList1.size();i++){
                Log.d("HomeActivity",foodList1.get(i).title + " " + foodList1.get(i).quantity);
                if(foodList1.get(i).quantity>0){
                    orderFoodList.add(foodList1.get(i));
                }
            }
            order = new Order(orderFoodList);
            order.setData();
            tvTotalPrice.setText("Total Price = Rs" + order.getTotalPrice() + " ETA:" + timeEst + "mins");

            String title = "";
            for(int i=0;i<order.getItemTitle().size();i++){
                title += order.getItemTitle().get(i) + "\n";
            }
            orderPlacedItemTitle.setText(title);
            String quantity = "";
            for(int i=0;i<order.getItemQuantity().size();i++){
                quantity += order.getItemQuantity().get(i) + "\n";
            }
            orderPlacedItemQuantity.setText(quantity);
            String price = "";
            for(int i=0;i<order.getItemPrice().size();i++){
                price += order.getItemPrice().get(i) + "\n";
            }
            orderPlacedItemPrice.setText(price);
            order.setPaymentStatus("Complete");

            List<String> catList = new ArrayList<>();
            for(int i=0;i<order.getFoodList().size();i++){
                catList.add(order.getFoodList().get(i).category);
            }
            timeEst = maxTime(catList);
            tvTotalPrice.setText("Total Price = Rs" + order.getTotalPrice() + "\nEstimated Time of Arrival : " + timeEst + "mins");

        });

        btnModifyCart.setOnClickListener(view -> {
            recyclerView.setVisibility(View.VISIBLE);
            btnModifyCart.setVisibility(View.INVISIBLE);
            orderPlacedTitle.setVisibility(View.INVISIBLE);
            orderPlacedItemTitle.setVisibility(View.INVISIBLE);
            orderPlacedQuantity.setVisibility(View.INVISIBLE);
            orderPlacedItemQuantity.setVisibility(View.INVISIBLE);
            orderPlacedPrice.setVisibility(View.INVISIBLE);
            orderPlacedItemPrice.setVisibility(View.INVISIBLE);
            tvTotalPrice.setVisibility(View.INVISIBLE);
            payButton.setVisibility(View.INVISIBLE);
            btnPaymentMode.setVisibility(View.INVISIBLE);
            btnViewCart.setVisibility(View.VISIBLE);
            btnViewCart.setText("View Cart");
            menuItems.setText("Menu");
            btnUsePreviousOrder.setVisibility(View.VISIBLE);
        });
    }

    private void makePayment(String amount, String upi, String name, String desc, String transactionId) {
        // on below line we are calling an easy payment method and passing
        // all parameters to it such as upi id,name, description and others.
        final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                .with(this)
                // on below line we are adding upi id.
                .setPayeeVpa(upi)
                // on below line we are setting name to which we are making payment.
                .setPayeeName(name)
                // on below line we are passing transaction id.
                .setTransactionId(transactionId)
                // on below line we are passing transaction ref id.
                .setTransactionRefId(transactionId)
                // on below line we are adding description to payment.
                .setDescription(desc)
                // on below line we are passing amount which is being paid.
                .setAmount(amount)
                // on below line we are calling a build method to build this ui.
                .build();
        // on below line we are calling a start
        // payment method to start a payment.
        easyUpiPayment.startPayment();
        // on below line we are calling a set payment
        // status listener method to call other payment methods.
        easyUpiPayment.setPaymentStatusListener(this);
    }

    @Override
    public void onTransactionCompleted(TransactionDetails transactionDetails) {
        // on below line we are getting details about transaction when completed.
        Log.d("HomeActivity","onTransactionCompleted");
    }

    @Override
    public void onTransactionSuccess() {
        // this method is called when transaction is successful and we are displaying a toast message.
        Toast.makeText(this, "Transaction successfully completed..", Toast.LENGTH_SHORT).show();
        orderPlaced();
    }

    private void orderPlaced() {
        Toast.makeText(this, "Order Placed..", Toast.LENGTH_SHORT).show();
        payButton.setVisibility(View.INVISIBLE);
        addOrderToFireStore(order);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    @Override
    public void onTransactionSubmitted() {
        Log.d("HomeActivity","onTransactionSubmitted");
    }

    @Override
    public void onTransactionFailed() {
        Toast.makeText(this, "Transaction Failed, Try again", Toast.LENGTH_SHORT).show();
        payButton.setText(R.string.try_payment_again);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    @Override
    public void onTransactionCancelled() {
        Toast.makeText(this, "Transaction Cancelled, Try again", Toast.LENGTH_SHORT).show();
        payButton.setText(R.string.try_payment_again);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    @Override
    public void onAppNotFound() {
        Toast.makeText(this, "App not found, Try again", Toast.LENGTH_SHORT).show();
        payButton.setText(R.string.try_payment_again);
    }

    private void addOrderToFireStore(Order order) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> mOrder = new HashMap<>();
        List<Map<String,Object>> foodItems = new ArrayList<>();
        for(int i=0;i<order.getFoodList().size();i++){
            Map<String,Object> food = new HashMap<>();
            food.put("Name",order.getFoodList().get(i).title);
            food.put("Quantity",order.getFoodList().get(i).quantity);
            food.put("Price",order.getFoodList().get(i).price);
            food.put("Category",order.getFoodList().get(i).category);
            foodItems.add(food);
        }
        mOrder.put("email",email);
        mOrder.put("foodItems", foodItems);
        mOrder.put("totalPrice", order.getTotalPrice());
        mOrder.put("Status","Active");
        mOrder.put("PaymentMode",order.getPaymentMode());
        mOrder.put("PaymentStatus",order.getPaymentStatus());
        mOrder.put("OrderPlacedTime", Calendar.getInstance().getTime());
        mOrder.put("estimatedTime", timeEst);

        // Add a new document with a generated ID
        db.collection("orders")
                .add(mOrder)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));

        // Add a new document with a generated ID
        db.collection("previous_order")
                .document(email)
                .set(mOrder)
                .addOnSuccessListener(unused -> Log.d(TAG, "DocumentSnapshot added with ID: " + email))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    int maxTime(List<String> catList){
        int timeMax = 0;
        for(String x:catList){
            switch (x) {
                case "MainCourse":
                    timeMax = max(timeMax, 30);
                    break;
                case "SouthIndian":
                    timeMax = max(timeMax, 20);
                    break;
                case "FastFood":
                    timeMax = max(timeMax, 10);
                    break;
                default:
                    timeMax = max(timeMax, 5);
                    break;
            }
        }
        return timeMax;
    }

    void getPreviousOrder(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("previous_order").document(email).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d(TAG,"success " + documentSnapshot.get("foodItems"));
                    if(documentSnapshot.get("foodItems") == null){
                        btnUsePreviousOrder.setVisibility(View.GONE);
                        return;
                    }
                    List<Map<String,Object>> foodItems = (List<Map<String, Object>>) documentSnapshot.get("foodItems");

                    List<Food> foodList1 = new ArrayList<>();
                    for(int i=0;i<foodItems.size();i++){
                        int tempVal = ((Long)foodItems.get(i).get("Quantity")).intValue();
                        foodList1.add(new Food((String) foodItems.get(i).get("Name"),tempVal));
                    }
                    previousOrder = new Order(foodList1);

                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "failure");
                });
    }
}
