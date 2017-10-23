package com.example.jwtasks2.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.jwtasks2.ui.fragments.ItemDetailFragment;
import com.example.jwtasks2.R;

public class ItemDetailActivity extends AppCompatActivity {
    public static final int CURRENT_LAYOUT = R.layout.activity_item_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(CURRENT_LAYOUT);
        if (savedInstanceState == null) {
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            navigateUpTo(new Intent(this, ItemListActivityMain.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
