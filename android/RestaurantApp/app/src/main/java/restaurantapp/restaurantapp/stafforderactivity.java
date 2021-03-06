package restaurantapp.restaurantapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class stafforderactivity extends ListActivity {
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
    String ID2look4 = "565a9ff2dcdbac2015e7b84a";
    String IDfoundObjtxt, oitemname, oitemprice;
    // int
    int index = 0;

    String emailtxt;

    FloatingActionButton staffback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stafforderlayout);

        emailtxt = getIntent().getExtras().getString("email");
        new GetShoppingBasket().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     **/
    private class GetShoppingBasket extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog as data is fetched
            pDialog = new ProgressDialog(stafforderactivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("email", emailtxt));

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET, nameValuePairs );

            if (jsonStr != null) {
                try {
                    String logPrefix = getClass().getEnclosingClass().getName();
                    Boolean compareresult = false;
                    // initialize a JSONObject from the GET response entity
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // search within found JSONObject to get the array with (specific name)
                    orderIDArray = jsonObj.getJSONArray("orders");

                    for(int n = 0; n < orderIDArray.length(); n++)
                    {
                        JSONObject orderObject = orderIDArray.getJSONObject(n);
                        Log.d("orderObject: ", "> " + orderObject.toString());

                        JSONArray menuItemsArray =  orderObject.getJSONArray("menuItems");
                        Log.d("menuItemsArray: ", "> " + menuItemsArray.toString());
                        for (int m = 0; m < menuItemsArray.length(); m++) {
                            JSONObject menuItem = menuItemsArray.getJSONObject(m);
                            String oitemname =  menuItem.getString("name");
                            String oitemprice =  menuItem.getString("price");
                            Log.d("oitemname: ", "> " + oitemname);
                            Log.d("oitemprice: ", "> " + oitemprice);
                            order_tmpmap.put("name", oitemname);
                            order_tmpmap.put("price", oitemprice);
                            sbasketlist.add(order_tmpmap);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
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
                    stafforderactivity.this, sbasketlist,

                    R.layout.stafforderlistlayout, new String[]{"name", "price"},
                    new int[]{R.id.foodname, R.id.foodprice}

            );

            setListAdapter(adapter);
        }
    }


}
