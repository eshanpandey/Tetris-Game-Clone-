package Tetris;

import Tetris.Shape.Tetro;
import javax.swing.Timer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class board extends JPanel {
    private final int boardwidth=10;
    private final int boardheight=22;
    private final int periodinterval=300;
    private Timer timer;
    private boolean fell=false;
    private boolean ispaused=false;
    private int linesremoved=0;
    private int curX=0;
    private int curY=0;
    private JLabel statusbar;
    private Shape curPiece;
    private Tetro [] board;
     
    
    
    public board(tetris parent) {

        initBoard(parent);
    }
    private void initBoard( tetris parent) {

        setFocusable(true);
        statusbar = parent.getStatusbar();
        addKeyListener(new TAdapter());
    }
    private int squarewidth(){
        return (int) getSize().getWidth() / boardwidth;

   } 
   private int squareHeight() {

    return (int) getSize().getHeight() / boardheight;
   }
   private Tetro shapeAt(int x, int y) {

    return board[(y * boardwidth) + x];
   }


    public void start() {
        curPiece = new Shape();
        board = new Tetro[boardwidth * boardheight];

        clearBoard();
        newPiece();

        timer = new Timer(periodinterval, new GameCycle());
        timer.start();
    }
    private void pause() {

        ispaused = !ispaused;

        if (ispaused) {

            statusbar.setText("paused");
        } else {

            statusbar.setText(String.valueOf(linesremoved));
        }

        repaint();
    }
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }
    private void doDrawing(Graphics g) {

        var size = getSize();
        int boardTop = (int) size.getHeight() - boardheight * squareHeight();

        for (int i = 0; i < boardheight; i++) {

            for (int j = 0; j < boardwidth; j++) {

                Tetro shape = shapeAt(j, boardheight - i - 1);

                if (shape != Tetro.NoShape) {

                    drawSquare(g, j * squarewidth(),
                            boardTop + i * squareHeight(), shape);
                }
            }
        }
        if (curPiece.getShape() != Tetro.NoShape) {

            for (int i = 0; i < 4; i++) {

                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);

                drawSquare(g, x * squarewidth(),
                        boardTop + (boardheight - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
        }
    }
    private void dropDown() {

        int newY = curY;

        while (newY > 0) {

            if (!tryMove(curPiece, curX, newY - 1)) {

                break;
            }

            newY--;
        }

        pieceDropped();
    }
    private void oneLineDown() {

        if (!tryMove(curPiece, curX, curY - 1)) {

            pieceDropped();
        }
    }
    private void clearBoard() {

        for (int i = 0; i < boardheight * boardwidth; i++) {

            board[i] = Tetro.NoShape;
        }
    }
    private void pieceDropped() {

        for (int i = 0; i < 4; i++) {

            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * boardwidth) + x] = curPiece.getShape();
        }

        removeFullLines();

        if (!fell) {

            newPiece();
        }
    }
    private void newPiece() {

        curPiece.setRandomShape();
        curX = boardwidth / 2 + 1;
        curY = boardheight - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {

            curPiece.setShape(Tetro.NoShape);
            timer.stop();

            var msg = String.format("Game over. Score: %d", linesremoved);
            statusbar.setText(msg);
        }
    }
    private boolean tryMove(Shape newPiece, int newX, int newY) {

        for (int i = 0; i < 4; i++) {

            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);

            if (x < 0 || x >= boardwidth || y < 0 || y >= boardheight) {

                return false;
            }

            if (shapeAt(x, y) != Tetro.NoShape) {

                return false;
            }
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;

        repaint();

        return true;
    }
    private void removeFullLines() {

        int numFullLines = 0;

        for (int i = boardheight- 1; i >= 0; i--) {

            boolean lineIsFull = true;

            for (int j = 0; j < boardwidth; j++) {

                if (shapeAt(j, i) == Tetro.NoShape) {

                    lineIsFull = false;
                    break;
                }
            }
            if (lineIsFull) {

                numFullLines++;

                for (int k = i; k < boardheight - 1; k++) {
                    for (int j = 0; j < boardwidth; j++) {
                        board[(k * boardwidth) + j] = shapeAt(j, k + 1);
                    }
                }
            }
        }

        if (numFullLines > 0) {

            linesremoved += numFullLines;

            statusbar.setText(String.valueOf(linesremoved));
            fell = true;
            curPiece.setShape(Tetro.NoShape);
        }
    }
    private void drawSquare(Graphics g, int x, int y, Tetro shape) {

        Color colors[] = {new Color(0, 0, 0), new Color(204, 102, 102),
                new Color(102, 204, 102), new Color(102, 102, 204),
                new Color(204, 204, 102), new Color(204, 102, 204),
                new Color(102, 204, 204), new Color(218, 170, 0)
        };

        var color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squarewidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squarewidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squarewidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squarewidth() - 1, y + squareHeight() - 1,
                x + squarewidth() - 1, y + 1);
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            doGameCycle();
        }
    }

    private void doGameCycle() {

        update();
        repaint();
    }

    private void update() {

        if (ispaused) {

            return;
        }

        if (fell) {

            fell = false;
            newPiece();
        } else {

            oneLineDown();
        }
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            if (curPiece.getShape() == Tetro.NoShape) {

                return;
            }

            int keycode = e.getKeyCode();

           
            switch (keycode) {

                case KeyEvent.VK_P -> pause();
                case KeyEvent.VK_LEFT -> tryMove(curPiece, curX - 1, curY);
                case KeyEvent.VK_RIGHT -> tryMove(curPiece, curX + 1, curY);
                case KeyEvent.VK_DOWN -> tryMove(curPiece.rotateRight(), curX, curY);
                case KeyEvent.VK_UP -> tryMove(curPiece.rotateLeft(), curX, curY);
                case KeyEvent.VK_SPACE -> dropDown();
                case KeyEvent.VK_D -> oneLineDown();
            }
        }
    }



    
}
