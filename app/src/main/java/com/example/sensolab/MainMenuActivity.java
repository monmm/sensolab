package com.example.sensolab;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.Nullable;

public abstract class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }//onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_about) {
            // Intent intent = new Intent(this, CreditosActivity.class);
            // startActivity(intent);
            Toast.makeText(this, "To-Do ...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_connect) {
            new IpDialogFragment().show(getSupportFragmentManager(), "IpDialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
