package com.example.minigame;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minigame.databinding.ActivityMemoryBinding;

import java.util.ArrayList;
import java.util.List;

public class MemoryActivity extends AppCompatActivity {

    static {
        System.loadLibrary("LedDriver");
    }

    String MEMORY = "MemoryActivity";

    // jni native function
    private native static int openLedDriver(String path);
    private native static int closeLedDirver();
    private native static void writeLedDriver(byte[] data, int length);

    // Random LED List (5 개부터 시작)
    List<Integer> randomIntList = new ArrayList<Integer>();

    // buttonList that you pressed
    List<Integer> pressedIntList = new ArrayList<Integer>();

    List<Button> buttonList = new ArrayList<Button>(8);


    int gameScore = 1; // score is same with the level
    // Count Down
    private CountDownTimer countDownTimer;
    private int count = 3;
    private static final int TOTAL = 4 * 1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;

    byte[] data = {0, 0, 0, 0, 0, 0, 0, 0};


    @Override
    protected void onPause() {
        closeLedDirver();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (openLedDriver("/dev/sm9s5422_led") < 0) {
            Toast.makeText(MemoryActivity.this, "Driver open failed", Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        openLedDriver("/dev/sm9s5422_led");
        Log.d(MEMORY, "openLedDriver called");

        initView();

        // LED 나온 순서대로 버튼 클릭
        // 클릭한 버튼의 위치를 list 에 저장
        setBtnClickListener();

    }

    private void emitLED(byte[] data) {
        for (int i = 0; i < (gameScore + 2); i++) {
            // TODO: 2022/11/24 Loading
//            countDownTimer();

            data[randomIntList.get(i)] = 1;
            writeLedDriver(data, data.length);

            data[randomIntList.get(i)] = 0;

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            writeLedDriver(data, data.length);

        }
    }

    // 랜덤 수 생성. gameScore 는 Level임.
    private void setRandomIntList(int gameScore) {
        for (int i = 0; i < (gameScore + 2); i++) {
            int randomValue = (int) (Math.random() * 8 + 0);
            randomIntList.add(randomValue);
            Log.d(MEMORY, "randomIntList : " + randomIntList);
        }
    }

    private void initView() {
        buttonList.clear();
        randomIntList.clear();
        pressedIntList.clear();

        buttonList.add(findViewById(R.id.memory_0_Btn));
        buttonList.add(findViewById(R.id.memory_1_Btn));
        buttonList.add(findViewById(R.id.memory_2_Btn));
        buttonList.add(findViewById(R.id.memory_3_Btn));
        buttonList.add(findViewById(R.id.memory_4_Btn));
        buttonList.add(findViewById(R.id.memory_5_Btn));
        buttonList.add(findViewById(R.id.memory_6_Btn));
        buttonList.add(findViewById(R.id.memory_7_Btn));


        Button memoryGameStartBtn = findViewById(R.id.memory_game_start_Btn);
        memoryGameStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.memory_game_guide_Tv).setVisibility(View.VISIBLE);
                findViewById(R.id.memory_game_start_Btn).setVisibility(View.GONE);
                findViewById(R.id.count_down_Tv).setVisibility(View.VISIBLE);

                // 숫자 카운트 다운. 0 이 되면 왼쪽 LED 점등.
                countDownTimer();
                countDownTimer.start();

                // set randomIntList
                setRandomIntList(gameScore);



            }
        });

    }

    private void countDownTimer() {
        countDownTimer = new CountDownTimer(TOTAL, COUNT_DOWN_INTERVAL) {

            @Override
            public void onTick(long l) {
                TextView countDownTv = findViewById(R.id.count_down_Tv);
                countDownTv.setText(String.valueOf(count));
                count--;
                if (count == 0){
                    findViewById(R.id.count_down_Tv).setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFinish() {
                findViewById(R.id.count_down_Tv).setVisibility(View.INVISIBLE);
                count = 3;

                // LED Emitting
                emitLED(data);
            }
        };
    }

    // LED 나온 순서대로 버튼 클릭
    // 클릭한 버튼의 위치를 list 에 저장
    private void setBtnClickListener() {

        for (int i = 0; i < 8; i++) {
            int finalI = i;
            buttonList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pressedIntList.add(finalI);
                    int lastIndex = pressedIntList.size() - 1;
                    if (pressedIntList.get(lastIndex) == randomIntList.get(lastIndex)) {
                        // TODO: 2022/11/24 It's OK
                        Log.d(MEMORY, "It's Ok");
                    } else {
                        // TODO: 2022/11/24 Game Over
                        Log.d(MEMORY, "Game Over");
                    }

                    // Level  통과 시
                    if (pressedIntList.size() == randomIntList.size()) {
                        // TODO: 2022/11/24 다음 레벨
                        randomIntList.clear();
                        Log.d(MEMORY, "Go Next Level");
                        gameScore += 1;

                        TextView memoryLevelTv = findViewById(R.id.memory_level_Tv);
                        memoryLevelTv.setText("Level " + gameScore);
                        Toast.makeText(MemoryActivity.this, "Next Level " + gameScore, Toast.LENGTH_LONG).show();

                        countDownTimer.start();

                        setRandomIntList(gameScore);


                    }

                }
            });
        }

        // new thread th1
        // th1 이

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            countDownTimer.cancel();
        } catch (Exception e) {

        }
        countDownTimer = null;
    }
}


