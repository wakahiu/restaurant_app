package restaurantapp.restaurantapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

public class loginactivity extends Activity {
    EditText email,password;
    Button login,registerbutton,test;
    String emailtxt,passwordtxt;
    Integer responsecode;
    Boolean authenticated = false;

    private static final String TAG_EMAIL = "email";
    private static final String TAG_PASSWORD = "password";
    // server URL
    private static String url = "http://dinnermate.azurewebsites.net/api/v1.0/user/authenticate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginlayout);

        // User input texts
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        emailtxt = email.toString().toLowerCase();
        passwordtxt = password.toString();

        // Buttons
        login = (Button)findViewById(R.id.log_in_button);
        registerbutton = (Button)findViewById(R.id.register);

        // for db testing
        test = (Button)findViewById(R.id.test);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent restaurantlistintent = new Intent(loginactivity.this,restaurantactivity.class);
                startActivity(restaurantlistintent);
            }
        });

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerintent = new Intent(loginactivity.this,registeractivity.class);
                startActivity(registerintent);
            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent testintent = new Intent(loginactivity.this, testmodeactivity.class);
                startActivity(testintent);
            }
        });

    }

    /* Use HTTP POST to request credential check */
    private class PostUserCredential extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... arg0) {
            // Creating HTTP client
            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            HttpClient httpClient = new DefaultHttpClient(params);
            // Creating HTTP Post
            HttpPost httpPost = new HttpPost(url);

            try {
                // Initialize List
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                // add the new information into nameValuePairs
                nameValuePairs.add(new BasicNameValuePair(TAG_EMAIL, emailtxt));
                nameValuePairs.add(new BasicNameValuePair(TAG_PASSWORD, passwordtxt));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

                // Execute HTTP Request and return response
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity httpEntity = response.getEntity();
                responsecode = response.getStatusLine().getStatusCode();
                // Log the results for debugging  information
                Log.e("httpEntity",httpEntity.toString());
                Log.e("Status Code",responsecode.toString());

            } catch (UnsupportedEncodingException err) {
                // writing error to Log
                err.printStackTrace();
                Log.e("Unsupp Enc Except",err.toString());
            } catch (ClientProtocolException err) {
                // writing exception to log
                err.printStackTrace();
                Log.e("Client Prot Except",err.toString());
            } catch (IOException err) {
                // writing exception to log
                err.printStackTrace();
                Log.e("IO Exception",err.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if  (responsecode.equals(201)) {
                Toast.makeText(loginactivity.this, "Login successful!",Toast.LENGTH_LONG).show();
//                Intent forward2restaurantintent = new Intent(loginactivity.this,restaurantactivity.class);
//                startActivity(forward2restaurantintent);
            } else {
                Toast.makeText(loginactivity.this, "Failed registration. Please try again!",Toast.LENGTH_LONG).show();
            }
        }
    }
}
