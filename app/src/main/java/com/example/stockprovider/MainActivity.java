package com.example.stockprovider;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static String baseURL = "https://query1.finance.yahoo.com/v7/finance/quote?lang=en-US&region=US&corsDomain=finance.yahoo.com&symbols=%s";
    public static EditText symbol;
    private SQLiteDatabase mDatabase;
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);

        final Button addStock = findViewById(R.id.addStock);
        symbol = findViewById(R.id.stockSymbol);

        addStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FetchData fetchData = new FetchData();
                fetchData.execute();
            }
        });

        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        mDatabase = databaseHelper.getWritableDatabase();
        mRecyclerView = findViewById(R.id.recycler_view_id);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayout.VERTICAL, true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager.setStackFromEnd(true);
        mAdapter = new Adapter(this, getContentResolver().query(Provider.CONTENT_URI, null, null, null, null));
        mRecyclerView.setAdapter(mAdapter);
    }

    class FetchData extends AsyncTask<String, Void, String> {
        String data = "";
        String longName;
        String regularMarketPrice;
        String regularMarketChange;
        String stockSymbol;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... voids) {
            String urlSpec = String.format(baseURL, symbol.getText().toString());
            try {
                URL url = new URL(urlSpec);
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    try {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String line = "";
                        while(line != null) {
                            line = bufferedReader.readLine();
                            // Read each and every line of the JSON file.
                            data = data + line;
                        }

                        JSONObject jsonObjectParent = new JSONObject(data);
                        String response = jsonObjectParent.getString("quoteResponse");
                        jsonObjectParent = new JSONObject(response);
                        response = jsonObjectParent.getString("result").replace("[", "")
                                .replace("]", "");
                        jsonObjectParent = new JSONObject(response);
                        longName = jsonObjectParent.getString("longName");
                        regularMarketPrice = jsonObjectParent.getString("regularMarketPrice");
                        regularMarketChange = jsonObjectParent.getString("regularMarketChange");
                        stockSymbol = jsonObjectParent.getString("symbol");

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        httpURLConnection.disconnect();
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
            try {
                Boolean checkStockName = databaseHelper.checkStockName(longName);
                if(checkStockName == false) {
                    Toast toastExist = Toast.makeText(MainActivity.this, "The stock is already stored in the app. ", Toast.LENGTH_SHORT);
                    toastExist.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 600);
                    toastExist.show();
                    symbol.setText("");
                } else if (checkStockName == true){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBConnect.StockName, longName);
                    contentValues.put(DBConnect.StockMarketPrice, regularMarketPrice);
                    contentValues.put(DBConnect.StockChange, regularMarketChange);
                    contentValues.put(DBConnect.StockSymbol, stockSymbol);
                    getContentResolver().insert(Provider.CONTENT_URI, contentValues);
                    mAdapter.swapCursor(getContentResolver().query(Provider.CONTENT_URI, null, null, null, null));
                    mRecyclerView.setAdapter(mAdapter);
                    symbol.setText("");
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, "Stock does not exist. ", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 600);
                    toast.show();
                    symbol.setText("");
                }
            } catch(IllegalArgumentException e) {
                Toast toastNotExist = Toast.makeText(getApplicationContext(), "Stock does not exist. ", Toast.LENGTH_SHORT);
                toastNotExist.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 600);
                toastNotExist.show();
                e.printStackTrace();
                symbol.setText("");
            }
        }
    }
}