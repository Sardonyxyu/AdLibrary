package com.yingyongduoduo.ad.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;
import com.yingyongduoduo.ad.ADControl;
import com.yingyongduoduo.ad.R;
import com.yingyongduoduo.ad.interfaceimpl.SelfBannerAdListener;

import java.util.List;
import java.util.Random;

public class GDTMuBanTuiPingDialog extends Dialog {
    private SelfBannerAdListener listener;
    private RelativeLayout rl_content;
    TextView bt_quit, bt_look;
    Context context;
    //    ADBean bean;
    Boolean ischange;
    NativeExpressADView adItem;

    public GDTMuBanTuiPingDialog(final Context context) {
        super(context, R.style.ad_prefix_dialog);
        this.context = context;

//        List<ADBean> beans = AppConfig.GetSelfADByCount(context, 1, "tp_count");
//        if (null != beans && beans.size() == 1) {
//            bean = beans.get(0);
//        }

    }

    public static List<NativeExpressADView> adItems;
    public static long initTime = 0;

    public static void Init(Context context, String appid, String sid) {
        int width = ADSize.FULL_WIDTH;//(int) (display.getWidth() * 0.8);
        int height = ADSize.AUTO_HEIGHT;// (int) (0.8 * width);
        ADSize adSize = new ADSize(width, height);
        NativeExpressAD nativeExpressAD =
                new NativeExpressAD(context, adSize, sid, new NativeExpressAD.NativeExpressADListener() {
                    @Override
                    public void onNoAD(AdError adError) {
                        GDTMuBanTuiPingDialog.adItems = null;
                    }

                    @Override
                    public void onADLoaded(List<NativeExpressADView> list) {
                        GDTMuBanTuiPingDialog.adItems = list;
                        initTime = System.currentTimeMillis();
                    }

                    @Override
                    public void onRenderFail(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onRenderSuccess(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onADExposure(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onADClicked(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onADClosed(NativeExpressADView nativeExpressADView) {
                    }

                    @Override
                    public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

                    }

                });
        nativeExpressAD.loadAD(10);
    }

    View.OnClickListener btListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.bt_look) {
                if (ischange) {//去看看
                    GDTMuBanTuiPingDialog.this.dismiss();
                } else {
                    GDTMuBanTuiPingDialog.this.dismiss();
                    ((Activity) context).finish();
                }

            } else if (i == R.id.bt_quit) {
                if (ischange) {
                    GDTMuBanTuiPingDialog.this.dismiss();
                    ((Activity) context).finish();
                } else {//去看看
                    GDTMuBanTuiPingDialog.this.dismiss();
                }

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.ad_prefix_selfmubantuipingdialog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        setCanceledOnTouchOutside(false);
//		setCancelable(false);
        bt_look = findViewById(R.id.bt_look);
        bt_quit = findViewById(R.id.bt_quit);
        bt_quit.setOnClickListener(btListen);
        bt_look.setOnClickListener(btListen);
        ischange = new Random(System.currentTimeMillis()).nextBoolean();
        if (ischange) {
            bt_quit.setText("确定退出");
            bt_look.setText("取消");
        } else {
            bt_quit.setText("取消");
            bt_look.setText("确定退出");
        }

        rl_content = (RelativeLayout) view.findViewById(R.id.rl_content);

        rl_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (listener != null)
//                    listener.onAdClick(bean);
//                AppConfig.openAD(context, bean, "tp_count");
            }
        });

//		Window dialogWindow = getWindow();
//		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        Display display = ((Activity) context).getWindowManager().getDefaultDisplay(); // 获取屏幕宽、高用
        if (adItems != null && adItems.size() > 0) {
            rl_content.setVisibility(View.VISIBLE);
            int index = new Random(System.currentTimeMillis()).nextInt(adItems.size());
            adItem = adItems.remove(index);
//            ViewGroup.LayoutParams params = rl_content.getLayoutParams();
//            params.width = (int) (display.getWidth() -100);
//            params.height = (int) (1 * params.width);
//            rl_content.setLayoutParams(params);

            if (rl_content.getChildCount() > 0) {
                rl_content.removeAllViews();
            }

            ViewGroup parent = (ViewGroup) adItem.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
            if (rl_content.getVisibility() != View.VISIBLE) {
                rl_content.setVisibility(View.VISIBLE);
            }
//            adItem.setVisibility(View.VISIBLE);
            rl_content.addView(adItem);
            adItem.render();
        } else {
//            my_image_view.setVisibility(View.GONE);

//            findViewById(R.id.ad_log).setVisibility(View.GONE);
            ADControl.InitGDTMuBanTP(context);
            rl_content.setVisibility(View.GONE);

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