package com.example.android.filmesfamosos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private TextView mTitle;
    private TextView mOverview;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private ImageView mMoviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        this.mTitle = findViewById(R.id.tv_title);
        this.mOverview = findViewById(R.id.tv_overview);
        this.mReleaseDate = findViewById(R.id.tv_release_date);
        this.mVoteAverage = findViewById(R.id.tv_rate_average);
        this.mMoviePoster = findViewById(R.id.iv_movie_poster);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)){
                Movie movieDetailed = intent.getParcelableExtra(Intent.EXTRA_TEXT);

                this.mTitle.setText(movieDetailed.mTitle);
                Picasso.with(this).load(movieDetailed.mImgUrl).into(mMoviePoster);
                this.mOverview.setText(movieDetailed.mOverview);
                this.mReleaseDate.setText(movieDetailed.mReleaseDate);
                this.mVoteAverage.setText(String.valueOf(movieDetailed.mVoteAverage));
            }
        }
    }
}
