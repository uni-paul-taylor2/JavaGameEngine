import java.awt.*;
import java.awt.geom.*;

/**
 * Detects collisions between instances of Line2D, Ellipse2D and Rectangle2D (ellipse to ellipse would give false positives)
 *
 * @author (Paul Taylor)
 * @version (31st/1/2025)
 */
public class CustomCollisionDetector
{
    public CustomCollisionDetector(){}
    public boolean detected(Shape s1, Shape s2){ //we will care about instances of Polgon and Arc2D another day fr
        if(s1 instanceof Rectangle2D && s2 instanceof Rectangle2D){ //both rectangles
            return s1.getBounds2D().intersects(s2.getBounds2D());
        }
        if(s1 instanceof Ellipse2D && s2 instanceof Ellipse2D){ //both ellipses
            return s1.getBounds2D().intersects(s2.getBounds2D());
        }
        if(s1 instanceof Line2D && s2 instanceof Line2D){ //both lines
            return ((Line2D)s1).intersectsLine((Line2D)s2);
        }
        if(s1 instanceof Ellipse2D && s2 instanceof Rectangle2D){
            return ellipseRectangleCheck((Ellipse2D)s1, (Rectangle2D)s2);
        }
        if(s2 instanceof Ellipse2D && s1 instanceof Rectangle2D){
            return ellipseRectangleCheck((Ellipse2D)s2, (Rectangle2D)s1);
        }
        if(s1 instanceof Ellipse2D && s2 instanceof Line2D){
            return ellipseLineCheck((Ellipse2D)s1, (Line2D)s2);
        }
        if(s2 instanceof Ellipse2D && s1 instanceof Line2D){
            return ellipseLineCheck((Ellipse2D)s2, (Line2D)s1);
        }
        if(s1 instanceof Rectangle2D && s2 instanceof Line2D){
            return rectangleLineCheck((Rectangle2D)s1, (Line2D)s2);
        }
        if(s2 instanceof Rectangle2D && s1 instanceof Line2D){
            return rectangleLineCheck((Rectangle2D)s2, (Line2D)s1);
        }
        return false;
    }
    private boolean ellipseRectangleCheck(Ellipse2D ellipse, Rectangle2D rectangle){
        double x1=rectangle.getMinX(), y1=rectangle.getMinY(), x2=rectangle.getMaxX(), y2=rectangle.getMaxY();
        return
            ellipse.contains(x1,y1)||ellipse.contains(x2,y1)||ellipse.contains(x1,y2)||ellipse.contains(x2,y2) //square inside circle
            || rectangle.contains(ellipse.getCenterX(),ellipse.getCenterY()) //circle inside square
            || ellipseLineCheck(ellipse,new Line2D.Double(x1,y1,x2,y1)) //top edge
            || ellipseLineCheck(ellipse,new Line2D.Double(x2,y1,x2,y2)) //right edge
            || ellipseLineCheck(ellipse,new Line2D.Double(x2,y2,x1,y2)) //bottom edge
            || ellipseLineCheck(ellipse,new Line2D.Double(x1,y2,x1,y1)); //left edge
    }
    private boolean ellipseLineCheck(Ellipse2D ellipse, Line2D line){
        double h=ellipse.getCenterX(), k=ellipse.getCenterY(), a=ellipse.getWidth()/2, b=ellipse.getHeight()/2; //ellipse constants
        double x1=line.getX1(), y1=line.getY1(), x2=line.getX2(), y2=line.getY2(); //line constants
        if(ellipse.contains(x1,y1) || ellipse.contains(x2,y2)) return true;
        double m=(y2-y1)/(x2-x1), c=y1-m*x1; //m and c from line
        
        double c2=c*c, a2=a*a, m2=m*m, b2=b*b, h2=h*h, ck=c-k, ck2=ck*ck;
        //return c2 < a2*m2+b2; //if line was infinite
        double A = (1/a2)+m2/b2;
        double B = (-2*h)/a2+(2*m*ck)/b2;
        double C = h2/a2+ck2/b2-1;
        double discriminant = B*B-4*A*C;
        if(discriminant<0) return false;
        double sqrtD = Math.sqrt(discriminant);
        double X1=(-B+sqrtD)/(2*A), Y1=m*X1+c, X2=(-B-sqrtD)/(2*A), Y2=m*X2+c;
        return isInLine(X1,Y1,x1,y1,x2,y2) || isInLine(X2,Y2,x1,y1,x2,y2);
    }
    private boolean rectangleLineCheck(Rectangle2D rectangle, Line2D line){
        double x1=rectangle.getMinX(), y1=rectangle.getMinY(), x2=rectangle.getMaxX(), y2=rectangle.getMaxY();
        return
            rectangle.contains(line.getX1(),line.getY1()) || rectangle.contains(line.getX2(),line.getY2()) //line inside square
            || line.intersectsLine(new Line2D.Double(x1,y1,x2,y1)) //top edge
            || line.intersectsLine(new Line2D.Double(x2,y1,x2,y2)) //right edge
            || line.intersectsLine(new Line2D.Double(x2,y2,x1,y2)) //bottom edge
            || line.intersectsLine(new Line2D.Double(x1,y2,x1,y1)); //left edge
    }
    private boolean isInLine(double x, double y, double x1, double y1, double x2, double y2){
        return (x>=Math.min(x1,x2) && x<=Math.max(x1,x2))  &&  (y>=Math.min(y1,y2) && y<=Math.max(y1,y2));
    }
}
