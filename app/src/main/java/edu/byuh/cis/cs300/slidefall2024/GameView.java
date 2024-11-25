package edu.byuh.cis.cs300.slidefall2024;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * the main "view" class of the program.
 * All drawing and user interaction "begins" here, although
 * this class delegates much of the work to other classes.
 */
public class GameView extends AppCompatImageView implements TickListener {

    private Grid grid;
    private Grid grid2;
    private boolean firstRun;
    private GuiButton[] buttons;
    private List<GuiToken> tokens;
    private GameBoard engine;
    private Timer tim;
    private GameMode mode;
    private MediaPlayer song;

    //In the original Slide game, the computer was X
    //and the human was O, so I've kept that here.
    //But it doesn't really matter.
    private static final Player COMPUTER = Player.X;
    private static final Player HUMAN = Player.O;



    /**
     * the public constructor. Like all view subclasses,
     * it requires a Context object, which gets forwarded
     * on to the superclass
     * @param context a reference to the Activity that created this view
     */
    public GameView(Context context, GameMode gmo) {
        super(context);
        mode = gmo;
        firstRun = true;
        buttons = new GuiButton[10];
        tokens = new ArrayList<>();
        engine = new GameBoard();
        tim = new Timer();
        tim.register(this);
        song = MediaPlayer.create(getContext(), R.raw.music);
        song.setLooping(true);
        song.start();
        setImageResource(R.drawable.bkgd5);
        setScaleType(ScaleType.FIT_XY);
    }

    /**
     * Pause the background music. This method is intended
     * to be called by Activity.onPause().
     */
    public void pauseMusic() {
        song.pause();
    }

    /**
     * Resume the background music. This method is intended
     * to be called by Activity.onResume().
     */
    public void resumeMusic() {
        song.start();
    }

    /**
     * Perform memory management tasks just prior to our
     * parent Activity going down. Release the MediaPlayer
     * object, and disable the Timer.
     * Intended to be called by Activity.onDestroy()
     */
    public void shutdown() {
        song.release();
        tim.pause();
    }

    /**
     * Whatever you want the user to see, needs to get
     * drawn from this method
     * @param c The Canvas object, provided by the OS when onDraw is called
     */
    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        //c.drawColor(Color.WHITE);
        if (firstRun) {
            init();
            firstRun = false;
        }

