package com.social.ipintoaddress;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity2 extends AppCompatActivity {

    private static final String API_URL = "https://api.ipify.org";
    private static final String API_DATA_URL = "http://ip-api.com/json/";
    TextView publicIp, ipCountry, ipCity, ipTimeZone, ipIsp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        publicIp = findViewById(R.id.ip_address_id);
        ipCountry = findViewById(R.id.ip_country_id);
        ipCity = findViewById(R.id.ip_city_id);
        ipTimeZone = findViewById(R.id.ip_timezone_id);
        ipIsp = findViewById(R.id.ip_isp_id);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper()); // Handler for updating UI

        executor.execute(() -> {
            try {
                String ipAddress = fetchPublicIpAddress();
                String jsonResult = fetchIpDetails(ipAddress);

                Gson gson = new Gson();
                IpApiResponse ipApiResponse = gson.fromJson(jsonResult, IpApiResponse.class);

                handler.post(() -> {
                    publicIp.setText(ipAddress);
                    ipCountry.setText(ipApiResponse.getCountry());
                    ipCity.setText(ipApiResponse.getCity());
                    ipTimeZone.setText(ipApiResponse.getTimezone());
                    ipIsp.setText(ipApiResponse.getIsp());

                    Log.d("Public IP", ipAddress);
                    Log.d("Country", ipApiResponse.getCountry());
                    Log.d("City", ipApiResponse.getCity());
                });
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Public IP", "Failed to fetch public IP address");
                // Handle error case, display error message to the user, etc.
            }
        });
    }

    private String fetchPublicIpAddress() throws IOException {
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

            return response.toString().trim();
        }

        throw new IOException("Failed to fetch public IP address");
    }

    private String fetchIpDetails(String ipAddress) throws IOException {
        String finalUrl = API_DATA_URL.concat(ipAddress);
        URL url = new URL(finalUrl);
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

            return response.toString().trim();
        }

        throw new IOException("Failed to fetch IP details");
    }
}
