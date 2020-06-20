package com.ArCoreDemo.mrganic.utils;

import android.app.Activity;
import android.graphics.Color;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public final class SnackBarHelper {
    private Snackbar snackbar;

    private String message;

    private enum DismissValue {HIDE, SHOW, FINISH}

    private static final int COLOR = Color.parseColor("#4aadff");

    public String getMessage() {
        return message;
    }

    public boolean isVisible() {
        return snackbar != null;
    }

    public void showMessage(Activity activity, String message) {
        this.message = message;
        show(activity, message, DismissValue.HIDE);
    }

    public void showDismissableMessage(Activity activity, String message) {
        this.message = message;
        show(activity, message, DismissValue.SHOW);
    }

    public void hide(Activity activity) {
        activity.runOnUiThread(() -> {
            if (snackbar != null) {
                snackbar.dismiss();
            }
            snackbar = null;
        });
    }

    private void show(Activity activity, String message, DismissValue behavior) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                snackbar =
                        Snackbar.make(
                                activity.findViewById(android.R.id.content),
                                message,
                                Snackbar.LENGTH_INDEFINITE);
                snackbar.getView().setBackgroundColor(COLOR);

                if (behavior != DismissValue.HIDE) {
                    snackbar.setAction("Dismiss", v -> snackbar.dismiss());
                }

                if (behavior == DismissValue.FINISH) {
                    snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            activity.finish();
                        }
                    });
                }

                snackbar.show();
            }
        });
    }
}