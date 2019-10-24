package com.example.sleeptimer.data.provider;

import android.content.Context;

import com.example.sleeptimer.data.QuoteModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class QuoteDataProvider {

    private ArrayList<QuoteModel> quoteModelArrayList;
    private Context context;

    public QuoteDataProvider(Context context) {
        this.context = context;
        quoteModelArrayList = new ArrayList<>();
    }

    private void parseJsonFileFromLocally() {
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset());
            for (int i = 0; i < jsonArray.length(); i++)
            {
                QuoteModel quoteModel = new QuoteModel();
                JSONObject jsonObjectQuote = jsonArray.getJSONObject(i);

                String content = jsonObjectQuote.getString("quote");
                String author = jsonObjectQuote.getString("author");

                quoteModel.setContent("\"" + content + "\"");
                quoteModel.setAuthor("(" + author + ")");
                quoteModelArrayList.add(quoteModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open("enterpreneur_quotes.json");       //TODO Json File  name from assets folder
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public QuoteModel getRandomQuote() {
        parseJsonFileFromLocally();
        return quoteModelArrayList.get(new Random().nextInt(quoteModelArrayList.size()));
    }
}
