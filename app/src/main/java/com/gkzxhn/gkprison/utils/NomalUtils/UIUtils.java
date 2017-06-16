package com.gkzxhn.gkprison.utils.NomalUtils;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;

/**
 * Author: Huang ZN
 * Date: 2016/12/26
 * Email:943852572@qq.com
 * Description:
 */

public class UIUtils {

    private static String defaultMsg = "请稍候...";

    /**
     *
     * @param context
     */
    public static ProgressDialog showProgressDialog(Context context){
        ProgressDialog progressDialog = null;
        try {
            progressDialog = showProgressDialog(context, defaultMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return progressDialog;
    }

    /**
     *
     * @param context
     * @param msg
     */
    public static ProgressDialog showProgressDialog(Context context, String msg){
        if (context instanceof Application)
            throw new IllegalArgumentException("not supported context");
        if (Thread.currentThread() != Looper.getMainLooper().getThread()){
            throw new IllegalStateException("must show dialog in main thread");
        }
        ProgressDialog dialog = null;
        try {
            dialog = new ProgressDialog(context);
            dialog.setMessage(msg);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    /**
     * dismiss
     * @param dialog
     */
    public static void dismissProgressDialog(ProgressDialog... dialog){
        if (Thread.currentThread() != Looper.getMainLooper().getThread()){
            throw new IllegalStateException("this controller must in main thread");
        }
        if (dialog == null || dialog.length < 1)
            return;
        for (ProgressDialog d : dialog) {
            if (d != null && d.isShowing())
                d.dismiss();
        }
    }

    /**
     * show对话框  cancelable为默认true
     * @param context
     * @param msg
     * @param okListener
     * @param cancelListener
     * @return
     */
    public static AlertDialog showAlertDialog(Context context, String msg, DialogInterface.OnClickListener
            okListener, DialogInterface.OnClickListener cancelListener){
        return showAlertDialog(context, msg, okListener, cancelListener, true);
    }

    /**
     * show对话框  可传入是否cancelable
     * @param context
     * @param msg
     * @param okListener
     * @param cancelListener
     * @param cancelable
     * @return
     */
    public static AlertDialog showAlertDialog(Context context, String msg, DialogInterface.OnClickListener
            okListener, DialogInterface.OnClickListener cancelListener, boolean cancelable){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setTitle(R.string.reminder)
                .setMessage(msg)
                .setPositiveButton(R.string.ok, okListener)
                .setCancelable(cancelable);
        if (cancelListener != null) {
            builder.setNegativeButton(R.string.cancel, cancelListener);
        }
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    /**
     * 显示列表对话框
     * @param context
     * @param titleMsg
     * @param items
     * @param listener
     * @return
     */
    public static AlertDialog showListDialog(Context context, String titleMsg,
           String[] items, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setTitle(titleMsg)
                .setItems(items, listener);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    /**
     * 协议对话框
     */
    public static AlertDialog showSoftProtocolDialog(Context context) {
        if (context instanceof Application)
            throw new IllegalArgumentException("not support context");
        if (Thread.currentThread() != Looper.getMainLooper().getThread()){
            throw new IllegalStateException("must show dialog in main thread");
        }
        AlertDialog.Builder agreement_builder = new AlertDialog.Builder(context);
        View agreement_view = View.inflate(context, R.layout.software_agreement_dialog, null);
        final AlertDialog agreement_dialog = agreement_builder.create();
        Button btn_ok = (Button) agreement_view.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreement_dialog.dismiss();
            }
        });
        agreement_dialog.setCancelable(true);
        agreement_builder.setView(agreement_view);
        agreement_builder.show();
        return agreement_dialog;
    }

    /**
     * 显示确认提交注册的提示对话框
     */
    public static AlertDialog showConfirmDialog(Context context, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.register_confirm_remind);
        builder.setPositiveButton(R.string.ok, okListener).setNegativeButton(R.string.confirm_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog register_remind_dialog = builder.create();
        register_remind_dialog.show();
        return register_remind_dialog;
    }

    /**
     * 获取用户信息失败   提示重新登录
     * @param context
     * @param okListener
     */
    public static AlertDialog showReLoginDialog(Context context, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.reminder);
        builder.setMessage(R.string.get_info_failed);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, okListener);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    /**
     *  云信id在其他设备登录
     * @param context
     * @param okListener
     * @return
     */
    public static AlertDialog showKickoutDialog(Context context, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.kickout_remind);
        builder.setCancelable(false);
        builder.setMessage("您的账号" + SPUtil.get(context, SPKeyConstants.ACCESS_TOKEN, "")
                + "在其他设备登录，点击重新登录。");
        builder.setPositiveButton(R.string.relogin, okListener);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    /**
     * dismiss dialog
     * @param alertDialogs
     */
    public static void dismissAlertDialog(AlertDialog... alertDialogs){
        if (Thread.currentThread() != Looper.getMainLooper().getThread())
            throw new IllegalStateException("dismiss dialog must be in main thread");
        if (alertDialogs == null || alertDialogs.length < 1)
            return;
        for (AlertDialog dialog : alertDialogs){
            if (dialog != null) {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        }
    }

}
