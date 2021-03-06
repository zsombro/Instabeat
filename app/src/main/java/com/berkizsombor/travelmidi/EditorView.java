/*

Copyright 2017 Berki Zoltán Zsombor

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package com.berkizsombor.travelmidi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.os.SystemClock;

import static android.os.SystemClock.elapsedRealtime;

/**
 * Created by berki on 2017. 02. 17..
 */

public class EditorView extends View {

    private String[] keyLabels =
            new String[] { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

    // this is done in an effort to cut out octaves below 300Hz
    // (since phone speakers are not prepared to playback those low frequencies)
    // and also to optimize the performance of the editor view
    public static final int LOWEST_OCTAVE = 3;
    public static final int HIGHEST_OCTAVE = 8;
    public static final int NUM_CHANNELS = 4;

    private int numOfKeys = (HIGHEST_OCTAVE - LOWEST_OCTAVE) * 12;
    private int numOfNotes = 50; // should be a multiple of 5 for rendering efficiency

    private int textSize = 35;
    private int textPadding = 15;
    private int keyHeight;
    private int keyWidth = 150;

    private int channel = 0;

    private int pressedNote = 0;
    private float startX, startY;

    /*
    Originally I planned to use a linked list to store notes in the view, but I decided against
    them when I discovered that object references take up 4 bytes of space,
    while a boolean/byte array only takes up 1 byte/item. Arrays are also faster to traverse
    than dynamic data structures
     */
    private byte[][][] placedNotes = new byte[NUM_CHANNELS][numOfKeys][numOfNotes];

    private String[] keys = new String[numOfKeys];
    private int[] keyVerticalPositions = new int[numOfKeys];
    private int[] lineVerticalPositions = new int[numOfKeys];
    private int[] lineHorizontalPositions = new int[numOfKeys];

    private Idea idea;

    // paint objects
    Paint backgroundPaint, keyPaint, textPaint, notePaint, noteTextPaint;

    // event handlers
    OnNotePressedListener notePressedListener;
    OnNoteReleasedListener noteReleasedListener;
    OnNotePlacedListener notePlacedListener;
    OnNoteRemovedListener noteRemovedListener;

    public EditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditorView(Context context) {
        super(context);
        init();
    }

    public EditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.idea = null;

        setupPaintObjects();
        generateLabels();
    }

