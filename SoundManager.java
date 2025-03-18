package JavaGameEngine;
import javax.sound.sampled.*;
import java.io.File;

/**
 * Manages WAV Sound loading, playing, looping and stopping.
 *
 * @author (Paul Taylor)
 * @version (18th/3/2025)
 */
public final class SoundManager
{
    public static Clip getAudio(String soundFileName){
        try{
            AudioInputStream sound = AudioSystem.getAudioInputStream(new File(soundFileName));
            Clip clip = AudioSystem.getClip();
            clip.open(sound);
            return clip;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static Clip getAudio(File soundFile){
        try{
            AudioInputStream sound = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(sound);
            return clip;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static void playAudio(Clip sound, boolean loop){
        if(sound.isRunning()) sound.stop();
        sound.setFramePosition(0);
        if(loop) sound.loop(Clip.LOOP_CONTINUOUSLY);
        else sound.start();
    }
    public static void playAudio(Clip sound){
        playAudio(sound,false);
    }
    private SoundManager(){}
}
