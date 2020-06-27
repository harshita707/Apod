package com.harshita.nasaapod;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static String BASE_URL = "https://api.nasa.gov";
    public static String API_KEY = "Jna3pnDwyWSL1oo4n5wVnEA1FOHgPyoWkoAbrPCS";
    public static String TYPE = "apod";

    private String url;
    private String videoId;
    private ApiInterface myInterface;

    private ImageView imageView;
    private LinearLayout layout;
    private YouTubePlayerView youTubePlayerView;
    private TextView textViewTitle;
    private TextView textViewExplanation;
    private ImageButton imageButton;
    private ImageButton calendarButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image);
        layout = findViewById(R.id.layout);
        textViewExplanation = findViewById(R.id.image_explanation);
        textViewTitle = findViewById(R.id.image_title);
        imageButton = findViewById(R.id.zoom_button);
        calendarButton = findViewById(R.id.calender_button);

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date Picker");
            }
        });

        youTubePlayerView = findViewById(R.id.youtube_player_view);

        getLifecycle().addObserver(youTubePlayerView);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        myInterface = retrofit.create(ApiInterface.class);
        Call<Results> call = myInterface.pictureOfDay(TYPE, API_KEY);

        apiCall(call);




    }

    private void apiCall(Call<Results> call) {
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                Results results = response.body();

                textViewExplanation.setText(results.getExplanation());
                textViewTitle.setText(results.getTitle());

                if (results.getMediaType().equals("image")) {
                    url = results.getHdurl();
                    imageMedia();
                } else {
                    url = results.getUrl();
                    videoId = url.substring(url.lastIndexOf('/') + 1);
                    videoMedia();
                }


            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }


    public void imageMedia() {
        youTubePlayerView.setVisibility(View.GONE);
        imageButton.setImageResource(R.drawable.ic_baseline_zoom_in_24);
        Glide.with(getBaseContext()).load(url).into(imageView);

        //background
        /*Glide.with(getBaseContext()).load(url).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    layout.setBackground(resource);
                }
            }
        });*/

    }

    public void videoMedia() {
        imageView.setVisibility(View.GONE);
        imageButton.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull final YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);

            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

        Call<Results> call = myInterface.pictureOfDay(TYPE, API_KEY,fDate);
        apiCall(call);



    }
}