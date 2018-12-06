package edu.illinois.cs.cs125.textsentimentapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TextSentimentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_sentiment);
    }

    /**
     * Takes user input and determines mood.
     * @param text User text.
     * @return Sentiment score for user text [0,1].
     */
    private double getTextSentiment(String text) {
        // TODO: get text sentiment from azure
        return 0.0;
    }
}
