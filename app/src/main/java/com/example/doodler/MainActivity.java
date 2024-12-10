package com.example.doodler;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private DoodleView doodleView;
    private Button eraserButton;
    private boolean isEraserActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doodleView = findViewById(R.id.doodle_view);
        Button clearButton = findViewById(R.id.btn_clear);
        Button colorPickerButton = findViewById(R.id.btn_color_picker);
        eraserButton = findViewById(R.id.btn_eraser);
        @SuppressLint("CutPasteId") SeekBar brushSizeSeekBar = findViewById(R.id.seek_brush_size);
        @SuppressLint("CutPasteId") SeekBar opacitySeekBar = findViewById(R.id.seek_opacity);

        // Set up brush size SeekBar
        brushSizeSeekBar.setMax(100); // Adjust as necessary for your desired range
        brushSizeSeekBar.setProgress(10); // Default starting value for brush size
        doodleView.setBrushSize(brushSizeSeekBar.getProgress()); // Initialize the brush size

        brushSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                doodleView.setBrushSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Set up opacity SeekBar
        opacitySeekBar.setMax(255); // Maximum value for full opacity
        opacitySeekBar.setProgress(255); // Default starting value (fully opaque)
        doodleView.setBrushOpacity(opacitySeekBar.getProgress()); // Initialize opacity

        opacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                doodleView.setBrushOpacity(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        clearButton.setOnClickListener(v -> doodleView.clearCanvas());

        colorPickerButton.setOnClickListener(v -> showColorPickerDialog());

        // Set up eraser button click listener
        eraserButton.setOnClickListener(v -> {
            if (isEraserActive) {
                doodleView.disableEraser();
                eraserButton.setText(R.string.eraser);
            } else {
                doodleView.enableEraser();
                eraserButton.setText(R.string.drawing);
            }
            isEraserActive = !isEraserActive;
        });
    }

    private void showColorPickerDialog() {
        // Create a color picker dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Color");

        // Define the color options as an array of color integers
        String[] colors = {"Red", "Green", "Blue", "Yellow", "Black", "White"};
        int[] colorValues = {
                ContextCompat.getColor(this, android.R.color.holo_red_dark),
                ContextCompat.getColor(this, android.R.color.holo_green_dark),
                ContextCompat.getColor(this, android.R.color.holo_blue_light),
                ContextCompat.getColor(this, android.R.color.holo_orange_light),
                ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, android.R.color.white)
        };

        builder.setItems(colors, (dialog, which) -> {
            // Set the brush color based on the selected color
            doodleView.setBrushColor(colorValues[which]);

            // Deactivate eraser if it's active and reset the button text
            if (isEraserActive) {
                doodleView.disableEraser();
                eraserButton.setText(R.string.eraser);
                isEraserActive = false;
            }
        });

        builder.create().show();
    }
}
