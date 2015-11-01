package com.example.jens.kitchenconverter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    //The Android's default system path of your application database.

    private static final String TAG = "DataBaseHelper";
    private static final String DATABASE_NAME = "kitchenConverter.db";
    private static final int DATABASE_VERSION = 2;

    private String pathToSaveDBFile;

    // constants for units table
    private static final String TABLE_UNITS = "units";
    private static final String UNITS_KEY_ID = "_id";
    private static final String UNITS_KEY_UNIT = "unit";
    private static final String UNITS_KEY_DIMENSION = "dimension";
    private static final String UNITS_KEY_FACTOR = "factor";
    private static final String UNITS_KEY_BASE = "base";

    private static final String TABLE_DENSITIES = "densities";
    private static final String DENSITIES_KEY_ID = "_id";
    private static final String DENSITIES_KEY_SUBSTANCE = "substance";
    private static final String DENSITIES_KEY_DENSITY = "density";

    private SQLiteDatabase myDataBase;
    private final Context myContext;

    // general database manipulation
    public DataBaseHelper(Context context, String filePath) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        pathToSaveDBFile = filePath + "/" + DATABASE_NAME;
    }
    public void prepareDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            Log.d(TAG, "Database exists");
            int currentDBVersion = getVersionId();
            Log.d(TAG,"currentDBVersion="+currentDBVersion);
            if (DATABASE_VERSION > currentDBVersion) {
                Log.d(TAG, "Database version is higher than old");
                deleteDB();
                try {
                    copyDataBase();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        } else {
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
    private boolean checkDataBase() {

        boolean checkDB = false;

        try {
            File file = new File(pathToSaveDBFile);
            checkDB = file.exists();
        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return checkDB;
    }
    private void copyDataBase() throws IOException{

        OutputStream os = new FileOutputStream(pathToSaveDBFile);
        InputStream is = myContext.getAssets().open(DATABASE_NAME);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer))>0){
            os.write(buffer, 0, length);
        }

        //Close the streams
        os.flush();
        os.close();
        is.close();

    }
    public void deleteDB() {
        File file = new File(pathToSaveDBFile);
        if(file.exists()) {
            file.delete();
            Log.d(TAG,"Database deleted");
        }
    }
    private int getVersionId() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT version_id FROM dbVersion";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int v =  cursor.getInt(0);
        db.close();
        return v;
    }

    // add elements
    public void addUnit(Unit unit) {
        // for logging
        Log.d("addUnit",unit.toString());

        // 1. Get reference to writable DB
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);

        // 2. create Contentvalues to add "key" column/value
        ContentValues values = new ContentValues();
        values.put(UNITS_KEY_UNIT,unit.getUnit()); // get unit name
        values.put(UNITS_KEY_DIMENSION,unit.getDimension()); // get dimension name
        values.put(UNITS_KEY_FACTOR, unit.getFactor()); // get factor

        // 3. insert
        db.insert(TABLE_UNITS,
                null,// nullColumnHack
                values);

        // 4. close
        db.close();
    }
    public void addDensity (Density density) {
        // for logging
        Log.d("addDensityt",density.toString());

        // 1. Get reference to writable DB
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);

        // 2. create Contentvalues to add "key" column/value
        ContentValues values = new ContentValues();
        values.put(DENSITIES_KEY_SUBSTANCE, density.getSubstance()); // get substance name
        values.put(DENSITIES_KEY_DENSITY, density.getDensity()); // get density

        // 3. insert
        db.insert(TABLE_DENSITIES,
                null,// nullColumnHack
                values);

        // 4. close
        db.close();
    }

    // update elements
    public int updateUnit(Unit unit) {

        Log.d("updateUnit",unit.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);

        // 2. Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(UNITS_KEY_UNIT,unit.getUnit());
        values.put(UNITS_KEY_DIMENSION,unit.getDimension());
        values.put(UNITS_KEY_FACTOR,unit.getFactor());

        // 3. updating row
        int i = db.update(TABLE_UNITS,
                values,
                UNITS_KEY_ID + " = ?",
                new String[] {String.valueOf(unit.getId()) });

        // 4. close
        db.close();

        return i;
    }
    public int updateDensity(Density density) {

        Log.d("updateDensity",density.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);

        // 2. Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(DENSITIES_KEY_SUBSTANCE,density.getSubstance());
        values.put(DENSITIES_KEY_DENSITY,density.getDensity());

        // 3. updating row
        int i = db.update(TABLE_DENSITIES,
                values,
                DENSITIES_KEY_ID + " = ?",
                new String[] {String.valueOf(density.getId()) });

        // 4. close
        db.close();

        return i;
    }

    // delete elements
    public void deleteUnit(Unit unit) {
        // 1. get reference to writeable DB
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);

        // 2. delete
        db.delete(TABLE_UNITS,
                UNITS_KEY_ID+" = ?",
                new String[] { String.valueOf(unit.getId()) });

        // 3. close
        db.close();

        Log.d("deleteUnit", unit.toString());
    }
    public void deleteDensity(Density density) {
        // 1. get reference to writeable DB
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);

        // 2. delete
        db.delete(TABLE_DENSITIES,
                DENSITIES_KEY_ID+" = ?",
                new String[] { String.valueOf(density.getId()) });

        // 3. close
        db.close();

        Log.d("deleteDensity", density.toString());
    }

    // get lists of elements
    public List<Unit> getAllUnits() {
        List<Unit> units = new LinkedList<>();

        // 1. build the query
        String query = "SELECT * FROM " + TABLE_UNITS;

        // 2. get reference to writable DB
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build unit and add it to list
        Unit unit;
        if(cursor.moveToFirst()) {
            do {
                unit = new Unit(myContext);
                unit.setId(Integer.parseInt(cursor.getString(0)));
                unit.setUnit(cursor.getString(1));
                unit.setDimension(cursor.getString(2));
                unit.setFactor(Double.parseDouble(cursor.getString(3)));

                // add unit to units
                units.add(unit);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return units;
    }
    public List<Unit> getUnitsDimension(String dimension) {
        List<Unit> units = new LinkedList<>();

        String[] dimensions = myContext.getResources().getStringArray(R.array.dimensions_array);

        if(!Arrays.asList(dimensions).contains(dimension)){
            throw new IllegalArgumentException("Dimension is not one of the permittable dimension names");
        }

        // 1. build the query, avoid SQL injection
        // String query = "SELECT * FROM " + TABLE_UNITS + " WHERE " + UNITS_KEY_DIMENSION + " = ?'" + dimension + "'";
        //String query = "SELECT * FROM ? WHERE ? = ?";
        String query = "SELECT * FROM " + TABLE_UNITS + " WHERE " + UNITS_KEY_DIMENSION +" = ?";



        // 2. get reference to writable DB
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile,null,SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery(query, new String[] { dimension });

        // 3. go over each row, build unit and add it to list
        Unit unit;
        if(cursor.moveToFirst()) {
            do {
                unit = new Unit(myContext);
                unit.setId(Integer.parseInt(cursor.getString(0)));
                unit.setUnit(cursor.getString(1));
                unit.setDimension(cursor.getString(2));
                unit.setFactor(Double.parseDouble(cursor.getString(3)));

                // add unit to units
                units.add(unit);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return units;
    }
    public List<Density> getAllDensities() {
        List<Density> densities = new LinkedList<>();

        // 1. build the query
        String query = "SELECT * FROM " + TABLE_DENSITIES;

        // 2. get reference to writable DB
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build unit and add it to list
        Density density;
        if(cursor.moveToFirst()) {
            do {
                density = new Density(myContext);
                density.setId(Integer.parseInt(cursor.getString(0)));
                density.setSubstance(cursor.getString(1));
                density.setDensity(Double.parseDouble(cursor.getString(2)));

                // add unit to units
                densities.add(density);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return densities;
    }


    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