        grid.draw(c);
        grid2.draw(c);
        tokens.forEach(t -> t.draw(c));
        for (var b : buttons) {
            b.draw(c);
        }
    }

    /**
     * this method sets up some of the variables
     * needed for drawing the UI
     */
    private void init() {
        float w = getWidth();
        float h = getHeight();
        float unit = w/16f;
        float gridX = unit * 2.5f;
        float cellSize = unit * 2.3f;
        float gridY = unit * 9;
        //grid = new Grid(gridX, gridY, cellSize);
        grid = Grid.getGridInstance(gridX, gridY, cellSize);
        grid2 = Grid.getGridInstance(0,0, 1000);
        float buttonTop = gridY - cellSize;
        float buttonLeft = gridX - cellSize;

        //instantiate the top row of buttons
        buttons[0] = new GuiButton('1', this, buttonLeft + cellSize*1, buttonTop, cellSize);
        buttons[1] = new GuiButton('2', this, buttonLeft + cellSize*2, buttonTop, cellSize);
        buttons[2] = new GuiButton('3', this, buttonLeft + cellSize*3, buttonTop, cellSize);
        buttons[3] = new GuiButton('4', this, buttonLeft + cellSize*4, buttonTop, cellSize);
        buttons[4] = new GuiButton('5', this, buttonLeft + cellSize*5, buttonTop, cellSize);

        //instantiate the left column of buttons
        buttons[5] = new GuiButton('A', this, buttonLeft, buttonTop + cellSize*1, cellSize);
        buttons[6] = new GuiButton('B', this, buttonLeft, buttonTop + cellSize*2, cellSize);
        buttons[7] = new GuiButton('C', this, buttonLeft, buttonTop + cellSize*3, cellSize);
        buttons[8] = new GuiButton('D', this, buttonLeft, buttonTop + cellSize*4, cellSize);
        buttons[9] = new GuiButton('E', this, buttonLeft, buttonTop + cellSize*5, cellSize);
    }

    private boolean anyMovers() {
//        boolean result = false;
//        for (var t : tokens) {
//            if (t.isMoving()) {
//                result = true;
//                break;
//            }
//        }
//        return result;
        return tokens.stream().anyMatch(t -> t.isMoving());
        //anyMatch - kind of like OR
        //allMatch - kind of like AND
    }

    /**
     * Handle touchscreen events. Right now, we only deal with button presses
     * @param m the MotionEvent object, provided by OS
     * @return true, always
     */
    @Override
    public boolean onTouchEvent(MotionEvent m) {
        cleanupFallenTokens();
        if (m.getAction() == MotionEvent.ACTION_DOWN) {
            if (anyMovers() == false) {
                float x = m.getX();
                float y = m.getY();
                boolean missed = true;
                for (GuiButton b : buttons) {
                    if (b.contains(x, y)) {
                        b.press();
                        handleButtonPress(b);
                        missed = false;
                    }
                }
                if (missed) {
                    Toast t = Toast.makeText(getContext(), "Please touch a button", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        } else if (m.getAction() == MotionEvent.ACTION_UP) {
            for (GuiButton b : buttons) {
                b.release();
            }
        }
        return true;
    }

    private void cleanupFallenTokens() {
        for (GuiToken t : tokens) {
            if (t.isInvisible(getHeight())) {
                tim.unregister(t);
                tokens.remove(t);
                break;
            }
        }
    }

    private void setupAnimation(GuiButton b, GuiToken tok) {
        List<GuiToken> neighbors = new ArrayList<>();
        neighbors.add(tok);
        if (b.isTopButton()) {
            char col = b.getLabel();
            for (char row = 'A'; row <= 'E'; row++) {
                GuiToken other = findTokenAt(row, col);
                if (other != null) {
                    neighbors.add(other);
                } else {
                    break;
                }
            }
            for (var n : neighbors) {
                n.startMovingDown();
            }
        } else {
            char row = b.getLabel();
            for (char col = '1'; col <= '5'; col++) {
                GuiToken other = findTokenAt(row, col);
                if (other != null) {
                    neighbors.add(other);
                } else {
                    break;
                }
            }
            for (var n : neighbors) {
                n.startMovingRight();
            }
        }
    }

    private GuiToken findTokenAt(char row, char col) {
        for (var t : tokens) {
            if (t.matches(row,col)) {
                return t;
            }
        }
        return null;
    }

    @Override
    public void onTick() {
        if (!GuiToken.anyMovers()) {
            Player winner = engine.checkForWin();
            if (winner != Player.BLANK) {
                tim.pause();
                String message;
                if (winner == Player.X) {
                    message = "Pineapple wins!";
                } else if (winner == Player.O) {
                    message = "Banana wins!";
                } else {
                    message = "It's a tie!";
                }
                AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
                ab.setMessage(message)
                        .setTitle("GAME OVER!")
                        .setCancelable(false)
                        .setPositiveButton("Play again", (d,i) -> reset())
                        .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int i) {
                                ((Activity)getContext()).finish();
                            }
                        });
                AlertDialog box = ab.create();
                box.show();
            } else {
                if (mode == GameMode.ONE_PLAYER
                        && engine.getCurrentPlayer() == COMPUTER) {
                    post(() -> {
                        int r = (int)(Math.random() * 10);
                        handleButtonPress(buttons[r]);
                    });
                }
            }
        }
        invalidate();
    }

    private void handleButtonPress(GuiButton b) {
        var tok = new GuiToken(engine.getCurrentPlayer(), b, getResources());
        engine.submitMove(b.getLabel());
        tokens.add(tok);
        tim.register(tok);
        setupAnimation(b, tok);
    }

    private void reset() {
        engine.clear();
        tokens.clear();
        tokens.forEach(t -> tim.unregister(t));
        tim.restart();
    }
}
