import java.awt.*;
import java.awt.geom.*;

/**
 * Gotta love Java and their abstraction called Area ;-;
 *
 * @author (Paul Taylor)
 * @version (31st/1/2025)
 */
public class CollisionDetector
{
    public CollisionDetector(){}
    public boolean detected(Shape s1, Shape s2){
        Area s1a=new Area(s1), s2a=new Area(s2);
        Area intersection = new Area(s1a);
        intersection.intersect(s2a);
        return !intersection.isEmpty();
    }
}
