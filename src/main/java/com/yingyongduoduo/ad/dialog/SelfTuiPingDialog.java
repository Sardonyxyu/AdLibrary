package com.yingyongduoduo.ad.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.yingyongduoduo.ad.R;
import com.yingyongduoduo.ad.bean.ADBean;
import com.yingyongduoduo.ad.config.AppConfig;
import com.yingyongduoduo.ad.interfaceimpl.SelfBannerAdListener;
import com.yingyongduoduo.ad.utils.GlideUtil;

import java.util.List;
import java.util.Random;

public class SelfTuiPingDialog extends Dialog {
    private SelfBannerAdListener listener;
    private ImageView my_image_view;
    private View rl_content, ad_close;
    Button bt_quit, bt_look;
    Context context;
    ADBean bean;
    Boolean ischange;

    private boolean isExit;

    public SelfTuiPingDialog(final Context context, ADBean bean, boolean isExit) {
        super(context, R.style.ad_prefix_dialog);
        this.context = context;
        this.isExit = isExit;
        this.bean = bean;
    }

    public SelfTuiPingDialog(final Context context, ADBean bean) {
        super(context, R.style.ad_prefix_dialog);
        this.context = context;

        this.bean = bean;
    }

    public SelfTuiPingDialog(final Context context) {
        super(context, R.style.ad_prefix_dialog);
        this.context = context;

        List<ADBean> beans = AppConfig.GetSelfADByCount(context, 1, "tp_count");
        if (null != beans && beans.size() == 1) {
            bean = beans.get(0);
        }
    }

    View.OnClickListener btListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.bt_look) {
                if (ischange) {
                    if (bean != null) {
                        if (listener != null)
                            listener.onAdClick(bean);
                        AppConfig.openAD(context, bean, "tp_count");
                        SelfTuiPingDialog.this.dismiss();
                    } else {
                        SelfTuiPingDialog.this.dismiss();
                    }
                } else {
                    SelfTuiPingDialog.this.dismiss();
                    ((Activity) context).finish();
                    if (isExit) {
//                        if (MusicPlayer.isPlaying()) {
//                            MusicPlayer.playOrPause();
//                        }
//                        unbindService();
//                        finish();

                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                }
            } else if (v.getId() == R.id.bt_quit) {
                if (ischange) {
                    SelfTuiPingDialog.this.dismiss();
                    ((Activity) context).finish();
                    if (isExit) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                } else {
                    if (bean != null) {
                        if (listener != null)
                            listener.onAdClick(bean);
                        AppConfig.openAD(context, bean, "tp_count");
                        SelfTuiPingDialog.this.dismiss();
                    } else {
                        SelfTuiPingDialog.this.dismiss();
                    }

                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.ad_prefix_selftuipingdialog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        setCanceledOnTouchOutside(false);
//		setCancelable(false);
        bt_look = (Button) findViewById(R.id.bt_look);
        bt_quit = (Button) findViewById(R.id.bt_quit);
        bt_quit.setOnClickListener(btListen);
        bt_look.setOnClickListener(btListen);
        ischange = new Random(System.currentTimeMillis()).nextBoolean();
        if (ischange) {
            bt_quit.setText("确定退出");
            bt_look.setText("去看看");
        } else {
            bt_quit.setText("去看看");
            bt_look.setText("确定退出");
        }

        my_image_view = view.findViewById(R.id.my_image_view);
        rl_content = view.findViewById(R.id.rl_content);
        ad_close = view.findViewById(R.id.ad_close);
        ad_close.setVisibility(View.GONE);
        rl_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onAdClick(bean);
                AppConfig.openAD(context, bean, "tp_count");
            }
        });
        ad_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelfTuiPingDialog.this.dismiss();
                System.out.println("广告被关闭");
            }
        });
//		Window dialogWindow = getWindow();
//		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay(); // 获取屏幕宽、高用
//		lp.width = (int) (display.widthPixels * 0.8); // 高度设置为屏幕的0.6
//		dialogWindow.setAttributes(lp);
        if (bean != null) {
            ViewGroup.LayoutParams params = my_image_view.getLayoutParams();
            params.width = (int) (display.getWidth() * 0.8);
            params.height = (int) (bean.getAd_thumbnailscal() * params.width);
            my_image_view.setLayoutParams(params);

            GlideUtil.loadCP(context, bean.getAd_banner(), my_image_view);

            if (listener != null)
                listener.onADReceiv(bean);
        } else {
            if (listener != null)
                listener.onAdFailed();
            my_image_view.setVisibility(View.GONE);
            ad_close.setVisibility(View.GONE);
            findViewById(R.id.ad_log).setVisibility(View.GONE);
            findViewById(R.id.tvMsg).setVisibility(View.VISIBLE);
            if (ischange) {
                bt_quit.setText("确定退出");
                bt_look.setText("取消");
            } else {
                bt_quit.setText("取消");
                bt_look.setText("确定退出");
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setADListener(SelfBannerAdListener listener) {
        this.listener = listener;
    }

}