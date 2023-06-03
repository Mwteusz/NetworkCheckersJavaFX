package com.checkers.utils;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.text.SimpleDateFormat;

public class Timer {
    Timeline timeline;
    private long millisecondsElapsed = 0;
    SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:SSS");
    public boolean isRunning = false;

    public String getTime() {
        return formatter.format(millisecondsElapsed);
    }

    public Timer() {
        timeline = new Timeline(new KeyFrame(Duration.millis(1), e -> millisecondsElapsed++));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public void start(long time) {
        timeline.play();
        millisecondsElapsed = time;
        isRunning = true;
    }

    public void stop(long time) {
        timeline.stop();
        millisecondsElapsed = time;
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
