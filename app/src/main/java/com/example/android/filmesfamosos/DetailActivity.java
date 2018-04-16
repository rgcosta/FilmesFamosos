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

        this.mTitle = (TextView) findViewById(R.id.tv_title);
        this.mOverview = (TextView) findViewById(R.id.tv_overview);
        this.mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        this.mVoteAverage = (TextView) findViewById(R.id.tv_rate_average);
        this.mMoviePoster = (ImageView) findViewById(R.id.iv_movie_poster);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT + "_title")){
                String title = intent.getStringExtra(Intent.EXTRA_TEXT + "_title");
                this.mTitle.setText(title);
            }
            if (intent.hasExtra(Intent.EXTRA_TEXT + "_poster")){
                String posterUrl = intent.getStringExtra(Intent.EXTRA_TEXT + "_poster");
                Picasso.with(this).load(posterUrl).into(mMoviePoster);
            }
            if (intent.hasExtra(Intent.EXTRA_TEXT + "_overview")){
                String overview = intent.getStringExtra(Intent.EXTRA_TEXT + "_overview");
                this.mOverview.setText(overview);
            }
            if (intent.hasExtra(Intent.EXTRA_TEXT + "_releaseDate")){
                String releaseDate = intent.getStringExtra(Intent.EXTRA_TEXT + "_releaseDate");
                this.mReleaseDate.setText(releaseDate);
            }
            if (intent.hasExtra(Intent.EXTRA_TEXT + "_voteAverage")){
                double voteAverage = intent.getDoubleExtra(Intent.EXTRA_TEXT + "_voteAverage", -1);
                this.mVoteAverage.setText(String.valueOf(voteAverage));
            }
        }
    }
}
