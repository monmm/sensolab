package com.example.sensolab;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.Nullable;

public abstract class AppBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }//onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.about_item) {
            Intent intent = new Intent(this, CreditosActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.connect_item) {
            new IpDialogFragment().show(getSupportFragmentManager(), "IpDialog");
            return true;
        } else if (id == R.id.adjust_item) {
            // new ConfigDialogFragment().show(getSupportFragmentManager(), "ConfigDialog");
            Toast.makeText(getApplicationContext(), "TO-DO...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.share_item) {
            Toast.makeText(getApplicationContext(), "TO-DO...", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
