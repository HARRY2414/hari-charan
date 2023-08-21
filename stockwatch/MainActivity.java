package com.ger.stockwatch;

import static android.content.ContentValues.TAG;
import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener, View.OnLongClickListener{
    private RecyclerView recyclerView;
    private StockAdapter stockAdapter;
    private ArrayList<Stock> stockArray;
    private SwipeRefreshLayout swiper; // The SwipeRefreshLayout
    private RequestQueue mRequestQueue;
    private JSONArray jsonArray=new JSONArray();
    private String url = "https://cloud.iexapis.com/stable/ref-data/symbols?token=pk_c7e0cef12c884c23ac12be74993f67c5";
    private StockParser Parser;
    private int Symbolsparsed=0;

    private  SharedPreferences sharedPref ;
    private  SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Recyler view  display of  stocks and financial data
        recyclerView=findViewById(R.id.recycle);

        //Webview Component for  Stock webview


        //Main arraylist [holding Stock elements ] and adapter class for recycler view
        stockArray=new ArrayList<>();
        stockAdapter=new StockAdapter(stockArray,this);
        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Refresh layout
        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(this::doRefresh);

        //Data storage
        sharedPref=this.getPreferences(Context.MODE_PRIVATE);
        editor= sharedPref.edit();

        String Jsondata= sharedPref.getString("json.file","0");

        if(hasNetworkConnection()){
            sendAndRequestResponse();}
        else {
            if (Jsondata != null)
                LoadFromFile(Jsondata);
        }
    }

    private void LoadFromFile(String Jsondata) {
        try {
                JSONObject jo=new JSONObject(Jsondata);
                JSONArray Array=jo.getJSONArray("json.file");
                //PARSE JSON save all symbols and company names to Hash map
                Parser = new StockParser(Array);
                setloadData();
             }catch (JSONException e)
             {
                e.printStackTrace();
             }
    }
    //seting stocks from Json file
    private void  setloadData()
    {   String id;
        //Display 100 stocks
        for(int y=0;y<50;y++) {
            id = Integer.toString(y);
            String Jsonobj = sharedPref.getString(id, "0");
            if (Jsonobj != null) {
                try {
                    JSONObject jsonObject = new JSONObject(Jsonobj);
                    //symbol data from JSONobj response
                    String Symbolval = jsonObject.getString("symbol");
                    String Companyval = jsonObject.getString("companyName");
                    double change = jsonObject.getDouble("change");
                    double changepercntval = jsonObject.getDouble("changePercent");
                    double latestprice = jsonObject.getDouble("latestPrice");


                    change = Math.floor(change * 100) / 100;

                    latestprice = Math.floor(latestprice * 100) / 100;
                    changepercntval = Math.floor(changepercntval * 100) / 100;

                    Stock stock = new Stock(Symbolval, Companyval, latestprice, change, changepercntval);
                    //Add the symbol stock modal to arraylist
                    stockArray.add(stock);
                    Collections.sort(stockArray, new StockArraySort());
                    stockAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //Request stocks data api key
    private void sendAndRequestResponse() {
        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);
        //array Request initialized
        JsonArrayRequest JAR=new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                  jsonArray = response;
                    //Load stock data to Recycler view
                    loadstocks();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,"Error :" + error.toString());
            }
        });
        mRequestQueue.add(JAR);
    }
    //Fill the list of stocks with data from JSONARRAY request
    private void loadstocks() {
         Parser = new StockParser(jsonArray);
        //Save JSON to a file
        JSONObject json = new JSONObject();
        try {
            json.put("json.file", jsonArray);
            editor.putString("json.file",json.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for(int i=0;i<50;i++)
            { try
                {//Iterate Stocks symbols array and load each symbol to recylerview
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    loaddata(jsonObject);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
    }

    //Checking network connectivity  False=No network Connection
    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    //Financial data Parser
    private void loaddata(JSONObject jsonObject) {
        //empty symbol variable
        String Symbl=" ";
        try{
             Symbl=jsonObject.getString("symbol");
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        if (Symbl==" ") return; //invalid symbol value

        //Url request + APIKEY
        String url1="https://cloud.iexapis.com/stable/stock/"+Symbl+"/quote?token=pk_c7e0cef12c884c23ac12be74993f67c5";
        mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest Jsonobjrequest= new JsonObjectRequest(Request.Method.GET, url1,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //symbol data from JSONobj response
                    String Symbolval=response.getString("symbol");
                    String Companyval=response.getString("companyName");
                    double change=response.getDouble("change");
                    double changepercntval=response.getDouble("changePercent");
                    double latestprice=response.getDouble("latestPrice");

                    //save Json Data to file
                    String id=Integer.toString(Symbolsparsed);
                    editor.putString(id,response.toString());
                    editor.apply();
                    Symbolsparsed+=1;

                    latestprice=latestprice*Math.pow(10,2);
                    latestprice=Math.floor(latestprice);
                    latestprice=latestprice/Math.pow(10,2);

                    change=change*Math.pow(10,2);
                    change=Math.floor(change);
                    change=change/Math.pow(10,2);

                    changepercntval=changepercntval*100;
                    changepercntval=changepercntval*Math.pow(10,2);
                    changepercntval=Math.floor(changepercntval);
                    changepercntval=changepercntval/Math.pow(10,2);


                   Stock stock=new Stock(Symbolval,Companyval,latestprice,change,changepercntval);
                    //Add the symbol stock modal to arraylist
                    stockArray.add(stock);
                    Collections.sort(stockArray,new StockArraySort());
                    stockAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,"Error :" + error.toString());
            }
        });
        mRequestQueue.add(Jsonobjrequest);
    }


    private void doRefresh() {
        //check for network connectivity before arefresh
        if(!hasNetworkConnection())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setTitle("No NetWork Connection");
            builder1.setMessage("Symbols Cannot be displayed Without a Network Connection");
            builder1.show();
            swiper.setRefreshing(false);
            return;
        }
        for(Stock f : stockArray )
        {
            String url1="https://cloud.iexapis.com/stable/stock/"+f.getSymbol()+"/quote?token=pk_c7e0cef12c884c23ac12be74993f67c5";
            mRequestQueue = Volley.newRequestQueue(this);

            JsonObjectRequest Jsonobjrequest= new JsonObjectRequest(Request.Method.GET, url1,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String Symbolval=response.getString("symbol");
                        String Companyval=response.getString("companyName");
                        double change=response.getDouble("change");

                        double changepercntval=response.getDouble("changePercent");
                        double latestprice=response.getDouble("latestPrice");
                        latestprice=latestprice*Math.pow(10,2);
                        latestprice=Math.floor(latestprice);
                        latestprice=latestprice/Math.pow(10,2);

                        change=change*Math.pow(10,2);
                        change=Math.floor(change);
                        change=change/Math.pow(10,2);

                        changepercntval=changepercntval*100;
                        changepercntval=changepercntval*Math.pow(10,2);
                        changepercntval=Math.floor(changepercntval);
                        changepercntval=changepercntval/Math.pow(10,2);

                        Stock stock=new Stock(Symbolval,Companyval,latestprice,change,changepercntval);
                        f.setPrice(latestprice);
                        f.setChangePercentage(changepercntval);
                        f.setPriceChange(change);
                        stockAdapter.notifyDataSetChanged();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG,"Error :" + error.toString());
                }
            });
            mRequestQueue.add(Jsonobjrequest);
        }
        swiper.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {  // click listener called by ViewHolder clicks
        //check for network connectivity before arefresh
        if(!hasNetworkConnection())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setTitle("No NetWork Connection");
            builder1.setMessage("Internet connection is required to view website");
            builder1.show();
            return;
        }
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock st=stockArray.get(pos);
        String url = "https://www.marketwatch.com/investing/stock/" +st.getSymbol();

        Intent  intent=new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);

    }


    @Override
    public boolean onLongClick(View v) {
        // long click listener called by ViewHolder long clicks delete stock items
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock st=stockArray.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Stock");
        builder.setIcon(R.drawable.delete);
        builder.setMessage("Delete Stock Symbol " + st.getSymbol() + "'");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            stockArray.remove(pos);
            stockAdapter.notifyItemRemoved(pos);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {});
        builder.create().show();
        return false;
    }

    //Options Menu ::adding stocks to the stocks list
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return  true;
    }

    @Override
    public  boolean onOptionsItemSelected(@NonNull MenuItem item){
        //Choosing Menu Items
        //Adding STOCK
        int id=item.getItemId();
        switch (id)
        {
            case R.id.additem:

                if(!hasNetworkConnection())
                {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setTitle("No NetWork Connection");
                    builder1.setMessage("Symbols Cannot be Added Without a Network Connection");
                    builder1.show();
                    return true;
                }

                SearchStock();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Searching Stock symbol in Static Hashmap of all Stocks
    private void SearchStock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stock Selection");
        builder.setMessage("Enter a Stock Symbol");
        final EditText input=new EditText(MainActivity.this);
        input.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        builder.setView(input);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Edit Text for Query search
                String result=input.getText().toString();
                int Tot=Parser.Search(result);
                //if Symbol was not found in hashmap
                if (Tot==0)
                {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setTitle("Symbol Not Found "+ result);
                    builder1.setMessage("Data for Stock Symbol");
                    builder1.show();
                }
                //if one symbol match search query
                if (Tot==1)
                {
                    if(!checkduplicate(result))
                    {
                        AddStock(result);
                    }
                    else
                    {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                        builder2.setTitle("Duplicate Stock");
                        builder2.setIcon(R.drawable.danger);
                        builder2.setMessage("Stock Symbol "+result+" is already dislayed");
                        builder2.show();
                    }
                }
                //if query has many results
                //return a hashmap of results
                //display multiple options
                if (Tot>1) {
                    HashMap<String, String> Result = new HashMap<String, String>();
                    Result=Parser.Searchmap(result);
                    ArrayList<String> sum=new ArrayList<>();

                    for (HashMap.Entry<String, String> set : Result.entrySet())
                    {
                       sum.add(set.getKey()+" "+set.getValue());
                    }

                    AlertDialog.Builder builder3 = new AlertDialog.Builder(MainActivity.this);
                    builder3.setTitle("Make a Selection");
                    String[] options=new String[sum.size()];
                    final boolean checkitems[]=new boolean[sum.size()];

                    int num =0;
                    for(String str:sum)
                    {
                     options[num]=sum.get(num)   ;
                     checkitems[num]=false;
                     num+=1;
                    }
                    final List<String> colorlist=Arrays.asList(options);
                    builder3.setMultiChoiceItems( options,checkitems, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                            checkitems[i]=b;
                            //check[i]=b;

                            String curr=colorlist.get(i);
                            curr=curr.split(" ")[0];
                            if(!checkduplicate(curr)){
                                    AddStock(curr);
                                    dialogInterface.cancel();}
                            else
                            {
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                                builder2.setTitle("Duplicate Stock");
                                builder2.setIcon(R.drawable.danger);
                                builder2.setMessage("Stock Symbol "+result+" is already dislayed");
                                builder2.show();
                                dialogInterface.cancel();
                            }
                        }
                    }).setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    AlertDialog  builder4=builder3.create();
                    builder4.show();

                    }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    builder.show();
    }
    //Adding a  stock to list of stocks
    private void AddStock(String result) {
        String url1="https://cloud.iexapis.com/stable/stock/"+result+"/quote?token=pk_c7e0cef12c884c23ac12be74993f67c5";
        mRequestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest Jsonobjrequest= new JsonObjectRequest(Request.Method.GET, url1,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String Symbolval=response.getString("symbol");
                    String Companyval=response.getString("companyName");
                    double change=response.getDouble("change");

                    double changepercntval=response.getDouble("changePercent");
                    double latestprice=response.getDouble("latestPrice");
                    change=Math.floor(change*100)/100;
                    latestprice=Math.floor(latestprice*100)/100;
                    changepercntval=Math.floor(changepercntval*100)/100;
                    Stock stock=new Stock(Symbolval,Companyval,latestprice,change,changepercntval);
                    stockArray.add(stock);
                    Collections.sort(stockArray,new StockArraySort());
                    stockAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i(TAG,"Error :" + error.toString());
            }
        });

        mRequestQueue.add(Jsonobjrequest);


    }


//checks for duplicate of parameter.:: False when there is no duplicated in the system
    private boolean checkduplicate(String result) {
        for(Stock s: stockArray)
        {
            if(result.equals(s.getSymbol()))

                return true;
        }
        return false;

    }

}