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
    private LinkedHashMap<Integer,GameObject> gameItems;
    private LinkedHashMap<Integer,GameObject> keyboardGameItems;
    private LinkedHashMap<Integer,GameObject> mouseGameItems;
    private LinkedHashMap<Integer,GameObject> deletedGameItems;
    private CollisionDetector detector;
    public synchronized void properlyRemoveItem(GameObject o){
        gameItems.remove(o.hashCode(),o);
        keyboardGameItems.remove(o.hashCode(),o);
        mouseGameItems.remove(o.hashCode(),o);
    }
    public synchronized void removeItem(GameObject o){
        deletedGameItems.put(o.hashCode(),o);
    }
    public synchronized void removeItem(CompositeGameObject o){
        o.removeFromPanel(this);
    }
    public synchronized void addItem(GameObject o){
        Dimension size = getSize();
        double width = size.getWidth();
        double height = size.getHeight();
        o.onPanelResize(width,height);
        gameItems.put(o.hashCode(),o);
    }
    public synchronized void addItem(CompositeGameObject o){
        o.addToPanel(this);
    }
    public synchronized void addItem(GameObject o, boolean keyboardListener, boolean mouseListener){
        if(keyboardListener) keyboardGameItems.put(o.hashCode(),o);
        if(mouseListener) mouseGameItems.put(o.hashCode(),o);
        addItem(o);
    }
    @Override
    public void paint(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for(GameObject gameObject: gameItems.values()){
            if(deletedGameItems.get(gameObject)!=null) continue;
            g2.setColor(gameObject.getColor());
            g2.fill(gameObject.getShape());
        }
    }
    
    public GamePanel(){
        setFocusable(true);
        requestFocusInWindow();
        setPreferredSize(new Dimension(Constants.DEFAULT_PANEL_WIDTH, Constants.DEFAULT_PANEL_HEIGHT));
        tick = 0;
        gameItems = new LinkedHashMap<>();
        keyboardGameItems = new LinkedHashMap<>();
        mouseGameItems = new LinkedHashMap<>();
        deletedGameItems = new LinkedHashMap<>();
        detector = new CollisionDetector();
        
        //game interval
        int ms = 1000/Constants.TICK_RATE;
        Timer interval = new Timer(ms,new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                tick++;
                for(GameObject item: deletedGameItems.values()) properlyRemoveItem(item);
                deletedGameItems.clear();
                HashMap<GameObject,ArrayList<GameObject>> collisions = getCollisions();
                ArrayList<GameObject> gameObjectsCopy = new ArrayList<>(gameItems.values()); //solves concurrency
                for(GameObject gameObject: gameObjectsCopy)
                    gameObject.onGameTick(tick,collisions.get(gameObject));
                repaint();
            }
        });
        interval.start();
        
        //keyboard listener
        addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                for(GameObject gameObject: keyboardGameItems.values()){
                    if(deletedGameItems.get(gameObject)!=null) continue;
                    gameObject.onKeyDown(e);
                }
            }
            @Override
            public void keyReleased(KeyEvent e){
                for(GameObject gameObject: keyboardGameItems.values()){
                    if(deletedGameItems.get(gameObject)!=null) continue;
                    gameObject.onKeyUp(e);
                }
            }
            @Override
            public void keyTyped(KeyEvent e){
                for(GameObject gameObject: keyboardGameItems.values()){
                    if(deletedGameItems.get(gameObject)!=null) continue;
                    gameObject.onKeyPress(e);
                }
            }
        });
        
        //mouse listener
        addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                for(GameObject gameObject: mouseGameItems.values()){
                    if(deletedGameItems.get(gameObject)!=null) continue;
                    if(gameObject.getShape().contains(e.getPoint())) gameObject.onMouseDown(e);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e){
                for(GameObject gameObject: mouseGameItems.values()){
                    if(deletedGameItems.get(gameObject)!=null) continue;
                    if(gameObject.getShape().contains(e.getPoint())) gameObject.onMouseUp(e);
                }
            }
            @Override
            public void mouseClicked(MouseEvent e){
                for(GameObject gameObject: mouseGameItems.values()){
                    if(deletedGameItems.get(gameObject)!=null) continue;
                    if(gameObject.getShape().contains(e.getPoint())) gameObject.onMouseClick(e);
                }
            }
            @Override
            public void mouseMoved(MouseEvent e){
                for(GameObject gameObject: mouseGameItems.values()){
                    if(deletedGameItems.get(gameObject)!=null) continue;
                    if(gameObject.getShape().contains(e.getPoint())) gameObject.onMouseMove(e);
                }
            }
            @Override
            public void mouseDragged(MouseEvent e){
                for(GameObject gameObject: mouseGameItems.values()){
                    if(deletedGameItems.get(gameObject)!=null) continue;
                    if(gameObject.getShape().contains(e.getPoint())) gameObject.onMouseDrag(e);
                }
            }
        });
        
        //component listener for panel resize listening
        addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e){
                Dimension size = getSize();
                double width = size.getWidth();
                double height = size.getHeight();
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
