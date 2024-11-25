package edu.byuh.cis.cs300.slidefall2024;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private GameView gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        GameMode mode = GameMode.valueOf(i.getStringExtra("GAME_MODE"));
        gv = new GameView(this, mode);
        setContentView(gv);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gv.pauseMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gv.resumeMusic();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gv.shutdown();
    }


}