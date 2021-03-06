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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.kkorkmaz.quizapp.SetsActivity.category_id;

public class QuestionActivity extends AppCompatActivity implements  View.OnClickListener{

    private TextView question, qCount, timer;
    private Button option1, option2, option3, option4;
    private List<Question> questionList;
    private int quesNum;
    private CountDownTimer countDown;
    private int score;
    private FirebaseFirestore firestore;
    private int setNo;




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

        setNo = getIntent().getIntExtra("SETNO",1);
        firestore = FirebaseFirestore.getInstance();

        getQuestionsList();
        score = 0;


    }


    private void getQuestionsList(){
        questionList = new ArrayList<>();

        firestore.collection("QUIZ").document("CAT" +String.valueOf(category_id))
                .collection("SET" + String.valueOf(setNo))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                   QuerySnapshot questions = task.getResult();

                    for(QueryDocumentSnapshot doc : questions){
                        questionList.add(new Question(doc.getString("QUESTION"),
                                doc.getString("A"),
                                doc.getString("B"),
                                doc.getString("C"),
                                doc.getString("D"),
                                Integer.valueOf(doc.getString("ANSWER"))
                        ));
                    }

                }
                else
                {

                    Toast.makeText(QuestionActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        });


        setQuestion();
    }


    private void setQuestion(){
        timer.setText(String.valueOf(10)); //zaman olarak 10sn

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

         countDown = new CountDownTimer(12000,1000) { //geri sayım saati //coun
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
    public void onClick(View v) { //implement edilen onClicklistener ın methodunu kullanıyoruz.//cevapları tıkladığımızda

        int selectedOption = 0;
//birden fazla option olduğu için herbir değişkene farklı durum yazılabileceğinden switch case kullandım
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

        if(selectedOption == questionList.get(quesNum).getCorrectAns()){ //seçilen buton questionliste tanımladığımız cevaplardan gelen sorularla eşleşiyorsa

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
        },2000); // 2 saniye bekle ve yeni soru getir


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





