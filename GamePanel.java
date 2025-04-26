package JavaGameEngine;

import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;

/**
 * Manages the timing and rendering of instances of GameObject
 * To Do: Integrate Key and Mouse functions to bubble down to the instances of GameObject
 *
 * @author (Paul Taylor)
 * @version (3rd/2/2025)
 */
public class GamePanel extends JPanel
{
    private int tick;
    public int getTick(){return tick;}
    private boolean stopped=true;
    public boolean isStopped(){return stopped;}
    private double cameraX=0, cameraY=0;
    public double getCameraX(){return cameraX;}
    public double getCameraY(){return cameraY;}
    public synchronized void setCamera(double x, double y){
        cameraX = x;
        cameraY = y;
    }
    private Timer interval=null;
    private ActionListener timerFn;
    private LinkedHashMap<Integer,GameObject> gameItems;
    private LinkedHashMap<Integer,GameObject> keyboardGameItems;
    private LinkedHashMap<Integer,GameObject> mouseGameItems;
    private LinkedHashMap<Integer,GameObject> deletedGameItems;
    private CollisionDetector detector;
    private ArrayList<MouseEvent> mouseEvents;
    private ArrayList<KeyEvent> keyboardEvents;
    
    public synchronized void properlyRemoveItem(GameObject o){
        gameItems.remove(o.hashCode(),o);
        keyboardGameItems.remove(o.hashCode(),o);
        mouseGameItems.remove(o.hashCode(),o);
    }
    public synchronized void removeItem(GameObject o){deletedGameItems.put(o.hashCode(),o);}
    public synchronized void removeItem(CompositeGameObject o){o.removeFromPanel(this);}
    public synchronized void addItem(GameObject o){
        Dimension size = getSize();
        double width = size.getWidth();
        double height = size.getHeight();
        o.onPanelResize(width,height);
        gameItems.put(o.hashCode(),o);
    }
    public synchronized void addItem(CompositeGameObject o){o.addToPanel(this);}
    public synchronized void addItem(GameObject o, boolean keyboardListener, boolean mouseListener){
        if(keyboardListener) keyboardGameItems.put(o.hashCode(),o);
        if(mouseListener) mouseGameItems.put(o.hashCode(),o);
        addItem(o);
    }
    @Override
    public void paint(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(-cameraX, -cameraY);
        for(GameObject gameObject: gameItems.values()){
            if(deletedGameItems.get(gameObject)!=null) continue;
            if(gameObject.hasImage()){
                g2.drawImage(gameObject.getImage(), (int)gameObject.getX(), (int)gameObject.getY(), this);
            }
            g2.setColor(gameObject.getColor());
            g2.fill(gameObject.getShape());
        }
    }
    
    public final void handleEvent(MouseEvent m){
        switch(m.getID()){
            case MouseEvent.MOUSE_CLICKED:
                onMouseClick(m);
                break;
            case MouseEvent.MOUSE_PRESSED:
                onMouseDown(m);
                break;
            case MouseEvent.MOUSE_RELEASED:
                onMouseUp(m);
                break;
            case MouseEvent.MOUSE_MOVED:
                onMouseMove(m);
                break;
            case MouseEvent.MOUSE_DRAGGED:
                onMouseDrag(m);
                break;
            default:
                break;
        }
    }
    public final void handleEvent(KeyEvent k){
        switch(k.getID()){
            case KeyEvent.KEY_PRESSED:
                onKeyDown(k);
                break;
            case KeyEvent.KEY_RELEASED:
                onKeyUp(k);
                break;
            case KeyEvent.KEY_TYPED:
                onKeyPress(k);
                break;
            default:
                break;
        }
    }
    public void onMouseDrag(MouseEvent m){}
    public void onMouseMove(MouseEvent m){}
    public void onMouseDown(MouseEvent m){}
    public void onMouseUp(MouseEvent m){}
    public void onMouseClick(MouseEvent m){} //the mouse was pressed and released
    public void onKeyPress(KeyEvent k){} //a key was pressed and released
    public void onKeyDown(KeyEvent k){}
    public void onKeyUp(KeyEvent k){}
    public void onPanelResize(double width, double height){}
    
