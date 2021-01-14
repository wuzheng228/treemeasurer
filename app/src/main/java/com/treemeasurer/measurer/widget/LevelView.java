package com.treemeasurer.measurer.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.content.res.AppCompatResources;

import com.treemeasurer.measurer.R;
import com.treemeasurer.measurer.utils.AppUtils;


public class LevelView extends View {
    // 左偏移
    float offsetLeft;
    // 上偏移
    float offsetTop;
    // 仪表盘的最大半径
    public int outRadius;
    // 仪表盘的内半径
    public int innerRaius;
    // 水平仪表盘图片
    Bitmap back;
    // 水平仪中的气泡图标
    Bitmap bubble;
    // 水平仪中气泡的X、Y坐标
    float bubbleX = 0f;
    float bubbleY = 0f;

    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 加载气泡图片
        bubble = createBall(60);
        // 获取窗口管理器
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        // 获取屏幕的宽度和高度
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        outRadius = (int)(screenWidth * 0.8) / 2;
        offsetLeft = (screenWidth - 2 * outRadius) / 2f;
        offsetTop = (screenHeight - 2 * outRadius) / 2f * 0.55f;;
        innerRaius = outRadius - bubble.getWidth();
        // 创建位图
        back = Bitmap.createBitmap(outRadius * 2, outRadius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(back);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        // 设置绘制风格: 边界
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        paint.setColor(Color.WHITE);
        // 绘制圆形
        canvas.drawCircle(outRadius, outRadius, outRadius, paint);
        canvas.drawCircle(outRadius, outRadius, innerRaius, paint);
        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        // 绘制风格：仅仅绘制边框
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(4f);
        paint2.setColor(Color.WHITE);
        // 绘制水平横线
        canvas.drawLine(0f, outRadius, outRadius * 2, outRadius, paint2);
        // 绘制垂直横线
        canvas.drawLine(outRadius, 0f, outRadius, outRadius * 2, paint2);
        // 绘制六角星
        Bitmap xing = AppUtils.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_xing);
        canvas.drawBitmap(xing, outRadius - xing.getWidth() / 2,
                outRadius - xing.getWidth()/2, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制
        canvas.drawBitmap(back, offsetLeft, offsetTop, null);
        // 根据气泡坐标绘制气泡
        canvas.drawBitmap(bubble, bubbleX+offsetLeft, bubbleY + offsetTop, null);
    }

    private Bitmap createBall(int r) {
        Bitmap ball = Bitmap.createBitmap(r * 2, r * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(ball);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        MaskFilter maskFilter = new BlurMaskFilter(25, BlurMaskFilter.Blur.INNER);
        paint.setMaskFilter(maskFilter);
        paint.setColor(Color.parseColor("#D4C282"));
        canvas.drawCircle(r,r,r,paint);
        return ball;
    }

    public Bitmap getBack() {
        return back;
    }

    public void setBack(Bitmap back) {
        this.back = back;
    }

    public Bitmap getBubble() {
        return bubble;
    }

    public void setBubble(Bitmap bubble) {
        this.bubble = bubble;
    }

    public float getBubbleX() {
        return bubbleX;
    }

    public void setBubbleX(float bubbleX) {
        this.bubbleX = bubbleX;
    }

    public float getBubbleY() {
        return bubbleY;
    }

    public void setBubbleY(float bubbleY) {
        this.bubbleY = bubbleY;
    }
}
