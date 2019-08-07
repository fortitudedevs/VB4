package com.fortitude.shamsulkarim.ieltsfordory;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.fortitude.shamsulkarim.ieltsfordory.WordAdapters.NewTrainRecyclerView;
import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.JustLearnedDatabaseAdvance;
import com.fortitude.shamsulkarim.ieltsfordory.databases.JustLearnedDatabaseBeginner;
import com.fortitude.shamsulkarim.ieltsfordory.databases.JustLearnedDatabaseIntermediate;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;
import com.github.clans.fab.FloatingActionButton;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import mehdi.sakout.fancybuttons.FancyButton;

public class NewTrain extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener{

    private String[] IELTSwordArray, IELTStranslationArray, IELTSgrammarArray, IELTSpronunArray, IELTSexample1array, IELTSexample2Array, IELTSexample3Array, IELTSvocabularyType;
    private String[] TOEFLwordArray, TOEFLtranslationArray, TOEFLgrammarArray, TOEFLpronunArray, TOEFLexample1array, TOEFLexample2Array, TOEFLexample3Array, TOEFLvocabularyType;
    private String[] SATwordArray, SATtranslationArray, SATgrammarArray, SATpronunArray, SATexample1array, SATexample2Array, SATexample3Array, SATvocabularyType;
    private String[] GREwordArray, GREtranslationArray, GREgrammarArray, GREpronunArray, GREexample1array, GREexample2array, GREexample3Array, GREvocabularyType;

    private int[] IELTSposition, TOEFLposition, SATposition, GREposition;
    private List<String> IELTSlearnedDatabase, TOEFLlearnedDatabase, SATlearnedDatabase, GRElearnedDatabase;
    private View topBackground;
    private CardView answerCard1, answerCard2, answerCard3, answerCard4, wordCard;
    private TextView wordView, answerView1, answerView2, answerView3, answerView4;
    private FloatingActionButton fab;
    private TextToSpeech tts;
    private FancyButton speak;
    private int lastMistake = 13;
    private int mistakes, wordsPerSession,FIVE_WORD_SIZE, showCycle, quizCycle, languageId, repeatPerSession, totalCycle, noshowads, progressCount, cb ;
    private SharedPreferences sp;
    private ArrayList<Word> words, fiveWords,fiveWordsCopy,questionWords;
    private RoundCornerProgressBar progress1;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String level;
    private boolean IsWrongAnswer = true;
    private int[] mistakeCollector;
    private boolean next  = true;
    private boolean  alreadyclicked = true;
    private boolean progress = false;
    private int totalMistakeCount, totalCorrects;
    private boolean soundState = true;
    boolean isAdShown1 = false;
    boolean isWhichvocbularyToText = false;
    private boolean isIeltsChecked, isToeflChecked, isSatChecked, isGreChecked;
    private PublisherInterstitialAd mPublisherInterstitialAd;


    private List<String> IELTSFav, TOEFLFav, SATFav, GREFav;

    private IELTSWordDatabase IELTSdatabase;
    private TOEFLWordDatabase TOEFLdatabase;
    private SATWordDatabase SATdatabase;
    private GREWordDatabase GREdatabase;
    private JustLearnedDatabaseBeginner justLearnedDatabaseBeginner;
    private JustLearnedDatabaseIntermediate justLearnedDatabaseIntermediate;
    private JustLearnedDatabaseAdvance justLearnedDatabaseAdvance;

    private StorageReference gsReference;
    private FirebaseStorage storage;
    public File localFile = null;
    public String audioPath= null;
    ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_new_train);





        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        level = sp.getString("level","NOTHING");
        languageId = sp.getInt("language",0);
        soundState = sp.getBoolean("soundState",true);
        noshowads = sp.getInt("noshowads",0);

        if(!sp.contains("totalWrongCount"+level)){

            sp.edit().putInt("totalWrongCount"+level,0).apply();



        }else {
            totalMistakeCount = sp.getInt("totalWrongCount"+level,0);
            totalCorrects =  sp.getInt("totalCorrects",0);
            cb = sp.getInt("cb",0);


        }
        mPublisherInterstitialAd = new PublisherInterstitialAd(this);
        mPublisherInterstitialAd.setAdUnitId("ca-app-pub-7815894766256601/6656734197");
        mPublisherInterstitialAd.loadAd(new PublisherAdRequest.Builder().build());

        tts = new TextToSpeech(this, this);


                initialization();
                addingLearnedDatabase();
                gettingResources();
                initializingWords();
                showWords(showCycle);

        mistakeCollector = new int[fiveWords.size()];

        for(int i = 0; i < mistakeCollector.length; i ++){

            mistakeCollector[i] = 0;
        }
        //Toast.makeText(this,"fiveWordsize: "+fiveWords.size()+" level: "+level+" word size: "+words.size(),Toast.LENGTH_SHORT).show();

        sp.edit().putInt("fiveWordSize",fiveWords.size()).apply();

        noshowads++;

        sp.edit().putInt("noshowads",noshowads).apply();

//                updateLearnedDatabase();
//        NewTrain.this.startActivity(new Intent(getApplicationContext(), TrainFinishedActivity.class));
//        NewTrain.this.finish();



    }

    public void next(View v){
        speak.setVisibility(View.INVISIBLE);

        Timer T=new Timer();

        if(!progress){

            progress = true;


            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {

                            progressCount++;
                            fab.setProgress(progressCount,true);



                            if( progressCount == 5){
                                progressCount = 0;
                                alreadyclicked = true;
                                progress = false;
                                cancel();
                            }
                        }
                    });
                }
            }, 1000, 1000);


        }


