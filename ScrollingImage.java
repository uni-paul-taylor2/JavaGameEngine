package JavaGameEngine;

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
    private double X=0, Y=0;
    int height=0, width=0;
    @Override
    public void addToPanel(GamePanel p){
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
        
        main.moveTo(X,Y);
    }
    public ScrollingImage(String imageFileName, int x, int y)
    {
        X = x;
        Y = y;
        main = new GameObject(imageFileName);
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
        
        left.attatchTo(main);
        right.attatchTo(main);
        up.attatchTo(main);
        down.attatchTo(main);
        upLeft.attatchTo(main);
        downLeft.attatchTo(main);
        upRight.attatchTo(main);
        downRight.attatchTo(main);
    }
    public ScrollingImage(String imageFileName)
    {
        int x=(int)X;
        int y=(int)Y;
        main = new GameObject(imageFileName);
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
        
        left.attatchTo(main);
        right.attatchTo(main);
        up.attatchTo(main);
        down.attatchTo(main);
        upLeft.attatchTo(main);
        downLeft.attatchTo(main);
        upRight.attatchTo(main);
        downRight.attatchTo(main);
    }
}
