package com.example.magdalena.chuckandcats;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class MainActivity extends AppCompatActivity {

    private static final String SUCCESS = "success";
    private int width, height;
    ProgressBar progressBar;

    Future<String> downloading;

    // auto-generated
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        progressBar = (ProgressBar) findViewById(R.id.progress);
    }


    public void chuckNorrisClick(View view) {
        if (downloading != null && !downloading.isCancelled()) {
            resetDownload();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(300);

        downloading = Ion.with(this)
                .load("http://api.icndb.com/jokes/random?limitTo=[nerdy]")
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        System.out.println("" + downloaded + " / " + total);
                        progressBar.setProgress((int) (long) downloaded);
                    }
                })
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        resetDownload();
                        if (e != null) {
                            Toast.makeText(MainActivity.this, "Error downloading joke", Toast.LENGTH_LONG).show();
                            return;
                        }
                        processChuckNorrisData(result);
                    }
                });
    }
//

    public void catClick(View view) {
        if (downloading != null && !downloading.isCancelled()) {
            resetDownload();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(3000);

        downloading = Ion.with(this)
                .load("http://thecatapi.com/api/images/get?results_per_page=10&format=xml&size=med")
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        System.out.println("" + downloaded + " / " + total);
                        progressBar.setProgress((int) (long) downloaded);
                    }
                })
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        resetDownload();
                        if (e != null) {
                            Toast.makeText(MainActivity.this, "Error downloading cats", Toast.LENGTH_LONG).show();
                            return;
                        }
                        processCatData(result);
                    }
                });
    }

    private void resetDownload() {
        // cancel any pending download
        downloading.cancel();
        downloading = null;

        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);
    }

    /*
     * The JSON data uses the following format:
     *
     *  {
     *   "type": "success",
     *   "value": {
     *              "id": 496,
     *              "joke": "Chuck Norris went out of an infinite loop.",
     *              "categories": ["nerdy"]
     *            }
     *  }
     */

    private void processChuckNorrisData(String data) {
        try {
            JSONObject json = new JSONObject(data);
            if (json.getString("type").equals(SUCCESS)) {
                JSONObject value = json.getJSONObject("value");
                String joke = value.getString("joke");
                JSONArray categories = value.getJSONArray("categories");

                joke = joke.replace("&amp;", "&").replace("&quot;", "\"");

                TextView output = (TextView) findViewById(R.id.output);
                output.setText(joke);
            } else {
                Log.e("JSON", "Json failed");
            }
        } catch (JSONException e) {
            Log.wtf("json", e);
        }
    }




    /*
     * The JSON data will be in the following format:
     *
     * {
     *   "response": {
     *     "data": {
     *         "images": {
     *             "image": [
     *                {"url":"http:\/\/24.media.tumblr.com\/tumblr_luw2y2MCum1qbdrypo1_500.jpg","id":"d40","source_url":"http:\/\/thecatapi.com\/?id=d40"},
     *                {"url":"http:\/\/25.media.tumblr.com\/tumblr_m4rwp4gkQ11r6jd7fo1_400.jpg","id":"e85","source_url":"http:\/\/thecatapi.com\/?id=e85"},
     *  ...
     * }
     */

    private void processCatData(String data) {
        try {
            XmlToJson xmlToJson = new XmlToJson.Builder(data).build();
            JSONObject json = xmlToJson.toJson();
            JSONArray jsonArray = json
                    .getJSONObject("response")
                    .getJSONObject("data")
                    .getJSONObject("images")
                    .getJSONArray("image");

            GridLayout gridLayout = (GridLayout) findViewById(R.id.grid);
            gridLayout.removeAllViews();

//            String url = jsonArray.getJSONObject(0).getString("url");
//
//            ImageView imageView = (ImageView) findViewById(R.id.outputCat);
//            Picasso.with(this)
//                    .load(url)
//                    .into(imageView);


            for(int i = 0; i < jsonArray.length(); i++) {
                String url = jsonArray.getJSONObject(i).getString("url");
                final String sourceUrl = jsonArray.getJSONObject(i).getString("source_url");

                ImageView imageView = new ImageView(this);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                imageView.setLayoutParams(params);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sourceUrl));
                        startActivity(browserIntent);
                    }
                });

                int padding = (int) (8 / getResources().getDisplayMetrics().density);

                int picSize = (width - padding * 2) / 2;

                Picasso.with(this)
                        .load(url)
                        .resize(picSize, picSize)
                        .centerCrop()
                        .into(imageView);
                gridLayout.addView(imageView);
            }
        } catch (JSONException e) {
            Log.wtf("JSON", "Json failed");
        }
    }

}