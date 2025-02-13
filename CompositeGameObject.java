package JavaGameEngine;


/**
 * All classes that comprise of several instances of GameObject should implement this.
 *
 * @author (Paul Taylor)
 * @version (13th/2/2025)
 */
public interface CompositeGameObject
{
    public void addToPanel(GamePanel p);
    public void removeFromPanel(GamePanel p);
}
