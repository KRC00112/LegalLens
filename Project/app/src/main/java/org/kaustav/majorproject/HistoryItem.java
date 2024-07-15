package org.kaustav.majorproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class HistoryItem {
    private long timestamp;
    String input;
    String output;

    public HistoryItem() {
        // Empty constructor required by Firebase
    }

    public HistoryItem(long timestamp, String input, String output) {
        this.timestamp = timestamp;
        this.input = input;
        this.output = output;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
