package edu.byuh.cis.cs300.slidefall2024;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * the main "view" class of the program.
 * All drawing and user interaction "begins" here, although
 * this class delegates much of the work to other classes.
 */
public class GameView extends View {

    private Grid grid;
    private boolean firstRun;
    private GuiButton[] buttons;
    private List<GuiToken> tokens;
    private GameBoard engine;
    private Timer tim;

    /**
     * This class pumps out "timer" events at
     * regular intervals, so we can do animation.
     */
    private class Timer extends Handler {

        public Timer() {
            sendMessageDelayed(obtainMessage(), 100);
        }

        @Override
        public void handleMessage(Message m) {
            for (GuiToken b : tokens) {
                b.move();
            }
            invalidate();
            sendMessageDelayed(obtainMessage(), 100);
        }
    }


    /**
     * the public constructor. Like all view subclasses,
     * it requires a Context object, which gets forwarded
     * on to the superclass
     * @param context a reference to the Activity that created this view
     */
    public GameView(Context context) {
        super(context);
        firstRun = true;
        buttons = new GuiButton[10];
        tokens = new ArrayList<>();
        engine = new GameBoard();
        tim = new Timer();
    }

    /**
     * Whatever you want the user to see, needs to get
     * drawn from this method
     * @param c The Canvas object, provided by the OS when onDraw is called
     */
    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        c.drawColor(Color.WHITE);
        if (firstRun) {
            init();
            firstRun = false;
        }

        grid.draw(c);
        for (var tok : tokens) {
            tok.draw(c);
        }
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
        grid = new Grid(gridX, gridY, cellSize);
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

    /**
     * Handle touchscreen events. Right now, we only deal with button presses
     * @param m the MotionEvent object, provided by OS
     * @return true, always
     */
    @Override
    public boolean onTouchEvent(MotionEvent m) {
        if (m.getAction() == MotionEvent.ACTION_DOWN) {
            float x = m.getX();
            float y = m.getY();
            boolean missed = true;
            for (GuiButton b : buttons) {
                if (b.contains(x,y)) {
                    b.press();
                    var tok = new GuiToken(engine.getCurrentPlayer(), b, getResources());
                    engine.submitMove(b.getLabel());
                    tokens.add(tok);
                    missed = false;
                }
            }
            if (missed) {
                Toast t = Toast.makeText(getContext(), "Please touch a button", Toast.LENGTH_SHORT);
                t.show();
            }
        } else if (m.getAction() == MotionEvent.ACTION_UP) {
            for (GuiButton b : buttons) {
                b.release();
            }
        }
        invalidate();
        return true;
    }

}
