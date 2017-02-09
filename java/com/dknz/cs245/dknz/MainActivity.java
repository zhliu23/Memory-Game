/***************************************************************
 * file: MainActivity.java
 * author: Zhen Liu, Kaythari Phon, Nam Huynh, Dulce Nava
 * class: CS 245 - Programming Graphical User Interfaces
 *
 * assignment: Android Project
 * date last modified: 11/29/2016
 *
 * purpose: the main activity, the main menu of the app. Allow user
 * to select difficulty of the game.
 ****************************************************************/

package com.dknz.cs245.dknz;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static MediaPlayer music;

    //Tag used for making log messages
    private static final String TAG ="MainActivity";

    //Buttons options for number of cards
    private Button newGame, highScore, musicButton;
    private int numOfCards;
    private boolean musicPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //using d method for debugging
        Log.d(TAG,"onCreate(Bundle) called");
        setContentView(R.layout.activity_main);

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
            if(music == null) {
                music = MediaPlayer.create(this, R.raw.arcadefunnk);
                if(musicPlaying) {
                    music.setLooping(true);
                    music.start();
                }
            }
        }

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Cartoon Marker.ttf");
        TextView myText = (TextView)findViewById(R.id.textview1);
        myText.setTypeface(myTypeface);

        musicButton = (Button) findViewById(R.id.button21);
        musicButton.setTypeface(myTypeface);

        newGame = (Button) findViewById(R.id.new_game);
        newGame.setText("New Game");
        newGame.setTypeface(myTypeface);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create AlertDialog to prompt user to enter a number
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);

                final EditText userInput = new EditText(MainActivity.this);
                userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                userInput.setHint("Enter an even number from 4-20 (default=4):");
                alertBuilder.setView(userInput);

                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(userInput.getText().toString().equals(""))
                            numOfCards = 4;
                        else
                            numOfCards = Integer.parseInt(userInput.getText().toString());
                        Intent ii = new Intent(MainActivity.this, GameActivity.class);
                        ii.putExtra("numOfCards", numOfCards);
                        ii.putExtra("musicPlaying", music.isPlaying());
                        music.release();
                        startActivity(ii);

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
            }
        });

        highScore = (Button) findViewById(R.id.high_score);
        highScore.setText("High Score");
        highScore.setTypeface(myTypeface);
        highScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ScoreActivity.class);
                i.putExtra("musicPlaying", music.isPlaying());
                music.release();
                startActivity(i);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //using d method for debugging
        Log.d(TAG,"onStart() called");
    }

    @Override
    public void onPause(){
        super.onPause();
        //using d method for debugging
        Log.d(TAG,"onPause() called");

        //music.release();
    }

    @Override
    public void onResume(){
        super.onResume();
        //using d method for debugging
        Log.d(TAG,"onResume() called");
    }

    @Override
    public void onStop(){
        super.onStop();
        //using d method for debugging
        Log.d(TAG,"onStop() called");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //using d method for debugging
        Log.d(TAG,"onDestroy() called");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        //super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("musicPlaying", musicPlaying);
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
