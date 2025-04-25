package JavaGameEngine;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * A combination of several instances of ScrollingImage.
 *
 * @author (Paul Taylor)
 * @version (23rd/4/2025)
 */
public class ParallaxScrollingImage implements CompositeGameObject
{
    LinkedHashSet<ScrollingImage> images = new LinkedHashSet<>();
    @Override
    public void addToPanel(GamePanel p){
        for(ScrollingImage s: images) p.addItem(s);
    }
    @Override
    public void removeFromPanel(GamePanel p){
        for(ScrollingImage s: images) p.removeItem(s);
    }
    public void scroll(double shiftX, double shiftY){
        for(ScrollingImage s: images) s.scroll(shiftX,shiftY);
    }
    public void insert(ScrollingImage item){images.add(item);}
    public void delete(ScrollingImage item){images.remove(item);}
    public void insertAll(ArrayList<ScrollingImage> scrollingImages){images.addAll(scrollingImages);}
    public void insertAll(HashSet<ScrollingImage> scrollingImages){images.addAll(scrollingImages);}
    public void insertAll(LinkedHashSet<ScrollingImage> scrollingImages){images.addAll(scrollingImages);}
    public void deleteAll(){images.clear();}
    public void clear(){images.clear();}
    
    public ParallaxScrollingImage(ArrayList<ScrollingImage> scrollingImages){images.addAll(scrollingImages);}
    public ParallaxScrollingImage(HashSet<ScrollingImage> scrollingImages){images.addAll(scrollingImages);}
    public ParallaxScrollingImage(LinkedHashSet<ScrollingImage> scrollingImages){images.addAll(scrollingImages);}
    public ParallaxScrollingImage(){}
}
