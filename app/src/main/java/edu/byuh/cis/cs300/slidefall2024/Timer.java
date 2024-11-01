package edu.byuh.cis.cs300.slidefall2024;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * This class pumps out "timer" events at
 * regular intervals, so we can do animation.
 */
public class Timer extends Handler {

    private List<TickListener> fans;
    private boolean paused;

    public Timer() {
        paused = false;
        fans = new ArrayList<>();
        sendMessageDelayed(obtainMessage(), 100);
    }

    public void register(TickListener t) {
        fans.add(t);
    }

    public void unregister(TickListener t) {
        fans.remove(t);
    }

    public void pause() {
        paused = true;
    }

    public void restart() {
        paused = false;
    }

    @Override
    public void handleMessage(Message m) {
        if (!paused) {
            for (var b : fans) {
                b.onTick();
            }
        }
        sendMessageDelayed(obtainMessage(), 100);
    }
}

