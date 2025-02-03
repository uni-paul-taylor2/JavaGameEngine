import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Every item in this game should be an instance of this
 * So the idea would be to extend this class into other subclasses
 * 
 * @author (Paul Taylor)
 * @version (31st/1/2025)
 */
public class GameObject
{
    protected int x;
    protected int y;
    protected int sync; //synchronisation with the current game tick
    protected Shape shape;
    protected Color color;
    protected double speedX;
    protected double speedY;
    protected boolean collidable;
    public final int getX(){
        return x;
    }
    public final int getY(){
        return y;
    }
    public final Shape getShape(){
        return shape;
    }
    public final Color getColor(){
        return color;
    }
    public final double getSpeedX(){
        return speedX;
    }
    public final double getSpeedY(){
        return speedY;
    }
    public final boolean isCollidable(){
        return collidable;
    }
    
    protected boolean validPos(int X, int Y){
        return true;
    }
    protected final boolean moveTo(int X, int Y){
        if(!validPos(X,Y)) return false;
        x = x;
        y = y;
        sync++;
        return true;
    }
    protected final boolean moveTo(int X, int Y, int tick){
        if(!validPos(X,Y)) return false;
        x = x;
        y = y;
        sync = tick;
        return true;
    }
    public final void onGameTickDefault(int tick, ArrayList<GameObject> collisions){
        if(sync==tick) return;
        //
        sync = tick;
    }
    public void onGameTick(int tick, ArrayList<GameObject> collisions){
        onGameTickDefault(tick,collisions);
    }
    
    public void onMouseMove(MouseEvent m){}
    public void onMouseDown(MouseEvent m){}
    public void onMouseUp(MouseEvent m){}
    public void onKeyPress(KeyEvent k){}
    public void onKeyDown(KeyEvent k){}
    public void onKeyUp(KeyEvent k){}
    
    public GameObject(){
        collidable = false;
        shape = new Rectangle2D.Double(0,0,10,10);
    }
    public GameObject(Shape s){
        shape = s;
        collidable = false;
    }
    public GameObject(boolean collides){
        collidable = collides;
        shape = new Rectangle2D.Double(0,0,10,10);
    }
    public GameObject(Shape s, boolean collides){
        shape = s;
        collidable = collides;
    }
}
