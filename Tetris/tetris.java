package Tetris;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class tetris extends JFrame {
    private JLabel statusbar;
    public tetris() {
        UI();
    }

    private void UI() {
    statusbar = new JLabel(" 0");
    add(statusbar, BorderLayout.SOUTH);
    var board= new board(this);
    add (board);
    board.start();
    setTitle("Tetris");
    setSize(200,400);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
   }
  JLabel getStatusbar() {
    
    return statusbar;

   }
   public static void main(String[] args) {
       EventQueue.invokeLater(()->{
           var game =new tetris();
           game.setVisible(true);
       });
   }
}
