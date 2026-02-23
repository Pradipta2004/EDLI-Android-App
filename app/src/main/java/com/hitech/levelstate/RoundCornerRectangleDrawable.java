package com.hitech.levelstate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.hitech.levelstate.R;

public class RoundCornerRectangleDrawable extends Drawable {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint[] paintx = new Paint[10];
    private Paint paint_darkGray = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint_amber = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint_red = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint_green = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint_gray = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float cornerRadius;

    private boolean fullScreenFlag;
    private Bitmap image;

    public RoundCornerRectangleDrawable(Context context,int imageResource, float cornerRadius,Paint[] paints,boolean fsf) {
        // Set the desired color here
        paint.setColor(context.getResources().getColor(R.color.main_body_color));
        paintx = paints;
        paint_darkGray.setColor(context.getResources().getColor(R.color.black));
        paint_amber.setColor(context.getResources().getColor(R.color.yellow));
        paint_red.setColor(context.getResources().getColor(R.color.red));
        paint_green.setColor(context.getResources().getColor(R.color.green));
        paint_gray.setColor(context.getResources().getColor(R.color.off_color));
        this.image = BitmapFactory.decodeResource(context.getResources(), imageResource);
        fullScreenFlag = fsf;
        this.cornerRadius = cornerRadius;

    }

    @Override
    public void draw(Canvas canvas) {
        RectF bounds = new RectF(getBounds());
        float width = bounds.width();
        float height = bounds.height();
        float rectangleWidth = width * 0.9f; // 60% of screen width
        float rectangleHeight = rectangleWidth ; //height * 0.6f;
        float top_pos = (height - rectangleHeight)/2.0f;
        // Calculate the left and right offsets to center the rectangle horizontally
        float leftOffset = (width - rectangleWidth) / 2;



        //draw main Body
        cornerRadius = 30;
        RectF rect = new RectF(leftOffset, top_pos, leftOffset + rectangleWidth, top_pos + rectangleHeight);
        Path path = new Path();
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);

        canvas.drawPath(path, paint);

