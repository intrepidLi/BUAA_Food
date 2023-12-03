package com.buaa.food.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.RecyclerView;

import com.buaa.food.R;
import com.buaa.food.app.AppAdapter;
import com.buaa.food.ui.activity.DishDetailsActivity;
import com.hjq.shape.view.ShapeTextView;

import java.sql.Array;
import java.util.ArrayList;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder> {
    private ArrayList<String> dishList;

    public DishAdapter(ArrayList<String> dishes) {
        this.dishList = dishes;
    }

    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hangwei_dish, parent, false);
        return new DishViewHolder(view);
    }

    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        String dish = dishList.get(position);
        holder.bindData(dish);
    }

    public int getItemCount() {
        return dishList.size();
    }

    public static class DishViewHolder extends RecyclerView.ViewHolder {
        private ShapeTextView dishNameTextView;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);
            dishNameTextView = itemView.findViewById(R.id.tv_hangwei_dishName);
            dishNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle the click event by starting DishDetailsActivity
                    Context context = view.getContext();
                    Intent intent = new Intent(context, DishDetailsActivity.class);
                    context.startActivity(intent);
                }
            });
        }

        public void bindData(String dish) {
            dishNameTextView.setText(dish);
            // 设置其他数据...
        }
    }

}
