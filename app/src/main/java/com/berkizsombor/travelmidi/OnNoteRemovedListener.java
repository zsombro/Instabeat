package com.berkizsombor.travelmidi;

/**
 * Created by berki on 2017. 03. 10..
 */

public interface OnNoteRemovedListener {
    void OnNoteRemoved(byte note, int pos);
}