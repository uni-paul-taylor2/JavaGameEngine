import javax.swing.*;
import java.util.HashMap;
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
    private HashMap<Integer,GameObject> gameItems;
    private CollisionDetector detector;
    public void removeItem(GameObject o){
        gameItems.remove(o.hashCode(),o);
    }
    public void addItem(GameObject o){
        Dimension size = getSize();
        double width = size.getWidth();
        double height = size.getHeight();
        o.onPanelResize(width,height);
        gameItems.put(o.hashCode(),o);
    }
    @Override
    public void paint(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int ms = 1000/Constants.TICK_RATE;
        Timer interval = new Timer(ms,new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                tick++;
                HashMap<GameObject,ArrayList<GameObject>> collisions = getCollisions();
                for(GameObject gameObject: gameItems.values()){
                    gameObject.onGameTick(tick,collisions.get(gameObject));
                    //TODO: draw the instance of GameObject
                }
            }
        });
        interval.start();
    }
    
    public GamePanel(){
        setFocusable(true);
        requestFocusInWindow();
        setPreferredSize(new Dimension(Constants.DEFAULT_PANEL_WIDTH, Constants.DEFAULT_PANEL_HEIGHT));
        tick = 0;
        gameItems = new HashMap<>();
        detector = new CollisionDetector();
        
        //keyboard listener
        addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                for(GameObject gameObject: gameItems.values()) gameObject.onKeyDown(e);
            }
            @Override
            public void keyReleased(KeyEvent e){
                for(GameObject gameObject: gameItems.values()) gameObject.onKeyUp(e);
            }
            @Override
            public void keyTyped(KeyEvent e){
                for(GameObject gameObject: gameItems.values()) gameObject.onKeyPress(e);
            }
        });
        
        //mouse listener
        addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                for(GameObject gameObject: gameItems.values()) gameObject.onMouseDown(e);
            }
            @Override
            public void mouseReleased(MouseEvent e){
                for(GameObject gameObject: gameItems.values()) gameObject.onMouseUp(e);
            }
            @Override
            public void mouseClicked(MouseEvent e){
                for(GameObject gameObject: gameItems.values()) gameObject.onMouseClick(e);
            }
            @Override
            public void mouseMoved(MouseEvent e){
                for(GameObject gameObject: gameItems.values()) gameObject.onMouseMove(e);
            }
            @Override
            public void mouseDragged(MouseEvent e){
                for(GameObject gameObject: gameItems.values()) gameObject.onMouseDrag(e);
            }
        });
        
        //component listener for panel resize listening
        addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e){
                Dimension size = getSize();
                double width = size.getWidth();
                double height = size.getHeight();
                for(GameObject gameObject: gameItems.values()) gameObject.onPanelResize(width,height);
            }
        });
    }
    
    private HashMap<GameObject,ArrayList<GameObject>> getCollisions(){
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
