package com.example.nadri4_edit1;

public interface ItemTouchHelperListener {
    boolean onItemMove(int from_position , int to_position);
    void onItemSwipe(int position);
}
