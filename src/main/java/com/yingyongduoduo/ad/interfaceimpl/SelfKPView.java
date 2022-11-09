package com.yingyongduoduo.ad.interfaceimpl;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yingyongduoduo.ad.R;
import com.yingyongduoduo.ad.bean.ADBean;
import com.yingyongduoduo.ad.config.AppConfig;
import com.yingyongduoduo.ad.utils.GlideUtil;

import java.util.List;

public class SelfKPView extends RelativeLayout {
    private SelfKPAdListener listener;
    private ImageView my_image_view;
    private View rl_content;
    private TextView tv_close;
    private Context context;
    ADBean bean;

    boolean isdismiss = false;

    public SelfKPView(final Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.ad_prefix_selfkpview, this);
        my_image_view = findViewById(R.id.my_image_view);
//        my_image_view = findViewById(R.id.my_image_view);
        rl_content = findViewById(R.id.rl_content);
        tv_close = (TextView) findViewById(R.id.tv_close);
        List<ADBean> beans = AppConfig.GetSelfADByCount(context, 1, "kp_count");
        if (null != beans && beans.size() == 1) {
            bean = beans.get(0);

        }
        rl_content.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onAdClick(bean);
                AppConfig.openAD(context, bean, "kp_count");
            }
        });
        tv_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (!isdismiss) {
                        isdismiss = true;
                        listener.onAdDismissed(bean);
                    }
                }

            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (bean != null) {
            GlideUtil.loadCP(context, bean.getAd_kp(), my_image_view);
            if (listener != null)
                listener.onAdPresent(bean);
            mCountDownTimer.start();

        } else {
            if (listener != null)
                listener.onAdFailed(bean);
            rl_content.setVisibility(View.GONE);
        }
    }

    public void setADListener(SelfKPAdListener listener) {
        this.listener = listener;
    }

    int i = 10;
    private CountDownTimer mCountDownTimer = new CountDownTimer(5 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (isdismiss)//在别的地方关闭的
                cancel();
            else
                tv_close.setText(millisUntilFinished / 1000 + "");
            ;
        }

        @Override
        public void onFinish() {
            isdismiss = true;
            listener.onAdDismissed(bean);

        }
    };


}