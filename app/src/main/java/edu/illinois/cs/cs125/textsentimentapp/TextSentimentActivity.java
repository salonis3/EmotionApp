package edu.illinois.cs.cs125.textsentimentapp;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TextSentimentActivity extends AppCompatActivity {

    private static final String SENTIMENT_ANALYSIS_URL =
            "https://eastus2.api.cognitive.microsoft.com/text/analytics/v2.0/sentiment";

    // BEFORE COMPILING APP, SET API KEY
    // NEVER COMMIT KEY TO GIT
    private static final String API_KEY = "4bc070402f1a41f8aec5dc56a78bb90d";

    //private final RequestQueue requestQueue = Volley.newRequestQueue(this);


    private TextView textView;
    private EditText editText;
    private ConstraintLayout constraintLayout;
    RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_sentiment);

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        textView = (TextView) findViewById(R.id.sentiment);
        editText = (EditText) findViewById(R.id.editText);
        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);

// Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

// Start the queue
        mRequestQueue.start();
    }

    public void onClick(View view) {
        getTextSentiment(editText.getText().toString());
    }

    /**
     * Takes user input and determines mood.
     * @param text User text.
     * @return Sentiment score for user text [0,1].
     */
    private void getTextSentiment(final String text) {
        JSONObject request = new JSONObject();
        try {
            request.put("documents", new JSONArray()
                    .put(new JSONObject()
                            .put("language", "en")
                            .put("id", "1")
                            .put("text", text)
                    )
            );
        } catch (JSONException e) {}

        // TODO: get text sentiment from azure
        JsonObjectRequest textAnalysisRequest = new JsonObjectRequest(
                Request.Method.POST,
                SENTIMENT_ANALYSIS_URL,
                request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        double sentiment;
                        try {
                            sentiment = response.getJSONArray("documents")
                                    .getJSONObject(0)
                                    .optDouble("score");
                        } catch (JSONException e) {
                            sentiment = Double.NaN;
                        }
                        textView.setText(Double.toString(sentiment));
                        if (sentiment >= 0.9) {
                            constraintLayout.setBackgroundColor(Color.rgb(225,0,159));
                        }
                        if (sentiment < 0.3) {
                            constraintLayout.setBackgroundColor(Color.rgb(17,98,188));
                        }
                        if (sentiment < 0.9 && sentiment > 0.3) {
                            constraintLayout.setBackgroundColor(Color.rgb(225, 225, 0));
                        }


                    }



                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO:
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers =  new HashMap<String, String>();
                headers.put("Ocp-Apim-Subscription-Key", API_KEY);
                return headers;
            }
        };

        mRequestQueue.add(textAnalysisRequest);

    }
}
