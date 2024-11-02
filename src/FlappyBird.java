
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    // Board Params
    int boardWidth = 360, boardHeight = 640;
    // Images
    Image backgroundImg, birdImg, topPipeImg, bottomPipeImg;
    // Bird Default Params
    int birdX = boardWidth / 8, birdY = boardHeight / 2;
    int birdWidth = 34, birdHeight = 24;

    public class Bird {

        int x = birdX, y = birdY, width = birdWidth, height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }
    // Pipes
    int pipeX = boardWidth, pipeY = 0; // At the right and top
    int pipeWidth = 64, pipeHeight = 512;

    public class Pipe {

        int x = pipeX, y = pipeY, width = pipeWidth, height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }

    }
    // Logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1; // Every frame, the bird will go down by 1 on the X axis
    ArrayList<Pipe> pipes;
    Random random = new Random();
    Timer gameLoop, placePipesTimer;
    boolean gameOver = false;
    double score = 0;
    public FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();
        //Timer
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

    }

    public void placePipes() {
        int randomPipeY = (int) ((int) (pipeY - pipeHeight / 4) - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    // JPanel paintComponent
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);

    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);

        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 31));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int)score), 10, 35);
        } else {
            g.drawString(String.valueOf((int)score), 10, 35);
        }
    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); // Cap the bird to the top of the JPanel
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
            }
            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }
        
        if (bird.y > boardHeight) {
            gameOver = true;
        }

    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width
                && a.x + a.width > b.x
                && a.y < b.height + b.y
                && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop(); 
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (gameOver) {
                score = 0;
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
