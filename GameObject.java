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
    protected double x;
    protected double y;
    protected int sync; //synchronisation with the current game tick
    protected Shape shape;
    protected Color color;
    protected double speedX; //pixels per second horizontally
    protected double speedY; //pixels per second vertically
    protected boolean moved;
    protected boolean collidable;
    public final double getX(){
        return x;
    }
    public final double getY(){
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
    
    protected boolean validPos(double X, double Y){ //for an external programmer to determine if a spot is valid to move to or not
        return true;
    }
    protected final boolean moveTo(double X, double Y){ //for use by an external programmer
        if(!validPos(X,Y)) return false;
        x = x;
        y = y;
        if(!moved) sync++;
        moved = true;
        return true;
    }
    protected final boolean moveTo(double X, double Y, int tick){ //for use by an instance of GamePanel
        if(!validPos(X,Y)) return false;
        x = X;
        y = Y;
        sync = tick;
        return true;
    }
    public final void onGameTickDefault(int tick, ArrayList<GameObject> collisions){ //default behaviour per game tick
        if(sync==tick) return;
        moveTo(x+(speedX/Constants.TICK_RATE), y+(speedY/Constants.TICK_RATE), tick); //simulate speed per tick
        //TODO: handle if there are collisions (if collisions is not an empty set)
        sync = tick;
        moved = false; //reset moved boolean for the next tick
    }
    public void onGameTick(int tick, ArrayList<GameObject> collisions){ //entry point defining behaviour per game tick
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
        moved = false;
        collidable = collides;
    }
}
