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
    public void paint(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Timer interval = new Timer(50,new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                tick++;
                HashMap<GameObject,ArrayList<GameObject>> collisions = getCollisions();
                for(GameObject gameObject: gameItems.values()){
                    gameObject.onGameTick(tick,collisions.get(gameObject));
                }
            }
        });
        interval.start();
    }
    public GamePanel(){
        tick = 0;
        gameItems = new HashMap<>();
        detector = new CollisionDetector();
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
