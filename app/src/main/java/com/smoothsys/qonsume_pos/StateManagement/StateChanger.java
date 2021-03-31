package com.smoothsys.qonsume_pos.StateManagement;

import android.util.Log;

/**
 * Created by Pontu on 2018-03-19.
 */

public class StateChanger {

    private static ScreenState currentState = ScreenState.firstTimeLaunched;

    public static ScreenState getState() {return currentState;}

    public static void setState(ScreenState newState) {
        currentState = newState;
        Log.d("ScreenState", "Screen State changed to " + getState().name());
    }


}
