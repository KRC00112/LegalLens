package org.kaustav.projectadmin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LineChartView extends View {
    private List<Long> timestamps = new ArrayList<>();
    private Map<Long, Integer> registrationCounts = new HashMap<>();
    private Paint linePaint = new Paint();
    private Paint textPaint = new Paint();
    private Paint dashPaint = new Paint();
    private Paint dashPaint2 = new Paint();
    private static final int PADDING = 100;

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        linePaint.setColor(Color.parseColor("#EF0466"));
        linePaint.setStrokeWidth(5f);
        linePaint.setAntiAlias(true);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30f);
        textPaint.setAntiAlias(true);

        dashPaint.setColor(Color.WHITE);
        dashPaint.setStrokeWidth(3f);
        dashPaint.setAntiAlias(true);

        dashPaint2.setColor(Color.WHITE);
        dashPaint2.setStrokeWidth(0.5f);
        dashPaint2.setAntiAlias(true);
    }

    public void setData(List<Long> timestamps) {
        this.timestamps = timestamps;
        processTimestamps();
        invalidate(); // Trigger a redraw
    }

    private void processTimestamps() {
        registrationCounts.clear();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
        for (Long timestamp : timestamps) {
            String dateKey = sdf.format(new Date(timestamp));
            long dateAsLong;
            try {
                dateAsLong = sdf.parse(dateKey).getTime();
            } catch (Exception e) {
                continue; // Skip this timestamp if parsing fails
            }

            registrationCounts.put(dateAsLong, registrationCounts.getOrDefault(dateAsLong, 0) + 1);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (registrationCounts.isEmpty()) return;

        int width = getWidth() - 2 * PADDING;
        int height = getHeight() - 2 * PADDING;

        List<Long> sortedDates = new ArrayList<>(registrationCounts.keySet());
        Collections.sort(sortedDates);

        long minDate = Collections.min(sortedDates);
        long maxDate = Collections.max(sortedDates);
        int maxCount = Collections.max(registrationCounts.values());

        float xInterval = (float) width / (sortedDates.size() - 1);

        // Draw horizontal grid lines
        int numYLabels = maxCount + 1; // Ensure all counts are included in labels
        for (int i = 0; i < numYLabels; i++) {
            float y = PADDING + height - i * height / (numYLabels - 1);
            canvas.drawLine(PADDING, y, PADDING + width, y, dashPaint2); // Draw horizontal line
        }

        // Draw lines
        for (int i = 0; i < sortedDates.size() - 1; i++) {
            float startX = PADDING + xInterval * i;
            float stopX = PADDING + xInterval * (i + 1);
            float startY = PADDING + height - (height * registrationCounts.get(sortedDates.get(i)) / (float) maxCount);
            float stopY = PADDING + height - (height * registrationCounts.get(sortedDates.get(i + 1)) / (float) maxCount);

            canvas.drawLine(startX, startY, stopX, stopY, linePaint);
        }

        // Draw points
        for (int i = 0; i < sortedDates.size(); i++) {
            float x = PADDING + xInterval * i;
            float y = PADDING + height - (height * registrationCounts.get(sortedDates.get(i)) / (float) maxCount);
            canvas.drawCircle(x, y, 8, linePaint);  // Reduced circle radius to 8
        }

        // Draw x-axis labels
        int numXLabels = 5;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.US);
        for (int i = 0; i < numXLabels; i++) {
            long date = minDate + i * (maxDate - minDate) / (numXLabels - 1);
            String dateLabel = sdf.format(new Date(date));
            float x = PADDING + i * width / (numXLabels - 1);
            canvas.drawText(dateLabel, x, height + PADDING + 40, textPaint);

            // Draw calibration dashes on x-axis
            canvas.drawLine(x, PADDING + height, x, PADDING + height + 20, dashPaint);
        }

        // Draw y-axis labels
        for (int i = 0; i < numYLabels; i++) {
            float value = i;
            String label = String.format(Locale.US, "%.0f", value);
            float y = PADDING + height - i * height / (numYLabels - 1);
            canvas.drawText(label, PADDING - 50, y, textPaint);

            // Draw calibration dashes on y-axis
            canvas.drawLine(PADDING - 20, y, PADDING, y, dashPaint);
        }

        // Draw x and y axis lines
        canvas.drawLine(PADDING, PADDING, PADDING, PADDING + height, textPaint); // y-axis
        canvas.drawLine(PADDING, PADDING + height, PADDING + width, PADDING + height, textPaint); // x-axis
    }


}
