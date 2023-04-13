
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import java.util.ArrayList;

public class Display extends JPanel implements Runnable{

    int cellSize = 50;
    int width;
    int height;
    Color color = new Color(0, 153, 204);
    Board board;
    private Thread thread;
    private Thread animateThread;

    int startX = cellSize+5;
    int startY = cellSize+5;
    int targetX = cellSize+5;
    int targetY = cellSize+5;
    int interimX;
    int interimY;

    int currentToMove;

    boolean animate = false;
    boolean solved = false;
    ArrayList<Move> moveSequence;

    public Display(Board b, ArrayList<Move> mS) {
        width = b.getWidth();
        height = b.getHeight();

        board = b;
        moveSequence = mS;

    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D graph = (Graphics2D)g;

        int x = 0;
        int y = 0;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (solved){
                    Color solvedColor = new Color(153, 204, 0);
                }
                if (!animate) {
                    Position pos = new Position(col, row);
                    if (board.getField(pos) != 0) {
                        graph.setColor(color);
                        graph.fillRoundRect(x + 5, y + 5, cellSize, cellSize, 15, 15);

                        graph.setColor(Color.WHITE);
                        int centerX = (x + (x + cellSize)) / 2;
                        int centerY = (y + (y + cellSize)) / 2;
                        graph.drawString(Integer.toString(board.getField(pos)), centerX, centerY + 5);
                    }
                    x += cellSize + 5;
                } else {
                    graph.setColor(getBackground());
                    graph.fillRect(startX + 5, startY + 5, cellSize, cellSize);

                    graph.setColor(color);
                    graph.fillRoundRect(interimX + 5, interimY + 5, cellSize, cellSize, 15, 15);

                    graph.setColor(Color.WHITE);
                    int centerX = (interimX + (interimX + cellSize)) / 2;
                    int centerY = (interimY + (interimY + cellSize)) / 2;

                    graph.drawString(Integer.toString(currentToMove), centerX, centerY + 5);

                }
            }
            x = 0;
            y += cellSize + 5;
        }

        startThread();

    }

    public static void pause(int t) {
        try {
            Thread.sleep(t);
        }
        catch (InterruptedException e) {
            System.out.println("Error sleeping");
        }
    }

    private void stopThread() {
        if (thread != null) {
            Thread temp = thread;
            thread = null;
            temp.interrupt();
        }
    }
    private void stopAnimateThread() {
        if (animateThread != null) {
            Thread temp = animateThread;
            animateThread = null;
            temp.interrupt();
        }
    }

    private void startAnimateThread() {
        if (animateThread == null) {
            animateThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (Thread.currentThread() == animateThread) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            break;
                        }

                        startX = interimX;
                        startY = interimY;
                        if (interimX >= targetX+5) {
                            interimX -= 5;
                        }
                        else if (interimX < targetX) {
                            interimX += 5;
                        }
                        if (interimY >= targetY +5) {
                            interimY -= 5;
                        }
                        else if (interimY < targetY) {
                            interimY += 5;
                        }
                        repaint();
                    }
                }
            });
            animateThread.start();
        }
    }
    private void startThread() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void run() {

        while (Thread.currentThread() == thread) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }

            animate = true;

            if (!moveSequence.isEmpty()) {
                if (board.getField(moveSequence.get(0).pos) != 0) {
                    currentToMove = board.getField(moveSequence.get(0).pos);
                }
                else {
                    currentToMove = board.getField(moveSequence.get(0).targetPosition());
                }
                Move currentMove = moveSequence.remove(0);

                startX = currentMove.pos.x * (cellSize + 5);
                startY = currentMove.pos.y * (cellSize + 5);

                interimX = startX;
                interimY = startY;

                targetX = currentMove.targetPosition().x * (cellSize + 5);
                targetY = currentMove.targetPosition().y * (cellSize + 5);

                board.doMove(currentMove);

                startAnimateThread();
                stopThread();
            }

            if (board.isSolved()) {
                solved = true;
            }

            repaint();
        }
    }

}
