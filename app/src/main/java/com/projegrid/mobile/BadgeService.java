package com.projegrid.mobile;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.github.library.bubbleview.BubbleTextVew;

/**
 * Created by yuki on 6/23/16.
 */
public class BadgeService extends Service implements View.OnTouchListener {
    View view;
    ViewGroup badgeViewGroup;
    WindowManager wm;
    final String SERVICE_NAME = "BadgeService";
    WindowManager.LayoutParams params;
    BubbleTextVew badgeBubbleLeft;
    BubbleTextVew badgeBubbleRight;
    ImageView badge;

    // ドラッグ中に移動量を取得するための変数
    private int oldX;
    private int oldY;

    private int maxX;
    private int maxY;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Viewからインフレータを作成する
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        // 重ね合わせするViewの設定を行う
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.TOP | Gravity.LEFT;

        // WindowManagerを取得する
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        // レイアウトファイルから重ね合わせするViewを作成する
        view = layoutInflater.inflate(R.layout.overlay, null);
        badgeViewGroup = (ViewGroup)view.findViewById(R.id.badge);
        badgeViewGroup.setOnTouchListener(this);

        // Viewを画面上に重ね合わせする
        wm.addView(view, params);

        // 画面サイズの取得
        Display display = wm.getDefaultDisplay();
        Point point = new Point(0, 0);
        display.getSize(point);
        maxX = point.x;
        maxY = point.y;

        // バッジの取得
        badge = (ImageView) badgeViewGroup.findViewById(R.id.badge_icon);

        // 吹き出しの取得
        badgeBubbleLeft = (BubbleTextVew) badgeViewGroup.findViewById(R.id.badge_bubble);
        badgeBubbleRight = (BubbleTextVew) badgeViewGroup.findViewById(R.id.badge_bubble_right);

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
            case MotionEvent.ACTION_UP:

                if(badgeBubbleLeft.getVisibility() == View.VISIBLE){
                    params.x += badgeBubbleLeft.getWidth();
                    wm.updateViewLayout(view, params);
                }

                badgeBubbleLeft.setVisibility(View.GONE);
                badgeBubbleRight.setVisibility(View.GONE);

                break;
            case MotionEvent.ACTION_MOVE:

                // 今回イベントでのView移動先の位置
                int left = v.getLeft() + (x - oldX);
                int top = v.getTop() + (y - oldY);
                // Viewを移動する
                v.layout(left, top, left + v.getWidth(), top
                        + v.getHeight());

                params.x += left;
                params.y += top;

                // アイコンの左端を計算
                int iconLeft = badgeBubbleLeft.getVisibility() == View.VISIBLE ? params.x + badgeBubbleLeft.getWidth() : params.x;

                if (params.x < 0) {
                    params.x = 0;
                }

                if(params.x > maxX - v.getWidth()){
                    params.x = maxX - v.getWidth();
                }

                if (params.y < 0) {
                    params.y = 0;
                }

                if(params.y > maxY - v.getHeight()){
                    params.y = maxY - v.getHeight();
                }

                // バッジが左のほうに寄ったら吹き出しを消す
                if(params.x < badge.getWidth()){
                    if(badgeBubbleLeft.getVisibility() == View.VISIBLE){
                        params.x += badgeBubbleLeft.getWidth();
                    }

                    badgeBubbleLeft.setVisibility(View.GONE);
                    badgeBubbleRight.setVisibility(View.VISIBLE);
                }else if(params.x > maxX - badge.getWidth() * 2){
                    badgeBubbleRight.setVisibility(View.GONE);
                    badgeBubbleLeft.setVisibility(View.VISIBLE);
                    params.x -= badgeBubbleLeft.getWidth();
                }else if(iconLeft > badge.getWidth() && iconLeft < maxX - badge.getWidth() * 2){
                    if(badgeBubbleLeft.getVisibility() == View.VISIBLE){
                        params.x += badgeBubbleLeft.getWidth();
                    }

                    badgeBubbleRight.setVisibility(View.GONE);
                    badgeBubbleLeft.setVisibility(View.GONE);
                }

                wm.updateViewLayout(view, params);

                break;
        }

        // 今回のタッチ位置を保持
        oldX = x;
        oldY = y;

        // 吹き出しに現在位置を表示
        badgeBubbleLeft.setText("x:" + params.x + " y:" + params.y);
        badgeBubbleRight.setText("x:" + params.x + " y:" + params.y);

        // イベント処理完了
        return true;
    }
}
