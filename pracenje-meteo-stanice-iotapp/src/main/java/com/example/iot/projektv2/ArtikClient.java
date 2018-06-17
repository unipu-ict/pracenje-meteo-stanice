package com.example.iot.projektv2;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ArtikClient {

    private static String TAG = "Artik";

    private static ArtikClient me;
    private Context ctx;
    private RequestQueue queue;

    private static final String ARTIK_URL = "https://api.artik.cloud/v1.1/messages";//URL na koji se salju podaci
    private String deviceId;//Identifikacijski broj uredaja na cloudu
    private String token;//Token broj uredaja na cloudu

    //Konstruktor
    private ArtikClient(Context ctx, String deviceId, String token) {
        this.ctx = ctx;
        this.deviceId = deviceId;
        this.token = token;
        createQueue();
    }

    public static final ArtikClient getInstance(Context ctx, String deviceId, String token) {
        if (me == null)
            me = new ArtikClient(ctx,deviceId,token);
        return me;
    }

    //Stvaranje "reda" za slanje podataka
    private void createQueue() {
        if (queue == null)
            queue = Volley.newRequestQueue(ctx.getApplicationContext());
    }

    //sendData metoda koja salje podatke na cloud servis
    public void sendData(final double temp, final double press) {
        StringRequest request = new StringRequest(Request.Method.POST,
                ARTIK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response ["+response+"]");//Povratna poruka od cloud servisa
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {

            @Override
            //Odredivanje izgleda tijela poruke koja sadrzi podatke
            public byte[] getBody() throws AuthFailureError {
                Log.d(TAG, "Creating body...");

                try {
                    JSONObject jsonRequest = new JSONObject();
                    jsonRequest.put("sdid", deviceId);
                    jsonRequest.put("ts", System.currentTimeMillis());
                    JSONObject data = new JSONObject();
                    data.put("temp", temp);
                    data.put("Pressure", press);
                    jsonRequest.put("data", data);

                    String sData = jsonRequest.toString();
                    Log.d(TAG, "Body:" + sData);

                    return sData.getBytes();
                }
                catch (JSONException jsoe) {
                    jsoe.printStackTrace();

                }
                return "".getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.d(TAG, "Get headers..");
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }
}
