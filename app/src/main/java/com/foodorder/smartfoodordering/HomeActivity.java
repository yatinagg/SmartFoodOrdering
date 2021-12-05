package com.foodorder.smartfoodordering;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodorder.smartfoodordering.adapter.FoodListAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private String phone;
    private String password;
    private RecyclerView recyclerView;
    private FoodListAdapter FoodListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();

        phone = intent.getStringExtra("phone");
        password = intent.getStringExtra("password");
        System.out.println("Login credentials " + phone + " " + password);

        Button buttonSubmit = findViewById(R.id.button_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Order Placed", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView = findViewById(R.id.recycler_view_food);

        List<Food> foodList = new ArrayList<>();
        foodList.add(new Food("Dal Makhni",R.drawable.dal_makhni,"Dal Makhni Description",180,0));
        foodList.add(new Food("Paneer",R.drawable.paneer,"Paneer Description",210,0));

        FoodListAdapter = new FoodListAdapter(foodList);
        recyclerView.setAdapter(FoodListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));


    }
}
