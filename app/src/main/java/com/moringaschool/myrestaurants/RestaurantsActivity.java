package com.moringaschool.myrestaurants;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantsActivity extends AppCompatActivity {

    @BindView(R.id.locationTextView)
    TextView mLocationTextView;
    @BindView(R.id.listview)
    ListView mListView;

    public static final String TAG = RestaurantsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        ButterKnife.bind(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String restaurant = ((TextView)view).getText().toString();

                Toast.makeText(RestaurantsActivity.this, restaurant,Toast.LENGTH_LONG).show();
            }
        });

        Log.d(TAG, "In the onCreate method!");
        Intent intent = getIntent();
        String location = intent.getStringExtra("location");
        mLocationTextView.setText("Here are all the restaurants near: " + location);

        YelpApi yelpApi = YelpClient.getClient();
        Call<YelpApiBusinessesSearchResponse> call = yelpApi.getRestaurants(location, "restaurants");

        call.enqueue(new Callback<YelpApiBusinessesSearchResponse>() {
            @Override
            public void onResponse(Call<YelpApiBusinessesSearchResponse> call, Response<YelpApiBusinessesSearchResponse> response) {
                if(response.isSuccessful()){
                    List<Business> restaurantsList = response.body().getBusinesses();
                    String[] restaurants = new String[restaurantsList.size()];
                    String[] categories = new String[restaurantsList.size()];

                    for(int i = 0; i < restaurants.length; i++){
                        restaurants[i] = restaurantsList.get(i).getName();
                    }

                    for(int i = 0; i < categories.length; i++){
                        Category category = restaurantsList.get(i).getCategories().get(0);
                        categories[i] = category.getTitle();
                    }
                    ArrayAdapter adapter = new MyRestaurantsArrayAdapter(RestaurantsActivity.this, android.R.layout.simple_list_item_1, restaurants, categories);

                    mListView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<YelpApiBusinessesSearchResponse> call, Throwable t) {

            }
        });
    }
}