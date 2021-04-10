import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.Scanner;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;


public class GameMap extends JPanel {

    public static final int MODE_VS_AI = 0;
    public static final int MODE_VS_HUMAN = 1;
    private static final Random RANDOM = new Random();

    private static final char DOT_HUMAN = 'X';
    private static final char DOT_AI = '0';
    private static final char DOT_EMPTY = '.';
    private static final int DOT_PADDING = 7;
    private static final int STATE_DRAW = 0;
    private static final int STATE_WIN_HUMAN = 1;
    private static final int STATE_WIN_AI = 2;

    private int stateGameOver;
    String massage;

    private  int fieldSizeX;
    private  int fieldSizeY;
    private  char[][] field;
    private static String playerOnename="";
    private  int v;
    private  int winLength;
    private  int n;
    private  int FieldSize;
    private int count=1;
    private int gameMode;
    private int cellWidth;
    private int cellHeight;
    private boolean isGameOver;
    private boolean initialized;

    private static   int attackCount=0;
    private   static   int attackMaxCount=0;
    public   static    int scoreHuman=0;
    public   static   int scoreAI=0;

public GameMap(){
    addMouseListener(new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            update(e);

        }
    });

}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setBackground(Color.white);
        render(g);


    }

    private void render(Graphics g) {

        if (!initialized) return;
        int width = getWidth();
        int height = getHeight();
        cellWidth = width / fieldSizeX;
        cellHeight = height / fieldSizeY;
        g.setColor(Color.BLACK);
        for (int i = 0; i <fieldSizeX ; i++) {
            int y=i*cellHeight;
            g.drawLine(0,y,width,y);

        }
        for (int i = 0; i <fieldSizeY ; i++) {
            int x=i*cellWidth;
            g.drawLine(x,0,x,height);

        }
        Graphics2D g2 = (Graphics2D) g;
        for (int x = 0; x < fieldSizeX; x++) {
            for (int y = 0; y <fieldSizeY ; y++) {
                if (isCellEmpty(x,y)) continue;
                if (field[x][y]== DOT_AI) {
                    g.setColor(new Color(1,1,255));
                    g.fillOval(x*cellWidth +DOT_PADDING,y*cellHeight+DOT_PADDING,
                            cellWidth-DOT_PADDING*2,cellHeight-DOT_PADDING*2);
                    g.setColor(Color.white);
                    g.fillOval(x*cellWidth +DOT_PADDING*2,y*cellHeight+DOT_PADDING*2,
                            cellWidth-DOT_PADDING*2*2,cellHeight-DOT_PADDING*2*2);
                }else if (field[x][y]== DOT_HUMAN) {
                    //g.setColor(Color.BLACK);
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(10.0f));
//                    g.fillRect(x*cellWidth +DOT_PADDING,y*cellHeight+DOT_PADDING,
//                            cellWidth-DOT_PADDING*2,cellHeight-DOT_PADDING*2);
                      g2.drawLine(x*cellWidth+DOT_PADDING*3,y*cellHeight+DOT_PADDING*3,
                              x*cellWidth+DOT_PADDING*3+cellWidth-DOT_PADDING*2*3,
                              y*cellHeight+DOT_PADDING*3+cellHeight-DOT_PADDING*2*3);
                      g2.drawLine(x*cellWidth+DOT_PADDING*3,y*cellHeight+cellHeight-DOT_PADDING*3,
                              x*cellWidth+cellWidth-DOT_PADDING*3,
                              y*cellHeight+DOT_PADDING*3
                              );

                } else {
                    throw new RuntimeException("Something wrong with coordinates");
                }

            }
        }
        //repaint();
        if (isGameOver){
            showMessageGameOver(g);


        }


    }

    private void update(MouseEvent e) {
    if (gameMode==MODE_VS_AI) {
        if (isGameOver || !initialized) return;
        int cellX = e.getX() / cellWidth;
        int cellY = e.getY() / cellHeight;
        if (!isCellValid(cellX, cellY) || !isCellEmpty(cellX, cellY)) return;
        field[cellX][cellY] = DOT_HUMAN;
        repaint();
        if (gameCheck(DOT_HUMAN, STATE_WIN_HUMAN)) return;
        aiTurn();
        repaint();
        if (gameCheck(DOT_AI, STATE_WIN_AI)) return;
        repaint();
    }
    if(gameMode==MODE_VS_HUMAN){
        if (isGameOver || !initialized) return;
        int cellX = e.getX() / cellWidth;
        int cellY = e.getY() / cellHeight;
        if (!isCellValid(cellX, cellY) || !isCellEmpty(cellX, cellY)) return;
        if(count>0) {
            field[cellX][cellY] = DOT_HUMAN;
           count=count*(-1);
        }
        else {
            field[cellX][cellY] = DOT_AI;
            count=count*(-1);
        }
        if (gameCheck(DOT_HUMAN, STATE_WIN_HUMAN)) return;



        //repaint();
        if (gameCheck(DOT_AI, STATE_WIN_AI)) return;
        repaint();
    }
    }


    public void startNewGame(int gameMode, int fieldSize, int winLength) {

        this.fieldSizeX = fieldSize;
        this.fieldSizeY = fieldSize;
        this.winLength = winLength;
        this.n = fieldSize;
        this.v = winLength;
        field = new char[fieldSizeX][fieldSizeY];
        initField();
        initialized=true;
        isGameOver =false;
        repaint();
        this.gameMode=gameMode;
        count=1;
            System.out.printf("new game with: %dx%d sized field, mode: %d and win length %d",fieldSize,fieldSize,gameMode,winLength);
        System.out.println();
}

    private  boolean gameCheck(char dot,int stateGameOver) {
        if (checkWin(dot)) {
            if (dot == DOT_HUMAN) {
                scoreHuman++;
                this.stateGameOver = stateGameOver;
                isGameOver = true;
                repaint();
                return true;
            }
            if (dot == DOT_AI) {
                this.stateGameOver = stateGameOver;
                isGameOver = true;
                repaint();
                scoreAI++;

                //System.out.println(s);
                return true;
            }
        }
          else  if (checkDraw()) {
                this.stateGameOver = STATE_DRAW;
                isGameOver = true;
                repaint();
                // System.out.println("Draw!!!");
                return true;
            }

            return false;

    }

    private  void aiTurn() {
        int x = 0;
        int y = 0;
        int x_getshot_on_win=0;
        int y_getshot_on_win=0;

       // System.out.println("ход компьютера");
        int kmax = 0;
        int k = 1;
        int kw= 1;
        int line_without_one_empty = 0;
        boolean danger=false;
        boolean isPreWin=false;
        int[] rezf= new int[2];

        attackMaxCount=0;
        attackCount=0;


        // ищем предвыйгрышную ситуацию для компьютера  -  гадшот на  нулях
        for (int i = 0; i < n; i++) {  if (isPreWin) break;
            for (int j = 0; j < n; j++) {
                if(isPreWin) break;

                if (field[i][j] == DOT_AI || field[i][j] == DOT_EMPTY) {

                    rezf=checkDiagonalLineForPreWinSituation(DOT_AI,i,j);
                    if (rezf!=null) {
                        x=rezf[0];
                        y=rezf[1];
                        isPreWin=true;
                    }


                    rezf=checkRevDiagonalLineForPreWinSituation(DOT_AI,i,j);
                    if (rezf!=null) {
                        x=rezf[0];
                        y=rezf[1];
                        isPreWin=true;
                    }



                    rezf=checkVerticalLineForPreWinSituation(DOT_AI,i,j);
                    if (rezf!=null) {
                        x=rezf[0];
                        y=rezf[1];
                        isPreWin=true;
                    }

                    rezf=checkHorizontalLineForPreWinSituation(DOT_AI,i,j);
                    if (rezf!=null) {
                        x=rezf[0];
                        y=rezf[1];
                        isPreWin=true;
                    }
                }
            }
        }

        //проверка на предвыйгрышную ситуацию у соперника

        if (!isPreWin) {
            for (int i = 0; i < n; i++) {   if (isPreWin) break;
                for (int j = 0; j < n; j++) { if (isPreWin) break;
                    if (field[i][j] == DOT_HUMAN || field[i][j] == DOT_EMPTY) {

                        rezf = checkDiagonalLineForPreWinSituation(DOT_HUMAN, i, j);
                        if (rezf != null) {
                            x = rezf[0];
                            y = rezf[1];
                            isPreWin = true;
                        }


                        rezf = checkRevDiagonalLineForPreWinSituation(DOT_HUMAN, i, j);
                        if (rezf != null) {
                            x = rezf[0];
                            y = rezf[1];
                            isPreWin = true;
                        }
//

                        rezf = checkVerticalLineForPreWinSituation(DOT_HUMAN, i, j);
                        if (rezf != null) {
                            x = rezf[0];
                            y = rezf[1];
                            isPreWin = true;
                        }
//


                        rezf = checkHorizontalLineForPreWinSituation(DOT_HUMAN, i, j);
                        if (rezf != null) {
                            x = rezf[0];
                            y = rezf[1];
                            isPreWin = true;
                        }

                    }
                }

            }
        }

        if (!isPreWin) {

            //ищем ячейку которая включена в наибольшее кол-во линий размером в v в которых нет DOT_HUMAN
            //это большее что за неделю я придумал (лучше чем случайный выбор клетки)

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {attackCount=0;
                    if (field[i][j] == DOT_AI || field[i][j] == DOT_EMPTY) {


                        if (checkLineWithoutRivalFieldDiagonal(DOT_AI,i,j))
                            attackCount++;


                        if (checkLineWithoutRivalFieldDiagonalReverse(DOT_AI,i,j))
                            attackCount++;

                        if  (checkLineWithoutRivalFieldVertical(DOT_AI,i,j))
                            attackCount++;

                        if  (checkLineWithoutRivalFieldHorizontal(DOT_AI,i,j))
                            attackCount++;


                    }

                    if (attackCount>attackMaxCount&&field[i][j] == DOT_EMPTY) {
                        attackMaxCount = attackCount;
                        x=i;
                        y=j;
                    }


                }
            }
//        do {
//
//                x = RANDOM.nextInt(fieldSizeX);
//                y = RANDOM.nextInt(fieldSizeY);
//            }
//        while (!isCellEmpty(x, y));

        }
        field[x][y] = DOT_AI;





    }

    private  void initField() {
        //field = new char[fieldSizeY][fieldSizeX];
        for (int i=0;i<fieldSizeX;i++) {
            for (int j=0;j<fieldSizeY;j++) {
                field[i][j]=DOT_EMPTY;
            }
        }

    }

    private  boolean checkWin(char c) {
        int k=1;
        for (int i=0;i<n;i++) {
            for (int j=0;j<n;j++) {
                if (field[i][j]==c) {k=1;
                    while(k<v&&i<=n-v&&j<=n-v)                    // проверка диагональ
                        if (field[i+k][j+k] == c) {
                            k++;
                            if (k==v) return true;
                        }
                        else {
                            k = 1;
                            break; }

                    while (k<v&&j-v+1>=0&&i<=n-v)           //проверка обратной диагонали
                        if (field[i+k][j-k]==c)  {
                            k++;
                            if (k==v) return  true;
                        }
                        else {k=1;
                            break;
                        }

                    while (k<v&&i<=n-v)             //проверка верникальных
                        if (field[i+k][j]==c) {
                            k++;
                            if (k==v) return  true;
                        }
                        else {k=1;
                            break;
                        }
                    while (k<v&&j<=n-v)              //проверка горизонтальных
                        if (field[i][j+k]==c) {
                            k++;
                            if (k==v)  return true;
                        }
                        else {k=1;
                            break;
                        }

                }
            }
        }
        return false;
    }

    private  boolean checkDraw(){
        for(int y=0;y<fieldSizeY;y++) {
            for (int x=0;x<fieldSizeX;x++) {
                if (isCellEmpty(x,y)) return false;
            }
        }
        return true;
    }

   private  boolean isCellValid(int x, int y) {
        return x>=0 && y>=0 && x < fieldSizeX && y < fieldSizeY;
    }

    private  boolean isCellEmpty(int x, int y) {
        return field[x][y] == DOT_EMPTY;

    }

    private void showMessageGameOver(Graphics g) {
    g.setColor(Color.DARK_GRAY);
    g.fillRect(0,200,getWidth(),150);
    g.setColor(Color.WHITE);
    g.setFont(new Font("TimesNewRoman", Font.BOLD,60));


    massage="Счет побед: "+scoreHuman +" : " +scoreAI;
    switch (stateGameOver){
        case STATE_DRAW:{
            g.drawString("DRAW",getWidth()/3,getHeight()/2);
            g.setFont(new Font("TimesNewRoman", Font.ITALIC,30));
            g.drawString(massage,getWidth()/3,getHeight()/2 +50);
            break;}
        case STATE_WIN_HUMAN: {
            if (gameMode == MODE_VS_AI) {
                g.drawString("HUMAN wins",getWidth()/3,getHeight()/2);
                g.setFont(new Font("TimesNewRoman", Font.ITALIC,30));
                g.drawString(massage,getWidth()/3,getHeight()/2 +50);
                break;}
            if (gameMode == MODE_VS_HUMAN) {
                g.drawString("Player 1 wins",getWidth()/3,getHeight()/2);
                g.setFont(new Font("TimesNewRoman", Font.ITALIC,30));
                g.drawString(massage,getWidth()/3,getHeight()/2 +50);
                break;}
        }

        case STATE_WIN_AI:{
            if (gameMode==MODE_VS_AI) {
                g.drawString("AI wins",getWidth()/3,getHeight()/2);
                g.setFont(new Font("TimesNewRoman", Font.ITALIC,30));
                g.drawString(massage,getWidth()/3,getHeight()/2 +50);
                break;}
            if (gameMode == MODE_VS_HUMAN) {
                g.drawString("Player 2 wins",getWidth()/3,getHeight()/2);
                g.setFont(new Font("TimesNewRoman", Font.ITALIC,30));
                g.drawString(massage,getWidth()/3,getHeight()/2 +50);
                break;}

            }

    }





}



    private  boolean checkLineWithoutRivalFieldDiagonal(char c,int x,int y){
        int k=0;
        if (x+v<=n&&y+v<=n&& x == y) {          //прямые диагонали//
//                            k = 1;

            while (k < v) {             //

                if (field[x+k][y+k] == c || field[x + k][y + k] == DOT_EMPTY) {
                    k++;
                    if (k == v)
                        return true;
                } else {
                    k = 1;

                    break;
                }
            }


        }
        return false;
    }

    private  boolean checkLineWithoutRivalFieldDiagonalReverse(char c,int x,int y) {
        int k=0;
        if ( x + y == n - 1) { //проверка обратной диагонали//(x+v <= n && y-v+1 >= 0  &&

            k = 0;

            while (k < v) {       //  &&i<=n-v&&j>=v-1&&j<=n-1

                if ((field[k][n- 1 - k] == DOT_AI) || (field[k][n-1-k] == DOT_EMPTY)) {
                    k++;
                    if (k == v)
                        return true;
                } else {
                    k = 1;
                    break;
                }

            }
        }

        return false;
    }

    private  boolean checkLineWithoutRivalFieldVertical(char c,int x,int y) {
        int k;
        if (x<=v) { //проверка вертикальных//&&x+v<=n
            k = 0;

            while (k < v) {        //  &&i<=n-v&&j>=v-1&&j<=n-1

                if ((field[k][y] == c) || (field[k][y] == DOT_EMPTY)) {
                    k++;
                    if (k == v)
                        return true;
                } else {
                    k = 1;

                    break;
                }

            }

        }

        return false;
    }
    private  boolean checkLineWithoutRivalFieldHorizontal(char c,int x,int y) {
        int k;
        if (y<=v) {         //&&y+v<=n                         //проверка горизонтальных
            k = 0;

            while (k < v) {         //  &&i<=n-v&&j>=v-1&&j<=n-1

                if ((field[x][k] == c) || (field[x][k] == DOT_EMPTY)) {
                    k++;
                    if (k == v)
                        return true;
                } else {
                    k = 1;
                    break;
                }

            }
        }

        return false;
    }

    private  int[] checkDiagonalLineForPreWinSituation (char c,int ii,int jj) {
        int line_without_one_empty=0;
        int k=0;
        int xx=0;
        int yy=0;
        int[] f = new int[2];
        if ((ii <= n - v && jj <= n - v )) { //проверка диагональ прямая&& ii == jj

            k = 0;


            while (k < v)
                if ((field[ii + k][jj + k] == c) || ((field[ii + k][jj + k] == DOT_EMPTY) && (line_without_one_empty == 0))) {

                    if (field[ii + k][jj + k] == DOT_EMPTY) {
                        line_without_one_empty = 1;
                        // kmax = k;
                        xx = ii + k;
                        f[0] = xx;
                        yy = jj + k;
                        f[1] = yy;
                    }
                    k++;

                } else {
                    k = 1;
                    line_without_one_empty = 0;
                    break;
                }

            //  }
            if (k == v && line_without_one_empty == 1) return f;
        }
        return null;
    }

    private  int[] checkRevDiagonalLineForPreWinSituation(char c,int ii,int jj) {
        int k=0;
        int line_without_one_empty=0;
        int xx=0;
        int yy=0;
        int[] f = new int[2];
        if ((ii <= n - v)&&(jj-v+1>=0)) {

            k = 0;


            while (k < v) {       //  &&i<=n-v&&j>=v-1&&j<=n-1
                if ((field[ii + k][jj - k] == c) || ((field[ii + k][jj - k] == DOT_EMPTY) && (line_without_one_empty == 0))) {

                    if (field[ii + k][jj - k] == DOT_EMPTY) {

                        line_without_one_empty = 1;
                        xx = ii + k;f[0]=xx;
                        yy = jj - k;f[1]=yy;
                    }
                    k++;
                } else {
                    k = 1;
                    break;
                }
                if (k == v && line_without_one_empty == 1)
                    return f;
            }
        }
        return null;
    }

    private  int[] checkVerticalLineForPreWinSituation(char c,int ii,int jj) {
        int k = 0;
        int line_without_one_empty=0;
        int xx = 0;
        int yy = 0;
        int[] f = new int[2];

        if ( ii <= n - v) { //проверка вертикальных
            k = 0;


            while (k < v) {

                if ((field[ii + k][jj] == c) || ((field[ii + k][jj] == DOT_EMPTY) && (line_without_one_empty == 0))) {

                    if (field[ii + k][jj] == DOT_EMPTY) { //kmax < k &&
                        //kmax = k;
                        line_without_one_empty = 1;
                        xx = ii + k; f[0]=xx;
                        yy = jj;   f[1]=yy;
                    }

                    k++;
                } else {
                    k = 1;
                    break;
                }

                if (k == v && line_without_one_empty == 1) {
                    return f;
                }
            }
        }

        return null;
    }

    private  int[] checkHorizontalLineForPreWinSituation(char c,int ii,int jj) {
        int k = 0;
        int line_without_one_empty=0;
        int xx = 0;
        int yy = 0;
        int[] f = new int[2];

        if ((jj <= n - v)) {                                  //проверка горизонтальных
            k = 0;


            while (k < v) {

                if ((field[ii][jj + k] == c) || ((field[ii][jj + k] == DOT_EMPTY) && (line_without_one_empty == 0))) {

                    if (field[ii][jj + k] == DOT_EMPTY) { //kmax < k &&
                        //kmax = k;
                        line_without_one_empty = 1;
                        xx = ii;f[0]=xx;
                        yy = jj + k;f[1]=yy;
                    }
                    k++;
                } else {
                    k = 1;
                    break;
                }

                if (k == v && line_without_one_empty == 1) {
                    return f;
                }
            }
        }

        return null;
    }



}
