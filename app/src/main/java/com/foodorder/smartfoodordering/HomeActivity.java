package com.foodorder.smartfoodordering;

import static java.lang.Math.max;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodorder.smartfoodordering.adapter.FoodListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.PaymentApp;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements PaymentStatusListener {

    private String email;
    private RecyclerView recyclerView;
    private FoodListAdapter foodListAdapter;
    private Button orderButton;
    private Order order;
    private Button payButton;
    private Button btUsePreviousOrder;
    private String TAG = "HomeActivity";
    private int timeEst=0;
    private TextView tvTotalPrice;
    private Order previousOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();

        email = intent.getStringExtra("email");

        getPreviousOrder();

        Button buttonSubmit = findViewById(R.id.button_submit);
        buttonSubmit.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "Order Placed", Toast.LENGTH_SHORT).show());

        recyclerView = findViewById(R.id.recycler_view_food);

        List<Food> foodList = new ArrayList<>();
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

        orderButton = findViewById(R.id.button_submit);
        payButton = findViewById(R.id.button_pay);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePayment(String.valueOf(order.getTotalPrice())+".00", "kriti03@axl","SmartFoodOrdering", order.getTotalPrice() +" "+email,email);
            }
        });
        Button btLogout = findViewById(R.id.button_logout);
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderButton.setVisibility(View.INVISIBLE);
                payButton.setVisibility(View.VISIBLE);
                btUsePreviousOrder.setVisibility(View.INVISIBLE);
                orderButton.setText("Order Placed");
                TextView menuItems = findViewById(R.id.tv_menu_items);
                menuItems.setText("Order Placed");
                findViewById(R.id.recycler_view_food).setVisibility(View.INVISIBLE);
                TextView orderPlacedItemTitle = findViewById(R.id.order_placed_item_title);
                TextView orderPlacedItemQuantity = findViewById(R.id.order_placed_item_quantity);
                TextView orderPlacedItemPrice = findViewById(R.id.order_placed_item_price);
                orderPlacedItemTitle.setVisibility(View.VISIBLE);
                orderPlacedItemQuantity.setVisibility(View.VISIBLE);
                orderPlacedItemPrice.setVisibility(View.VISIBLE);
                findViewById(R.id.order_placed_title).setVisibility(View.VISIBLE);
                findViewById(R.id.order_placed_quantity).setVisibility(View.VISIBLE);
                findViewById(R.id.order_placed_price).setVisibility(View.VISIBLE);
                tvTotalPrice = findViewById(R.id.tv_total_price);
                tvTotalPrice.setVisibility(View.VISIBLE);

                final List<Food> foodList1 = foodListAdapter.getFoodList();
                List<Food> orderFoodList = new ArrayList<>();
                for(int i=0;i<foodList1.size();i++){
                    Log.d("HomeActivity",foodList1.get(i).title + " " + foodList1.get(i).quantity);
                    if(foodList1.get(i).quantity>0){
                        orderFoodList.add(foodList1.get(i));
                    }
                }
                order = new Order(orderFoodList);
                Log.d("HomeActivity","yatin " + order);
                order.setData();
