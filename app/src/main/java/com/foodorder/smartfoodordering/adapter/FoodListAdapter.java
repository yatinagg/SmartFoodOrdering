package com.foodorder.smartfoodordering.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodorder.smartfoodordering.Food;
import com.foodorder.smartfoodordering.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ViewHolder> {


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView description;
        TextView price;
        TextView quantity;
        FloatingActionButton buttonAdd;
        FloatingActionButton buttonDec;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.image_view_food_icon);
            title = itemView.findViewById(R.id.tv_food_title);
            description = itemView.findViewById(R.id.tv_food_description);
            price = itemView.findViewById(R.id.tv_food_price);
            quantity = itemView.findViewById(R.id.tv_food_quantity);
            buttonAdd = itemView.findViewById(R.id.floating_action_button_add);
            buttonDec = itemView.findViewById(R.id.floating_action_button_minus);
        }
    }

    private final List<Food> foodList;

    public FoodListAdapter(List<Food> list) {
        this.foodList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder((LayoutInflater.from(parent.getContext())).inflate(R.layout.listview_food,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Log.d("output1","points : "+foodList);

        Food food = foodList.get(position);

        viewHolder.title.setText(food.getTitle());
        viewHolder.description.setText(food.getDescription());
        viewHolder.price.setText(String.valueOf(food.getPrice()));
        viewHolder.quantity.setText(String.valueOf(food.getQuantity()));
        viewHolder.icon.setImageResource(food.getIcon());

        viewHolder.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                food.incrementQuantity();
                Log.d("output1","clicked");
                viewHolder.quantity.setText(String.valueOf(food.getQuantity()));
            }
        });
        viewHolder.buttonDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                food.decrementQuantity();
                if(food.getQuantity() < 0){
                    Toast.makeText(v.getContext(), "Invalid option",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("output1","clicked");
                viewHolder.quantity.setText(String.valueOf(food.getQuantity()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

}