package com.example.doodler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DoodleView extends View {

    private Paint paint;
    private Path currentPath;
    private List<Path> paths;
    private List<Paint> pathPaints;
    private int brushSize = 10; // Default brush size
    private int brushOpacity = 255; // Default opacity (fully opaque)

    public DoodleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFF000000); // Default color is black
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAlpha(brushOpacity); // Set default opacity

        paths = new ArrayList<>();
        pathPaints = new ArrayList<>();
    }

    public void setBrushSize(int size) {
        brushSize = size;
        if (currentPath != null) {
            // Update the most recent path's Paint with the new brush size
            Paint pathPaint = new Paint(paint);
            pathPaint.setStrokeWidth(brushSize);
            pathPaint.setAlpha(brushOpacity);
            pathPaints.set(pathPaints.size() - 1, pathPaint); // Update last added Paint
        }
    }

    public void setBrushColor(int color) {
        paint.setColor(color);
    }

    public void setBrushOpacity(int opacity) {
        brushOpacity = opacity;
        paint.setAlpha(brushOpacity);
        if (currentPath != null) {
            // Update the most recent path's Paint with the new opacity
            Paint pathPaint = new Paint(paint);
            pathPaint.setStrokeWidth(brushSize);
            pathPaint.setAlpha(brushOpacity);
            pathPaints.set(pathPaints.size() - 1, pathPaint);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Draw all the paths stored in the paths list
        for (int i = 0; i < paths.size(); i++) {
            canvas.drawPath(paths.get(i), pathPaints.get(i));
        }

        // Draw the current path if it's not null
        if (currentPath != null) {
            canvas.drawPath(currentPath, paint);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                currentPath.moveTo(x, y);
                paths.add(currentPath);

                // Create a new Paint object with the current brush size and opacity for this path
                Paint pathPaint = new Paint(paint);
                pathPaint.setStrokeWidth(brushSize);
                pathPaint.setAlpha(brushOpacity);
                pathPaints.add(pathPaint);
                break;

            case MotionEvent.ACTION_MOVE:
                if (currentPath != null) {
                    currentPath.lineTo(x, y);
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                currentPath = null; // Reset after finishing a path
                break;
        }
        // Notify that the click has been performed for accessibility
        performClick();
        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    public void clearCanvas() {
        paths.clear();
        pathPaints.clear();
        invalidate();
    }
}
