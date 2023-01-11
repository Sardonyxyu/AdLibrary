package com.yingyongduoduo.ad.dialog;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.yingyongduoduo.ad.R;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created by Sardonyxyu on 2018/3/9.
 */
public class DialogTextViewBuilderAD {
    private String title;
    private String content;
    private int titleSize;
    private int contentSize;
    private int contentColor;
    private String oneButton;
    private String twoButton;
    private TextView tv_primary;
    private TextView tv_secondary;
    private boolean isClose;
    private DialogOnClickListener listener;

    public Context ctx;
    private AlertDialog alertDialog;
    private AlertDialog.Builder dialogBuilder;
    private View dialogView;
    private boolean isCancel;

    public DialogTextViewBuilderAD(Builder builder) {
        this.ctx = builder.ctx;
        this.title = builder.title;
        this.content = builder.content;
        this.titleSize = builder.titleSize;
        this.contentSize = builder.contentSize;
        this.contentColor = builder.contentColor;
        this.oneButton = builder.oneButton;
        this.twoButton = builder.twoButton;
        this.tv_primary = builder.tv_primary;
        this.tv_secondary = builder.tv_secondary;
        this.isClose = builder.isClose;
        this.listener = builder.listener;

        this.alertDialog = builder.alertDialog;
        this.dialogBuilder = builder.dialogBuilder;
        this.dialogView = builder.dialogView;
    }

    /**
     * 获取AlertDialog
     * @return
     */
    public AlertDialog getAlertDialog() {
        return alertDialog;
    }

    /**
     * 获取对话框显示的view布局
     * @return
     */
    public View getDialogView() {
        return dialogView;
    }

    /**
     * 对话框关闭
     */
    public void dismissDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    /**
     * 关闭反射阻止的对话框
     */
    public void dismissReflectDialog() {
        try {
            Field field = alertDialog.getClass().getSuperclass()
                    .getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(alertDialog, true);
        } catch (Exception e) {
        }
        alertDialog.dismiss();
    }

    /**
     * 获取确定按钮
     * @return
     */
    public TextView getPrimaryButton() {
        return tv_primary;
    }

    /**
     * 获取取消按钮
     * @return
     */
    public TextView getSecondaryButton() {
        return tv_secondary;
    }

    public static class Builder implements OnClickListener {
        private final Context ctx;
        private final String title;
        private final String content;
        private int titleSize;
        private int contentSize;
        private int contentColor;
        private final String oneButton;
        private String twoButton;
        private boolean isClose = true;
        private boolean isSystemType = false;
        private DialogOnClickListener listener;

        private AlertDialog alertDialog;
        private AlertDialog.Builder dialogBuilder;
        private View dialogView;
        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_primary;
        private TextView tv_secondary;
        private View viewSpace;
        private View close;

        /** 创建dialog，设置view布局到dialog
         * @param ctx
         * @param title
         * @param content
         * @param oneButton
         */
        public Builder(Context ctx, String title, String content,
                       String oneButton) {
            this.ctx = ctx;
            this.title = title;
            this.content = content;
            this.oneButton = oneButton;
            //用原本的布局
            setView(-100);
        }

        /** 创建dialog，设置view布局到dialog
         * @param ctx
         * @param title
         * @param content
         * @param oneButton
         * @param view 自定义布局，必须包含原本的控件id
         */
        public Builder(Context ctx, String title, String content,
                       String oneButton, int view) {
            this.ctx = ctx;
            this.title = title;
            this.content = content;
            this.oneButton = oneButton;
            setView(view);
        }

