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
    private Paint originalPaint; // To store the original paint color
    private Path currentPath;
    private List<Path> paths;
    private List<Paint> pathPaints;
    private int brushSize = 10; // Default brush size
    private int brushOpacity = 255; // Default opacity (fully opaque)
    private boolean isEraserActive = false; // To track if the eraser is active
    private boolean isColorActive = false; // To track if a color is actively selected

    public DoodleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        originalPaint = new Paint();
        paint.setColor(0xFF000000); // Default color is black
        originalPaint.setColor(0xFF000000); // Original color to store the drawing color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAlpha(brushOpacity); // Set default opacity

        originalPaint.setStyle(Paint.Style.STROKE);
        originalPaint.setStrokeCap(Paint.Cap.ROUND);
        originalPaint.setStrokeJoin(Paint.Join.ROUND);
        originalPaint.setAlpha(brushOpacity);

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
        // If the eraser is active, deactivate it and restore the original color
        if (isEraserActive) {
            disableEraser();
        }

        paint.setColor(color);
        originalPaint.setColor(color); // Store the original color
        isColorActive = true; // Set color mode as active
    }

    public void setBrushOpacity(int opacity) {
        // Update brush opacity only if the eraser is not active
        if (!isEraserActive) {
            brushOpacity = opacity;
            paint.setAlpha(brushOpacity);
            originalPaint.setAlpha(brushOpacity);
        }
    }

    public void enableEraser() {
        // If a color is active, it should be set to color mode before switching to the eraser
        if (isColorActive) {
            disableColor(); // Reset the color mode
        }

        // The background color (RGB 253, 248, 255)
        int backgroundColor = 0xFFF8F8FF;
        paint.setColor(backgroundColor); // Set paint color to match the background
        paint.setAlpha(255); // Set opacity to fully opaque for the eraser
        isEraserActive = true; // Mark the eraser as active
    }

    public void disableEraser() {
        paint.setColor(originalPaint.getColor()); // Restore the original paint color
        paint.setAlpha(brushOpacity); // Restore the original opacity
        isEraserActive = false; // Mark the eraser as inactive
    }

    public void disableColor() {
        isColorActive = false; // Reset color mode
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
                pathPaint.setAlpha(isEraserActive ? 255 : brushOpacity); // Full opacity if eraser is active
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