//                tvTotalPrice.setText("Total Price = Rs" + order.getTotalPrice() + " ETA:" + timeEst + "mins");

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

                addOrderToFireStore(order);
            }
        });

        btUsePreviousOrder = findViewById(R.id.bt_use_previous_order);
        btUsePreviousOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btUsePreviousOrder.getText().equals("Use Previous Order")) {
                    for (int i = 0; i < previousOrder.getFoodList().size(); i++) {
                        for (int j = 0; j < foodList.size(); j++) {
                            if (previousOrder.getFoodList().get(i).title.equals(foodList.get(j).title)) {
                                foodList.get(j).quantity = previousOrder.getFoodList().get(i).quantity;
                            }
                        }
                    }
                    btUsePreviousOrder.setText("Don't use previous order");
                    foodListAdapter = new FoodListAdapter(foodList);
                    recyclerView.setAdapter(foodListAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                    Log.d(TAG,"check check 1");
                }
                else{
                    for (int j = 0; j < foodList.size(); j++) {
                        foodList.get(j).quantity = 0;
                    }
                    btUsePreviousOrder.setText("Use previous order");
                    foodListAdapter = new FoodListAdapter(foodList);
                    recyclerView.setAdapter(foodListAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                    Log.d(TAG,"check check 2");
                }
            }
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
        payButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onTransactionSubmitted() {
        Log.d("HomeActivity","onTransactionSubmitted");
    }

    @Override
    public void onTransactionFailed() {
        Toast.makeText(this, "Transaction Failed, Try again", Toast.LENGTH_SHORT).show();
        payButton.setText("Try payment again");
    }

    @Override
    public void onTransactionCancelled() {
        Toast.makeText(this, "Transaction Cancelled, Try again", Toast.LENGTH_SHORT).show();
        payButton.setText("Try payment again");
    }

    @Override
    public void onAppNotFound() {
        Toast.makeText(this, "App not found, Try again", Toast.LENGTH_SHORT).show();
        payButton.setText("Try payment again");
    }

    private void addOrderToFireStore(Order order) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user with a first and last name
        Map<String, Object> mOrder = new HashMap<>();
        List<Map<String,Object>> foodItems = new ArrayList<>();
        List<String> catList = new ArrayList<>();
        for(int i=0;i<order.getFoodList().size();i++){
            Map<String,Object> food = new HashMap<>();
            food.put("Name",order.getFoodList().get(i).title);
            food.put("Quantity",order.getFoodList().get(i).quantity);
            food.put("Price",order.getFoodList().get(i).price);
            food.put("Category",order.getFoodList().get(i).category);
            catList.add(order.getFoodList().get(i).category);
            foodItems.add(food);
        }
        timeEst = maxTime(catList);
        tvTotalPrice.setText("Total Price = Rs" + order.getTotalPrice() + "\nEstimated Time of Arrival : " + timeEst + "mins");
        mOrder.put("email",email);
        mOrder.put("foodItems", foodItems);
        mOrder.put("totalPrice", order.getTotalPrice());
        mOrder.put("Status","Active");
        mOrder.put("OrderPlacedTime", Calendar.getInstance().getTime());
        mOrder.put("estimatedTime", timeEst);

        // Add a new document with a generated ID
        db.collection("orders")
                .add(mOrder)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        // Add a new document with a generated ID
        db.collection("previous_order")
                .document(email)
                .set(mOrder)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + email);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    int maxTime(List<String> catList){
        int timeMax = 0;
        for(String x:catList){
            if(x.equals("MainCourse"))
                timeMax = max(timeMax,30);
            else if(x.equals("SouthIndian"))
                timeMax = max(timeMax,20);
            else if(x.equals("FastFood"))
                timeMax = max(timeMax,10);
            else
                timeMax = max(timeMax,5);
        }
        return timeMax;
    }

    void getPreviousOrder(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("previous_order").document(email).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG,"success " + documentSnapshot.get("foodItems"));
                        if(documentSnapshot.get("foodItems") == null){
                            btUsePreviousOrder.setVisibility(View.GONE);
                            return;
                        }
                        List<Map<String,Object>> foodItems = (List<Map<String, Object>>) documentSnapshot.get("foodItems");

                        List<Food> foodList1 = new ArrayList<>();
                        for(int i=0;i<foodItems.size();i++){
                            Log.d(TAG,"yatin 1234" + foodItems.get(i));
                            int tempVal = ((Long)foodItems.get(i).get("Quantity")).intValue();
                            foodList1.add(new Food((String) foodItems.get(i).get("Name"),tempVal));
                        }
                        Log.d(TAG,"yatin 12345" + foodList1);
                        previousOrder = new Order(foodList1);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"failure");
                    }
                });
    }
}
