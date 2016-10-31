import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Mitchell Sayer on 5/9/15.
 */
public class Minesweeper implements ActionListener {

    JFrame frame = new JFrame("Minesweeper");
    JButton reset = new JButton("Reset");
    JButton solve = new JButton("Solve");
    JToggleButton[][] buttons = new JToggleButton[20][20];
    int[][] counts = new int [20][20];
    Container grid = new Container();
    boolean lost = false;
    boolean firstLost = true;
    boolean canSolve = false;
    int mineCount = 30;
    final int MINE = 10;
    ArrayList<Integer> marked = new ArrayList<>();

    public static void main(String[] args)
    {
        new Minesweeper();
    }
    //lets gooo

    public Minesweeper()
    {
        frame.setSize(900, 900);
        frame.setLayout(new BorderLayout());
        frame.add(reset, BorderLayout.NORTH);
        frame.add(solve, BorderLayout.SOUTH);
        reset.addActionListener(this);
        solve.addActionListener(this);
        grid.setLayout(new GridLayout(20, 20));
        for (int r = 0; r < buttons.length; r++)
            for (int c = 0; c < buttons[0].length; c++) {
                buttons[r][c] = new JToggleButton();
                buttons[r][c].addActionListener(this);
                grid.add(buttons[r][c]);
                buttons[r][c].setSize(frame.getWidth() / 20, frame.getHeight() / 22);
            }
        frame.add(grid,BorderLayout.CENTER);
        addRandomMines();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void addRandomMines()
    {
        ArrayList<Integer> mineList = new ArrayList<>();
        for (int x = 0; x < counts.length; x++) {
            for (int y = 0; y < counts[0].length; y++){
                mineList.add((x*100)+y);
            }
        }
        counts = new int[20][20];
        for (int i = 0; i < mineCount; i++) {
            int choice = (int)(Math.random()*mineList.size());
            counts[mineList.get(choice)/100][mineList.get(choice)%100] = MINE;
            mineList.remove(choice);
        }


        for (int x = 0; x < counts.length; x++) {
            for (int y = 0; y < counts[0].length; y++){
                if (counts[x][y]!=MINE) {
                    int mineCount = 0;
                    if (x > 0 && y > 0 && counts[x - 1][y - 1] == MINE)
                        mineCount++;
                    if (y > 0 && counts[x][y - 1] == MINE)
                        mineCount++;
                    if (x > 0 && counts[x - 1][y] == MINE)
                        mineCount++;
                    if (x < counts.length - 1 && counts[x + 1][y] == MINE)
                        mineCount++;
                    if (y < counts.length - 1 && counts[x][y + 1] == MINE)
                        mineCount++;
                    if (x < counts.length - 1 && y < counts.length - 1 && counts[x + 1][y + 1] == MINE)
                        mineCount++;
                    if (x > 0 && y < counts.length - 1 && counts[x - 1][y + 1] == MINE)
                        mineCount++;
                    if (x < counts.length - 1 && y > 0 && counts[x + 1][y - 1] == MINE)
                        mineCount++;
                    counts[x][y] = mineCount;
                }
            }
        }
    }

    public void showTile(int r, int c)
    {
        if (counts[r][c] == 0) {
            buttons[r][c].setText("");
            buttons[r][c].setSelected(true);
        }
        else if (counts[r][c]==MINE) {
            buttons[r][c].setForeground(Color.red);
            buttons[r][c].setText("X");
            buttons[r][c].setSelected(true);
        }
        else {
            buttons[r][c].setText(counts[r][c] + "");
            if (counts[r][c]==1)
                buttons[r][c].setForeground(Color.blue);
            else if (counts[r][c]==2)
                buttons[r][c].setForeground(Color.magenta);
            else if (counts[r][c]==3)
                buttons[r][c].setForeground(Color.green);
            buttons[r][c].setSelected(true);
        }
    }

    public void clearEmpty(int row, int col) {
        for ( int r = row - 1; r <= row + 1; r++ ) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (r >= 0 && r < counts.length && c >= 0 && c < counts[0].length) {
                    if (!buttons[r][c].isSelected()) {
                        showTile(r, c);
                        if (counts[r][c] == 0)
                            clearEmpty(r, c);
                    }
                }
            }
        }
    }

    public boolean checkLose() {
        boolean won = true;
        for (int x = 0; x < buttons.length; x++) {
            for (int y = 0; y < buttons[0].length; y++){
                if (counts[x][y]==MINE&&buttons[x][y].isSelected())
                    won = false;
            }
        }
        if (!won) {
            for (int x = 0; x < buttons.length; x++) {
                for (int y = 0; y < buttons[0].length; y++){
                    buttons[x][y].setEnabled(false);
                    if (counts[x][y]==MINE) {
                        buttons[x][y].setEnabled(true);
                        showTile(x,y);
                    }
                }
            }
            return true;
        }
        else
            return false;
    }

    public boolean checkWin() {
        boolean won = true;
        for (int x = 0; x < buttons.length; x++) {
            for (int y = 0; y < buttons[0].length; y++){
                if (counts[x][y]!=MINE&&!buttons[x][y].isSelected())
                    won = false;
            }
        }
        if (won&&!lost) {
            for (int x = 0; x < buttons.length; x++) {
                for (int y = 0; y < buttons[0].length; y++){
                    buttons[x][y].setEnabled(false);
                }
            }
            return true;
        }
        else
            return false;
    }

    public int surroundingClosed(int x, int y) {
        int count = 0;
        for ( int r = x - 1; r <= x + 1; r++ ) {
            for (int c = y - 1; c <= y + 1; c++) {
                if (r >= 0 && r < counts.length && c >= 0 && c < counts[0].length) {
                    if (!buttons[r][c].isSelected())
                        count++;
                }
            }
        }
        return count;
    }

    public void markItem(int x, int y, int n) {
        int count = 0;
        for ( int r = x - 1; r <= x + 1; r++ ) {
            for (int c = y - 1; c <= y + 1; c++) {
                if (r >= 0 && r < counts.length && c >= 0 && c < counts[0].length) {
                    if (!buttons[r][c].isSelected()) {
                        if (count>n)
                            return;
                        else {
                            if (!marked.contains(r*100+c)) {
                                marked.add(r * 100 + c);
                                count++;
                            }
                        }
                    }
                }
            }
        }
    }

    public int knownMineCount(int x,int y) {
        int count = 0;
        for ( int r = x - 1; r <= x + 1; r++ ) {
            for (int c = y - 1; c <= y + 1; c++) {
                if (r >= 0 && r < counts.length && c >= 0 && c < counts[0].length) {
                    int arrayVal = r*100+c;
                    if (marked.contains(arrayVal))
                        count++;
                }
            }
        }
        return count;
    }

    public int openNonMines(int x, int y) {
        int count = 0;
        for ( int r = x - 1; r <= x + 1; r++ ) {
            for (int c = y - 1; c <= y + 1; c++) {
                if (r >= 0 && r < counts.length && c >= 0 && c < counts[0].length) {
                    int arrayVal = r*100+c;
                    if (!marked.contains(arrayVal)) {
                        showTile(r,c);
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public boolean solveGame() {
        int count = 0;
        int dif=0;
        for (int x = 0; x < counts.length; x++) {
            for (int y = 0; y < counts[0].length; y++) {
                if (buttons[x][y].isSelected()) {
                    int surround = surroundingClosed(x, y);
                    int curCount = counts[x][y];
                    int kmc = knownMineCount(x, y);
                    if (surround == curCount)
                        markItem(x, y, curCount);
                    if (surround > curCount && kmc == curCount&&!marked.contains(x*100+y)) {
                        int cOld = count;
                        count+=openNonMines(x, y);
                        dif=count-cOld;
                    }
                }
            }
        }
        if (marked.size()==30&&checkWin()) {
            canSolve = true;
            return false;
        }
        if (dif>0)
            return true;
        else
            return false;

    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource().equals(reset)) {
            for (int r = 0; r < buttons.length; r++) {
                for (int c = 0; c < buttons[0].length; c++) {
                    buttons[r][c].setEnabled(true);
                    buttons[r][c].setSelected(false);
                    buttons[r][c].setText("");
                }
            }
            canSolve=false;
            marked.clear();
            addRandomMines();
        }
        else if (event.getSource().equals(solve)) {
            while (solveGame())
                canSolve=false;
            if (canSolve||checkWin()) {
                JOptionPane.showMessageDialog(frame, "The computer wins dumbass");
                for (int x = 0; x < buttons.length; x++) {
                    for (int y = 0; y < buttons[0].length; y++){
                        buttons[x][y].setEnabled(false);
                    }
                }
            }
            else if (!canSolve) {
                JOptionPane.showMessageDialog(frame, "Uh oh, you are gonna have to guess, dumbass");
            }
        }
        else {
            for (int r = 0; r < buttons.length; r++) {
                for (int c = 0; c < buttons[0].length; c++) {
                    if (event.getSource().equals(buttons[r][c])) {
                        if (counts[r][c] == 0) {
                            clearEmpty(r,c);
                        }
                        showTile(r, c);
                        if (checkWin()) {
                            JOptionPane.showMessageDialog(frame, "YOU WIN DUMBASS");
                            return;
                        }
                        else if (checkLose()) {
                            JOptionPane.showMessageDialog(frame, "you lose dumbass");
                            return;
                        }
                    }
                }
            }
        }
    }
}