        /**
         * 监听
         * @param listener
         * @return
         */
        public Builder listener(DialogOnClickListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * 设置button按钮点击后不关闭Dialog
         * @return
         */
        public Builder notClose() {
            this.isClose = false;
            return this;
        }

        public Builder titleSize(int titleSize) {
            this.titleSize = titleSize;
            tv_title.setTextSize(titleSize);
            return this;
        }

        public Builder contentSize(int contentSize) {
            this.contentSize = contentSize;
            tv_content.setTextSize(contentSize);
            return this;
        }

        public Builder contentColor(int contentColor) {
            this.contentColor = contentColor;
            tv_content.setTextColor(contentColor);
            return this;
        }

        public Builder isGravity(boolean isGravity) {
            if (!isGravity) {
                tv_content.setGravity(Gravity.CENTER_VERTICAL);
            }
            return this;
        }

        public Builder twoButton(String twoButton) {
            this.twoButton = twoButton;
            return this;
        }

        public Builder titleClick(OnClickListener onClickListener) {
            tv_title.setOnClickListener(onClickListener);
            return this;
        }

        public Builder contentClick(OnClickListener onClickListener) {
            tv_content.setOnClickListener(onClickListener);
            return this;
        }

        public Builder setContentTextPartColor(String color, int start, int end) {
            SpannableString spannableString = new SpannableString(content);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_content.setText(spannableString);
            return this;
        }

        /**
         * 返回键起不作用
         *
         * @return
         */
        public Builder isCancelable() {
            close.setVisibility(View.INVISIBLE);
            dialogBuilder.setCancelable(false);
            return this;
        }

        /**
         * 设置为系统类型的Dialog(可以在桌面显示)
         *
         * @return
         */
        public Builder isSystemType() {
            isSystemType = true;
            return this;
        }

        /**
         * @param isCanceledOnTouchOutside 点击对话框外是否关闭对话框
         * @return
         */
        public DialogTextViewBuilderAD build(boolean isCanceledOnTouchOutside) {
            setButton();
            alertDialog = dialogBuilder.create();
            if (!isCanceledOnTouchOutside) {
                alertDialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
            }
            if (isSystemType) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//判断是否拥有悬浮权限（6.0以后的）
                    if(!Settings.canDrawOverlays(ctx)) {
                        Toast.makeText(ctx, "请打开"+ getAppName(ctx)+"悬浮窗权限", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + ctx.getPackageName()));
                        ctx.startActivity(intent);
                    } else {
                        setSystemToast();
                    }
                } else {
                    setSystemToast();
                }
            }
            alertDialog.show();
            return new DialogTextViewBuilderAD(this);
        }

        /**
         * 设置为系统通知
         */
        private void setSystemToast(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);//设定为系统级警告
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 4.4至6.0判断权限是否打开
                //AppOpsManager添加于API 19
                if (!checkAlertWindowsPermission(ctx)) {
                    applyCommonPermission(ctx);
                    Toast.makeText(ctx, "请打开"+getAppName(ctx)+"悬浮窗权限", Toast.LENGTH_LONG).show();
                }
                Objects.requireNonNull(alertDialog.getWindow()).setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//设定为系统级警告
            } else {
                Objects.requireNonNull(alertDialog.getWindow()).setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//设定为系统级警告
            }
        }

        private void setButton() {
            tv_primary.setText(oneButton);
            if (twoButton != null) {
                tv_secondary.setText(twoButton);
            }
            tv_secondary.setVisibility(twoButton == null ? View.GONE : View.VISIBLE);
            viewSpace.setVisibility(twoButton == null ? View.GONE : View.VISIBLE);
        }

        private void setView(int view) {
            if (view == -100) {
                dialogView = LayoutInflater.from(ctx).inflate(R.layout.ad_dialog_base_textview, null);
            } else {
                dialogView = LayoutInflater.from(ctx).inflate(view, null);
            }
            tv_title = dialogView.findViewById(R.id.tv_title);
            tv_title.setText(title);
            tv_content = dialogView.findViewById(R.id.tv_content);
            tv_content.setText(content);
            close = dialogView.findViewById(R.id.close);
            tv_primary = dialogView.findViewById(R.id.tv_primary);
            tv_secondary = dialogView.findViewById(R.id.tv_secondary);
            viewSpace = dialogView.findViewById(R.id.viewSpace);
            setTextViewBold(tv_primary);
            setTextViewBold(tv_secondary);
            tv_primary.setOnClickListener(this);
            tv_secondary.setOnClickListener(this);
            close.setOnClickListener(this);
            dialogBuilder = new AlertDialog.Builder(ctx).setView(dialogView);

            tv_title.setVisibility(title != null && !title.equals("") ? View.VISIBLE : View.GONE);
            tv_content.setVisibility(content != null && !content.equals("") ? View.VISIBLE : View.GONE);
        }

        /**
         * 通过反射 阻止关闭对话框
         */
        private void preventDismissDialog() {
            try {
                Field field = alertDialog.getClass().getSuperclass()
                        .getDeclaredField("mShowing");
                field.setAccessible(true);
                // 设置mShowing值，欺骗android系统
                field.set(alertDialog, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.tv_primary) {
                if (listener == null) {
                    alertDialog.dismiss();
                } else {
                    if (!isClose) {
                        // 点击按钮不关闭对话框
                        listener.oneClick();
                    } else {
                        // 点击按钮回调并关闭对话框
                        listener.oneClick();
                        alertDialog.dismiss();
                    }
                }
            } else if (id == R.id.tv_secondary) {
                if (listener == null) {
                    alertDialog.dismiss();
                } else {
                    if (!isClose) {
                        // 点击按钮不关闭对话框
                        listener.twoClick();
                    } else {
                        // 点击按钮回调并关闭对话框
                        listener.twoClick();
                        alertDialog.dismiss();
                    }
                }
            } else if (id == R.id.close) {
                if (isClose) {
                    alertDialog.dismiss();
                }
            }
        }
    }

    /**
     * 选择的对话框
     * @param ctx
     * @param title 标题
     * @param str 数组
     * @param listener 回调
     */
    public static void setChoiceDialog(Context ctx, String title, final String[] str, final DialogChoiceListener listener){
        final AlertDialog.Builder builders = new AlertDialog.Builder(ctx);
        builders.setTitle(title);
        builders.setItems(str, (dialog, which) -> {
            dialog.dismiss();
            listener.onChoice(dialog, which, str[which]);
        });
        builders.create().show();
    }

    /**
     * 判断悬浮窗口权限是否打开
     *
     * @param context
     * @return true 允许  false禁止
     */
    private static boolean checkAlertWindowsPermission(Context context) {
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = 24;
            arrayOfObject1[1] = Binder.getCallingUid();
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1));
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 6.0以下到4.4之间打开悬浮窗权限设置页面
     *
     * @param context
     */
    private static void applyCommonPermission(Context context) {
        try {
            Class clazz = Settings.class;
            Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
            Intent intent = new Intent(field.get(null).toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "请进入设置页面打开" + getAppName(context) + "悬浮窗权限！", Toast.LENGTH_LONG).show();
        }
    }

    private static String getAppName(Context context) {
        PackageManager packageManagers = context.getPackageManager();
        try {
            String appName = (String) packageManagers.getApplicationLabel(packageManagers.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA));
            return appName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 设置textview加粗
     *
     * @param textView
     */
    public static void setTextViewBold(TextView textView) {
        if (textView == null) {
            return;
        }
        TextPaint tp = textView.getPaint();
        tp.setFakeBoldText(true);
        textView.postInvalidate();
    }

    public interface DialogOnClickListener {
        void oneClick();

        void twoClick();
    }

    public interface DialogChoiceListener {

        void onChoice(DialogInterface dialog, int position, String s);
    }

    public static class ListenerRealize implements DialogOnClickListener{

        @Override
        public void oneClick() {

        }

        @Override
        public void twoClick() {

        }
    }
}