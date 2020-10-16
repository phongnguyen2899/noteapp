package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText txtusername;
    EditText txtpassword;
    Button btnlogin;
    TextView tvforgot,tvtest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        innit();
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load_text();
            }
        });

    }
    //khoi tao
    public void innit(){
        txtusername=(EditText) findViewById(R.id.edtusername);
        txtpassword=(EditText) findViewById(R.id.edtusername);
        btnlogin=(Button) findViewById(R.id.btnlogin);
        tvforgot=(TextView) findViewById(R.id.tvforgot);
        tvtest=(TextView) findViewById(R.id.tvtest);
    }
    //check internet
    private boolean checkInternetConnection() {
        // Get Connectivity Manager
        ConnectivityManager connManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Details about the currently active default data network
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            Toast.makeText(this, "No default network is currently active", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!networkInfo.isConnected()) {
            Toast.makeText(this, "Network is not connected", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!networkInfo.isAvailable()) {
            Toast.makeText(this, "Network not available", Toast.LENGTH_LONG).show();
            return false;
        }
        Toast.makeText(this, "Network OK", Toast.LENGTH_LONG).show();
        return true;
    }

    void load_text()
    {
        if (checkInternetConnection())
        {

            String webUrl = "https://192.168.0.101:45456/api/accounts/"+txtusername.getText().toString()+"/"+txtpassword.getText().toString()+"";

            // Create a task to download and display image.
            DownloadJsonTask task = new DownloadJsonTask(this.tvtest);

            // Execute task (Pass imageUrl).
            task.execute(webUrl);
        }
    }
    //post data
    public class DownloadJsonTask
            // AsyncTask<Params, Progress, Result>
            extends AsyncTask<String, Void, String> {

        public  TextView tvtest;
        public DownloadJsonTask(TextView context)  {
            this.tvtest=context;
        }

        @Override
        protected String doInBackground(String... params) {
            String textUrl = params[0];

            InputStream in = null;
            BufferedReader br= null;
            try {
                URL url = new URL(textUrl);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                int resCode = httpConn.getResponseCode();

                if (resCode == HttpURLConnection.HTTP_OK) {
                    in = httpConn.getInputStream();
                    br= new BufferedReader(new InputStreamReader(in));

                    StringBuilder sb= new StringBuilder();
                    String s= null;
                    while((s= br.readLine())!= null) {
                        sb.append(s);
                        sb.append("\n");
                    }
                    return sb.toString();
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Close
            }
            return null;
        }

        // When the task is completed, this method will be called
        // Download complete. Lets update UI
        @Override
        protected void onPostExecute(String result) {
            if(result  != null){
                try {
                    JSONArray arr= new JSONArray(result);
                    //lap va them vao arrayadapter
                        JSONObject objcountry= arr.getJSONObject(0);
                        //them
                    String rs=objcountry.getString("result");
                    tvtest.setText(rs);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else{
                Log.e("MyMessage", "Failed to fetch data!");
            }
        }
    }
}