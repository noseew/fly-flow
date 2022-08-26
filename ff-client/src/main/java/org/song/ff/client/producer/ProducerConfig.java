package org.song.ff.client.producer;

import java.time.Duration;

public class ProducerConfig {
    private Duration window;
    private int windowSize;

    public Duration getWindow() {
        return window;
    }

    public void setWindow(Duration window) {
        this.window = window;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }
}
