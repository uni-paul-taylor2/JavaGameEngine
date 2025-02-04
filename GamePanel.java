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
 * @version (31st/1/2025)
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
        tick = 0;
        gameItems = new HashMap<>();
        detector = new CollisionDetector();
        
        //keyboard listener (incomplete)
        addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                for(GameObject gameObject: gameItems.values()) gameObject.onKeyPress(e);
            }
        });
        
        //mouse listener (incomplete)
        addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                for(GameObject gameObject: gameItems.values()) gameObject.onMouseDown(e);
            }
            @Override
            public void mouseReleased(MouseEvent e){
                for(GameObject gameObject: gameItems.values()) gameObject.onMouseUp(e);
            }
        });
    }
    
    private HashMap<GameObject,ArrayList<GameObject>> getCollisions(){
        HashMap<GameObject,ArrayList<GameObject>> collisions = new HashMap();
        for(GameObject item: gameItems.values()){
            ArrayList<GameObject> itemCollisions = new ArrayList<>();
            for(GameObject compared: gameItems.values()){
                if(item==compared) continue;
                if(detector.detected(item.getShape(),compared.getShape()))
                    itemCollisions.add(compared);
            }
            collisions.put(item,itemCollisions);
        }
        return collisions;
    }
}
