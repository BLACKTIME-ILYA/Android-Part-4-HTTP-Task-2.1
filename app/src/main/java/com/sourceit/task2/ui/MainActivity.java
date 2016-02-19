package com.sourceit.task2.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.sourceit.task2.R;
import com.sourceit.task2.utils.L;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    private final int CRITICAL = 3;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private System root;

    private Bank rate;
    private Bank temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Retrofit.getBanks(new Callback<System>() {
            @Override
            public void success(System system, Response response) {
                root = system;
                setAdapter();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), "something went wrong :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter() {
        recyclerView.setAdapter(new MyRecyclerViewInfo(countRate()));
    }

    private ArrayList<Bank> countRate() {
        int j = 0;

        if (rate == null) {
            j = checkFirst(j);
        }

        for (int i = j; i < root.organizations.size(); i++) {
            if (root.organizations.get(i).currencies.EUR != null && root.organizations.get(i).currencies.USD != null && root.organizations.get(i).currencies.RUB != null) {
                temp = root.organizations.get(i);
                compare();
            }
        }

        ArrayList<Bank> rateArray = new ArrayList<>();
        rateArray.add(rate);
        return rateArray;
    }

    private int checkFirst(int value) {
        int count = value;
        if (root.organizations.get(count).currencies.EUR != null && root.organizations.get(count).currencies.USD != null && root.organizations.get(count).currencies.RUB != null) {
            rate = root.organizations.get(count);
        } else checkFirst(++count);
        return count;
    }

    private void compare() {
        L.d("in compare cycle");
        int rating = 0;

        if (Float.parseFloat(rate.currencies.EUR.bid) < Float.parseFloat(temp.currencies.EUR.bid)) {
            rating++;
        }
        if (Float.parseFloat(rate.currencies.EUR.ask) >= Float.parseFloat(temp.currencies.EUR.ask)) {
            rating++;
        }
        if (Float.parseFloat(rate.currencies.USD.bid) < Float.parseFloat(temp.currencies.USD.bid)) {
            rating++;
        }
        if (Float.parseFloat(rate.currencies.USD.ask) >= Float.parseFloat(temp.currencies.USD.ask)) {
            rating++;
        }
        if (Float.parseFloat(rate.currencies.RUB.bid) < Float.parseFloat(temp.currencies.RUB.bid)) {
            rating++;
        }
        if (Float.parseFloat(rate.currencies.RUB.ask) >= Float.parseFloat(temp.currencies.RUB.ask)) {
            rating++;
        }
        if (rating >= CRITICAL) {
            rate = temp;
        }
    }
}
