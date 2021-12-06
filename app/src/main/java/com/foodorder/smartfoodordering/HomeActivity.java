package com.foodorder.smartfoodordering;

import android.content.Intent;
import android.os.Bundle;
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
        buttonSubmit.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "Order Placed", Toast.LENGTH_SHORT).show());

        recyclerView = findViewById(R.id.recycler_view_food);

        List<Food> foodList = new ArrayList<>();
        foodList.add(new Food("Dal Makhni", R.drawable.dal_makhni, "Dal Makhni Description", 180, 0));
        foodList.add(new Food("Paneer", R.drawable.paneer, "Paneer Description", 210, 0));
        foodList.add(new Food("Chole Bhature", R.drawable.chole_bhature, "2 pcs Bhature with Chole", 80, 0));
        foodList.add(new Food("Idli", R.drawable.idli_sambhar, "2 pcs Idli with Sambhar", 70, 0));
        foodList.add(new Food("Malai Kofta", R.drawable.malai_kofta, "Malai Kofta description", 190, 0));
        foodList.add(new Food("Masala Dosa", R.drawable.masala_dosa, "Masala dosa with sambhar and chutney", 100, 0));
        foodList.add(new Food("Mix Veg", R.drawable.mix_veg, "Mix Veg Description", 160, 0));
        foodList.add(new Food("Pav Bhaji", R.drawable.pav_bhaji, "2 pcs Pav with bhaji", 150, 0));
        foodList.add(new Food("Sambhar Vada", R.drawable.sambhar_vada, "2 pcs vada with sambhar and chutney", 80, 0));


        FoodListAdapter = new FoodListAdapter(foodList);
        recyclerView.setAdapter(FoodListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));


    }
}
