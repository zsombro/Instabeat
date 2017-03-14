package com.berkizsombor.travelmidi;

/**
 * Created by berki on 2017. 02. 21..
 */

public interface OnNotePlacedListener {
    void OnNotePlaced(int channel, byte note, int pos);
}
