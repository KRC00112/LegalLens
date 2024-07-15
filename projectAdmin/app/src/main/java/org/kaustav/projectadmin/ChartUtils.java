package org.kaustav.projectadmin;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;

public class ChartUtils {

    public static Bitmap createBarChart(int[] satisfactionCounts) {
        int width = 800, height = 500; // Increased height to accommodate legend
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStrokeWidth(4);

        int barWidth = width / satisfactionCounts.length;
        int total = 0;
        for (int count : satisfactionCounts) {
            total += count;
        }

        int maxCount = 0;
        for (int count : satisfactionCounts) {
            if (count > maxCount) maxCount = count;
        }

        int minHeight = 50; // Minimum height for bars
        int[] colors = {Color.RED, Color.MAGENTA, Color.YELLOW, Color.GREEN, Color.rgb(135, 206, 235)};
        String[] labels = {"Very Dissatisfied", "Dissatisfied", "Neutral", "Satisfied", "Very Satisfied"};

        // Draw bars
        for (int i = 0; i < satisfactionCounts.length; i++) {
            int barHeight = Math.max(minHeight, (int) (((float) satisfactionCounts[i] / maxCount) * (height - 100))); // Ensure minimum height
            paint.setColor(colors[i % colors.length]);
            canvas.drawRect(i * barWidth, height - barHeight, (i + 1) * barWidth, height - 100, paint); // Adjust height for legend

            // Add percentage text
            float percentage = ((float) satisfactionCounts[i] / total) * 100;
            paint.setColor(Color.WHITE);
            paint.setTextSize(30);
            canvas.drawText(String.format("\t\t\t\t\t%.1f%%", percentage), i * barWidth, height - barHeight - 10, paint);
        }

        return bitmap;
    }



    public static Bitmap createPieChart(int[] satisfactionCounts) {
        int width = 800, height = 900; // Increased height to accommodate legend
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        int total = 0;
        for (int count : satisfactionCounts) {
            total += count;
        }

        RectF rect = new RectF(0, 100, width, height); // Adjust position for legend
        float startAngle = 0;

        int[] colors = {Color.RED, Color.MAGENTA, Color.YELLOW, Color.GREEN, Color.rgb(135, 206, 235)};

        // Filter out segments with zero counts
        List<Integer> nonZeroCounts = new ArrayList<>();
        List<Integer> nonZeroColors = new ArrayList<>();
        for (int i = 0; i < satisfactionCounts.length; i++) {
            if (satisfactionCounts[i] != 0) {
                nonZeroCounts.add(satisfactionCounts[i]);
                nonZeroColors.add(colors[i % colors.length]);
            }
        }

        // Draw pie chart
        for (int i = 0; i < nonZeroCounts.size(); i++) {
            float sweepAngle = ((float) nonZeroCounts.get(i) / total) * 360;
            paint.setColor(nonZeroColors.get(i));
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint);

            // Calculate the angle for placing text closer to the center of the segment
            float angle = startAngle + sweepAngle / 2;

            // Calculate the coordinates for text placement
            float x = (float) (width / 2 + (width / 3) * Math.cos(Math.toRadians(angle)));
            float y = (float) (height / 2 + (height / 3) * Math.sin(Math.toRadians(angle)));

            // Add percentage text
            float percentage = ((float) nonZeroCounts.get(i) / total) * 100;
            paint.setColor(Color.BLACK);
            paint.setTextSize(30);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD)); // Set typeface to bold
            canvas.drawText(String.format("%.1f%%", percentage), x, y, paint);

            startAngle += sweepAngle;
        }

        return bitmap;
    }



      static void drawLegends(Canvas canvas, Paint paint, int[] colors, String[] labels, int width) {
        paint.setTextSize(30);
        paint.setStrokeWidth(2);

        int legendSize = 30;
        int spacing = 10;
        int x = 10;
        int y = 10; // Adjusted to push legends above charts

        for (int i = 0; i < labels.length; i++) {
            paint.setColor(colors[i % colors.length]);
            canvas.drawRect(x, y - legendSize, x + legendSize, y, paint);

            paint.setColor(Color.WHITE);
            canvas.drawText(labels[i], x + legendSize + spacing, y, paint);

            y += legendSize + spacing;
        }
    }

    public static Bitmap createLegends(int[] satisfactionCounts) {
        int width = 300, height = 200; // Adjust width and height according to your needs
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setTextSize(30);
        paint.setStrokeWidth(2);

        int legendSize = 30;
        int spacing = 10;
        int x = 10;
        int y = 30;

        int[] colors = {Color.RED, Color.MAGENTA, Color.YELLOW, Color.GREEN, Color.rgb(135, 206, 235)};
        String[] labels = {"Very Dissatisfied", "Dissatisfied", "Neutral", "Satisfied", "Very Satisfied"};

        for (int i = 0; i < labels.length; i++) {
            paint.setColor(colors[i % colors.length]);
            canvas.drawRect(x, y - legendSize, x + legendSize, y, paint);

            paint.setColor(Color.WHITE);
            canvas.drawText(labels[i], x + legendSize + spacing, y, paint);

            y += legendSize + spacing;
        }

        return bitmap;
    }

}
