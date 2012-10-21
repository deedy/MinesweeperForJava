/**TO DO
 * 0. Don't Let player lose on first click
 * 1. Add Reset Button
 * 2. Game Lost, Game Won sign
 * 3. Real Flag and Bomb symbols
 * 4. Time Calculator
 * 5. High score table
 * 6. Number of bomb setting option - custom mode
 * 7. Difficulty mode option
 * 8. Fix Flagging non-Pushable scores
 */


import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Minesweeper extends GraphicsProgram implements MouseMotionListener {
    
    /** Number of bricks per row */
    public static  int TILE_COLUMNS= 10;
    /** Number of rows of bricks, in range 1..10. */
    public static  int TILE_ROWS= 10;
    /** Width of a brick */
    public static int TILE_SIDE=40;
    /** Width of the game display (al coordinates are in pixels) */
    public static final int GAME_WIDTH= TILE_COLUMNS*TILE_SIDE;
    /** Height of the game display */
    public static final int GAME_HEIGHT= TILE_ROWS*TILE_SIDE;
    public static char Mines[][]=new char [TILE_COLUMNS] [TILE_ROWS];
    public static boolean TILE_BLOCK=false;
    public static boolean GAME_LOSS, GAME_WIN;
    /** Mine Number. Default setting - 10% of squares */
    public static final int MINE_NUMBER= (int) (TILE_COLUMNS*TILE_ROWS/10);
     /** rowColors[i] is the color of row i of bricks */
    private static final Color[] TileColors= {Color.white, Color.blue, Color.green, Color.red, new Color (128,0,128),Color.black,
    new Color (128,0,0), new Color (64, 224, 208), new Color (128,128,128)};
    
    /** random number generator */
    private RandomGenerator rgen= new RandomGenerator();
    /** Run the program as an application.
      */
    public static void main(String[] args) {
        String[] sizeArgs= {"width=" + GAME_WIDTH, "height=" + GAME_HEIGHT};
        new Minesweeper().start(sizeArgs);
    }
    
    /** Run the Minesweeper program. */
    public void run() {
        setup();
    }
    
    /** Set Up all the components of Minesweeper */
    public void setup () {
        createBricks();
        MineGenerator();
        NumberGenerator();
        addMouseListeners();
    }
    
    /** Creates the set of Bricks for the Minesweeper board */
    private void createBricks () {
        for (int i=0;i<TILE_COLUMNS;i++) {
            for (int j=0;j<TILE_ROWS;j++) {
                Tile tile=new Tile (i*TILE_SIDE, j*TILE_SIDE, TILE_SIDE,TILE_SIDE);
                tile.setColor (new Color (100,100,100));
                tile.setRaised (true);
                tile.setFilled (true);
                add(tile);
            }
        }
    }
    
    /** Randomly generate mines throughtout the board */
    private void MineGenerator () {
        int i=0;
        while (i<MINE_NUMBER) {
            int x=rgen.nextInt(0,TILE_COLUMNS-1);
            int y=rgen.nextInt(0,TILE_ROWS-1);
            if (Mines[x][y]!='x') {
                Mines[x][y]='x';
                i++;
            }
        }           
    }
    
    /** Generates the numbers on each of the tiles throughout the board. Each number denoted the number of mines surrounding
      * the tile. 0 is not displayed. */
    private void NumberGenerator () {
        for (int i=0;i<TILE_COLUMNS;i++) {
            for (int j=0;j<TILE_ROWS;j++) {
                if (Mines[i][j]!='x') 
                    Mines [i][j]=(char) (CountSurround (i,j)+48);
                if (Mines[i][j]=='0')
                    Mines[i][j]=' ';
            }
        }
    }
    
    /** Counts the mines surrounding the Tile at [i,j] */
    private int CountSurround (int i, int j) {
        int count=0;
        if (i+1!=TILE_COLUMNS && Mines [i+1][j]=='x')
            count++;
        if (i+1!=TILE_COLUMNS &&  j+1!=TILE_ROWS && Mines [i+1][j+1]=='x')
            count++;
        if (i-1!=-1 && j+1!=TILE_ROWS && Mines [i-1][j+1]=='x')
            count++;
        if (i+1!=TILE_COLUMNS && j-1!=-1 && Mines [i+1][j-1]=='x')
            count++;
        if (i-1!=-1 && j-1!=-1 && Mines [i-1][j-1]=='x')
            count++;
        if (i-1!=-1 &&  Mines [i-1][j]=='x')
            count++;
        if (j+1!=TILE_ROWS &&  Mines [i][j+1]=='x')
            count++;
        if (j-1!=-1 &&  Mines [i][j-1]=='x')
            count++;
        return count;
    }
  
    /** Procedure that is run when mouse is clicked */
    public void mouseClicked(MouseEvent e) {
        if (GAME_LOSS==false && GAME_WIN==false) {
            GPoint p= new GPoint(e.getPoint());
            if (SwingUtilities.isRightMouseButton(e)) {
                FlagTile (getTileAt (p));
            }
            else if (SwingUtilities.isLeftMouseButton(e)) {
                GLabel label=getLabelAt(p);
                if (label!=null && label.getLabel()==""+'#')
                    remove (label);
                PressTile (getTileAt(p), true);
            }
        }
    }
     
    /** Flag the Tile t */
    public void FlagTile (Tile t) {
        if (t.flagged==false) {
            t.flagged=true;
            t.setColor (new Color (220,220,220));
            GLabel label=new GLabel (""+'#', t.getLocation().getX(),t.getLocation().getY()+TILE_SIDE);
            label.setFont(new Font("Lucida Grande", Font.BOLD, 3*TILE_SIDE/4));
            label.move((TILE_SIDE-label.getWidth())/2 , -2*(TILE_SIDE-label.getHeight()));
            label.setColor (Color.red);
            add (label);
        }
        else {
            t.flagged=false;
            t.setColor (new Color (100,100,100));
            remove (getLabelAt(t.getLocation()));
        }
    }
        
    /** Press the Tile t */
    public void PressTile (Tile t, boolean clearblanks) {
        t.setColor (new Color (200,200,200));
        char c=LabelPrinter (t.getLocation());
        if (c=='x') {
            t.setColor (Color.red);
            BlockAllTiles();
        }
        if (c==' ' && clearblanks==true)
            clearBlanks ((int) t.getLocation().getX()/TILE_SIDE,(int)t.getLocation().getY()/TILE_SIDE);
        t.setRaised(false);
        if (isRemaining()==false)
            WinSign();
    }
     
    /** Blocks all tiles when Mine is hit */
    public void BlockAllTiles() {
        GAME_LOSS=true;
        for (int i=0;i<TILE_COLUMNS;i++) {
            for (int j=0;j<TILE_ROWS;j++) {
                Tile t= getTileAt(i,j);
                t.setColor (Color.red);
            }
        }
    }
    
    /** Clear surrounding blank tile if a blank tile is pressed */        
    public void clearBlanks(int i, int j) {
        if (i==-1 || i==TILE_COLUMNS || j==-1 || j==TILE_ROWS  || getTileAt(i,j).isRaised()==false) 
            return;
        if (Mines [i][j]!=' ') {
            PressTile (getTileAt(i,j), false);
            return;
        }
        PressTile (getTileAt(i,j), false);
        for (int p=-1;p<=1;p++) {
            for (int q=-1;q<=1;q++) {
                clearBlanks (i+p,j+q);
            }
        }
    }
    
    /** Prints the appropriate Tile label at Gpoint p and returns the character printed */
    public char LabelPrinter (GPoint p) {
        int x=(int) (p.getX()/TILE_SIDE);
        int y=(int) (p.getY()/TILE_SIDE);
        GLabel label=new GLabel (""+Mines[x][y], x*TILE_SIDE,(y+1)*TILE_SIDE);
        label.setFont(new Font("Lucida Grande", Font.BOLD, 3*TILE_SIDE/4));
        label.move((TILE_SIDE-label.getWidth())/2 , -2*(TILE_SIDE-label.getHeight()));
        if (49<=Mines [x][y] && 57>=Mines[x][y])
            label.setColor (TileColors[Mines[x][y]-48]);
        add (label);
        return Mines[x][y];
    }
    
    /** Prints the Win Symbol*/
    public void WinSign() {
        for (int i=0;i<TILE_COLUMNS;i++) {
            for (int j=0;j< TILE_ROWS;j++) {
                Tile t=getTileAt (i,j);
                t.setColor(Color.green);
                if (t.isRaised()) {
                    GLabel g=getLabelAt (t.getLocation());
                    if (g!=null)
                        g.setLabel ("W");
                    else {
                        g=new GLabel ("W",t.getLocation().getX(),t.getLocation().getY()+TILE_SIDE);
                        g.setFont(new Font("Lucida Grande", Font.BOLD, 3*TILE_SIDE/4));
                        g.move((TILE_SIDE-g.getWidth())/2 , -2*(TILE_SIDE-g.getHeight()));
                        add (g);
                    }
                    g.setColor (Color.black);
                }
            }
        }
    }
    
    /** Returns true if there are pressable tiles remaining and false otherwise */
    public boolean isRemaining() {
        for (int i=0;i<TILE_COLUMNS;i++) {
            for (int j=0;j< TILE_ROWS;j++) {
                Tile t=getTileAt(i,j);
                if (t.isRaised()==true) {
                    if (Mines [i][j]=='x')
                        continue;
                    else
                        return true;
                }
            }
        }
        GAME_WIN=true;
        return false;
    }

    /** Returns the tile at GPoint p */
     public Tile getTileAt (GPoint p) {
         int x=((int)(p.getX()/TILE_SIDE))*TILE_SIDE+1;
         int y=((int)(p.getY()/TILE_SIDE))*TILE_SIDE+1;
         GObject g=getElementAt (x,y);
         return (Tile)g;
     }
     
     /** Returns the tile at the coordinate i,j */
     public Tile getTileAt (int i, int j) {
         int x=i*TILE_SIDE+1;
         int y=j*TILE_SIDE+1;
         GObject g=getElementAt (x,y);
         return (Tile)g;
     }
     
     /** Returns the label at the coordinate i,j.Null, if there is none */
      public GLabel getLabelAt (int i, int j) {
         int x=i*TILE_SIDE+(TILE_SIDE/2);
         int y=j*TILE_SIDE+(TILE_SIDE/2);
         GObject g=getElementAt (x,y);
         if (g instanceof GLabel)
             return (GLabel)g;
         return null;
      }
      
      /** Returns the label at the Point p.Null, if there is none */
      public GLabel getLabelAt (GPoint p) {
         int x=((int)(p.getX()/TILE_SIDE))*TILE_SIDE+(TILE_SIDE/2);
         int y=((int)(p.getY()/TILE_SIDE))*TILE_SIDE+(TILE_SIDE/2);
         GObject g=getElementAt (x,y);
         if (g instanceof GLabel)
             return (GLabel)g;
         return null;
      }
}

/** An instance is a Brick */
class Tile extends G3DRect {
    public boolean flagged;
    /** Constructor: a new brick with width w and height h*/
    public Tile(double w, double h) {
           super (w,h);
           flagged=false;
    }
    
    /** Constructor: a new brick at (x,y) with width w and height h*/
    public Tile(double x, double y, double w, double h) {
             super (x,y,w,h);
             flagged=false;
    }
}