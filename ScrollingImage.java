package JavaGameEngine;
import java.util.ArrayList;

/**
 * Manages a scrolling image.
 *
 * @author (Paul Taylor)
 * @version (17th/3/2025)
 */
public class ScrollingImage implements CompositeGameObject
{
    private GameObject main=null;
    private GameObject left=null, right=null, up=null, down=null, upLeft=null, downLeft=null, upRight=null, downRight=null;
    private double startX=0, startY=0, X=0, Y=0, scrollFactor=1;
    private int height=0, width=0;
    private GamePanel panel=null;
    private double cameraX=0, cameraY=0;
    @Override
    public void addToPanel(GamePanel p){
        panel = p;
        p.addItem(main);
        p.addItem(left);
        p.addItem(right);
        p.addItem(up);
        p.addItem(down);
        p.addItem(upLeft);
        p.addItem(downLeft);
        p.addItem(upRight);
        p.addItem(downRight);
    }
    @Override
    public void removeFromPanel(GamePanel p){
        p.removeItem(main);
        p.removeItem(left);
        p.removeItem(right);
        p.removeItem(up);
        p.removeItem(down);
        p.removeItem(upLeft);
        p.removeItem(downLeft);
        p.removeItem(upRight);
        p.removeItem(downRight);
    }
    
    public void scroll(double shiftX, double shiftY){
        shiftX *= scrollFactor;
        shiftY *= scrollFactor;
        X += shiftX;
        Y += shiftY;
        
        if(X<0){
            X *= -1;
            X = X%width;
            X *= -1;
        }
        else X=X%width;
        
        if(Y<0){
            Y *= -1;
            Y = Y%height;
            Y *= -1;
        }
        else Y=Y%height;
        
        main.moveTo(panel.getCameraX()+X+startX, panel.getCameraY()+Y+startY);
    }
    public ScrollingImage(String imageFileName, int x, int y, double factor)
    {
        X = 0;
        Y = 0;
        startX = x;
        startY = y;
        scrollFactor = factor;
        main = new GameObject(imageFileName){
            @Override
            public void onGameTick(int tick, ArrayList<GameObject> collisions){
                if(panel!=null && (panel.getCameraX()!=cameraX || panel.getCameraY()!=cameraY)){
                    scroll(cameraX-panel.getCameraX(), cameraY-panel.getCameraY());
                    cameraX = panel.getCameraX();
                    cameraY = panel.getCameraY();
                }
                onGameTickDefault(tick,collisions);
            }
        };
        height = main.getImage().getHeight();
        width = main.getImage().getWidth();
        
        left = new GameObject(imageFileName, -width, y);
        right = new GameObject(imageFileName, width, y);
        up = new GameObject(imageFileName, x, -height);
        down = new GameObject(imageFileName, x, height);
        upLeft = new GameObject(imageFileName, -width, -height);
        downLeft = new GameObject(imageFileName, -width, height);
        upRight = new GameObject(imageFileName, width, -height);
        downRight = new GameObject(imageFileName, width, height);
        
        left.attachTo(main);
        right.attachTo(main);
        up.attachTo(main);
        down.attachTo(main);
        upLeft.attachTo(main);
        downLeft.attachTo(main);
        upRight.attachTo(main);
        downRight.attachTo(main);
    }
    public ScrollingImage(String imageFileName, int x, int y)
    {
        this(imageFileName, x, y, 1);
    }
    public ScrollingImage(String imageFileName)
    {
        this(imageFileName, 0, 0, 1);
    }
    public ScrollingImage(String imageFileName, double factor){
        this(imageFileName, 0, 0, factor);
    }
}
