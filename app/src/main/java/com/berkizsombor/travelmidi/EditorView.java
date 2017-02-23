package com.berkizsombor.travelmidi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by berki on 2017. 02. 17..
 */

public class EditorView extends View {

    private String[] keyLabels =
            new String[] { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

    // this is done in an effort to cut out octaves below 300Hz
    // (since phone speakers are not prepared to playback those low frequencies)
    // and also to optimize the performance of the editor view
    private int minOctave = 3;
    private int maxOctave = 8;

    private int numOfKeys = (maxOctave - minOctave) * 12;
    private int numOfNotes = 50; // should be a multiple of 5 for rendering efficiency
    private int textSize = 35;
    private int textPadding = 15;
    private int keyHeight;
    private int keyWidth = 150;

    private String[] keys = new String[numOfKeys];
    private int[] keyVerticalPositions = new int[numOfKeys];
    private int[] lineVerticalPositions = new int[numOfKeys];
    private int[] lineHorizontalPositions = new int[numOfKeys];

    private Idea idea;

    // paint objects
    Paint backgroundPaint, keyPaint, textPaint, notePaint, noteTextPaint;

    // event handlers
    OnNotePressedListener notePressedListener;
    OnNotePlacedListener notePlacedListener;

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

        noteTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        noteTextPaint.setColor(Color.GRAY);
        noteTextPaint.setTextSize(textSize);
    }

    int currentNote = 0;
    int currentOctave = minOctave;

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

        // this is just debug stuff TODO: eventually remove this line
        canvas.drawText(idea.getFileName(), keyWidth, 20, keyPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO: get position and return either onNotePressed or onNotePlaced
        int action = event.getAction();

        switch(event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (event.getX() < keyWidth) {
                    // note pressed
                    if (notePressedListener != null) {
                        byte note = (byte) ( minOctave * 12 + (numOfKeys - (event.getY() / keyHeight)) );
                        notePressedListener.onNotePressed(note);
                    }
                } else {
                    // note placed
                }
                break;
        }

        return true;
    }

    private void renderLoop(Canvas canvas, int i) {
        // keys
        canvas.drawRect(0, i * keyHeight, keyWidth, lineVerticalPositions[i] + keyHeight, keyPaint);
        canvas.drawLine(0, i * keyHeight, getMeasuredWidth(), i * keyHeight, keyPaint);

        // note names
        canvas.drawText(keys[i],
                keyWidth / 3, keyVerticalPositions[i], textPaint);

        // note names horizontally
        for (int j = 1; j < numOfNotes; j += 5) {
            horizontalRenderLoop(canvas, i, j);
            horizontalRenderLoop(canvas, i, j + 1);
            horizontalRenderLoop(canvas, i, j + 2);
            horizontalRenderLoop(canvas, i, j + 3);
            horizontalRenderLoop(canvas, i, j + 4);
        }
    }

    private void horizontalRenderLoop(Canvas canvas, int i, int j) {

        canvas.drawLine(lineHorizontalPositions[j], 0, lineHorizontalPositions[j], getMeasuredHeight(), keyPaint);

        canvas.drawText(keys[i],
                lineHorizontalPositions[j] + 20, keyVerticalPositions[i], noteTextPaint);
    }

    public void setNotePressedListener(OnNotePressedListener notePressedListener) {
        this.notePressedListener = notePressedListener;
    }

    public void setNotePlacedListener(OnNotePlacedListener notePlacedListener) {
        this.notePlacedListener = notePlacedListener;
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
