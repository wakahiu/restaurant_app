package restaurantapp.restaurantapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class shoppingbasketactivity extends ListActivity {
    // initialize bundle that will be sent to the basket
    //Bundle passthisback2menu = new Bundle();

    // progress dialog is initiated while fetching data from server
    private ProgressDialog pDialog;

    // server URL
    private static String url = "http://dinnermate.azurewebsites.net/api/v1.0/order";

    // JSONArrays
    JSONArray orderIDArray = null;
    JSONArray IDmenuitems = null;

    // JSONObjs
    JSONObject IDfoundObj;
    JSONObject IDorder;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> sbasketlist = new ArrayList<HashMap<String, String>>();
    // tmp hashmap for this specific order
    HashMap<String, String> order_tmpmap = new HashMap<String, String>();
    // Strings
    //String ID2look4 = "565a9ff2dcdbac2015e7b84a";
    String ID2look4 = "";
    String counterlimit = null;
    String IDfoundObjtxt, oitemname, oitemprice, ordertotalcosttxt,restaurant4menu;
    // int
    int index = 0;
    int oitempriceint = 0;
    int oitempriceinttotal = 0;
    int oitempricehold = 0;
    int counterint = 0;

    // buttons
    Button purchasebtn, cancelorderbtn;
    // TextView
    TextView ordertotalcost,sbasketmaintitle, ordertotaltxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoppingbasketlayout);

        ListAdapter adapter = new SimpleAdapter(
                shoppingbasketactivity.this, sbasketlist,

                R.layout.sbasketlistlayout, new String[]{"name", "price"},
                new int[]{R.id.foodname, R.id.foodprice}
        );

        setListAdapter(adapter);

        // intialize font
        final Typeface sbasketfont = Typeface.createFromAsset(getAssets(),"txtfont1.ttf");

        // pull things from Intent that had putExtra
        Intent frommenuintent = this.getIntent();
        // get the value of key from intent's extra
        if (frommenuintent != null){
            counterlimit = getIntent().getStringExtra("jvalue");
            //restaurant4menu = getIntent().getStringExtra("restaurantname2basket");
        }
        // put the restaurant name into bundle so can be put as Extra when cancel is pressed.
        //passthisback2menu.putString("chosenrestaurant", restaurant4menu);

        new GetShoppingBasket().execute();

        sbasketmaintitle = (TextView)findViewById(R.id.sbaskettitle);
        sbasketmaintitle.setTypeface(sbasketfont);
        ordertotalcost = (TextView)findViewById(R.id.totalcost);
        ordertotalcost.setTypeface(sbasketfont);
        ordertotaltxt = (TextView)findViewById(R.id.ordertotal);
        ordertotaltxt.setTypeface(sbasketfont);

        // buttons
        purchasebtn = (Button)findViewById(R.id.purchase);
        purchasebtn.setTypeface(sbasketfont);
        //cancelorderbtn = (Button) findViewById(R.id.cancel);
        //cancelorderbtn.setTypeface(sbasketfont);

        ordertotalcost.setText(String.valueOf(oitempriceinttotal));

        purchasebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ordertotalcosttxt = ordertotalcost.getText().toString();

                if (ordertotalcosttxt.equals("0"))
                {
                    Toast.makeText(shoppingbasketactivity.this, "Please add order!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent purchaseintent = new Intent(shoppingbasketactivity.this, paymentactivity.class);
                    startActivity(purchaseintent);
                    finish();
                }
            }
        });
        /*cancelorderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cancelorderintent = new Intent(shoppingbasketactivity.this, MenuActivity.class);
                cancelorderintent.putExtras(passthisback2menu);
                startActivity(cancelorderintent);
                finish();
            }
        });*/
    }

    /**
     * Async task class to get json by making HTTP call
     **/
    private class GetShoppingBasket extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog as data is fetched
            pDialog = new ProgressDialog(shoppingbasketactivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.e("Response: ", "> " + jsonStr);

            // change counterlimit to int
            if ( counterlimit != null )
                counterint = Integer.parseInt(counterlimit);
            //
            Log.d("counterint", String.valueOf(counterint));
            //
            if (jsonStr != null) {
                try { //counterint = 10;
                    for (int jk = 1; jk < counterint + 1; jk++) {
                        ID2look4 = getIntent().getStringExtra("chosenorderID" + jk);
                        //
                        Boolean compareresult = false;
                        // initialize a JSONObject from the GET response entity
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // search within found JSONObject to get the array with (specific name)
                        orderIDArray = jsonObj.getJSONArray("orders");
                        //
                        Log.d("orderID", orderIDArray.toString());
                        //
                        for (int i = 0; i < orderIDArray.length(); i++) {
                            IDfoundObj = orderIDArray.getJSONObject(i);
                            //
                            Log.d("orderID Array", String.valueOf(orderIDArray.length()));
                            //
                            IDfoundObjtxt = IDfoundObj.get("_id").toString();

                            //
                            Log.d("orderID Array as String", IDfoundObjtxt);
                            Log.d("ArrayString2Compare", ID2look4 + "");
                            //
                            if (IDfoundObjtxt.equals(ID2look4)) {
                                compareresult = true;
                                index = i;
                            }
                        }
                        // if desired ID object is found
                        if (compareresult.equals(true)) {
                            IDorder = orderIDArray.getJSONObject(index);
                            IDmenuitems = IDorder.getJSONArray("menuItems");
                            //IDmenuitems = IDfoundObj.getJSONArray("menuItems");
                            //
                            Log.d("ID order Obj", IDorder.toString());
                            Log.d("ID menuitem array", IDmenuitems.toString());
                            //

                            for (int j = 0; j < IDmenuitems.length(); j++) {
                                JSONObject menuitemobjs = IDmenuitems.getJSONObject(j);
                                //
                                Log.d("menuItems Obj", menuitemobjs.toString());
                                //
                                oitemname = menuitemobjs.get("name").toString();
                                oitemprice = menuitemobjs.get("price").toString();
                                oitempriceint = menuitemobjs.getInt("price");
                                //
                                Log.d("oitemname", oitemname);
                                Log.d("oitemprice", oitemprice);
                                // add value to each key
                                order_tmpmap.put("name", oitemname);
                                order_tmpmap.put("price", oitemprice);
                                // refresh total price of the order
                                oitempriceinttotal = oitempricehold + oitempriceint;
                                oitempricehold = oitempriceinttotal;

                                // add order_tmpmap to sbasket master list
                                sbasketlist.add(order_tmpmap);
                                //
                                Log.d("sbasketlist", sbasketlist.toString());
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } // catch JSONException here

            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            //}
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            // Updating parsed JSON data into ListView
            ListAdapter adapter = new SimpleAdapter(
                    shoppingbasketactivity.this, sbasketlist,

                    R.layout.sbasketlistlayout, new String[]{"name", "price"},
                    new int[]{R.id.foodname, R.id.foodprice}
            );
            //
            Log.d("oitempriceinttotal", String.valueOf(oitempriceinttotal));
            //
            ordertotalcost.setText(String.valueOf(oitempriceinttotal));
            setListAdapter(adapter);
        }
    }


}
