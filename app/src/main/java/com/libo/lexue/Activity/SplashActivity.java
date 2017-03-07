package com.libo.lexue.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.libo.lexue.MainActivity;
import com.libo.lexue.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by libo on 2017/2/28.
 */

public class SplashActivity extends Activity {
    @Bind(R.id.move_img)
    ImageView moveImg;
    @Bind(R.id.relative_view)
    RelativeLayout relativeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        ButterKnife.bind(this);

        initAnimation();

    }
    private void initAnimation(){
//上下摇摆
        final TranslateAnimation alphaAnimation2 = new TranslateAnimation(0f, 0f, 50F, 80F);  //同一个x轴 (开始结束都是50f,所以x轴保存不变)  y轴开始点50f  y轴结束点80f
        alphaAnimation2.setDuration(5000);  //设置时间
        alphaAnimation2.setRepeatCount(Animation.INFINITE);  //为重复执行的次数。如果设置为n，则动画将执行n+1次。INFINITE为无限制播放
        alphaAnimation2.setRepeatMode(Animation.REVERSE);  //为动画效果的重复模式，常用的取值如下。RESTART：重新从头开始执行。REVERSE：反方向执行

        // AccelerateDecelerateInterpolator 在动画开始与介绍的地方速率改变比较慢，在中间的时候加速
        // AccelerateInterpolator  在动画开始的地方速率改变比较慢，然后开始加速
        // AnticipateInterpolator 开始的时候向后然后向前甩
        // AnticipateOvershootInterpolator 开始的时候向后然后向前甩一定值后返回最后的值
        // BounceInterpolator   动画结束的时候弹起
        // CycleInterpolator 动画循环播放特定的次数，速率改变沿着正弦曲线
        // DecelerateInterpolator 在动画开始的地方快然后慢
        // LinearInterpolator   以常量速率改变
        // OvershootInterpolator    向前甩一定值后再回到原来位置

        //上面那些效果可以自已尝试下
        alphaAnimation2.setInterpolator(new BounceInterpolator());//动画结束的时候弹起
        moveImg.setAnimation(alphaAnimation2);
        alphaAnimation2.start();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                alphaAnimation2.cancel();
                enterHomeActivity();
            }
        }, 3000);

    }
    private void enterHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
