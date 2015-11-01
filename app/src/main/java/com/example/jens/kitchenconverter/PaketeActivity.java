package com.example.jens.kitchenconverter;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class PaketeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "PaketeActivity";
    private RadioButton radioButton;
    private RadioButton radioFilterButton;
    final Context context = this;

    PaketAdapter mPaketAdapter;
    ListView mainListView;


    LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            RadioGroup.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pakete);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if( getSupportActionBar() != null ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        DataBaseHelper myDbHelper = new DataBaseHelper(this,getFilesDir().getAbsolutePath());

        // 5. Set this Activity to react to list items being pressed
        mainListView = (ListView) findViewById(R.id.listView);
        mainListView.setOnItemClickListener(this);

        // output as list
        List<Paket> list = myDbHelper.getAllPakete();

        // Create a UnitAdapter for the ListView and Set the ListView to use the UnitAdapter
        mPaketAdapter = new PaketAdapter(getLayoutInflater());
        mainListView.setAdapter(mPaketAdapter);

        mPaketAdapter.updateData(list);
        myDbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE, 99,Menu.NONE,R.string.add).setIcon(R.drawable.ic_add_white_48dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);


        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String[] dimensions = getResources().getStringArray(R.array.dimensions_array);
        switch(id) {
            case R.id.action_settings:
                Intent i = new Intent(PaketeActivity.this,SettingsActivity.class);
                startActivity(i);
                break;

            case 99: // add
                final Dialog d = new Dialog(context);
                d.setContentView(R.layout.add_paket_dialog);
                d.setTitle("Add package");
                d.setCancelable(true);
                final EditText editSubstance = (EditText) d.findViewById(R.id.editTextSubstance);
                final RadioGroup radioDimensionGroup= (RadioGroup) d.findViewById(R.id.radio_group);

                DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());

                ArrayList<List<Unit>> unitListArray = new ArrayList<>(); // collection of unit lists; each array elemtn corresponds to one dimension
                final ArrayList<SpinnerUnitAdapter> unitAdapterArray = new ArrayList<>();

                for(int j=0; j < dimensions.length; j++) {
                    RadioButton rb = new RadioButton(context);
                    rb.setText(dimensions[j]);
                    rb.setId(j);
                    radioDimensionGroup.addView(rb, j, layoutParams);

                    List<Unit> list = myDbHelper.getUnitsDimension(dimensions[j]);
                    SpinnerUnitAdapter sUnitAdapter = new SpinnerUnitAdapter(this, android.R.layout.simple_spinner_item, list);
                    unitListArray.add(j,list);
                    unitAdapterArray.add(sUnitAdapter);
                }


                myDbHelper.close();

                final EditText editValue = (EditText) d.findViewById(R.id.editTextValue);
                final Spinner unitSpinner = (Spinner) d.findViewById(R.id.unit_spinner);




                radioDimensionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        unitSpinner.setAdapter(unitAdapterArray.get(checkedId));
                    }
                });



                Button addBtn = (Button) d.findViewById(R.id.button1);


                // set click listener for add button in add_unit_dialog
                addBtn.setOnClickListener(new View.OnClickListener() {
                                              public void onClick(View v) {

                                                  String substanceName = editSubstance.getText().toString();

                                                  int rgid = radioDimensionGroup.getCheckedRadioButtonId();
                                                  radioButton = (RadioButton) d.findViewById(rgid);
                                                  String paketDimension = radioButton.getText().toString();

                                                  Double paketValue = Double.valueOf(editValue.getText().toString());


                                                  Unit selected_unit = (Unit) unitSpinner.getSelectedItem();
                                                  Double spinner_factor = selected_unit.getFactor();

                                                  Paket addpaket = new Paket(substanceName, paketDimension, paketValue * spinner_factor, context);
                                                  DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());

                                                  myDbHelper.addPaket(addpaket);
                                                  mPaketAdapter.updateData(myDbHelper.getAllPakete());
                                                  myDbHelper.close();
                                                  d.dismiss();
                                              }
                                          }


                );
                d.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Paket paket = mPaketAdapter.getItem(position);


        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.edit_paket_dialog);
        d.setTitle("Edit or delete package");
        d.setCancelable(true);

        // fill form with stored values
        final EditText editSubstance = (EditText) d.findViewById(R.id.editTextSubstance);
        editSubstance.setText(paket.getSubstance());

        String savedDimension=paket.getDimension();

        final RadioGroup radioDimensionGroup= (RadioGroup) d.findViewById(R.id.radio_group);
        String[] dimensions = getResources().getStringArray(R.array.dimensions_array);
        // add radio buttons programmatically
        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        final Spinner unitSpinner = (Spinner) d.findViewById(R.id.unit_spinner);

        DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());
        ArrayList<List<Unit>> unitListArray = new ArrayList<>(); // collection of unit lists; each array elemtn corresponds to one dimension
        final ArrayList<SpinnerUnitAdapter> unitAdapterArray = new ArrayList<>();

        for(int i=0; i < dimensions.length; i++) {
            RadioButton rb= new RadioButton(context);
            rb.setText(dimensions[i]);
            rb.setId(i);
            if(dimensions[i].equals(savedDimension)) { rb.setChecked(true); }
            radioDimensionGroup.addView(rb,i,layoutParams);

            List<Unit> list = myDbHelper.getUnitsDimension(dimensions[i]);
            SpinnerUnitAdapter sUnitAdapter = new SpinnerUnitAdapter(this, android.R.layout.simple_spinner_item, list);
            unitListArray.add(i, list);
            unitAdapterArray.add(sUnitAdapter);
            if(dimensions[i].equals(savedDimension)) { unitSpinner.setAdapter(sUnitAdapter); }
        }

        myDbHelper.close();



        final EditText editValue = (EditText) d.findViewById(R.id.editTextValue);
        editValue.setText(Double.toString(paket.getValue()));


        d.show();

        // end of displaying old values
        // start of processing new values


        Button deleteBtn = (Button) d.findViewById(R.id.button_delete);
        Button modifyBtn = (Button) d.findViewById(R.id.button_modify);


        radioDimensionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                unitSpinner.setAdapter(unitAdapterArray.get(checkedId));
            }
        });

        // set click listener for delete button in modify_dialog
        deleteBtn.setOnClickListener(new View.OnClickListener() {
                                         public void onClick(View v) {

                                             DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());
                                             myDbHelper.deletePaket(paket);
                                             mPaketAdapter.updateData(myDbHelper.getAllPakete());
                                             myDbHelper.close();
                                             d.dismiss();
                                         }
                                     }
        );

        // set click listener for modify button in modify_dialog
        modifyBtn.setOnClickListener(new View.OnClickListener() {
                                         public void onClick(View v) {
                                             DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());

                                             String substanceName = editSubstance.getText().toString();
                                             int rgid = radioDimensionGroup.getCheckedRadioButtonId();

                                             radioButton = (RadioButton) d.findViewById(rgid);
                                             String paketDimension = radioButton.getText().toString();

                                             Double paketValue = Double.valueOf(editValue.getText().toString());

                                             Unit selected_unit = (Unit) unitSpinner.getSelectedItem();
                                             Double spinner_factor = selected_unit.getFactor();

                                             paket.setSubstance(substanceName);
                                             paket.setDimension(paketDimension);
                                             paket.setValue(paketValue * spinner_factor);

                                             myDbHelper.updatePaket(paket);
                                             mPaketAdapter.updateData(myDbHelper.getAllPakete());
                                             myDbHelper.close();
                                             d.dismiss();
                                         }
                                     }
        );


    }
}
