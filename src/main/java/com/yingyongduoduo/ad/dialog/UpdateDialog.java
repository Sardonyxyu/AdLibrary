package com.yingyongduoduo.ad.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.yingyongduoduo.ad.R;
import com.yingyongduoduo.ad.bean.ADBean;
import com.yingyongduoduo.ad.config.AppConfig;
import com.yingyongduoduo.ad.newad.PublicUtil;
import com.yingyongduoduo.ad.utils.DownLoaderAPK;

public class UpdateDialog extends Dialog {
    Button bt_quit, bt_look;
    TextView tvMsg;
    Context context;


    public UpdateDialog(final Context context) {
        super(context, R.style.dialog_translation);
        this.context = context;

    }

    View.OnClickListener btListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.bt_look) {
                UpdateDialog.this.dismiss();
            } else if (id == R.id.bt_quit) {
                ADBean adbean = new ADBean();
                adbean.setAd_name(PublicUtil.getAppName(context));
                adbean.setAd_packagename(AppConfig.configBean.updatemsg.packageName);
                adbean.setAd_apkurl(AppConfig.configBean.updatemsg.url);
                adbean.setAd_versioncode(AppConfig.configBean.updatemsg.versioncode);
                if (DownLoaderAPK.getInstance(context).addDownload(adbean)) {
                    Toast.makeText(context, "开始下载:" + adbean.getAd_name(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, adbean.getAd_name() + " 已经在下载了:", Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.updatedialog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        setCanceledOnTouchOutside(false);
        bt_look = findViewById(R.id.bt_look);
        bt_quit = findViewById(R.id.bt_quit);
        tvMsg = findViewById(R.id.tvMsg);
        bt_quit.setOnClickListener(btListen);
        bt_look.setOnClickListener(btListen);
        try {
            tvMsg.setText(AppConfig.configBean.updatemsg.msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}