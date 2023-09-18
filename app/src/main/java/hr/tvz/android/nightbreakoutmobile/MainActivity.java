package hr.tvz.android.nightbreakoutmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import hr.tvz.android.nightbreakoutmobile.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private boolean isSoundOn = false;
    private MediaPlayer mainMenuMusic;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mainMenuMusic = MediaPlayer.create(this, R.raw.mainscreen);
        playSound();
    }

    public void playSound() {
        if (mainMenuMusic != null && !mainMenuMusic.isPlaying()) {
            mainMenuMusic.start();
        }
    }

    public void stopSound() {
        if (mainMenuMusic != null) {
            mainMenuMusic.stop();
            mainMenuMusic.prepareAsync();
        }
    }

    protected void onPause() {
        super.onPause();
        stopSound();
    }

    public void start(View view){
        GameView gameView = new GameView(this);
        setContentView(gameView);
        stopSound();
    }
}