//
//
//        if(!alreadyclicked && !next){
//
//            next = true;
//
//
//            T.scheduleAtFixedRate(new TimerTask() {
//                @Override
//                public void run() {
//                    runOnUiThread(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//
//                            nextCount++;
////                            fab.setProgress(nextCount,true);
//
//                            if( nextCount == 5){
//                                nextCount = 0;
//                                alreadyclicked = true;
//                                cancel();
//                            }
//                        }
//                    });
//                }
//            }, 1000, 1000);
//
//
//        }
//
//
//
        if( alreadyclicked){
            toNextWord(v);
            alreadyclicked = false;
            next = false;
        }







    }


    public void settingNextProgress(){
        final Handler handler = new Handler();

        handler.post(new Runnable() {

            int i = 0;
            @Override
            public void run() {
                i++;




                if( i <= 5){
                    fab.setProgress(i,true);
                    alreadyclicked = true;
                }
                handler.postDelayed(this,1000l);

            }
        });



    }


    public void toNextWord(View view){
        if (showCycle >= FIVE_WORD_SIZE){

            // hide all the views after the learning stage
            //------------------------------

            //hideViews();
            if(!isWhichvocbularyToText){
                whichVocabularyToTest(view);
                isWhichvocbularyToText = true;
            }else {

            isAdShown1 = showInterstitialAd(isAdShown1);
            answerCard1.setVisibility(View.VISIBLE);
            answerCard2.setVisibility(View.VISIBLE);
            answerCard3.setVisibility(View.VISIBLE);
            answerCard4.setVisibility(View.VISIBLE);
            speak.setVisibility(View.INVISIBLE);

                if(fiveWords.size()>0){

                    wordView.setText(fiveWords.get(quizCycle).getWord());

                }


                quizWords(quizCycle, view);
            }


            //------------------------------







        }

        showWords(showCycle);

    }
    @Override
    public void onClick(View v) {

        if( v== speak){

            String word = wordView.getText().toString();;
            if(showCycle < FIVE_WORD_SIZE+1){

                word = wordView.getText().toString();

//                tts.setLanguage(Locale.US);
//                tts.speak(word, TextToSpeech.QUEUE_ADD, null);
                downloadAudio(word);

                if(showCycle == FIVE_WORD_SIZE){
                    showCycle++;
                }

            }else {

//                word = wordViewMiddle.getText().toString();

            }

//            tts.setLanguage(Locale.US);
//            tts.speak(word, TextToSpeech.QUEUE_ADD, null);
            downloadAudio(word);

        }


        if( v == answerCard1 || v == answerCard2 || v == answerCard3 ||  v == answerCard4 ){



            if( quizCycle <= (FIVE_WORD_SIZE*repeatPerSession)-1){
                quizWords(quizCycle, v);
            }

        }


    }

    private void showWords(int showCycle){

        Handler handler = new Handler();

            if( showCycle < FIVE_WORD_SIZE){

                try{
                    wordView.setText(fiveWords.get(showCycle).getWord());

                }catch (NullPointerException i){
                    Log.i("Error","Quiz Cycle: "+quizCycle+" ShowCycle: "+showCycle);
 
                }

//                wordViewMiddle.setText(fiveWords.get(showCycle).getWord());


                if(showCycle == 0){
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DefExamAnimation();
                        }
                    }, 200L);
                }else {


                    DefExamAnimation();
                }


                adapter = new NewTrainRecyclerView(this,fiveWords.get(showCycle));
                recyclerView.setAdapter(adapter);
                this.showCycle++;
                progress1.setProgress(quizCycle+showCycle);
            }



    }

    private void quizWords(int quizCycle, View v){



        if(quizCycle == 0){

            answerCardAnimation();
        }
        if(showCycle >= FIVE_WORD_SIZE|| quizCycle <= (FIVE_WORD_SIZE*repeatPerSession)-1){



            //hideViews();
            answerCard1.setVisibility(View.VISIBLE);
            answerCard2.setVisibility(View.VISIBLE);
            answerCard3.setVisibility(View.VISIBLE);
            answerCard4.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            fab.animate().scaleX(0f).scaleY(0f).setDuration(350L).setInterpolator(new AnticipateOvershootInterpolator());


            checkingAnswer(v);
            cycleQuiz(quizCycle,v);
        }


    }

    private void initialization(){
        //Typeface comfortaRegular = Typeface.createFromAsset(getAssets(),"fonts/Comfortaa-Regular.ttf");

        topBackground = findViewById(R.id.top_background);
        wordCard = findViewById(R.id.wordCard);
        recyclerView = (RecyclerView)findViewById(R.id.train_recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        wordsPerSession = sp.getInt("wordsPerSession",5);
        repeatPerSession = sp.getInt("repeatationPerSession",5);
        wordView =    (TextView)findViewById(R.id.train_word);
        answerCard1 = (CardView)findViewById(R.id.answer_card1);
        answerCard2 = (CardView)findViewById(R.id.answer_card2);
        answerCard3 = (CardView)findViewById(R.id.answer_card3);
        answerCard4 = (CardView)findViewById(R.id.answer_card4);
        answerView1 = (TextView)findViewById(R.id.train_answer_text1);
        answerView2 = (TextView)findViewById(R.id.train_answer_text2);
        answerView3 = (TextView)findViewById(R.id.train_answer_text3);
        answerView4 = (TextView)findViewById(R.id.train_answer_text4);
        speak =       (FancyButton) findViewById(R.id.train_speaker_icon);
        speak.setVisibility(View.INVISIBLE);
        storage = FirebaseStorage.getInstance();

        progressBar = findViewById(R.id.spin_kit);
        Sprite doubleBounce = new Wave();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.INVISIBLE);

        isIeltsChecked = sp.getBoolean("isIELTSActive",true);
        isToeflChecked = sp.getBoolean("isTOEFLActive", true);
        isSatChecked =   sp.getBoolean("isSATActive", true);
        isGreChecked =   sp.getBoolean("isGREActive",true);


        fab = (FloatingActionButton)findViewById(R.id.train_fab);
        fab.setMax(5);
        progress1 = (RoundCornerProgressBar) findViewById(R.id.progress_1);
        progress1.setSecondaryProgressColor(getResources().getColor(R.color.colorPrimaryDark));
        progress1.setProgressColor(getResources().getColor(R.color.colorPrimary));
        progress1.setProgressBackgroundColor(getResources().getColor(R.color.grey));

        IELTSdatabase = new IELTSWordDatabase(this);
        TOEFLdatabase = new TOEFLWordDatabase(this);
        SATdatabase = new SATWordDatabase(this);
        GREdatabase = new GREWordDatabase(this);
        justLearnedDatabaseBeginner = new JustLearnedDatabaseBeginner(this);
        justLearnedDatabaseIntermediate = new JustLearnedDatabaseIntermediate(this);
        justLearnedDatabaseAdvance = new JustLearnedDatabaseAdvance(this);


        IELTSFav = new ArrayList<>();
        TOEFLFav = new ArrayList<>();
        SATFav = new ArrayList<>();
        GREFav = new ArrayList<>();

        IELTSlearnedDatabase = new ArrayList<>();
        TOEFLlearnedDatabase = new ArrayList<>();
        SATlearnedDatabase = new ArrayList<>();
        GRElearnedDatabase = new ArrayList<>();







        fiveWords = new ArrayList<>();
        fiveWordsCopy = new ArrayList<>();
        questionWords = new ArrayList<>();

        answerCard1.setAlpha(0f);
        answerCard2.setAlpha(0f);
        answerCard3.setAlpha(0f);
        answerCard4.setAlpha(0f);

        answerCard1.setY(100f);
        answerCard2.setY(100f);
        answerCard3.setY(100f);
        answerCard4.setY(100f);


        answerCard1.setOnClickListener(this);
        answerCard2.setOnClickListener(this);
        answerCard3.setOnClickListener(this);
        answerCard4.setOnClickListener(this);
        speak.setOnClickListener(this);

        answerCard1.setVisibility(View.INVISIBLE);
        answerCard2.setVisibility(View.INVISIBLE);
        answerCard3.setVisibility(View.INVISIBLE);
        answerCard4.setVisibility(View.INVISIBLE);

    }

    private void gettingResources() {


            IELTSwordArray = getResources().getStringArray(R.array.IELTS_words);
            IELTStranslationArray = getResources().getStringArray(R.array.IELTS_translation);
            IELTSgrammarArray =  getResources().getStringArray(R.array.IELTS_grammar);
            IELTSpronunArray =  getResources().getStringArray(R.array.IELTS_pronunciation);
            IELTSexample1array = getResources().getStringArray(R.array.IELTS_example1);
            IELTSexample2Array = getResources().getStringArray(R.array.IELTS_example2);
            IELTSexample3Array = getResources().getStringArray(R.array.IELTS_example3);
            IELTSvocabularyType = getResources().getStringArray(R.array.IELTS_level);
            IELTSposition = getResources().getIntArray(R.array.IELTS_position);


            TOEFLwordArray = getResources().getStringArray(R.array.TOEFL_words);
            TOEFLtranslationArray = getResources().getStringArray(R.array.TOEFL_translation);
            TOEFLgrammarArray =  getResources().getStringArray(R.array.TOEFL_grammar);
            TOEFLpronunArray =  getResources().getStringArray(R.array.TOEFL_pronunciation);
            TOEFLexample1array = getResources().getStringArray(R.array.TOEFL_example1);
            TOEFLexample2Array = getResources().getStringArray(R.array.TOEFL_example2);
            TOEFLexample3Array = getResources().getStringArray(R.array.TOEFL_example3);
            TOEFLvocabularyType = getResources().getStringArray(R.array.TOEFL_level);
            TOEFLposition = getResources().getIntArray(R.array.TOEFL_position);


            SATwordArray = getResources().getStringArray(R.array.SAT_words);
            SATtranslationArray = getResources().getStringArray(R.array.SAT_translation);
            SATgrammarArray =  getResources().getStringArray(R.array.SAT_grammar);
            SATpronunArray =  getResources().getStringArray(R.array.SAT_pronunciation);
            SATexample1array = getResources().getStringArray(R.array.SAT_example1);
            SATexample2Array = getResources().getStringArray(R.array.SAT_example2);
            SATexample3Array = getResources().getStringArray(R.array.SAT_example3);
            SATvocabularyType = getResources().getStringArray(R.array.SAT_level);
            SATposition = getResources().getIntArray(R.array.SAT_position);


            GREwordArray = getResources().getStringArray(R.array.GRE_words);
            GREtranslationArray = getResources().getStringArray(R.array.GRE_translation);
            GREgrammarArray = getResources().getStringArray(R.array.GRE_grammar);
            GREpronunArray = getResources().getStringArray(R.array.GRE_pronunciation);
            GREexample1array = getResources().getStringArray(R.array.GRE_example1);
            GREexample2array =getResources().getStringArray(R.array.GRE_example2);
            GREexample3Array = getResources().getStringArray(R.array.GRE_example3);
            GREvocabularyType = getResources().getStringArray(R.array.GRE_level);
            GREposition = getResources().getIntArray(R.array.GRE_position);




    }

    private  void initializingWords(){

        words = new ArrayList<>();


        SharedPreferences sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        if(!sp.contains(level)){
            sp.edit().putInt(level,0).apply();
        }
        //int countWords = sp.getInt(level,0);






//        for (int i = 0; i < IELTSwordArray.length; i++) {
//            if(languageId > 0){
//                words.add(new Word(IELTSwordArray[i], IELTStranslationArray[i],"", IELTSpronunArray[i], IELTSgrammarArray[i], IELTSexample1array[i], IELTSexample2Array[i], IELTSexample3Array[i],""));
//
//            }
//            else{
//
//                words.add(new Word(IELTSwordArray[i], IELTStranslationArray[i],"", IELTSpronunArray[i], IELTSgrammarArray[i], IELTSexample1array[i], IELTSexample2Array[i], IELTSexample3Array[i],""));
//
//            }
//
//        }

        getWords();


       // wordsPerSession += countWords;

        if( wordsPerSession > words.size()){
            wordsPerSession = words.size();
        }



        if(wordsPerSession <= words.size()){
            for ( int k = 0; k < wordsPerSession;k++) {

                fiveWords.add(words.get(k));


            }
        }
        if(words.size() == 0){




//            Intent intent = new Intent(this,NoWordLeft.class);
//            this.startActivity(intent);
//            this.finish();
        }

        sp.edit().putInt("fiveWordSize",fiveWords.size()).apply();
        FIVE_WORD_SIZE = fiveWords.size();
        progress1.setMax(FIVE_WORD_SIZE+(FIVE_WORD_SIZE*repeatPerSession));
        progress1.setSecondaryProgress(FIVE_WORD_SIZE);



    }

    private void answerCardAnimation(){


                answerCard1.animate().translationY(0f).alpha(1f).setDuration(500L).setInterpolator(new AccelerateDecelerateInterpolator());
                answerCard2.animate().translationY(0f).alpha(1f).setDuration(500L).setInterpolator(new AccelerateDecelerateInterpolator());
                answerCard3.animate().translationY(0f).alpha(1f).setDuration(500L).setInterpolator(new AccelerateDecelerateInterpolator());
                answerCard4.animate().translationY(0f).alpha(1f).setDuration(500L).setInterpolator(new AccelerateDecelerateInterpolator());



    }

    private void cycleQuiz(int quizCycle, View v){


        if(this.quizCycle <= (FIVE_WORD_SIZE*repeatPerSession)-1){

            ArrayList<Word> answers = gettingAnswer();
            if(!IsWrongAnswer){
                IsWrongAnswer = true;

            }else {
//                wordViewMiddle.setVisibility(View.VISIBLE);
//                wordViewMiddle.setText(fiveWords.get(this.quizCycle).getWord());
            }


            if( languageId == 0){

                answerView1.setText(answers.get(0).getTranslation());
                answerView2.setText(answers.get(1).getTranslation());
                answerView3.setText(answers.get(2).getTranslation());
                answerView4.setText(answers.get(3).getTranslation());

            }else {
                answerView1.setText(answers.get(0).getExtra());
                answerView2.setText(answers.get(1).getExtra());
                answerView3.setText(answers.get(2).getExtra());
                answerView4.setText(answers.get(3).getExtra());

            }




        }

    }

    private ArrayList<Word> gettingAnswer() {
        if( quizCycle == FIVE_WORD_SIZE ){

            quizCycle = 0;
        }

        ArrayList<Word> answers = new ArrayList<>();


        if( this.quizCycle <=(FIVE_WORD_SIZE*repeatPerSession)-1){


        Word word = fiveWords.get(this.quizCycle);

            Collections.shuffle(questionWords);
        for (int i = 0; i < 4;i++) {

            answers.add(questionWords.get(i));

        }

        if( !answers.contains(word)){

            answers.set(0,word);

        }

        Collections.shuffle(answers);

        }
        return answers;
    }

    private void checkingAnswer(final View v){

        MediaPlayer correctAudio = MediaPlayer.create(this, R.raw.correct);
        MediaPlayer incorrectAudio = MediaPlayer.create(this, R.raw.incorrect);



        String answer = "";


        if(this.quizCycle < (FIVE_WORD_SIZE*repeatPerSession)){

            if(languageId == 0){
                answer = fiveWords.get(quizCycle).getTranslation();
            }
            else {
                answer = fiveWords.get(quizCycle).getExtra();

            }




        if(v == answerCard1){

            if(answerView1.getText().toString().equalsIgnoreCase(answer)){
                StyleableToast.makeText(this, "Correct!", 10, R.style.correct).show();

                this.quizCycle++;

                this.totalCycle++;

                if(soundState){

                    correctAudio.start();
                }
                totalCorrects++;

                answerCardAnimation2();

            }else {

                if(soundState){

                    incorrectAudio.start();
                }

                mistakeCollector[quizCycle] = mistakeCollector[quizCycle]+1;

                wrongAnswerAnimation();

                mistakes++;
                totalMistakeCount++;

                if( lastMistake == quizCycle){
                    StyleableToast.makeText(this, "wrong answer again",10, R.style.wrong_again).show();


                }else {


                    
                    lastMistake = quizCycle;
                    if(mistakes <= 3){

                        StyleableToast.makeText(this, "Wrong answer", 10, R.style.wrong).show();

                    }
                    if(mistakes >= 4){
                        StyleableToast.makeText(this, "Oh no! wrong answer", 10, R.style.MyToast).show();

                    }
                }








        } }

         if(v == answerCard2) {

             if (answerView2.getText().toString().equalsIgnoreCase(answer)) {
                 StyleableToast.makeText(this, "Correct!", 10, R.style.correct).show();
                 this.quizCycle++;

                 this.totalCycle++;
                 if(soundState){

                     correctAudio.start();
                 }
                 totalCorrects++;

                 answerCardAnimation2();

             } else {


                 mistakeCollector[quizCycle] = mistakeCollector[quizCycle]+1;
                 wrongAnswerAnimation();
                 if(soundState){

                     incorrectAudio.start();
                 }


                 mistakes++;
                 totalMistakeCount++;

                 if( lastMistake == quizCycle){
                     StyleableToast.makeText(this, "wrong answer again", 10, R.style.wrong_again).show();


                 }else {

                     lastMistake = quizCycle;
                     if(mistakes <= 3){

                         StyleableToast.makeText(this, "Wrong answer!", 10, R.style.wrong).show();

                     }
                     if(mistakes >= 4){
                         StyleableToast.makeText(this, "Oh no! wrong answer", 10, R.style.MyToast).show();

                     }
                 }


             }
         }

        if(v == answerCard3){

            if(answerView3.getText().toString().equalsIgnoreCase(answer)){

                StyleableToast.makeText(this, "Correct!", 10, R.style.correct).show();
                this.quizCycle++;

                this.totalCycle++;
                if(soundState){

                    correctAudio.start();
                }
                totalCorrects++;

                answerCardAnimation2();

            }else {
                if(soundState){

                    incorrectAudio.start();
                }

                mistakeCollector[quizCycle] = mistakeCollector[quizCycle]+1;

                wrongAnswerAnimation();


                mistakes++;
                totalMistakeCount++;

                if( lastMistake == quizCycle){
                    StyleableToast.makeText(this, "Wrong answer again", 10, R.style.wrong_again).show();


                }else {

                    lastMistake = quizCycle;
                    if(mistakes <= 3){

                        StyleableToast.makeText(this, "Wrong answer!", 10, R.style.wrong).show();

                    }
                    if(mistakes >= 4){
                        StyleableToast.makeText(this, "Oh no! wrong answer", 10, R.style.MyToast).show();

                    }
                }

            }


        }
        if(v == answerCard4){

            if(answerView4.getText().toString().equalsIgnoreCase(answer)){

                StyleableToast.makeText(this, "Correct!", 10, R.style.correct).show();
                this.quizCycle++;

                this.totalCycle++;
                answerCardAnimation2();
                if(soundState){

                    correctAudio.start();
                }
                totalCorrects++;

                cycleQuiz(quizCycle, v);



            }else {
                if(soundState){

                    incorrectAudio.start();
                }


                mistakeCollector[quizCycle] = mistakeCollector[quizCycle]+1;

                wrongAnswerAnimation();


                mistakes++;
                totalMistakeCount++;

                if( lastMistake == quizCycle){
                    StyleableToast.makeText(this, "wrong answer again?", 10, R.style.wrong_again).show();


                }else {

                    lastMistake = quizCycle;
                    if(mistakes <= 3){

                        StyleableToast.makeText(this, "Wrong answer", 10, R.style.wrong).show();

                    }
                    if(mistakes >= 4){
                        StyleableToast.makeText(this, "Oh no! Wrong answer", 10, R.style.MyToast).show();

                    }
                }

            }


        }

        }



        progress1.setProgress(totalCycle+showCycle);

        if( quizCycle == FIVE_WORD_SIZE ){

            quizCycle = 0;
        }
        wordView.setText(fiveWords.get(quizCycle).getWord());

        if (totalCycle == (FIVE_WORD_SIZE*repeatPerSession)){


            int pos = getMostMistakenWord(mistakeCollector);
            fiveWords.clear();
            fiveWords.addAll(fiveWordsCopy);
            updateLearnedDatabase();
            updateJustlearnedDatabase(pos);


            sp.edit().putInt("NTmistakes",mistakes).apply();
            sp.edit().putInt("totalWrongCount"+level,totalMistakeCount).apply();
            sp.edit().putInt("totalCorrects",totalCorrects).apply();
            if(pos != -1){

                String word = fiveWords.get(pos).getPronun();
                String def = fiveWords.get(pos).getTranslation();
                String spanish = fiveWords.get(pos).getExtra();
                String example = fiveWords.get(pos).getExample2();

                StringBuilder mostMistakenWord = new StringBuilder("shit"+"+"+word+"+"+def+"+"+spanish+"+"+example);
                sp.edit().putString("MostMistakenWord",mostMistakenWord.toString()).apply();



            }else {
                sp.edit().putString("MostMistakenWord","no").apply();

            }

            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    NewTrain.this.startActivity(new Intent(getApplicationContext(), TrainFinishedActivity.class));
                    NewTrain.this.finish();
                }
            }, 200L);



        }


    }

    private void DefExamAnimation(){

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float width = dm.widthPixels;
        float height = dm.heightPixels;

        final ValueAnimator va = ValueAnimator.ofFloat(height,0);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                float value = (float)valueAnimator.getAnimatedValue();
                recyclerView.setTranslationY(value/10);
            }
        });


        va.setRepeatMode(ValueAnimator.REVERSE);
        va.setInterpolator(new DecelerateInterpolator());
        va.setDuration(400L);
        va.start();




    }

    private void answerCardAnimation2(){

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float width = dm.widthPixels;
        float height = dm.heightPixels;

        final ValueAnimator va = ValueAnimator.ofFloat(height,0);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                float value = (float)valueAnimator.getAnimatedValue();
                answerCard1.setTranslationY(value/10);
                answerCard2.setTranslationY(value/10);
                answerCard3.setTranslationY(value/10);
                answerCard4.setTranslationY(value/10);
            }
        });


        va.setRepeatMode(ValueAnimator.REVERSE);
        va.setInterpolator(new DecelerateInterpolator());
        va.setDuration(400L);
        va.start();




    }


    private void wrongAnswerAnimation(){
        IsWrongAnswer = false;

       fab.animate().scaleX(1f).scaleY(1f).setDuration(350L).setInterpolator(new AnticipateOvershootInterpolator());

        recyclerView.setVisibility(View.VISIBLE);
//
//        wordViewMiddle.setText("");
//        wordViewMiddle.setVisibility(View.INVISIBLE);
        answerCard1.setVisibility(View.INVISIBLE);
        answerCard2.setVisibility(View.INVISIBLE);
        answerCard3.setVisibility(View.INVISIBLE);
        answerCard4.setVisibility(View.INVISIBLE);
        wordView.setVisibility(View.VISIBLE);
        speak.setVisibility(View.INVISIBLE);


        wordView.setText(fiveWords.get(quizCycle).getWord());
//        wordViewMiddle.setText(fiveWords.get(quizCycle).getWord());

        DefExamAnimation();
        adapter = new NewTrainRecyclerView(this,fiveWords.get(quizCycle));
        recyclerView.setAdapter(adapter);

//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        float width = dm.widthPixels;
//        float height = dm.heightPixels;
//
//        final ValueAnimator va = ValueAnimator.ofFloat(height,0);
//
//        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
//
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//
//                float value = (float)valueAnimator.getAnimatedValue();
//                recyclerView.setTranslationY(value/10);
//            }
//        });
//
//
//        va.setRepeatMode(ValueAnimator.REVERSE);
//        va.setInterpolator(new DecelerateInterpolator());
//        va.setDuration(400L);
//        va.start();


    }

    @Override
    public void onInit(int status) {

    }


    private int getMostMistakenWord(int[] list){

        int wordIndex = 0;
        int pos = -1;


        for(int i = 0; i < list.length; i++){

            int current = list[i];

            if( current> wordIndex){
                pos = i;

                wordIndex = current;

            }

        }

        return pos;




    }


    @Override
    public void onBackPressed() {


        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_leave)
                .setTitle("Do you want to leave this session, "+sp.getString("userName","Boo")+"?")
                .setMessage("Leaving this session will make you lose your progress")
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NewTrain.this.startActivity(new Intent(NewTrain.this,MainActivity.class));
                        NewTrain.this.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void getWords(){

        double IELTSwordSize = IELTSwordArray.length;
        double TOEFLwordSize = TOEFLwordArray.length;
        double SATwordSize =   SATwordArray.length;
        double GREwordSize = GREwordArray.length;

        double IELTSbeginnerNumber = getPercentageNumber(30d, IELTSwordSize);
        double TOEFLbeginnerNumber = getPercentageNumber(30d, TOEFLwordSize);
        double SATbeginnerNumber =   getPercentageNumber(30d, SATwordSize);
        double GREbeginnerNumber =   getPercentageNumber(30d, GREwordSize);

        double IELTSintermediateNumber = getPercentageNumber(40d,IELTSwordSize);
        double TOEFLintermediateNumber = getPercentageNumber(40d, TOEFLwordSize);
        double SATintermediateNumber = getPercentageNumber(40d, SATwordSize);
        double GREintermediateNumber = getPercentageNumber(40d, GREwordSize);

        if( level.equalsIgnoreCase("beginner")){

           addIELTSwords(0d,IELTSbeginnerNumber);
           addTOEFLwords(0d,TOEFLbeginnerNumber);
           addSATwords(0d,SATbeginnerNumber);
           addGREwords(0d,GREbeginnerNumber);

        }

        if( level.equalsIgnoreCase("intermediate")){

            addIELTSwords(IELTSbeginnerNumber,IELTSintermediateNumber+IELTSbeginnerNumber);
            addTOEFLwords(TOEFLbeginnerNumber,TOEFLbeginnerNumber+TOEFLintermediateNumber);
            addSATwords(SATbeginnerNumber,SATbeginnerNumber+SATintermediateNumber);
            addGREwords(GREbeginnerNumber,GREbeginnerNumber+GREintermediateNumber);

        }

        if( level.equalsIgnoreCase("advance")){

            addIELTSwords(IELTSintermediateNumber+IELTSbeginnerNumber, IELTSwordSize);
            addTOEFLwords(TOEFLintermediateNumber+TOEFLbeginnerNumber, TOEFLwordSize);
            addSATwords(SATintermediateNumber+SATbeginnerNumber, SATwordSize);
            addGREwords(GREintermediateNumber+GREbeginnerNumber,GREwordSize);

        }


    }


    private double getPercentageNumber(double percentage, double number){


        double p = percentage/100d;
        double beginnerNum = p*number;

        return beginnerNum;

    }

    private  void addIELTSwords(double startPoint,double IELTSbeginnerNumber){



        if(isIeltsChecked){
            for(int i = (int) startPoint; i  < IELTSbeginnerNumber; i++){

                questionWords.add(new Word(IELTSwordArray[i], IELTStranslationArray[i],"", IELTSpronunArray[i], IELTSgrammarArray[i], IELTSexample1array[i], IELTSexample2Array[i], IELTSexample3Array[i],IELTSvocabularyType[i],IELTSposition[i], IELTSlearnedDatabase.get(i), IELTSFav.get(i)));


                if( IELTSlearnedDatabase.get(i).equalsIgnoreCase("false")){

                    words.add(new Word(IELTSwordArray[i], IELTStranslationArray[i],"", IELTSpronunArray[i], IELTSgrammarArray[i], IELTSexample1array[i], IELTSexample2Array[i], IELTSexample3Array[i],IELTSvocabularyType[i],IELTSposition[i], IELTSlearnedDatabase.get(i), IELTSFav.get(i)));
                }

            }

        }


    }

    private  void addTOEFLwords(double startPoint, double TOEFLbeginnerNumber){


        if(isToeflChecked){


            for(int i = (int) startPoint; i < TOEFLbeginnerNumber; i++){

                questionWords.add(new Word(TOEFLwordArray[i], TOEFLtranslationArray[i], "", TOEFLpronunArray[i], TOEFLgrammarArray[i], TOEFLexample1array[i], TOEFLexample2Array[i], TOEFLexample3Array[i], TOEFLvocabularyType[i], TOEFLposition[i], TOEFLlearnedDatabase.get(i), TOEFLFav.get(i)));

                if( TOEFLlearnedDatabase.get(i).equalsIgnoreCase("false")) {
                    words.add(new Word(TOEFLwordArray[i], TOEFLtranslationArray[i], "", TOEFLpronunArray[i], TOEFLgrammarArray[i], TOEFLexample1array[i], TOEFLexample2Array[i], TOEFLexample3Array[i], TOEFLvocabularyType[i], TOEFLposition[i], TOEFLlearnedDatabase.get(i), TOEFLFav.get(i)));


                }
            }
        }



    }

    private void addSATwords (double startPoint ,double SATbeginnerNumber){


        if(isSatChecked){

            for(int i = (int) startPoint; i < SATbeginnerNumber; i++){

                questionWords.add(new Word(SATwordArray[i], SATtranslationArray[i], "", SATpronunArray[i], SATgrammarArray[i], SATexample1array[i], SATexample2Array[i], SATexample3Array[i], SATvocabularyType[i], SATposition[i], SATlearnedDatabase.get(i), SATFav.get(i)));
                if( SATlearnedDatabase.get(i).equalsIgnoreCase("false")) {

                    words.add(new Word(SATwordArray[i], SATtranslationArray[i], "", SATpronunArray[i], SATgrammarArray[i], SATexample1array[i], SATexample2Array[i], SATexample3Array[i], SATvocabularyType[i], SATposition[i], SATlearnedDatabase.get(i), SATFav.get(i)));

                }

            }

        }





    }

    private void addGREwords (double startPoint ,double SATbeginnerNumber){


        if(isGreChecked){

            for(int i = (int) startPoint; i < SATbeginnerNumber; i++){

                questionWords.add(new Word(GREwordArray[i], GREtranslationArray[i],"", GREpronunArray[i], GREgrammarArray[i], GREexample1array[i], GREexample2array[i], GREexample3Array[i],GREvocabularyType[i],GREposition[i], GRElearnedDatabase.get(i),GREFav.get(i)));

                if( GRElearnedDatabase.get(i).equalsIgnoreCase("false")) {

                    words.add(new Word(GREwordArray[i], GREtranslationArray[i],"", GREpronunArray[i], GREgrammarArray[i], GREexample1array[i], GREexample2array[i], GREexample3Array[i],GREvocabularyType[i],GREposition[i], GRElearnedDatabase.get(i), GREFav.get(i)));
                }

            }

        }





    }

    private void addingLearnedDatabase(){

        Cursor beginnerRes = IELTSdatabase.getData();
        Cursor TOEFLres =  TOEFLdatabase.getData();
        Cursor SATres = SATdatabase.getData();
        Cursor GREres = GREdatabase.getData();

             while (beginnerRes.moveToNext()){

                IELTSlearnedDatabase.add(beginnerRes.getString(3));
                IELTSFav.add(beginnerRes.getString(2));

             }

             while (TOEFLres.moveToNext()){

                 TOEFLlearnedDatabase.add(TOEFLres.getString(3));
                 TOEFLFav.add(TOEFLres.getString(2));

             }

             while (SATres.moveToNext()){

                 SATlearnedDatabase.add(SATres.getString(3));
                 SATFav.add(SATres.getString(2));

             }

             while (GREres.moveToNext()){

                 GRElearnedDatabase.add(GREres.getString(3));
                 GREFav.add(GREres.getString(2));

             }





    }

    private void updateLearnedDatabase(){

        int j = 1;

        for(int i = 0; i < fiveWords.size(); i++){

            Word word = fiveWords.get(i);




            if(word.vocabularyType.equalsIgnoreCase("IELTS")){

                IELTSdatabase.updateLearned(word.position+"","true");



              //  Toast.makeText(this, "IELTS learned: "+word.position, Toast.LENGTH_SHORT).show();
            }

            if( word.vocabularyType.equalsIgnoreCase("TOEFL")){


                TOEFLdatabase.updateLearned(word.position+"", "true");

             //   Toast.makeText(this, "TOEFL learned: "+word.position, Toast.LENGTH_SHORT).show();
            }

            if(word.vocabularyType.equalsIgnoreCase("SAT")){


                SATdatabase.updateLearned(word.position+"", "true");

              //  Toast.makeText(this, "SAT learned: "+word.position, Toast.LENGTH_SHORT).show();

            }

            if(word.vocabularyType.equalsIgnoreCase("GRE")){


                GREdatabase.updateLearned(word.position+"", "true");

              //  Toast.makeText(this, "GRE learned: "+word.position, Toast.LENGTH_SHORT).show();

            }

            j++;


        }



    }

    private void updateJustlearnedDatabase(int pos){

        justLearnedDatabaseBeginner.removeAll();
        int j = 1;


        if(level.equalsIgnoreCase("beginner")){

            for(int i = 0; i < fiveWords.size(); i++){
                Word word = fiveWords.get(i);

                if(i == pos){

                    justLearnedDatabaseBeginner.insertData(j,""+word.position,word.getWord(),word.getTranslation(),word.getExtra(),word.getPronun(),word.getGrammar(),word.getExample1(),word.getExample2(),word.getExample3(),word.vocabularyType,"true",word.isFavorite(),"true");

                }else {

                }



                justLearnedDatabaseBeginner.insertData(j,""+word.position,word.getWord(),word.getTranslation(),word.getExtra(),word.getPronun(),word.getGrammar(),word.getExample1(),word.getExample2(),word.getExample3(),word.vocabularyType,"true",word.isFavorite(),"false");

                j++;

            }
        }

        if(level.equalsIgnoreCase("intermediate")){


            for(int i = 0; i < fiveWords.size(); i++){
                Word word = fiveWords.get(i);

                if( i == pos){

                    justLearnedDatabaseIntermediate.insertData(j,""+word.position,word.getWord(),word.getTranslation(),word.getExtra(),word.getPronun(),word.getGrammar(),word.getExample1(),word.getExample2(),word.getExample3(),word.vocabularyType,"true",word.isFavorite, "true");


                }else {

                    justLearnedDatabaseIntermediate.insertData(j,""+word.position,word.getWord(),word.getTranslation(),word.getExtra(),word.getPronun(),word.getGrammar(),word.getExample1(),word.getExample2(),word.getExample3(),word.vocabularyType,"true",word.isFavorite, "false");

                }




                j++;

            }


        }

        if(level.equalsIgnoreCase("advance")){


            for(int i = 0; i < fiveWords.size(); i++){
                Word word = fiveWords.get(i);


                if(pos == i){

                    justLearnedDatabaseAdvance.insertData(j,""+word.position,word.getWord(),word.getTranslation(),word.getExtra(),word.getPronun(),word.getGrammar(),word.getExample1(),word.getExample2(),word.getExample3(),word.vocabularyType,"true",word.isFavorite, "true");


                }else {

                    justLearnedDatabaseAdvance.insertData(j,""+word.position,word.getWord(),word.getTranslation(),word.getExtra(),word.getPronun(),word.getGrammar(),word.getExample1(),word.getExample2(),word.getExample3(),word.vocabularyType,"true",word.isFavorite, "false");

                }



                j++;

            }


        }








    }
    private Boolean showInterstitialAd(Boolean isAdShown){

        if(mPublisherInterstitialAd.isLoaded()){



            if(!isAdShown){

                //if( cb != 1){

                    mPublisherInterstitialAd.show();
               // }



                isAdShown = true;
            }


        }
        return isAdShown;
    }

    private void hideViews(){
        topBackground.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        answerCard1.setVisibility(View.INVISIBLE);
        answerCard2.setVisibility(View.INVISIBLE);
        answerCard3.setVisibility(View.INVISIBLE);
        answerCard4.setVisibility(View.INVISIBLE);
        progress1.setVisibility(View.INVISIBLE);
        wordCard.setVisibility(View.INVISIBLE);

    }
    private void unhideViews(){
        topBackground.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        answerCard1.setVisibility(View.VISIBLE);
        answerCard2.setVisibility(View.VISIBLE);
        answerCard3.setVisibility(View.VISIBLE);
        answerCard4.setVisibility(View.VISIBLE);
        progress1.setVisibility(View.VISIBLE);
        wordCard.setVisibility(View.VISIBLE);

    }

    private void whichVocabularyToTest(View v){

        final View view = v;
        fiveWordsCopy.addAll(fiveWords);

        final ArrayList<Word> userSelectedWords = new ArrayList<>(fiveWords);
        String[] items = new String[fiveWords.size()];




        for(int i = 0; i < fiveWords.size(); i++){

            items[i] = fiveWords.get(i).getWord();
        }



        new LovelyChoiceDialog(this, R.style.CheckBoxTintTheme)
                .setTopColorRes(R.color.colorPrimary)
                .setTitle("Which vocabularies do you want to test?")
                .setIcon(R.drawable.ic_stat_notification_icon)
                .setItemsMultiChoice(items, new LovelyChoiceDialog.OnItemsSelectedListener<String>() {
                    @Override
                    public void onItemsSelected(List<Integer> positions, List<String> items) {

                        fiveWords.clear();

                        for(int i = 0; i < items.size(); i++){

                            for(int x = 0; x < userSelectedWords.size(); x++){

                                if(items.get(i).equalsIgnoreCase(userSelectedWords.get(x).getWord())){
                                    fiveWords.add(userSelectedWords.get(x));
                                }
                            }
                        }

                        if(fiveWords.size()>0){


                            progress1.setMax(userSelectedWords.size()+(fiveWords.size()*repeatPerSession));
                            wordView.setText(fiveWords.get(quizCycle).getWord());
                            quizWords(quizCycle, view);
                        }else{

                            fiveWords.addAll(userSelectedWords);
                            updateJustlearnedDatabase(-1);
                            updateLearnedDatabase();

                            Handler handler = new Handler();

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    NewTrain.this.startActivity(new Intent(getApplicationContext(), TrainFinishedActivity.class));
                                    NewTrain.this.finish();
                                }
                            }, 200L);
                        }


                        FIVE_WORD_SIZE = fiveWords.size();



                    }

                })
                .setConfirmButtonText("Confirm")
                .show();
        unhideViews();
    }

    public void downloadAudio(String wordName){

        progressBar.setVisibility(View.VISIBLE);

        wordName = wordName.toLowerCase();
        gsReference = storage.getReferenceFromUrl("gs://fir-userauthentication-f751c.appspot.com/audio/"+wordName+".mp3");

        try{
            localFile = File.createTempFile("Audio","mp3");
        }catch (IOException e){
            e.printStackTrace();
        }

        gsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {


            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
               // Toast.makeText(getApplicationContext() , localFile.getAbsolutePath(),Toast.LENGTH_SHORT).show();
                audioPath = localFile.getAbsolutePath();

                MediaPlayer mp = new MediaPlayer();
               // Toast.makeText(getApplicationContext(),audioPath,Toast.LENGTH_LONG).show();
                try{

                    mp.setDataSource(audioPath);
                    mp.prepare();
                    mp.start();
                    speak.setEnabled(false);
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            speak.setEnabled(true);
                           // Toast.makeText(getApplicationContext(),"play finished", Toast.LENGTH_LONG).show();
                        }
                    });
                }catch (IOException e){
                    e.printStackTrace();
                }



            }
        }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
               // Toast.makeText(getApplicationContext(),"Completed",Toast.LENGTH_LONG).show();
                speak.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);;
            }
        });

    }

}