    private void setupPaintObjects() {
        backgroundPaint = new Paint(0);
        backgroundPaint.setColor(Color.BLACK);

        keyPaint = new Paint(0);
        keyPaint.setColor(Color.WHITE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(textSize);

        notePaint = new Paint(0);
        notePaint.setColor(getResources().getColor(R.color.colorPrimary));

        noteTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        noteTextPaint.setColor(Color.GRAY);
        noteTextPaint.setTextSize(textSize);
    }

    int currentNote = 0;
    int currentOctave = LOWEST_OCTAVE;

    private void generateLabels() {
        for (int i = numOfKeys - 1; i >= 0; i--) {

            keys[i] = keyLabels[currentNote] + currentOctave;

            if (currentNote == 11) {
                currentNote = 0;
                currentOctave++;
            } else {
                currentNote++;
            }
        }
    }

    private void generateMeasurements() {

        for (int i = 0; i < numOfKeys; i++) {
            // positions for key labels
            keyVerticalPositions[i] = i * keyHeight + textPadding * 2 + textSize / 2;

            lineVerticalPositions[i] = i * keyHeight;
            lineHorizontalPositions[i] = i * keyWidth;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // calculate the size of keys
        keyHeight = textSize + textPadding * 2;

        generateMeasurements();

        // height = number of keys * height of one key
        // height of one key = height of text + padding top + padding bottom
        // width = width of note * ??? (should be extendable indefinitely?)
        this.setMeasuredDimension(keyWidth * numOfNotes, keyHeight * numOfKeys);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void draw(Canvas canvas) {
        long frameStart = SystemClock.elapsedRealtime();

        super.draw(canvas);

        // background TODO: could be nicer tho :(
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), backgroundPaint);

        // loop rollout for speed
        for (int i = 0; i < numOfKeys; i += 12) {
            renderLoop(canvas, i);
            renderLoop(canvas, i + 1);
            renderLoop(canvas, i + 2);
            renderLoop(canvas, i + 3);
            renderLoop(canvas, i + 4);
            renderLoop(canvas, i + 5);
            renderLoop(canvas, i + 6);
            renderLoop(canvas, i + 7);
            renderLoop(canvas, i + 8);
            renderLoop(canvas, i + 9);
            renderLoop(canvas, i + 10);
            renderLoop(canvas, i + 11);
        }

        // measure and calculate rendering speed
        long fps = (long) (1000.0 / (SystemClock.elapsedRealtime() - frameStart));

        // this is just debug stuff
        canvas.drawText(idea.getFileName(), keyWidth, 20, keyPaint);
        canvas.drawText(String.valueOf(fps), keyWidth, 40, keyPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startY = event.getY();
        startX = event.getX();

        byte note =
                (byte) ( LOWEST_OCTAVE * 12 + (numOfKeys - (startY / keyHeight)) );

        int pos = (byte) (startX / keyWidth);

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN: // note pressed

                if (notePressedListener != null && event.getX() < keyWidth) {
                    notePressedListener.onNotePressed(note);
                    pressedNote = note;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dX = event.getX() - startX;
                float dY = event.getY() - startY;
                double d = Math.sqrt(dX * dX + dY * dY);

                if (d > 50 && noteReleasedListener != null) {
                    noteReleasedListener.onNoteReleased((byte) pressedNote);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (noteReleasedListener != null) {
                    noteReleasedListener.onNoteReleased((byte) pressedNote);
                }
                if (event.getX() > keyWidth) {
                    if (notePlacedListener != null && noteRemovedListener != null) {

                        byte key = (byte) (startY / keyHeight);

                        //placedNotes[key][pos] = (byte) (placedNotes[key][pos] == 0 ? 1 : 0);

                        if (placedNotes[channel][key][pos] == 0) {
                            placedNotes[channel][key][pos] = 1;
                            notePlacedListener.OnNotePlaced(channel + 1, note, pos - 1);
                        } else {
                            placedNotes[channel][key][pos] = 0;
                            noteRemovedListener.OnNoteRemoved(channel + 1, note, pos - 1);
                        }

                        invalidate();
                    }
                }
                break;
        }

        return true;
    }

    public void placeNote(int channel, byte key, int position) {
        placedNotes[channel - 1][HIGHEST_OCTAVE * 12 - key - 1][position + 1] = 1;
    }

    private void renderLoop(Canvas canvas, int i) {
        // keys
        canvas.drawRect(0, i * keyHeight, keyWidth, lineVerticalPositions[i] + keyHeight, keyPaint);
        canvas.drawLine(0, i * keyHeight, getMeasuredWidth(), i * keyHeight, keyPaint);

        // note names horizontally
        for (int j = 0; j < numOfNotes; j += 5) {
            horizontalRenderLoop(canvas, i, j);
            horizontalRenderLoop(canvas, i, j + 1);
            horizontalRenderLoop(canvas, i, j + 2);
            horizontalRenderLoop(canvas, i, j + 3);
            horizontalRenderLoop(canvas, i, j + 4);
        }
    }

    private void horizontalRenderLoop(Canvas canvas, int i, int j) {

        canvas.drawLine(lineHorizontalPositions[j], 0, lineHorizontalPositions[j], getMeasuredHeight(), keyPaint);

        if (placedNotes[channel][i][j] == 1) {
            canvas.drawRect(
                    lineHorizontalPositions[j], i * keyHeight,
                    lineHorizontalPositions[j] + keyWidth, (i * keyHeight) + keyHeight,
                    notePaint
            );
        }

        canvas.drawText(keys[i],
                lineHorizontalPositions[j] + 20, keyVerticalPositions[i], noteTextPaint);
    }

    public void setNotePressedListener(OnNotePressedListener notePressedListener) {
        this.notePressedListener = notePressedListener;
    }

    public void setNotePlacedListener(OnNotePlacedListener notePlacedListener) {
        this.notePlacedListener = notePlacedListener;
    }

    public void setNoteReleasedListener(OnNoteReleasedListener noteReleasedListener) {
        this.noteReleasedListener = noteReleasedListener;
    }

    public void setNoteRemovedListener(OnNoteRemovedListener noteRemovedListener) {
        this.noteRemovedListener = noteRemovedListener;
    }

    public int getChannel() {
        return channel + 1;
    }

    public void setChannel(int channel) {
        this.channel = channel - 1;

        // TODO actually, if my rendering is set up right, i just need to invalidate
        // TODO event handlers!!! save/load!
        invalidate();
    }

    public Idea getIdea() {
        return idea;
    }

    public void setIdea(Idea idea) {
        this.idea = idea;
        invalidate();
        requestLayout();
    }
}
