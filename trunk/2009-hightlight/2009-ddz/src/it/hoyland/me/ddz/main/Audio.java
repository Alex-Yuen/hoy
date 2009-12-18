package it.hoyland.me.ddz.main;

import java.io.IOException;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import java.io.InputStream;
public class Audio
{
    public static final int TOP_SCORE = 0;
    public static final int BACK_GROUND = 1;
    public static final int FAIL = 2;
    public static final int BONUS = 3;
    public static final int EXPLOSION = 4;
    public static final int DEAD = 5;
    public static final int WIN = 6;
    public static final int START = 7;
    protected static Player[]sounds = new Player[8];

    protected static final String TYPE_WAV = "audio/wav";
    protected static final String TYPE_MIDI = "audio/midi";
    private static Audio instance;    
    static Audio getInstance()
    {
        if (instance == null)
        {
            instance = new Audio();
        }
        return instance;
    }
    private Audio(){}    
    public void playSound(int snd)
    {

            // No player, create one
            if (sounds[snd] == null)
            {
                createSound(snd);
            }
            // Start player
            Player player = sounds[snd];
            if (player != null)
            {
                try
                {
                    player.setLoopCount(-1);
                    player.start();                   
                }
                catch (MediaException e)
                {
                    e.printStackTrace();
                }
            }
     
    }
    /**
     * Stops specified sound if it is playing.
     * @param snd		The id of the sound to stop.
     */
    public void stopSound(int snd)
    {
        if (sounds[snd] != null)
        {
            try
            {
                sounds[snd].stop();
            }
            catch (MediaException e)
            {
                e.printStackTrace();
            }
        }
    }
    /**
     * Stops all sounds and cleans up resources.
     */
    public void shutdown()
    {
        for (int i = 0; i < sounds.length; i++)
        {
            stopSound(i);
            if (sounds[i] != null)
            {
                sounds[i].deallocate();
            }
        }
    }
    /**
     * Creates a player for specified sound
     * and popuplates the Player array.
     * @param snd	The sound to create
     */
    protected void createSound(int snd)
    {
        try
        {
            String rsc = "/TopScore.mid";
            String type = TYPE_MIDI;
            switch (snd)
            {
                case TOP_SCORE:
                    type = TYPE_MIDI;
                    rsc = "/TopScore.mid";
                    break;
                case BACK_GROUND:
                    type = TYPE_MIDI;
                    rsc = "/back.mid";
                    break;
                case FAIL:
                    type = TYPE_MIDI;
                    rsc = "/Fail.mid";
                    break;
                case BONUS:
                    type = TYPE_MIDI;
                    rsc = "/Bonus.mid";
                    break;
                case EXPLOSION:
                    type = TYPE_MIDI;
                    rsc = "/Explosion.mid";
                    break;
                case DEAD:
                    type = TYPE_MIDI;
                    rsc = "/Dead.mid";
                    break;
                case WIN:
                    type = TYPE_MIDI;
                    rsc = "/Win.mid";
                    break;
                case START:
                    type = TYPE_MIDI;
                    rsc = "/Start.mid";
                    break;
            }
            InputStream is = getClass().getResourceAsStream(rsc);
            sounds[snd] = Manager.createPlayer(is, type)
                ;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (MediaException e)
        {
            e.printStackTrace();
        }
    }
    /** Prevent instantiation */

}
