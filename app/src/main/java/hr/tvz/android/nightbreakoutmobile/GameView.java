package hr.tvz.android.nightbreakoutmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import java.util.Random;

public class GameView extends View {

    private boolean isSoundOn = false;

    Context context;
    float ballX, ballY;
    Velocity velocity = new Velocity(25, 32);
    Handler handler;
    final long UPDATE_MILLIS = 30;
    Runnable runnable;
    Paint textScore = new Paint();
    Paint healthText = new Paint();
    Paint healthBar = new Paint();
    Paint brick = new Paint();
    float TEXT_SIZE = 120;
    float paddlePosX, paddlePosY;
    float oldX, oldPaddleX;
    int points = 0;
    int life = 3;
    Bitmap ball, paddle;
    int screenWidth, screenHeight;
    int ballWidth, ballHeight;
    MediaPlayer ballBounce, brickHit, levelMusic, paddleHit;
    Random random;
    Brick[] bricks = new Brick[30];
    int numBricks = 0;
    int destroyedBricks = 0;
    boolean gameOver = false;

    public GameView(Context context) {
        super(context);
        this.context = context;
        ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        paddle = BitmapFactory.decodeResource(getResources(), R.drawable.paddle);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };

        levelMusic = MediaPlayer.create(context, R.raw.levelmusic);
        ballBounce = MediaPlayer.create(context, R.raw.ballbounce);
        brickHit = MediaPlayer.create(context, R.raw.brickbreak);
        paddleHit = MediaPlayer.create(context, R.raw.paddlehit);

        playSound();

        textScore.setColor(Color.RED);
        textScore.setTextSize(TEXT_SIZE);
        textScore.setTextAlign(Paint.Align.LEFT);
        healthText.setColor(Color.GREEN);
        healthText.setTextSize(TEXT_SIZE);
        healthBar.setColor(Color.GREEN);
        brick.setColor(Color.argb(255, 188, 74, 60));
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        random = new Random();
        ballX = random.nextInt(screenWidth - 50);
        ballY = screenHeight / 3;
        paddlePosY = (screenHeight * 4) / 5;
        paddlePosX = screenWidth / 2 - paddle.getWidth() / 2;
        ballWidth = ball.getWidth();
        ballHeight = ball.getHeight();
        createBricks();
    }

    private void createBricks() {
        int brickWidth = screenWidth / 8;
        int brickHeight = screenHeight / 16;
        for(int column = 0; column < 8; column++){
            for(int row = 0; row < 3; row++){
                bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                numBricks++;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        ballX += velocity.getX();
        ballY += velocity.getY();
        if((ballX >= screenWidth - ball.getWidth()) || ballX <= 0){
            velocity.setX(velocity.getX() * -1);
        }
        if(ballY <= 0){
            velocity.setY(velocity.getY() * -1);
        }
        if(ballY > paddlePosY + paddle.getHeight()) {
            ballX = 1 + random.nextInt(screenWidth - ball.getWidth() - 1);
            ballY = screenHeight / 3;
            if (ballBounce != null) {
                ballBounce.start();
            }
            velocity.setX(xVelocity());
            velocity.setY(32);
            life--;
            if (life == 0) {
                gameOver = true;
                launchGameOver();
            }
        }
            if(((ballX + ball.getWidth()) >= paddlePosX)
                    && (ballX <= paddlePosX + paddle.getWidth())
                    && (ballY + ball.getHeight() >= paddlePosY)
                    && (ballY + ball.getHeight() <= paddlePosY + paddle.getHeight())){
                if(paddleHit != null){
                    paddleHit.start();
                }
                velocity.setX(velocity.getX() + 1);
                velocity.setY((velocity.getY() + 1) * -1);
            }
            canvas.drawBitmap(ball, ballX, ballY, null);
            canvas.drawBitmap(paddle, paddlePosX, paddlePosY, null);
            for(int i = 0; i < numBricks; i++){
                if(bricks[i].getVisibility()){
                        canvas.drawRect(bricks[i].column * bricks[i].width + 1,
                                bricks[i].row * bricks[i].height + 1,
                                bricks[i].column * bricks[i].width + bricks[i].width -1,
                                bricks[i].row * bricks[i].height + bricks[i].height - 1,
                                brick);
                }
            }
            canvas.drawText("" + points, 20, TEXT_SIZE, textScore);
            if(life == 2){
                healthBar.setColor(Color.YELLOW);
                healthText.setColor(Color.YELLOW);
            }
            else if(life == 1){
                healthBar.setColor(Color.RED);
                healthText.setColor(Color.RED);
            }
            canvas.drawText("Health:", screenWidth - 630, TEXT_SIZE-30, healthText);
            canvas.drawRect(screenWidth - 200, 30, screenWidth - 200 + 60 * life, 80, healthBar);
            for(int i = 0; i < numBricks; i++){
                if(bricks[i].getVisibility()){
                    if(ballX + ballWidth >= bricks[i].column * bricks[i].width
                    && ballX <= bricks[i].column * bricks[i].width + bricks[i].width
                    && ballY <= bricks[i].row * bricks[i].height + bricks[i].height
                    && ballY >= bricks[i].row * bricks[i].height){
                        if(brickHit != null){
                            brickHit.start();
                        }
                        velocity.setY((velocity.getY() + 1) * -1);
                        bricks[i].setInvisible();
                        points += 10;
                        destroyedBricks++;
                        if(destroyedBricks == 24){
                            launchGameOver();
                        }
                    }
                }
            }

            if(destroyedBricks == numBricks){
                gameOver = true;
            }
            if(!gameOver){
                handler.postDelayed(runnable, UPDATE_MILLIS);
            }
        }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if(touchY >= paddlePosY){
            int action = event.getAction();
            if(action == MotionEvent.ACTION_DOWN){
                oldX = event.getX();
                oldPaddleX = paddlePosX;
            }
            if(action == MotionEvent.ACTION_MOVE){
                float shift = oldX - touchX;
                float newPaddleX = oldPaddleX - shift;
                if(newPaddleX <= 0){
                    paddlePosX = 0;
                }
                else if(newPaddleX >= screenWidth - paddle.getWidth()){
                    paddlePosX = screenWidth - paddle.getWidth();
                }
                else{
                    paddlePosX = newPaddleX;
                }
            }
        }
        return true;
    }

    private void launchGameOver() {
        handler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(context, GameOver.class);
        intent.putExtra("points", points);
        context.startActivity(intent);
        ((Activity) context).finish();
        stopSound();
    }

    private int xVelocity() {
        int[] values = {-35, -30, -25, 25, 30, 35};
        int index = random.nextInt(6);
        return values[index];
    }

    public void playSound() {
        if (levelMusic != null && !levelMusic.isPlaying()) {
            levelMusic.start();
        }
    }

    public void stopSound() {
        if (levelMusic != null) {
            levelMusic.stop();
            levelMusic.prepareAsync();
        }
    }
}
