package com.example.fithub.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fithub.R;
import com.example.fithub.model.Model;
import com.example.fithub.repository.DataRepository;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Locale;

public class TrainingActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private TextView tvTimer;
    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private long startTimeInMillis; // Total time (from JSON)
    private long timeLeftInMillis;  // Current remaining time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        Model.Exercise exercise = (Model.Exercise) getIntent().getSerializableExtra("EXERCISE");

        TextView title = findViewById(R.id.tvTrainingName);
        TextView instructions = findViewById(R.id.tvInstructions);
        ImageView img = findViewById(R.id.imgTraining);
        tvTimer = findViewById(R.id.tvTimer);

        mediaPlayer = android.media.MediaPlayer.create(this, R.raw.beep);

        Button btnStart = findViewById(R.id.btnStart);
        Button btnPause = findViewById(R.id.btnPause);
        Button btnReset = findViewById(R.id.btnReset);
        // Inside onCreate
        Button btnPlus = findViewById(R.id.btnPlus);
        Button btnMinus = findViewById(R.id.btnMinus);

        btnPlus.setOnClickListener(v -> {
            if (!isRunning) {
                timeLeftInMillis += 10000; // Add 10 seconds
                startTimeInMillis += 10000;
                updateCountDownText();
            }
        });

        btnMinus.setOnClickListener(v -> {
            if (!isRunning && timeLeftInMillis > 10000) {
                timeLeftInMillis -= 10000; // Remove 10 seconds
                startTimeInMillis -= 10000;
                updateCountDownText();
            }
        });

        if (exercise != null) {
            title.setText(exercise.name);
            instructions.setText(exercise.instructions);

            // Set initial time from JSON (multiply by 1000 for milliseconds)
            startTimeInMillis = exercise.duration * 1000L;
            timeLeftInMillis = startTimeInMillis;

            updateCountDownText(); // Show initial time (e.g., 01:00)

            int resId = DataRepository.getResId(this, exercise.imageName);
            if (resId != 0) img.setImageResource(resId);
        }

        btnStart.setOnClickListener(v -> {
            if (!isRunning) {
                startTimer();
            }
        });

        btnPause.setOnClickListener(v -> {
            if (isRunning) {
                pauseTimer();
            }
        });

        btnReset.setOnClickListener(v -> {
            resetTimer();
        });
    }

    private void startTimer() {
        // Create a new timer starting from wherever we left off (timeLeftInMillis)
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }



            // inside CountDownTimer onFinish()
            @Override
            public void onFinish() {
                isRunning = false;
                tvTimer.setText("00:00");
                if (mediaPlayer != null) mediaPlayer.start(); // Play sound
            }
        }.start();

        isRunning = true;
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isRunning = false;
    }

    private void resetTimer() {
        // Stop current timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isRunning = false;

        // Reset time to original
        timeLeftInMillis = startTimeInMillis;
        updateCountDownText();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(timeFormatted);
    }
}