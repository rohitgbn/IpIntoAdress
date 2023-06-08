package com.social.ipintoaddress;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://api.ipify.org";
    private static final String Api_DATA_URL = "https://script.google.com/macros/s/AKfycbwhSYyColEnxhxKswwfJ21UxFhX68fStsCRLSvFrn5Dov4V3XkK6ZrzYgufTKnaweA/exec?ip=";
    TextView publicIp,ipCountry,ipCity,ipTimeZone,ipIsp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        publicIp = findViewById(R.id.ip_address_id);
        ipCountry = findViewById(R.id.ip_country_id);
        ipCity = findViewById(R.id.ip_city_id);
        ipTimeZone = findViewById(R.id.ip_timezone_id);
        ipIsp = findViewById(R.id.ip_isp_id);

        // Create a single-threaded executor
        Executor executor = Executors.newSingleThreadExecutor();

        // Execute the fetchPublicIpAddressRunnable using the executor
        executor.execute(fetchPublicIpAddressRunnable);
    }

    private final Runnable fetchPublicIpAddressRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    final String ipAddress = response.toString().trim();
                    String finalUrl = Api_DATA_URL.concat(ipAddress);
                    URL url1 = new URL(finalUrl);
                    HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
                    connection1.setRequestMethod("GET");
                    int responseCode1 = connection1.getResponseCode();
                    if (responseCode1 == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader1 = new BufferedReader(new InputStreamReader(connection1.getInputStream()));
                        StringBuilder response1 = new StringBuilder();
                        String line1;

                        while ((line1 = reader1.readLine()) != null) {
                            response1.append(line1);
                        }
                        reader1.close();

                        final String jsonResult = response1.toString().trim();

                        // Parse the JSON response using Gson and map it to the IpApiResponse object
                        Gson gson = new Gson();
                        IpApiResponse ipApiResponse = gson.fromJson(jsonResult, IpApiResponse.class);

                        // Update the public IP TextView with the retrieved public IP address and display other details
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                publicIp.setText(ipAddress);
                                ipCountry.setText(ipApiResponse.getCountry());
                                ipCity.setText(ipApiResponse.getCity());
                                ipTimeZone.setText(ipApiResponse.getTimezone());
                                ipIsp.setText(ipApiResponse.getIsp());
                                Log.d("Public IP", ipAddress);
                                // Access the parsed data from ipApiResponse and update UI accordingly
                                Log.d("Country", ipApiResponse.getCountry());
                                Log.d("City", ipApiResponse.getCity());
                                // ...
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Handle error case
                Log.e("Public IP", "Failed to fetch public IP address");
            }
        }
    };
}
