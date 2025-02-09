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
 * @version (3rd/2/2025)
 */
public class GameObject
{
    protected double x;
    protected double y;
    protected double angle=0, pivotX=0, pivotY=0;
    protected boolean rotated=false; //if shape has been rotated before
    protected int sync; //synchronisation with the current game tick
    protected Shape shape;
    protected Color color;
    protected double speedX=0; //pixels per second horizontally
    protected double speedY=0; //pixels per second vertically
    protected boolean moved=false;
    protected double panelWidth=Constants.DEFAULT_PANEL_WIDTH;
    protected double panelHeight=Constants.DEFAULT_PANEL_HEIGHT;
    protected boolean collidable;
    protected GameObject attatchedItem=null;
    protected double diffX=0, diffY=0; //difference in position between self and attatchedItem
    
    public static final Path2D Rotate(Shape s, double angle, double pivotX, double pivotY){
        Path2D path = new Path2D.Double(s);
        AffineTransform transformer = new AffineTransform();
        transformer.setToRotation(angle,pivotX,pivotY);
        path.transform(transformer);
        return path;
    }
    
    public final double getX(){
        if(!rotated) return shape.getBounds2D().getX();
        Shape original = Rotate(shape,-angle,pivotX,pivotY);
        return original.getBounds2D().getX();
    }
    public final double getY(){
        if(!rotated) return shape.getBounds2D().getY();
        Shape original = Rotate(shape,-angle,pivotX,pivotY);
        return original.getBounds2D().getY();
    }
    public final Shape getShape(){
        return shape;
    }
    public final void setShape(Shape s){
        shape = s;
    }
    public final Color getColor(){
        return color;
    }
    public final void setColor(Color c){
        color = c;
    }
    public final double getSpeedX(){
        return speedX;
    }
    public final double getSpeedY(){
        return speedY;
    }
    public final void setSpeed(double x_speed, double y_speed){
        speedX = x_speed;
        speedY = y_speed;
    }
    public final boolean isCollidable(){
        return collidable;
    }
    
    protected boolean validPos(double X, double Y){ //for an external programmer to determine if a spot is valid to move to or not
        return true;
    }
    protected final boolean moveTo(double X, double Y){ //for use by an external programmer
        if(!validPos(X,Y)) return false;
        if(!moved) sync++;
        AffineTransform transformer = new AffineTransform();
        double translateX = X-getX();
        double translateY = Y-getY();
        if(attatchedItem!=null){
            pivotX += translateX;
            pivotY += translateY;
        }
        transformer.translate(translateX, translateY);
        shape = transformer.createTransformedShape(shape);
        x = getX();
        y = getY();
        return true;
    }
    protected final boolean moveTo(double X, double Y, int tick){ //for use by an instance of GamePanel
        if(!validPos(X,Y)) return false;
        sync = tick;
        moved = true;
        moveTo(X,Y);
        return true;
    }
    public final void attatchTo(GameObject o){
        diffX = getX()-o.getX();
        diffY = getY()-o.getY();
        attatchedItem = o;
    }
    public final void attatchItem(GameObject o){
        o.attatchTo(this);
    }
    public final boolean rotate(double angle){
        //rotation necessitates shape to be instance of Path2D
        if(!(shape instanceof Path2D)) return false;
        Path2D path = (Path2D)shape;
        AffineTransform transformer = new AffineTransform();
        transformer.setToRotation(-this.angle,this.pivotX,this.pivotY);
        if(rotated) path.transform(transformer);
        transformer.setToRotation(angle);
        path.transform(transformer);
        this.pivotX = 0;
        this.pivotY = 0;
        this.angle = angle;
        rotated = true;
        return true;
    }
    public final boolean rotate(double angle, double pivotX, double pivotY){
        //rotation necessitates shape to be instance of Path2D
        if(!(shape instanceof Path2D)) return false;
        Path2D path = (Path2D)shape;
        AffineTransform transformer = new AffineTransform();
        transformer.setToRotation(-this.angle,this.pivotX,this.pivotY);
        if(rotated) path.transform(transformer);
        transformer.setToRotation(angle,pivotX,pivotY);
        path.transform(transformer);
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.angle = angle;
        rotated = true;
        return true;
    }
    public final void onGameTickDefault(int tick, ArrayList<GameObject> collisions){ //default behaviour per game tick
        if(sync==tick) return;
        x = getX();
        y = getY();
        if(attatchedItem==null)
            moveTo(x+(speedX/Constants.TICK_RATE), y+(speedY/Constants.TICK_RATE), tick); //simulate speed per tick
        else
            moveTo(attatchedItem.getX()+diffX, attatchedItem.getY()+diffY); //maintain attatchment
        sync = tick;
        moved = false; //reset moved boolean for the next tick
    }
    public void onGameTick(int tick, ArrayList<GameObject> collisions){ //entry point defining behaviour per game tick
        onGameTickDefault(tick,collisions);
    }
    public final void onPanelResizeDefault(double width, double height){ //default behaviour on game panel resize
        if(panelWidth<1 || panelHeight<1) return;
        double ratioWidth = width/panelWidth;
        double ratioHeight = height/panelHeight;
        speedX *= ratioWidth;
        speedY *= ratioHeight;
        panelWidth = width;
        panelHeight = height;
    }
    public void onPanelResize(double width, double height){ //handler for when game panel resizes
        onPanelResizeDefault(width,height);
    }
    
    public void onMouseDrag(MouseEvent m){}
    public void onMouseMove(MouseEvent m){}
    public void onMouseDown(MouseEvent m){}
    public void onMouseUp(MouseEvent m){}
    public void onMouseClick(MouseEvent m){} //the mouse was pressed and released
    public void onKeyPress(KeyEvent k){} //a key was pressed and released
    public void onKeyDown(KeyEvent k){}
    public void onKeyUp(KeyEvent k){}
    
    public GameObject(){
        shape = new Path2D.Double(new Rectangle2D.Double(0,0,10,10));
        color = new Color(255,0,0);
        collidable = false;
        x = getX();
        y = getY();
    }
    public GameObject(Shape s){
        shape = s;
        color = new Color(255,0,0);
        collidable = false;
        x = getX();
        y = getY();
    }
    public GameObject(boolean collides){
        shape = new Path2D.Double(new Rectangle2D.Double(0,0,10,10));
        color = new Color(255,0,0);
        collidable = collides;
        x = getX();
        y = getY();
    }
    public GameObject(Shape s, boolean collides){
        shape = s;
        color = new Color(255,0,0);
        collidable = collides;
        x = getX();
        y = getY();
    }
    public GameObject(Shape s, Color c, boolean collides){
        shape = s;
        color = c;
        collidable = collides;
        x = getX();
        y = getY();
    }
}