        //draw bezel
        cornerRadius = 10;
        float bezel_width = rectangleWidth * 0.5f;
        float bezel_height = rectangleHeight * 0.8f;
        float bezelleftOffset = leftOffset + (rectangleWidth - bezel_width)/2;
        float top_pos_bezel = top_pos + (rectangleHeight - bezel_height)/2;
        rect = new RectF(bezelleftOffset, top_pos_bezel, bezelleftOffset + bezel_width,  top_pos_bezel + bezel_height);
        path = new Path();
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);

        canvas.drawPath(path, paint_darkGray);

        //draw LED


        if(paintx == null)
        {

        }
        else {


            float led_width = bezel_width * 0.2f;
            float led_height = led_width;
            float led_left_xpos = bezelleftOffset + (bezel_width - 2.0f * led_width) / 2f;
            float led_top_pos = top_pos_bezel + (bezel_height - 5.0f * (led_height+2)) / 2f;
            if(fullScreenFlag)
            {
                rect = new RectF(0, 0, width, height);
                path = new Path();
                path.addRoundRect(rect, 0, 0, Path.Direction.CW);

                canvas.drawPath(path, paint_darkGray);
                led_width = width/2;
                led_height = led_width;
                if(led_height*5>height)
                {
                    led_height = height/5;
                    led_width = led_height;
                }
                led_left_xpos = (width - (2*led_width))/2;
                led_top_pos = 1;
            }
            else
            {

            }



            rect = new RectF(led_left_xpos, led_top_pos, led_left_xpos + led_width, led_top_pos + led_height);
            path = new Path();
            path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);

            canvas.drawPath(path, paintx[9]);


            float led_right_xpos = led_left_xpos + led_width + 1f;
            //led_top_pos = top_pos + (bezel_height - 5.0f * led_height) /2f;
            rect = new RectF(led_right_xpos + 1, led_top_pos, led_right_xpos + led_width + 1, led_top_pos + led_height);
            path = new Path();
            path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);

            canvas.drawPath(path, paintx[8]);
           // canvas.drawPath(path, paint_amber);


            rect = new RectF(led_left_xpos, led_top_pos + (led_height + 1), led_left_xpos + led_width, led_top_pos + (led_height + 1) + led_height);
            path = new Path();
            path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);

            canvas.drawPath(path, paintx[7]);

            rect = new RectF(led_right_xpos, led_top_pos + (led_height + 1), led_right_xpos + led_width, led_top_pos + (led_height + 1) + led_height);
            path = new Path();
            path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);

            canvas.drawPath(path, paintx[6]);

            rect = new RectF(led_left_xpos, led_top_pos + 2 * (led_height + 1), led_left_xpos + led_width, led_top_pos + 2 * (led_height + 1) + led_height);
            path = new Path();
            path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);

            canvas.drawPath(path, paintx[5]);

            rect = new RectF(led_right_xpos, led_top_pos + 2 * (led_height + 1), led_right_xpos + led_width, led_top_pos + 2 * (led_height + 1) + led_height);
            path = new Path();
            path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);

            canvas.drawPath(path, paintx[4]);


            rect = new RectF(led_left_xpos, led_top_pos + 3 * (led_height + 1), led_left_xpos + led_width, led_top_pos + 3 * (led_height + 1) + led_height);
            path = new Path();
            path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);

            canvas.drawPath(path, paintx[3]);

            rect = new RectF(led_right_xpos, led_top_pos + 3 * (led_height + 1), led_right_xpos + led_width, led_top_pos + 3 * (led_height + 1) + led_height);
            path = new Path();
            path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);

            canvas.drawPath(path, paintx[2]);


            rect = new RectF(led_left_xpos, led_top_pos + 4 * (led_height + 1), led_left_xpos + led_width, led_top_pos + 4 * (led_height + 1) + led_height);
            path = new Path();
            path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);

            canvas.drawPath(path, paintx[1]);


            rect = new RectF(led_right_xpos, led_top_pos + 4 * (led_height + 1), led_right_xpos + led_width, led_top_pos + 4 * (led_height + 1) + led_height);
            path = new Path();
            path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);

            canvas.drawPath(path, paintx[0]);

            if(!fullScreenFlag) {
                // Draw text


                String textToDraw1 = "SF";
                Paint textPaint1 = new Paint();
                textPaint1.setColor(Color.WHITE);
                textPaint1.setTextSize(30);
                textPaint1.setAntiAlias(true);
                int textX = (int) ((led_left_xpos - bezelleftOffset)/2 + bezelleftOffset); // Center the text horizontally within the rectangle
                int textY = (int) (led_top_pos +  (led_height + 1) - led_height/2);
                canvas.drawText(textToDraw1, textX, textY, textPaint1);


                textToDraw1 = "PF";
                textPaint1 = new Paint();
                textPaint1.setColor(Color.WHITE);
                textPaint1.setTextSize(30);
                textPaint1.setAntiAlias(true);
                textX = (int) (led_right_xpos + led_width + 35); // Center the text horizontally within the rectangle
                //textY = (int) led_top_pos + 15;
                canvas.drawText(textToDraw1, textX, textY, textPaint1);

                canvas.drawText("4", textX, led_top_pos + 2 * (led_height + 1) - led_height/2, textPaint1);
                canvas.drawText("3", textX, led_top_pos + 3 * (led_height + 1)  - led_height/2, textPaint1);
                canvas.drawText("2", textX, led_top_pos + 4 * (led_height + 1)  - led_height/2, textPaint1);
                canvas.drawText("1", textX, led_top_pos + 5 * (led_height + 1)  - led_height/2, textPaint1);

                String textToDraw = "ELS200+";
                Paint textPaint = new Paint();
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(40);
                textPaint.setAntiAlias(true);
                textX = (int) bezelleftOffset + 15; // Center the text horizontally within the rectangle
                textY = (int) top_pos_bezel + 50; // Center the text vertically within the rectangle
                canvas.drawText(textToDraw, textX, textY, textPaint);


                float imageWidth = image.getWidth();
                float imageHeight = image.getHeight();
                float scaleFactor = rectangleWidth / (imageWidth * 3);

                float scaledImageWidth = imageWidth * scaleFactor;
                float scaledImageHeight = imageHeight * scaleFactor;
                float imageLeft = bezelleftOffset + 20; // + (rectangleWidth - scaledImageWidth) / 2;
                float imageTop = top_pos_bezel + bezel_height - (scaledImageHeight) - 20;

                canvas.drawBitmap(image, null, new RectF(imageLeft, imageTop, imageLeft + scaledImageWidth, imageTop + scaledImageHeight), null);
            }
        }
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(android.graphics.ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.TRANSLUCENT;
    }
}