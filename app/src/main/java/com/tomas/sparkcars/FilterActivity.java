package com.tomas.sparkcars;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class FilterActivity extends AppCompatActivity {

    private EditText plateEditText;
    private DiscreteSeekBar remainingBattery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        plateEditText = findViewById(R.id.plateEditText);
        remainingBattery = findViewById(R.id.seek_bar);
    }

    public void filterClicked(View view){
        Intent intent = new Intent();
        intent.putExtra("battery", remainingBattery.getProgress());
        if(String.valueOf(plateEditText.getText()).equals("")){
            setResult(1, intent);
        }else{
            intent.putExtra("plate", String.valueOf(plateEditText.getText()));
            setResult(2, intent);
        }
        finish();
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_filter_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                Intent intent = new Intent();
                setResult(3, intent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
