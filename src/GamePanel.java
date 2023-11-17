import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.*;

import javax.swing.*;

public class GamePanel extends JPanel implements Runnable{
//    static final int GAME_WIDTH = 1000;
    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = (int) (GAME_WIDTH * (0.5555)) ;

    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH,GAME_HEIGHT);

    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 150;
    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;
    Paddle paddle2;
    Ball ball;
    Score score;


    GamePanel(){
        newPaddles();
        newBall();
        score = new Score(GAME_WIDTH,GAME_HEIGHT);
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void newBall(){
        random = new Random();
//        ball = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2), (GAME_HEIGHT/2)-(BALL_DIAMETER/2),BALL_DIAMETER,BALL_DIAMETER);
        ball = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2), random.nextInt(GAME_HEIGHT-BALL_DIAMETER),BALL_DIAMETER,BALL_DIAMETER);
    }
    public void newPaddles(){
        paddle1 = new Paddle(0,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,1);
        paddle2 = new Paddle(GAME_WIDTH - PADDLE_WIDTH,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,2);
    }
    public void paint(Graphics g){
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image,0,0,this);

    }
    public void draw(Graphics g){
        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);
    }
    public void move(){
        paddle1.move();
        paddle2.move();
        ball.move();
    }
    public void checkCollision(){
        File file1 = new File("C:\\Users\\pc\\IdeaProjects\\Pong\\src\\Paddle_Collision.wav");
        File file2 = new File("C:\\Users\\pc\\IdeaProjects\\Pong\\src\\Wall_Collision.wav");
        File file3 = new File("C:\\Users\\pc\\IdeaProjects\\Pong\\src\\win.wav");
        if (ball.y<=0){
            try {
                AudioInputStream ais = AudioSystem.getAudioInputStream(file2);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.loop(0);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                throw new RuntimeException(e);
            }
            ball.setYDirection(-ball.yVelocity);
        }
        if(ball.y>=(GAME_HEIGHT)-(BALL_DIAMETER)){
            try {
                AudioInputStream ais = AudioSystem.getAudioInputStream(file2);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.loop(0);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                throw new RuntimeException(e);
            }
            ball.setYDirection(-ball.yVelocity);
        }
        if(ball.intersects(paddle1)){
            try {
                AudioInputStream ais = AudioSystem.getAudioInputStream(file1);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.loop(0);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                throw new RuntimeException(e);
            }
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++; //optional
            if(ball.yVelocity > 0){//optional
                ball.yVelocity++;
            }
            else{
                ball.yVelocity--;
            }
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }
        if(ball.intersects(paddle2)){
            try {
                AudioInputStream ais = AudioSystem.getAudioInputStream(file1);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.loop(0);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                throw new RuntimeException(e);
            }
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++; //optional
            if(ball.yVelocity > 0){//optional
                ball.yVelocity++;
            }
            else{
                ball.yVelocity--;
            }
            ball.setXDirection(-ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }
        if(paddle1.y <=0){
            paddle1.y=0;
        }
        if(paddle1.y >=(GAME_HEIGHT-PADDLE_HEIGHT)){
            paddle1.y=GAME_HEIGHT-PADDLE_HEIGHT;
        }
        if(paddle2.y <=0){
            paddle2.y=0;
        }
        if(paddle2.y >=(GAME_HEIGHT-PADDLE_HEIGHT)){
            paddle2.y=GAME_HEIGHT-PADDLE_HEIGHT;
        }
        if(ball.x <= 0){
            try {
                AudioInputStream ais = AudioSystem.getAudioInputStream(file3);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.loop(0);
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }
            score.player2++;
            newPaddles();
            newBall();
            System.out.println("Player 2 : "+score.player2);
        }
        if(ball.x >= (GAME_WIDTH-BALL_DIAMETER)){
            try {
                AudioInputStream ais = AudioSystem.getAudioInputStream(file3);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.loop(0);
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }
            score.player1++;
            newPaddles();
            newBall();
            System.out.println("Player 1 : "+score.player1);
        }
    }
    public void run(){
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        while(true){
            long now = System.nanoTime();
            delta += (now-lastTime)/ns;
            lastTime = now;
            if (delta >= 1){
                move();
                checkCollision();
                repaint();
                delta--;
            }
        }
    }
//    creating an inner class 'AL' - Action Listener
    public class AL extends KeyAdapter{
        public void keyPressed(KeyEvent e){
            paddle1.keyPressed(e);
            paddle2.keyPressed(e);
        }
        public void keyReleased(KeyEvent e){
            paddle1.keyReleased(e);
            paddle2.keyReleased(e);
        }
}
}
