package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class chercherclient extends AppCompatActivity {
    EditText codeText, nomText, adressText, contactText;
    Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chercherclient);

        codeText = findViewById(R.id.codetext);
        nomText = findViewById(R.id.nomtext);
        adressText = findViewById(R.id.adressetext);
        contactText = findViewById(R.id.contactadresse);
        searchButton = findViewById(R.id.buttonsearch);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = codeText.getText().toString();
                new SearchClientTask().execute(code);
            }
        });
    }

    private class SearchClientTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String code = params[0];
            String phpScriptUrl = "http://10.0.2.2/exam/chercherclient"; // Replace with your actual PHP script URL

            try {
                URL url = new URL(phpScriptUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set the connection properties
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Build the request parameters
                String parameters = "code=" + code;

                // Send the request
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(parameters.getBytes());
                outputStream.flush();
                outputStream.close();

                // Get the response
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Update the EditText fields with the retrieved data
                    String responseString = response.toString();
                    String[] parts = responseString.split(";");
                    if (parts.length == 3) {
                        String name = parts[0];
                        String adress = parts[1];
                        String contact = parts[2];

                        return name + ";" + adress + ";" + contact;
                    } else {
                        return "Client not found";
                    }
                } else {
                    return "HTTP error: " + responseCode;
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle the result
            if (result.startsWith("Error:")) {
                Toast.makeText(chercherclient.this, result, Toast.LENGTH_SHORT).show();
            } else {
                String[] parts = result.split(";");
                if (parts.length == 3) {
                    String name = parts[0];
                    String adress = parts[1];
                    String contact = parts[2];

                    nomText.setText(name);
                    adressText.setText(adress);
                    contactText.setText(contact);
                } else {
                    Toast.makeText(chercherclient.this, result, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
