package com.example.screenbreak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UnlockReceiver extends BroadcastReceiver {
    private int unlockCount = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            // Se incrementa el contador cada vez que el usuario desbloquea el dispositivo
            unlockCount++;
        }
    }

    public int getUnlockCount() {
        return unlockCount;
    }
}
