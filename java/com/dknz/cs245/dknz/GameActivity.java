/***************************************************************
 * file: GameActivity.java
 * author: Zhen Liu, Kaythari Phon, Nam Huynh, Dulce Nava
 * class: CS 245 - Programming Graphical User Interfaces
 *
 * assignment: Android Project
 * date last modified: 11/29/2016
 *
 * purpose: this activity is the main concentration game of the app.
 * It creates cards number from 4-20 (even) using zodiac names.
 *
 ****************************************************************/

package com.dknz.cs245.dknz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

public class GameActivity extends AppCompatActivity {

    private static MediaPlayer music;
    private static final String TAG ="GameActivity";
    private Typeface myTypeface;
    private Button tryButton, newButton, endButton, musicButton;
    private Button[] cards;
    private TextView scoreView;

    private final ArrayList zodiac = new ArrayList();
    private ArrayList container = new ArrayList();
    private ArrayList<Integer> scores = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();

    private int numOfCards, score, selected1, selected2;
    private boolean secondCard, twoFlip, musicPlaying;
    private boolean[] flipStatus;
    private boolean[] correct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null) {
            //get number of cards selected
            Bundle b = getIntent().getExtras();
            if (b != null) {
                numOfCards = (int) b.get("numOfCards");
                if (numOfCards % 2 == 1)
                    numOfCards = 4;
                else if (numOfCards < 4)
                    numOfCards = 4;

                musicPlaying = (boolean) b.get("musicPlaying");
            }

            //initialize variables
            music = MediaPlayer.create(this, R.raw.arcadefunnk);
            music.setLooping(true);
            if(musicPlaying)
                music.start();

            score = 0;
            selected1 = 0;
            selected2 = 0;
            secondCard = false;
            twoFlip = false;
            flipStatus = new boolean[numOfCards];
            correct = new boolean[numOfCards];
            for (int i = 0; i < numOfCards; i++) {
                flipStatus[i] = false;
                correct[i] = false;
            }

            //Initialize score array
            try {
                InputStream is = this.openFileInput(Integer.toString(numOfCards) + "score.txt");
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                StringTokenizer st = new StringTokenizer(br.readLine(), "/");
                while(st.hasMoreTokens())
                {
                    names.add(st.nextToken());
                    scores.add(Integer.parseInt(st.nextToken()));
                }
                is.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            //ArrayList initialize and randomize
            zodiac.add("MOUSE");
            zodiac.add("OX");
            zodiac.add("TIGER");
            zodiac.add("RABBIT");
            zodiac.add("DRAGON");
            zodiac.add("SNAKE");
            zodiac.add("HORSE");
            zodiac.add("GOAT");
            zodiac.add("MONKEY");
            zodiac.add("ROOSTER");
            zodiac.add("DOG");
            zodiac.add("PIG");
            Collections.shuffle(zodiac);

            for (int i = 0; i < numOfCards / 2; i++) {
                container.add(zodiac.get(i));
                container.add(zodiac.get(i));
            }
            Collections.shuffle(container);

        } else {

            numOfCards = (int) savedInstanceState.get("numOfCards");
            score = (int) savedInstanceState.get("score");
            selected1 = (int) savedInstanceState.get("selected1");
            selected2 = (int) savedInstanceState.get("selected2");
            secondCard = (boolean) savedInstanceState.get("secondCard");
            twoFlip = (boolean) savedInstanceState.get("twoFlip");
            flipStatus = (boolean[]) savedInstanceState.get("flipStatus");
            correct = (boolean[]) savedInstanceState.get("correct");
            container = (ArrayList) savedInstanceState.get("container");
            musicPlaying = (boolean) savedInstanceState.get("musicPlaying");
            if(music == null) {
                music = MediaPlayer.create(this, R.raw.arcadefunnk);
                if(musicPlaying) {
                    music.setLooping(true);
                    music.start();
                }
            }
        }

        //retrieve xml file
        switch(numOfCards)
        {
            case 4:
                setContentView(R.layout.four_cards);
                break;
            case 6:
                setContentView(R.layout.six_cards);
                break;
            case 8:
                setContentView(R.layout.eight_cards);
                break;
            case 10:
                setContentView(R.layout.ten_cards);
                break;
            case 12:
                setContentView(R.layout.twelve_cards);
                break;
            case 14:
                setContentView(R.layout.fourteen_cards);
                break;
            case 16:
                setContentView(R.layout.sixteen_cards);
                break;
            case 18:
                setContentView(R.layout.eighteen_cards);
                break;
            case 20:
                setContentView(R.layout.twenty_cards);
                break;
            default:
                setContentView(R.layout.four_cards);
                numOfCards = 4;
                break;
        }

        myTypeface = Typeface.createFromAsset(getAssets(), "Cartoon Marker.ttf");

        //Create and fill the button array
        cards = new Button[numOfCards];
        createButtons(numOfCards);

        //score view
        scoreView = (TextView) findViewById(R.id.scoreView);
        scoreView.setText("Score: " + score);
        scoreView.setTextColor(0xFF000000);
        scoreView.setTypeface(myTypeface);

        //try button
        tryButton = (Button) findViewById(R.id.try_button);
        tryButton.setText("Try Again");
        tryButton.setTypeface(myTypeface);
        tryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(twoFlip) {
                    cards[selected1].setBackgroundDrawable(getResources().getDrawable(R.drawable.card_back));
                    cards[selected1].setText("");
                    cards[selected1].setClickable(true);
                    flipStatus[selected1] = false;
                    cards[selected2].setBackgroundDrawable(getResources().getDrawable(R.drawable.card_back));
                    cards[selected2].setText("");
                    cards[selected2].setClickable(true);
                    flipStatus[selected2] = false;
                    secondCard = false;
                    twoFlip = false;
                }
            }
        });

        //new button
        newButton = (Button) findViewById(R.id.new_button);
        newButton.setText("New Game");
        newButton.setTypeface(myTypeface);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start GameActivity
                music.release();
                Intent i= new Intent(GameActivity.this, MainActivity.class);
                i.putExtra("MusicPlaying", musicPlaying);
                startActivity(i);
            }
        });

        //end button
        endButton = (Button) findViewById(R.id.end_button);
        endButton.setText("End Game");
        endButton.setTypeface(myTypeface);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < cards.length; i++) {
                    cards[i].setBackgroundColor(255);
                    cards[i].setText((String) container.get(i));
                    cards[i].setTypeface(myTypeface);
                }
            }
        });

        //music button
        musicButton = (Button) findViewById(R.id.button21);
        musicButton.setTextColor(0xFF000000);
        musicButton.setTypeface(myTypeface);
    }

    //method: createButtons
    //purpose: to initialize and create onClickListeners for all buttons
    public void createButtons(int num) {
        Button card1 = (Button) findViewById(R.id.button1);
        cards[0] = card1;
        if(flipStatus[0]){
            drawCard(0);
            if (correct[0])
                cards[0].setEnabled(false);
        }
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 0;
                        secondCard = true;
                    } else {
                        selected2 = 0;
                        twoFlip = true;
                    }
                    drawCard(0);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        Button card2 = (Button) findViewById(R.id.button2);
        cards[1] = card2;
        if(flipStatus[1]){
            drawCard(1);
            if (correct[1])
                cards[1].setEnabled(false);
        }
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 1;
                        secondCard = true;
                    } else {
                        selected2 = 1;
                        twoFlip = true;
                    }
                    drawCard(1);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });


        Button card3 = (Button) findViewById(R.id.button3);
        cards[2] = card3;
        if(flipStatus[2]){
            drawCard(2);
            if (correct[2])
                cards[2].setEnabled(false);
        }
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 2;
                        secondCard = true;
                    } else {
                        selected2 = 2;
                        twoFlip = true;
                    }
                    drawCard(2);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        Button card4 = (Button) findViewById(R.id.button4);
        cards[3] = card4;
        if(flipStatus[3]){
            drawCard(3);
            if (correct[3])
                cards[3].setEnabled(false);
        }
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 3;
                        secondCard = true;
                    } else {
                        selected2 = 3;
                        twoFlip = true;
                    }
                    drawCard(3);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        if(num == 4)
            return;

        Button card5 = (Button) findViewById(R.id.button5);
        cards[4] = card5;
        if(flipStatus[4]){
            drawCard(4);
            if (correct[4])
                cards[4].setEnabled(false);
        }
        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 4;
                        secondCard = true;
                    } else {
                        selected2 = 4;
                        twoFlip = true;
                    }
                    drawCard(4);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        Button card6 = (Button) findViewById(R.id.button6);
        cards[5] = card6;
        if(flipStatus[5]){
            drawCard(5);
            if (correct[5])
                cards[5].setEnabled(false);
        }
        card6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 5;
                        secondCard = true;
                    } else {
                        selected2 = 5;
                        twoFlip = true;
                    }
                    drawCard(5);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        if(num == 6)
            return;

        Button card7 = (Button) findViewById(R.id.button7);
        cards[6] = card7;
        if(flipStatus[6]){
            drawCard(6);
            if (correct[6])
                cards[6].setEnabled(false);
        }
        card7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 6;
                        secondCard = true;
                    } else {
                        selected2 = 6;
                        twoFlip = true;
                    }
                    drawCard(6);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        Button card8 = (Button) findViewById(R.id.button8);
        cards[7] = card8;
        if(flipStatus[7]){
            drawCard(7);
            if (correct[7])
                cards[7].setEnabled(false);
        }
        card8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 7;
                        secondCard = true;
                    } else {
                        selected2 = 7;
                        twoFlip = true;
                    }
                    drawCard(7);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        if(num == 8)
            return;

        Button card9 = (Button) findViewById(R.id.button9);
        cards[8] = card9;
        if(flipStatus[8]){
            drawCard(8);
            if (correct[8])
                cards[8].setEnabled(false);
        }
        card9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 8;
                        secondCard = true;
                    } else {
                        selected2 = 8;
                        twoFlip = true;
                    }
                    drawCard(8);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        Button card10 = (Button) findViewById(R.id.button10);
        cards[9] = card10;
        if(flipStatus[9]){
            drawCard(9);
            if (correct[9])
                cards[9].setEnabled(false);
        }
        card10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 9;
                        secondCard = true;
                    } else {
                        selected2 = 9;
                        twoFlip = true;
                    }
                    drawCard(9);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        if(num == 10)
            return;

        Button card11 = (Button) findViewById(R.id.button11);
        cards[10] = card11;
        if(flipStatus[10]){
            drawCard(10);
            if (correct[10])
                cards[10].setEnabled(false);
        }
        card11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 10;
                        secondCard = true;
                    } else {
                        selected2 = 10;
                        twoFlip = true;
                    }
                    drawCard(10);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        Button card12 = (Button) findViewById(R.id.button12);
        cards[11] = card12;
        if(flipStatus[11]){
            drawCard(11);
            if (correct[11])
                cards[11].setEnabled(false);
        }
        card12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 11;
                        secondCard = true;
                    } else {
                        selected2 = 11;
                        twoFlip = true;
                    }
                    drawCard(11);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        if(num == 12)
            return;

        Button card13 = (Button) findViewById(R.id.button13);
        cards[12] = card13;
        if(flipStatus[12]){
            drawCard(12);
            if (correct[12])
                cards[12].setEnabled(false);
        }
        card13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 12;
                        secondCard = true;
                    } else {
                        selected2 = 12;
                        twoFlip = true;
                    }
                    drawCard(12);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        Button card14 = (Button) findViewById(R.id.button14);
        cards[13] = card14;
        if(flipStatus[13]){
            drawCard(13);
            if (correct[13])
                cards[13].setEnabled(false);
        }
        card14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 13;
                        secondCard = true;
                    } else {
                        selected2 = 13;
                        twoFlip = true;
                    }
                    drawCard(13);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        if(num == 14)
            return;

        Button card15 = (Button) findViewById(R.id.button15);
        cards[14] = card15;
        if(flipStatus[14]){
            drawCard(14);
            if (correct[14])
                cards[14].setEnabled(false);
        }
        card15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 14;
                        secondCard = true;
                    } else {
                        selected2 = 14;
                        twoFlip = true;
                    }
                    drawCard(14);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        Button card16 = (Button) findViewById(R.id.button16);
        cards[15] = card16;
        if(flipStatus[15]){
            drawCard(15);
            if (correct[15])
                cards[15].setEnabled(false);
        }
        card16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 15;
                        secondCard = true;
                    } else {
                        selected2 = 15;
                        twoFlip = true;
                    }
                    drawCard(15);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        if(num == 16)
            return;

        Button card17 = (Button) findViewById(R.id.button17);
        cards[16] = card17;
        if(flipStatus[16]){
            drawCard(16);
            if (correct[16])
                cards[16].setEnabled(false);
        }
        card17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 16;
                        secondCard = true;
                    } else {
                        selected2 = 16;
                        twoFlip = true;
                    }
                    drawCard(16);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        Button card18 = (Button) findViewById(R.id.button18);
        cards[17] = card18;
        if(flipStatus[17]){
            drawCard(17);
            if (correct[17])
                cards[17].setEnabled(false);
        }
        card18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 17;
                        secondCard = true;
                    } else {
                        selected2 = 17;
                        twoFlip = true;
                    }
                    drawCard(17);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        if(num == 18)
            return;

        Button card19 = (Button) findViewById(R.id.button19);
        cards[18] = card19;
        if(flipStatus[18]){
            drawCard(18);
            if (correct[18])
                cards[18].setEnabled(false);
        }
        card19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 18;
                        secondCard = true;
                    } else {
                        selected2 = 18;
                        twoFlip = true;
                    }
                    drawCard(18);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });

        Button card20 = (Button) findViewById(R.id.button20);
        cards[19] = card20;
        if(flipStatus[19]){
            drawCard(19);
            if (correct[19])
                cards[19].setEnabled(false);
        }
        card20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twoFlip) {
                    if (!secondCard) {
                        selected1 = 19;
                        secondCard = true;
                    } else {
                        selected2 = 19;
                        twoFlip = true;
                    }
                    drawCard(19);
                    if (twoFlip)
                        checkMatch();
                }
            }
        });
    }

    //method: checkMatch
    //purpose: to check if the two selected cards match
    public void checkMatch() {
        if(container.get(selected1) == container.get(selected2)) {
            score += 2;
            secondCard = false;
            twoFlip = false;
            cards[selected1].setClickable(false);
            cards[selected2].setClickable(false);
            correct[selected1] = true;
            correct[selected2] = true;

        }
        else {
            if(score > 0)
                score -= 1;
        }
        scoreView.setText("Score: "+score);
        checkComplete();
    }

    //method: checkComplete
    //purpose: to check if the game is over and prompt user to enter name
    public void checkComplete() {
        if (completeStatus()) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

            if (scores.size() < 3 || score > scores.get(scores.size() - 1)) {

                final EditText userInput = new EditText(this);
                userInput.setInputType(InputType.TYPE_CLASS_TEXT);
                userInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                userInput.setHint("Enter your name: ");
                alertBuilder.setView(userInput);

                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = userInput.getText().toString();
                        saveScore(name);
                        Intent ii = new Intent(GameActivity.this, MainActivity.class);
                        ii.putExtra("musicPlaying", musicPlaying);
                        music.release();
                        startActivity(ii);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                        Intent ii = new Intent(GameActivity.this, MainActivity.class);
                        ii.putExtra("musicPlaying", musicPlaying);
                        music.release();
                        startActivity(ii);
                    }
                });


            } else {
                final TextView msg = new TextView(this);
                msg.setText("Boo, you didn't score high enough.");
                msg.setTypeface(myTypeface);
                msg.setGravity(Gravity.CENTER);
                alertBuilder.setView(msg);
                alertBuilder.setNegativeButton("Home Screen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent ii = new Intent(GameActivity.this, MainActivity.class);
                        ii.putExtra("musicPlaying", musicPlaying);
                        music.release();
                        startActivity(ii);
                    }
                });

            }
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();

        }
    }

    //method: drawCard
    //purpose: draw the front of the card to the screen
    public void drawCard(int i) {
        cards[i].setBackgroundColor(255);
        cards[i].setText((String) container.get(i));
        cards[i].setTypeface(myTypeface);
        cards[i].setClickable(false);
        flipStatus[i] = true;

    }

    //method: completeStatus
    //purpose: check if all the cards are flip
    public boolean completeStatus() {
        for(boolean i: flipStatus) {
            if (!i)
                return false;
        }
        return true;
    }

    public void sortScores() {
        int temp;
        String temp2;
        for(int i = 0; i < scores.size() - 1; i++)
        {
            for(int j = 1; j < scores.size(); j++)
            {
                if(scores.get(j) > scores.get(j-1))
                {
                    temp = scores.get(j);
                    scores.add(scores.get(j-1));
                    scores.add(temp);

                    temp2 = names.get(j);
                    names.add(names.get(j-1));
                    names.add(temp2);
                }
            }
        }
    }

    public void saveScore(String name) {
        String filename = Integer.toString(numOfCards) + "score.txt";

        names.add(name);
        scores.add(score);
        sortScores();

        try {
            OutputStreamWriter o = new OutputStreamWriter(this.openFileOutput(filename, Context.MODE_PRIVATE));
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < names.size(); i++) {
                System.out.println(names.get(i) + "/" + scores.get(i) + "/");
                sb.append(names.get(i) + "/" + scores.get(i) + "/");
            }
            o.write(sb.toString());
            o.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //method: onSaveInstanceState
    //purpose: to save the data and recreate the game from where it left off
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        //super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("numOfCards", numOfCards);
        savedInstanceState.putInt("score", score);
        savedInstanceState.putInt("selected1", selected1);
        savedInstanceState.putInt("selected2", selected2);
        savedInstanceState.putBoolean("secondCard", secondCard);
        savedInstanceState.putBoolean("twoFlip", twoFlip);
        savedInstanceState.putStringArrayList("container", container);
        savedInstanceState.putBooleanArray("flipStatus", flipStatus);
        savedInstanceState.putBooleanArray("correct", correct);
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

}
