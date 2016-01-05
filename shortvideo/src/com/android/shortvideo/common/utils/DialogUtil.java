
package com.android.shortvideo.common.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.MailTo;
import android.view.KeyEvent;

/**
 * 显示对话框的工具
 */
public class DialogUtil
{
    
    
    public static void showDialog(ProgressDialog dialog) {
        dismissDialog(dialog);
        try {
            dialog.setMessage("正在加载�?..");
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (KeyEvent.KEYCODE_BACK == keyCode || KeyEvent.KEYCODE_SEARCH == keyCode) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            dialog.show();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDialog(ProgressDialog dialog, String msg) {
        dismissDialog(dialog);
        try {
            dialog.setMessage(msg);
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (KeyEvent.KEYCODE_BACK == keyCode || KeyEvent.KEYCODE_SEARCH == keyCode) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            dialog.show();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissDialog(ProgressDialog dialog) {
        if (null != dialog) {
            try {
                dialog.dismiss();
                dialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    
    public static void showMailDialg(Context context, String url) {
        MailTo mt = MailTo.parse(url);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_EMAIL, new String[] { mt.getTo() });
        i.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject());
        i.putExtra(Intent.EXTRA_CC, mt.getCc());
        i.putExtra(Intent.EXTRA_TEXT, mt.getBody());
        context.startActivity(i);
    }

    
}
