package net.wakamesoba98.saltmanager.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import net.wakamesoba98.saltmanager.R;

public abstract class ConfirmDialog {
    public void build(Context context, int titleResId, int messageResId) {
        build(context, titleResId, messageResId, null);
    }

    public void build(Context context, int titleResId, int messageResId, String messageText){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        String message = context.getResources().getString(messageResId);
        if (messageText != null) {
            message += "\n\n" + messageText;
        }

        builder.setTitle(context.getResources().getString(titleResId));
        builder.setMessage(message);

        builder.setPositiveButton(context.getResources().getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onPositiveButtonClick();
            }
        });

        builder.setNegativeButton(context.getResources().getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onNegativeButtonClick();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onDialogCancelled();
            }
        });

        builder.create().show();
    }

    public abstract void onPositiveButtonClick();
    public void onNegativeButtonClick() {}
    public void onDialogCancelled() {}
}

