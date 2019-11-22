package com.example.fakefriendgps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class JoinGroupActivity extends AppCompatActivity {
    private static final String TAG = "JoinGroupActivity";
    private Button button, group1, group2, group3, leave;
    String jsonaddress = "https://api.myjson.com/bins/koeva";
    private int currentGroup = 0;
    public static final String BUNDLE_NAME = "Group";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JSONAsyncTask().execute(jsonaddress);
            }
        });
        group1 = findViewById(R.id.group1);
        group1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentGroup = 1;
            }
        });
        group2 = findViewById(R.id.group2);
        group2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentGroup = 2;
            }
        });
        group3 = findViewById(R.id.group3);
        group3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentGroup = 3;
            }
        });
        leave = findViewById(R.id.leave);
        leave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentGroup = 0;
            }
        });


    }

    private class JSONAsyncTask extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection httpURLConnection = null;

            try{
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStreamReader reader = new InputStreamReader(httpURLConnection.getInputStream());
                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);

            try{
                JSONArray array = new JSONArray(s);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                ArrayList<Person> group = new ArrayList<>();
                for(int i = 0; i < array.length(); i++){
                    JSONObject object = array.getJSONObject(i);
                    String name = array.getJSONObject(i).getString("name");
                    Log.d(TAG, "onPostExecute: " + name);
                    int groupNumber = object.getInt("group");
                    JSONObject address = object.getJSONObject("address");
                    JSONObject geo = address.getJSONObject("geo");
                    double longitude = Double.parseDouble(geo.getString("lng"));
                    double latitude = Double.parseDouble(geo.getString("lat"));
                    Log.d(TAG, "onPostExecute: " + longitude + ", " + latitude + ", GRP = " + groupNumber);

                    switch (currentGroup){
                        case 0:
                            group.clear();
                            break;
                        case 1:
                            if(groupNumber == 1)
                                group.add(new Person(name, latitude, longitude));
                            break;
                        case 2:
                            if(groupNumber == 2)
                                group.add(new Person(name, latitude, longitude));
                            break;
                        case 3:
                            if(groupNumber == 3)
                                group.add(new Person(name, latitude, longitude));
                            break;
                    }

                }
                bundle.putParcelableArrayList("Group", group);
                intent.putExtra("Bundle", bundle);
                setResult(RESULT_OK, intent);
                finish();

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