    public final void stop(){
        if(stopped) return;
        if(interval!=null) interval.stop();
        ArrayList<GameObject> gameObjectsCopy = new ArrayList<>(gameItems.values()); //copy solves concurrency
        for(GameObject item: gameObjectsCopy) removeItem(item);
        stopped = true;
        interval = null;
    }
    public final void start(){
        if(!stopped) return;
        int ms = 1000/Constants.TICK_RATE;
        interval = new Timer(ms,timerFn);
        tick = 0;
        cameraX = 0;
        cameraY = 0;
        interval.start();
        stopped = false;
    }
    public void pause(){
        if(interval!=null) interval.stop();
        interval = null;
    }
    public void resume(){
        if(interval!=null) return;
        int ms = 1000/Constants.TICK_RATE;
        interval = new Timer(ms,timerFn);
        interval.start();
    }
    public void perTickCallback(){}
    
    public GamePanel(){
        setFocusable(true);
        requestFocusInWindow();
        setPreferredSize(new Dimension(Constants.DEFAULT_PANEL_WIDTH, Constants.DEFAULT_PANEL_HEIGHT));
        gameItems = new LinkedHashMap<>();
        keyboardGameItems = new LinkedHashMap<>();
        mouseGameItems = new LinkedHashMap<>();
        deletedGameItems = new LinkedHashMap<>();
        mouseEvents = new ArrayList<>();
        keyboardEvents = new ArrayList<>();
        detector = new CollisionDetector();
        
        timerFn = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                tick++;
                for(GameObject item: deletedGameItems.values()) properlyRemoveItem(item);
                deletedGameItems.clear();
                perTickCallback();
                HashMap<GameObject,ArrayList<GameObject>> collisions = getCollisions();
                ArrayList<GameObject> gameObjectsCopy = new ArrayList<>(gameItems.values()); //copy solves concurrency
                for(GameObject gameObject: gameObjectsCopy){
                    gameObject.onGameTick(tick,collisions.get(gameObject));
                    for(MouseEvent ev: mouseEvents) gameObject.handleEvent(ev);
                    for(KeyEvent ev: keyboardEvents) gameObject.handleEvent(ev);
                }
                mouseEvents.clear();
                keyboardEvents.clear();
                if(stopped) return;
                repaint();
            }
        };
        
        //keyboard listener
        addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                keyboardEvents.add(e);
                handleEvent(e);
            }
            @Override
            public void keyReleased(KeyEvent e){
                keyboardEvents.add(e);
                handleEvent(e);
            }
            @Override
            public void keyTyped(KeyEvent e){
                keyboardEvents.add(e);
                handleEvent(e);
            }
        });
        
        //mouse listener
        addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                mouseEvents.add(e);
                handleEvent(e);
            }
            @Override
            public void mouseReleased(MouseEvent e){
                mouseEvents.add(e);
                handleEvent(e);
            }
            @Override
            public void mouseClicked(MouseEvent e){
                mouseEvents.add(e);
                handleEvent(e);
            }
            @Override
            public void mouseMoved(MouseEvent e){
                mouseEvents.add(e);
                handleEvent(e);
            }
            @Override
            public void mouseDragged(MouseEvent e){
                mouseEvents.add(e);
                handleEvent(e);
            }
        });
        
        //component listener for panel resize listening
        addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e){
                Dimension size = getSize();
                double width = size.getWidth();
                double height = size.getHeight();
                onPanelResize(width,height);
                for(GameObject gameObject: gameItems.values()){
                    if(deletedGameItems.get(gameObject)!=null) continue;
                    gameObject.onPanelResize(width,height);
                }
            }
        });
    }
    
    private synchronized HashMap<GameObject,ArrayList<GameObject>> getCollisions(){
        HashMap<GameObject,ArrayList<GameObject>> collisions = new HashMap();
        for(GameObject item: gameItems.values()){
            ArrayList<GameObject> itemCollisions = new ArrayList<>();
            for(GameObject compared: gameItems.values()){
                if(item==compared) continue;
                if(!item.isCollidable() || !compared.isCollidable()) continue;
                if(detector.detected(item.getShape(),compared.getShape()))
                    itemCollisions.add(compared);
            }
            collisions.put(item,itemCollisions);
        }
        return collisions;
    }
}
