package com.example.jens.kitchenconverter;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;


public class ConverterActivity extends AppCompatActivity {

    private Spinner from_spinner;
    private Spinner to_spinner;
    SpinnerUnitAdapter fUnitAdapter;
    SpinnerUnitAdapter tUnitAdapter;

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        String dim = getIntent().getStringExtra("dimension");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView testDisplay = (TextView) findViewById(R.id.test_display);
        testDisplay.setText("Dimension is: " + dim);

        from_spinner = (Spinner) findViewById(R.id.from_spinner);
        to_spinner = (Spinner) findViewById(R.id.to_spinner);

        final EditText enterString = (EditText) findViewById(R.id.enter_value);
        final TextView resultValue = (TextView) findViewById(R.id.result_value);


        // create or open Database

        DataBaseHelper myDbHelper = new DataBaseHelper(this);

        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }

        List<Unit> list = myDbHelper.getUnitsDimension(dim);

        fUnitAdapter = new SpinnerUnitAdapter(this,android.R.layout.simple_spinner_item,list);
        tUnitAdapter = new SpinnerUnitAdapter(this,android.R.layout.simple_spinner_item,list);
        from_spinner.setAdapter(fUnitAdapter);
        to_spinner.setAdapter(tUnitAdapter);


        enterString.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //float enterValue = Float.valueOf(enterString.getText().toString());
                if(!s.toString().isEmpty()) {
                    float enterValue = Float.valueOf(s.toString());

                    Unit fUnit = (Unit) from_spinner.getSelectedItem();
                    float from_factor = fUnit.getFactor();

                    Unit tUnit = (Unit) to_spinner.getSelectedItem();
                    float to_factor = tUnit.getFactor();

                    resultValue.setText(String.valueOf(enterValue * from_factor / to_factor));
                }
            }
        });

        from_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                if(!enterString.getText().toString().isEmpty()) {
                    float enterValue = Float.valueOf(enterString.getText().toString());

                    Unit fUnit = fUnitAdapter.getItem(position);
                    float from_factor = fUnit.getFactor();

                    Unit tUnit = (Unit) to_spinner.getSelectedItem();
                    float to_factor = tUnit.getFactor();

                    resultValue.setText(String.valueOf(enterValue * from_factor / to_factor));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        to_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                if(!enterString.getText().toString().isEmpty()) {
                    float enterValue = Float.valueOf(enterString.getText().toString());

                    Unit fUnit = (Unit) from_spinner.getSelectedItem();
                    float from_factor = fUnit.getFactor();

                    Unit tUnit = tUnitAdapter.getItem(position);
                    float to_factor = tUnit.getFactor();

                    resultValue.setText(String.valueOf(enterValue * from_factor / to_factor));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        myDbHelper.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                Intent i = new Intent(ConverterActivity.this,SettingsActivity.class);
                startActivity(i);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
