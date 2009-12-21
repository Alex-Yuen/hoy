/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bobble;

import bobble.utils.MyMath;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author zhlk
 */
public class Ball {
    private short x, y, initX, initY, step = 0;
    private byte color, rate = 10, count;
    private boolean running = true;
    private short degree = 0;
    public Ball(int x, int y, short degree, byte color)
    {
        this.x = (short) x;
        this.y = (short) y;
        initX = this.x;
        initY = this.y;
        this.degree = degree;
        this.color = color;
    }

    public byte getCount()
    {
        return this.count;
    }

    public void setCount(int count)
    {
        this.count = (byte)count;
    }

    public void go(Graphics g, int size)
    {
        if(this.running)
        {
            step += 10;
            x = (short) (this.initX + (step * MyMath.cos(degree) >> 14));
            y = (short) (this.initY + (step * MyMath.sin(degree) >> 14));
            if(this.x < GameScreen.minX + (GameScreen.ballSize>>1))
            {
                x = (short) (GameScreen.minX + (GameScreen.ballSize >> 1));
                this.initX = x;
                step = 0;
                degree =  (short) (180 - degree);
            }
            if(this.x > GameScreen.maxX - (GameScreen.ballSize>>1))
            {
                x = (short) (GameScreen.maxX - (GameScreen.ballSize >> 1));
                this.initX = x;
                step = 0;
                degree =  (short) (180 - degree);
            }
            if(this.y < GameScreen.minY + (GameScreen.ballSize>>1))
            {
                y = (short) (GameScreen.minY + (GameScreen.ballSize >> 1));
                count = GameScreen.firstCount;
                this.running = false;
                if(GameScreen.count == GameScreen.firstCount)
                {
                    int tempCount = (x - GameScreen.minX) / GameScreen.ballSize;
                    int oneX = tempCount * GameScreen.ballSize - (GameScreen.ballSize>>1);
                    int twoX = tempCount * GameScreen.ballSize + (GameScreen.ballSize>>1);
                    if(x - oneX > twoX - x)
                    {
                        x = (short) (tempCount * GameScreen.ballSize + (GameScreen.ballSize >> 1));
                    }
                    else
                    {
                        x = (short) (tempCount * GameScreen.ballSize - (GameScreen.ballSize >> 1));
                    }
                    
                }
                else
                {
                    int tempCount = (x - GameScreen.minX) / GameScreen.ballSize;
                    int oneX = tempCount * GameScreen.ballSize;
                    int twoX = tempCount * GameScreen.ballSize + GameScreen.ballSize;
                    if(x - oneX > twoX - x)
                    {
                        x = (short) (tempCount * GameScreen.ballSize + GameScreen.ballSize);
                    }
                    else
                    {
                        x = (short) (tempCount * GameScreen.ballSize);
                    }
                }
                GameScreen.runnings.removeElementAt(size);
                GameScreen.stays.addElement(this);
            }
        }
        g.drawRegion(GameScreen.balls, color * GameScreen.ballSize, 0, GameScreen.ballSize, GameScreen.ballSize, 0, x, y, 3);
    }

    public byte getColor()
    {
        return this.color;
    }

    public short getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = (short) degree;
    }

    public short getInitX() {
        return initX;
    }

    public void setInitX(int initX) {
        this.initX = (short) initX;
    }

    public short getInitY() {
        return initY;
    }

    public void setInitY(int initY) {
        this.initY = (short) initY;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public short getX() {
        return x;
    }

    public void setX(int x) {
        this.x = (short) x;
    }

    public short getY() {
        return y;
    }

    public void setY(int y) {
        this.y = (short) y;
    }



}
