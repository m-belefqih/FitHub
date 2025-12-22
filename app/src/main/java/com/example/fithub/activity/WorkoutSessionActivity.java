package com.example.fithub.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fithub.R;
import com.example.fithub.model.Model;

import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.Locale;

public class WorkoutSessionActivity extends AppCompatActivity {

    private List<Model.Exercise> exerciseList;
    private int currentExerciseIndex = 0;

    // Timer Variables
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;      // Current time left on clock
    private long initialTimeInMillis;   // Total time for the exercise
    private boolean isTimerRunning = false;

    // Views
    private TextView tvName, tvTimer, tvInstructions, tvProgress;
    private ImageView imgExercise;
    private Button btnStartPause, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_session);

        // Receive Data
        Model.Workout workout = (Model.Workout) getIntent().getSerializableExtra("WORKOUT_DATA");
        if (workout != null) {
            exerciseList = workout.exercises;
        }

        initViews();
        loadExercise(currentExerciseIndex);
    }

    private void initViews() {
        tvName = findViewById(R.id.tvExerciseName);
        tvTimer = findViewById(R.id.tvTimer);
        tvInstructions = findViewById(R.id.tvInstructions);
        tvProgress = findViewById(R.id.tvProgress);
        imgExercise = findViewById(R.id.imgExercise);
        btnStartPause = findViewById(R.id.btnStartPause);
        btnNext = findViewById(R.id.btnNext);

        // Button Listeners
        btnStartPause.setOnClickListener(v -> {
            if (isTimerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        btnNext.setOnClickListener(v -> nextExercise());
    }

    private void loadExercise(int index) {
        if (index >= exerciseList.size()) {
            finishWorkout();
            return;
        }

        Model.Exercise current = exerciseList.get(index);

        // 1. Update UI Text
        tvName.setText(current.name);
        tvInstructions.setText(current.instructions);
        tvProgress.setText("Exercise " + (index + 1) + " of " + exerciseList.size());

        // 2. Load Image
        int resId = getResources().getIdentifier(current.imageName, "drawable", getPackageName());
        if (resId != 0) imgExercise.setImageResource(resId);

        // 3. Setup Timer Data (but don't start yet)
        initialTimeInMillis = current.duration * 1000L;
        timeLeftInMillis = initialTimeInMillis;
        updateCountDownText();

        // 4. Reset Button State
        isTimerRunning = false;
        btnStartPause.setText("Start");
        btnStartPause.setBackgroundColor(getResources().getColor(R.color.primary));
        btnStartPause.setTextColor(getResources().getColor(R.color.black_primary));
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                btnStartPause.setText("Finished");
                btnStartPause.setEnabled(false);
                Toast.makeText(WorkoutSessionActivity.this, "Exercise Complete!", Toast.LENGTH_SHORT).show();
                // Optional: Auto-load next or wait for user to click Next
            }
        }.start();

        isTimerRunning = true;
        btnStartPause.setText("Pause");
        // Change color to indicate "active/stop" state
        btnStartPause.setBackgroundColor(getResources().getColor(R.color.red));
        btnStartPause.setTextColor(getResources().getColor(R.color.white));
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        isTimerRunning = false;
        btnStartPause.setText("Resume");
        // Reset color to primary
        btnStartPause.setBackgroundColor(getResources().getColor(R.color.primary));
        btnStartPause.setTextColor(getResources().getColor(R.color.black_primary));
    }

    private void nextExercise() {
        // Stop current timer if running
        if (isTimerRunning) {
            countDownTimer.cancel();
        }

        currentExerciseIndex++;
        loadExercise(currentExerciseIndex);

        // Re-enable button if it was disabled by finish()
        btnStartPause.setEnabled(true);
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(timeFormatted);
    }

    private void finishWorkout() {
        Toast.makeText(this, "All Exercises Completed!", Toast.LENGTH_LONG).show();
        finish();
    }
}