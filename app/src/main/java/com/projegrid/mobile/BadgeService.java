package com.projegrid.mobile;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;



/**
 * Created by yuki on 6/23/16.
 */
public class BadgeService extends Service implements View.OnTouchListener {
    View view;
    WindowManager wm;
    final String SERVICE_NAME = "BadgeService";

    // ドラッグ中に移動量を取得するための変数
    private int oldx;
    private int oldy;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(SERVICE_NAME, "-------- onStartCommand --------R");

        // Viewからインフレータを作成する
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        // 重ね合わせするViewの設定を行う
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                1000,
//                1000,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        // WindowManagerを取得する
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        // レイアウトファイルから重ね合わせするViewを作成する
        view = layoutInflater.inflate(R.layout.overlay, null);
        ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
        imageView.setImageResource(R.mipmap.ic_launcher);
        imageView.setOnTouchListener(this);
//        ImageView imageView = new ImageView();

        // Viewを画面上に重ね合わせする
        wm.addView(view, params);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // タッチしている位置取得
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // 今回イベントでのView移動先の位置
                int left = v.getLeft() + (x - oldx);
                int top = v.getTop() + (y - oldy);
                // Viewを移動する
                v.layout(left, top, left + v.getWidth(), top
                        + v.getHeight());
                break;
        }

        // 今回のタッチ位置を保持
        oldx = x;
        oldy = y;
        // イベント処理完了
        return true;
    }
}
