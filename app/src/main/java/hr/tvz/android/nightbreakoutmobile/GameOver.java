package hr.tvz.android.nightbreakoutmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import hr.tvz.android.nightbreakoutmobile.databinding.GameOverBinding;

public class GameOver extends AppCompatActivity {

    private boolean isSoundOn = false;

    private MediaPlayer endSong;

    TextView pointsText;
    TextView hsText;
    ImageView newHighScore;

    private static final String PREFS_NAME = "MyPrefs";
    private static final String HIGH_SCORE_KEY = "HighScore";

    private GameOverBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = GameOverBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        newHighScore = findViewById(R.id.newHighScore);
        pointsText = findViewById(R.id.points);
        hsText = findViewById(R.id.hsText);
        endSong = MediaPlayer.create(this, R.raw.endsound);
        playSound();

        int points = getIntent().getExtras().getInt("points");
        int hs = getHighScore();
        if(points > hs){
            hs = points;
            newHighScore.setVisibility(View.VISIBLE);
            saveHighScore(hs);
        }
        binding.hsText.setText("Highscore: " + hs);
        binding.points.setText(String.valueOf(points));
    }

    public void playSound() {
        if (endSong != null && !endSong.isPlaying()) {
            endSong.start();
        }
    }

    public void stopSound() {
        if (endSong != null) {
            endSong.stop();
            endSong.prepareAsync();
        }
    }

    private void saveHighScore(int highScore) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(HIGH_SCORE_KEY, highScore);
        editor.apply();
    }

    private int getHighScore() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt(HIGH_SCORE_KEY, 0);
    }

    public void restart(View view){
        Intent intent = new Intent(GameOver.this, MainActivity.class);
        startActivity(intent);
        stopSound();
        finish();
    }

    public void exit(View view){
        stopSound();
        finish();
    }
}
