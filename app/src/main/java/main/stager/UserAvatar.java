package main.stager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;

import main.stager.utils.ThemeController;
import main.stager.utils.Utilits;


public class UserAvatar extends View {
    private String mUserName;
    private String mName;
    private String mEmail;
    private Rect mTextBoundRect = new Rect();
    private Paint paint = new Paint();

    public UserAvatar(Context context) {
        super(context);
    }

    public UserAvatar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UserAvatar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UserAvatar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    private void updateName() {

        mName = (Utilits.isNullOrBlank(mUserName) || mUserName.trim().length() < 2)
                ? ((Utilits.isNullOrBlank(mEmail) || mEmail.trim().length() < 2)
                    ? "A"
                    : mEmail)
                : mUserName;

        mName = mName.trim().toUpperCase();
        if (mName.length() > 2)
            mName = mName.substring(0,2);
        invalidate();
    }

    public void setUserName(String userName) {
        mUserName = userName;
        updateName();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mName == null)
            updateName();

        float height, center, radius, textWidth, textHeight;
        height = getHeight();
        radius = height / 2;
        center = height / 2;

        // Стиль заливка
        paint.setAlpha(Color.TRANSPARENT);
        canvas.drawPaint(paint);

        // Рисуем круг
        paint.setAntiAlias(true);

        int color = Color.TRANSPARENT;
        Drawable background = getRootView().getBackground();

        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();

        paint.setColor(colorChangeBrightness(
                color,
                32 * (ThemeController.isCurrentDark(getContext()) ? 1 : -1)
                )
        );

        canvas.drawCircle(center, center, radius, paint);

        // Рисуем текст
        if (ThemeController.isCurrentDark(getContext()))
            paint.setColor(Color.WHITE);
        else paint.setColor(Color.BLACK);

        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextSize((int) (radius * 0.9));

        // Подсчитаем размер текста
        paint.getTextBounds(mName, 0, mName.length(), mTextBoundRect);

        // Используем measureText для измерения ширины
        textWidth = paint.measureText(mName);
        textHeight = mTextBoundRect.height();

        canvas.drawText(mName,
                center - (textWidth / 2f),
                center + (textHeight /2f),
                paint
        );

    }

    private int colorChangeBrightness(int color, int brightnessOffset) {
        return Color.rgb(((color >> 16)&0xFF) + brightnessOffset,
                ((color >> 8)&0xFF) + brightnessOffset,
                (color&0xFF) + brightnessOffset);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        int size = Math.min(w, h);
        setMeasuredDimension(size, size);
    }
}