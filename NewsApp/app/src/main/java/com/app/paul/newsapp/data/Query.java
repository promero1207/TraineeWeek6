package com.app.paul.newsapp.data;

import android.text.TextUtils;
import android.util.Log;

import com.app.paul.newsapp.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for creating json and making https request
 */
final class Query {
    private static final String LOG_TAG = Query.class.getSimpleName();

    //constructor
    private Query() {
    }

    /**
     * Method for getting News List filled with json data
     * @param path path of the query
     * @return list of news
     */
    static List<News> getNewsData(String path){
        URL url = createUrl(path);
        String jsonParsed = null;
        try{
            jsonParsed = makeHttpRequest(url);
        } catch (IOException ignored){
            Log.e(LOG_TAG, "Json Ignored ");
        }
        return extractJson(jsonParsed);
    }

    /**
     * Method for creating url
     * @param stringUrl url string
     * @return url created
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Method for making http request
     * @param url url
     * @return full json string read from input stream
     * @throws IOException can throw exception if no connection can be reached
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * method for reading from stream
     * @param inputStream stream
     * @return string in utf-8 format
     * @throws IOException exception if strin cant be build
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * method for parsing json
     * @param json json string
     * @return list of news parsed from json
     */
    private static List<News> extractJson(String json) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        List<News> list = new ArrayList<>();

        try{
            JSONObject fullJson = new JSONObject(json);
            JSONObject response = fullJson.getJSONObject("response");
            JSONArray newsArray = response.getJSONArray("results");

            if(newsArray.length() != 0) {
                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject itemNewsJs = newsArray.getJSONObject(i);
                    String section = itemNewsJs.getString("sectionName");

                    JSONObject fields = itemNewsJs.getJSONObject("fields");
                    String headline = fields.getString("headline");

                    String thumbnail1 = "";
                    if (fields.has("thumbnail")) {
                        thumbnail1 = fields.getString("thumbnail");
                    }

                    String body = fields.getString("bodyText");

                    String web = "";
                    if (itemNewsJs.has("webUrl")) {
                        web = itemNewsJs.getString("webUrl");
                    }

                    String newsId = itemNewsJs.getString("id");

                    list.add(new News(newsId, headline, section, thumbnail1, body, web, 0));
                }
            }

        }catch (JSONException e){
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return list;
    }
}
