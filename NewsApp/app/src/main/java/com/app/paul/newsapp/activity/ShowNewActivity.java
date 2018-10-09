package com.app.paul.newsapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.paul.newsapp.R;
import com.app.paul.newsapp.adapter.AdapterRvMainNews;

import static com.app.paul.newsapp.fragments.FragmentBase.SHOW_BODY;
import static com.app.paul.newsapp.fragments.FragmentBase.SHOW_IMG;
import static com.app.paul.newsapp.fragments.FragmentBase.SHOW_TITLE;
import static com.app.paul.newsapp.fragments.FragmentBase.SHOW_WEB;

/**
 * Activity for showing new information
 */
public class ShowNewActivity extends AppCompatActivity {
    //fields
    String title = "";
    String body = "";
    String img_url = "";
    String web_url = "";

    //on create method for inflating view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_new);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            title = bundle.getString(SHOW_TITLE);
            body = bundle.getString(SHOW_BODY);
            img_url = bundle.getString(SHOW_IMG);
            web_url = bundle.getString(SHOW_WEB);
        }

        TextView textviewTitle = findViewById(R.id.title_new);
        TextView textviewBody = findViewById(R.id.content_new);
        final TextView web = findViewById(R.id.web);
        ImageView img = findViewById(R.id.img_new);

        textviewTitle.setText(title);
        textviewBody.setText(body);
        if(!"".equals(img_url)) {
            img.setVisibility(View.INVISIBLE);
            new AdapterRvMainNews.DownloadImageTask(img).execute(img_url);
        }
        else{
            img.setVisibility(View.GONE);
        }

        if(!"".equals(web_url)){
            web.setText(web_url);
        }
        else {
            web.setVisibility(View.GONE);
        }
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebPage(web.getText().toString());
            }
        });

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    //metho for open web url on browser
    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //method for back arrow pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
