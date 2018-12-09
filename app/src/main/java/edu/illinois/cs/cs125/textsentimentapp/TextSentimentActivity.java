package edu.illinois.cs.cs125.textsentimentapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class TextSentimentActivity extends AppCompatActivity {

    private static final String SENTIMENT_ANALYSIS_URL =
            "https://eastus2.api.cognitive.microsoft.com/text/analytics/v2.0/sentiment";

    // BEFORE COMPILING APP, SET API KEY
    // NEVER COMMIT KEY TO GIT
    private static final String API_KEY = "hunter2";

    private final RequestQueue requestQueue = Volley.newRequestQueue(this);

    final TextView textView = (TextView) findViewById(R.id.sentiment);
    final EditText editText = (EditText) findViewById(R.id.editText);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_sentiment);
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
                Map<String, String> headers =  super.getHeaders();
                headers.put("Ocp-Apim-Subscription-Key", API_KEY);
                return headers;
            }
        };

        requestQueue.add(textAnalysisRequest);
    }
}
