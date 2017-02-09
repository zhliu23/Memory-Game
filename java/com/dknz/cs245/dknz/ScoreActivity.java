/***************************************************************
 * file: ScoreActivity.java
 * author: Zhen Liu, Kaythari Phon, Nam Huynh, Dulce Nava
 * class: CS 245 - Programming Graphical User Interfaces
 *
 * assignment: Android Project
 * date last modified: 11/29/2016
 *
 * purpose: this activity reads in the score saved from the phone
 * and displays it to the user.
 *
 ****************************************************************/
package com.dknz.cs245.dknz;

import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class ScoreActivity extends AppCompatActivity {

    private static MediaPlayer music;
    private Spinner spinner;
    private Button musicButton;
    private ArrayAdapter<CharSequence> adapter;
    private TextView score1, score2, score3, scoreType;
    private boolean musicPlaying;
    private String position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        scoreType = (TextView) findViewById(R.id.scoreType);
        score1 = (TextView) findViewById(R.id.score1);
        score2 = (TextView) findViewById(R.id.score2);
        score3 = (TextView) findViewById(R.id.score3);

        if(savedInstanceState == null) {
            music = MediaPlayer.create(this, R.raw.arcadefunnk);
            music.setLooping(true);

            Bundle b = getIntent().getExtras();
            if (b != null)
                musicPlaying = b.getBoolean("musicPlaying");
            if (musicPlaying)
                music.start();
        } else {
            musicPlaying = (boolean) savedInstanceState.get("musicPlaying");
            position = (String) savedInstanceState.get("position");
            drawHighScore(position);
            if(music == null) {
                music = MediaPlayer.create(this, R.raw.arcadefunnk);
                if(musicPlaying) {
                    music.setLooping(true);
                    music.start();
                }
            }
        }

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Cartoon Marker.ttf");
        TextView highScore = (TextView)findViewById(R.id.high_score);
        highScore.setTypeface(myTypeface);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



//        scoreType.setTypeface(myTypeface);
//        score1.setTypeface(myTypeface);
//        score2.setTypeface(myTypeface);
//        score3.setTypeface(myTypeface);

        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.spinner_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i != 0) {
                    position = (String) adapterView.getItemAtPosition(i);
                    drawHighScore((String) adapterView.getItemAtPosition(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        musicButton = (Button) findViewById(R.id.button21);
        musicButton.setTypeface(myTypeface);
;   }

    public void drawHighScore(String num){
        score1.setText("");
        score2.setText("");
        score3.setText("");
        try {
            String filename = num + "score.txt";

            InputStream is = this.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            StringTokenizer st = new StringTokenizer(br.readLine(), "/");

            scoreType.setText(num + " Cards");

            if(st.hasMoreTokens())
                score1.setText(st.nextToken() + "   " + st.nextToken());
            if(st.hasMoreTokens())
                score2.setText(st.nextToken() + "   " + st.nextToken());
            if(st.hasMoreTokens())
                score3.setText(st.nextToken() + "   " + st.nextToken());
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
            scoreType.setText("No high score yet.");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        //super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("musicPlaying", musicPlaying);
        savedInstanceState.putString("position", position);
    }

    public void playMusic(View view) {
        if(music.isPlaying()) {
            music.setLooping(false);
            music.pause();
            musicPlaying = true;
        }
        else {
            music.setLooping(true);
            music.start();
            musicPlaying = false;
        }
    }

}
