package com.kkorkmaz.quizapp;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity implements  View.OnClickListener{

    private TextView question, qCount, timer;
    private Button option1, option2, option3, option4;
    private List<Question> questionList;
    private int quesNum;
    private CountDownTimer countDown;
    private int score;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        question = findViewById(R.id.question);
        qCount = findViewById(R.id.quest_num);
        timer = findViewById(R.id.countdown);

        option1=findViewById(R.id.option1);
        option2=findViewById(R.id.option2);
        option3=findViewById(R.id.option3);
        option4=findViewById(R.id.option4);

        option1.setOnClickListener(this); //çalışabilmesi için AppCompatActivity ye implemet etmemiz lazım
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);

        getQuestionsList();
        score = 0;


    }


    private void getQuestionsList(){
        questionList = new ArrayList<>();

        questionList.add(new Question("Question 1","A","B","C","D",2));
        questionList.add(new Question("Question 2","B","B","D","A",2));
        questionList.add(new Question("Question 3","C","B","A","D",2));
        questionList.add(new Question("Question 4","A","D","C","B",2));
        questionList.add(new Question("Question 5","C","D","A","D",2));

        setQuestion();
    }


    private void setQuestion(){
        timer.setText(String.valueOf(10));

        question.setText(questionList.get(0).getQuestion());
        option1.setText(questionList.get(0).getOptionA());
        option2.setText(questionList.get(0).getOptionB());
        option3.setText(questionList.get(0).getOptionC());
        option4.setText(questionList.get(0).getOptionD());

        qCount.setText(String.valueOf(1) + "/" + String.valueOf(questionList.size()));

        startTimer();

        quesNum = 0;

    }

        private void startTimer(){

         countDown = new CountDownTimer(12000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished < 10000)
                timer.setText(String.valueOf(millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {
                //zaman 0 olduğunda sıradaki soruyu getir.
                changeQuestion();

            }
        };
        countDown.start();

        }




    @Override
    public void onClick(View v) { //implement edilen onClicklistener ın methodunu kullanıyoruz.

        int selectedOption = 0;

        switch(v.getId()){
            case R.id.option1 :
                selectedOption = 1 ;
                break;

            case R.id.option2:
                selectedOption = 2 ;
                break;

            case R.id.option3:
                selectedOption = 3 ;
                break;

            case R.id.option4:
                selectedOption = 4 ;
                break;

            default:
        }
        //zaman durdurma
        countDown.cancel();

        checkAnswer(selectedOption, v);
    }

    private void checkAnswer(int selectedOption, View view){

        if(selectedOption == questionList.get(quesNum).getCorrectAns()){

            //doğru cevap
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //BackgroundTintList kullanabilmek için
                ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));

                score++;
            }


        }
        else{
            //yanlış cevap

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.RED));

                switch (questionList.get(quesNum).getCorrectAns())
                {
                    case 1:
                        option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        break;
                    case 2:
                        option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        break;
                    case 3:
                        option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        break;
                    case 4:
                        option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        break;

                }

            }

        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeQuestion();
            }
        },2000);


    }

           private void changeQuestion(){

                if(quesNum < questionList.size() - 1){

                    quesNum ++;
                    playAnim(question,0,0);
                    playAnim(option1,0,1);
                    playAnim(option2,0,2);
                    playAnim(option3,0,3);
                    playAnim(option4,0,4);

                    // questionCount arttırma işlemi
                    qCount.setText(String.valueOf(quesNum+1) + "/" + String.valueOf(questionList.size()));

                    timer.setText(String.valueOf(10));
                        startTimer();

                }
                else{
                    //score Activity ye git
                    Intent intent = new Intent(QuestionActivity.this,ScoreActivity.class);
                    intent.putExtra("SCORE", String.valueOf(score) + "/" + String.valueOf(questionList.size()));
                   //programın geri dönüşünü engelliyor.
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                   // QuestionActivity.this.finish();
                }
           }

           private void playAnim(final View view, final int value , final int viewNum)
           {
                view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500)
                        .setStartDelay(100).setInterpolator(new DecelerateInterpolator())
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {

                                if(value == 0){

                                    switch (viewNum){
                                        case 0:
                                            ((TextView)view).setText(questionList.get(quesNum).getQuestion());
                                            break;

                                        case  1 :
                                            ((Button)view).setText(questionList.get(quesNum).getOptionA());
                                            break;
                                        case  2 :
                                            ((Button)view).setText(questionList.get(quesNum).getOptionB());
                                            break;
                                        case  3 :
                                            ((Button)view).setText(questionList.get(quesNum).getOptionC());
                                            break;
                                        case  4 :
                                            ((Button)view).setText(questionList.get(quesNum).getOptionD());
                                            break;

                                    }
                                    //doğru veya yanlış seçilen butonların renklerinin diğer sorulara geçmesini engelleme ve kendi rengine dönme

                                    //if(viewNum != 0)
                                        //((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99")));



                                    playAnim(view,1,viewNum);
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
           }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        countDown.cancel();

    }
}




