/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bobble;

import bobble.utils.Key;
import bobble.utils.MyMath;
import bobble.utils.Utils;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author zhlk
 */
public class GameScreen implements Painter
{
    public static Image balls = null;
    public static byte ballSize = 16, count, firstCount;
    public static Vector runnings = new Vector(), stays = new Vector();
    public static short minX , maxX, minY, maxY;
    private Image bg = null, menu = null, cloud0, cloud1;
    private boolean loading = false;
    private byte step = 1, faceIndex = 2, radius = 25, currentColor = 0, nextColor = 1;
    private Image offScreen = Image.createImage(Datas.width, Datas.height);
    private Image[] faces = null;
    private Graphics og = offScreen.getGraphics();
    private byte selectedIndex = 0;
    private boolean showRank = false;
    private short[] cloudx = {0,0}, cloudy = {0,0};
    private long time = 0;
    private short degree = 270;
    private short bowX = (short) (Datas.width >> 1), bowY = (short) (Datas.height - radius);
    


    public GameScreen(int step)
    {
        count = (byte) (Datas.width / ballSize - 1);
        firstCount = count;
        minX = (short) ((Datas.width - count * ballSize) >> 1);
        maxX = (short) ((Datas.width + count * ballSize) >> 1);
        minY = (short) (ballSize >> 1);
        maxY = (short) (minY + (ballSize << 4));
        this.step = (byte)step;
        if(step == 1)
        {
            new Loading().start();
        }
    }

    private void init()
    {}

    public void paint(Graphics g) {
        g.drawImage(offScreen, 0, 0, 20);
        if(loading)
        {
            return;
        }
        this.updateStep();
    }

    private void updateStep()
    {
        switch(step)
        {
            case 1:
                updateOne();
                break;
            case 2:
                updateTwo();
                break;
        }
    }

    private void updateOne()
    {
        if((MainCanvas.keyPressed & Key.MENU_SELECT)!=0)
        {
            if(this.selectedIndex == 0)
            {
                this.step ++;
                new Loading().start();
            }
            if(this.selectedIndex == 1)
            {
                MainCanvas.switchScreen(new SoundSetting(this));
            }
        }
        else if((MainCanvas.keyPressed & Key.MENU_UP)!=0 || (MainCanvas.keyPressed & Key.MENU_LEFT)!=0)
        {
            selectedIndex --;
            if(selectedIndex < 0)
            {
                selectedIndex = 4;
            }
        }
        else if((MainCanvas.keyPressed & Key.MENU_DOWN)!=0 || (MainCanvas.keyPressed & Key.MENU_RIGHT)!=0)
        {
            selectedIndex ++;
            if(selectedIndex > 4)
            {
                selectedIndex = 0;
            }
        }
        MainCanvas.keyPressed = 0;
        og.drawImage(bg, 0, 0, 20);
        if(Datas.now - time - 500 > 0)
        {
            showRank = !showRank;
            time = Datas.now;
            if(!showRank)
            {
                time -= 450;
            }
        }
        if(showRank)
        {
            og.drawRegion(menu, 0, selectedIndex * 16, 71, 16, 0, 190, 285, 3);
        }
    }


    private void updateTwo()
    {
        if((MainCanvas.keyPressed & Key.MENU_SELECT)!=0)
        {
            runnings.addElement(new Ball(bowX, bowY, degree, currentColor));
            currentColor = nextColor;
            nextColor = (byte)(Utils.getRank(7));
        }
        else if((MainCanvas.keyPressed & Key.MENU_UP)!=0)
        {
          if(degree > 270)
          {
              degree -= 10;
          }
          else if(degree < 270)
          {
              degree += 10;
          }
        }
        else if((MainCanvas.keyPressed & Key.MENU_LEFT)!=0)
        {
              degree -= 10;
        }
        else if((MainCanvas.keyPressed & Key.MENU_DOWN)!=0)
        {
          if(degree >= 270)
          {
              degree += 10;
          }
          else if(degree < 270)
          {
              degree -= 10;
          }
        }
        else if((MainCanvas.keyPressed & Key.MENU_RIGHT)!=0)
        {
           degree += 10;
        }
        if(degree < 190)
        {
            degree = 190;
        }
        if(degree > 350)
        {
            degree = 350;
        }
        MainCanvas.keyPressed = 0;
        cloudx[0] += 2;
        if(cloudx[0] > Datas.width + (cloud0.getWidth()>>1))
        {
             cloudx[0] = (short) (- cloud0.getWidth()>>1);
             cloudy[0] = (short)(Utils.getRank(Datas.height>>1));
        }
        cloudx[1]++;
        if(cloudx[1] > Datas.width + (cloud1.getWidth()>>1))
        {
            cloudx[1] = (short) (- cloud1.getWidth()>>1);
            cloudy[1] = (short)(Utils.getRank((Datas.height>>1) + 50));
        }
        og.drawImage(bg, 0, 0, 20);
        og.drawImage(cloud1, cloudx[1], cloudy[1], 3);
        og.drawImage(cloud0, cloudx[0], cloudy[0], 3);
        
        if(Datas.now - time - 3000 > 0)
        {
            time = Datas.now;
            if(faceIndex == 1 || faceIndex == 3)
            {
                faceIndex --;
            }
            else if(faceIndex == 0 || faceIndex == 2)
            {
                faceIndex ++;
                time -= 2900;
            }
        }
        
        og.setColor(0x774475);
        short tempX = (short) (this.bowX + ((radius * MyMath.cos(degree)) >> 14));
        short tempY = (short) (this.bowY + ((radius * MyMath.sin(degree)) >> 14));
        short tempXNext = (short) (this.bowX + ((radius - 5) * MyMath.cos(degree + 180) >> 14));
        short tempYNext = (short) (this.bowY + ((radius - 5) * MyMath.sin(degree + 180) >> 14));
        short tempXNextTwo = (short) (this.bowX + ((radius - 5) * MyMath.cos(degree - 5) >> 14));
        short tempYNextTwo = (short) (this.bowY + ((radius - 5) * MyMath.sin(degree - 5) >> 14));

        og.drawLine(tempX, tempY, tempXNext, tempYNext);
        og.drawLine(tempX, tempY, tempXNextTwo, tempYNextTwo);

        tempXNextTwo = (short) (this.bowX + ((radius - 5) * MyMath.cos(degree + 5) >> 14));
        tempYNextTwo = (short) (this.bowY + ((radius - 5) * MyMath.sin(degree + 5) >> 14));
        og.drawLine(tempX, tempY, tempXNextTwo, tempYNextTwo);

        tempX = (short) (this.bowX + (radius * MyMath.cos(degree - 5 + 180) >> 14));
        tempY = (short) (this.bowY + (radius * MyMath.sin(degree - 5 + 180) >> 14));
        og.drawLine(tempX, tempY, tempXNext, tempYNext);

        tempX = (short) (this.bowX + ((radius) * MyMath.cos(degree + 5 + 180) >> 14));
        tempY = (short) (this.bowY + ((radius) * MyMath.sin(degree + 5 + 180) >> 14));
        og.drawLine(tempX, tempY, tempXNext, tempYNext);
        og.setColor(0);
        og.drawRect(minX, minY, maxX - minX, maxY - minY);
        og.drawLine(minX - 1, minY, minX - 1, maxY);
        og.drawImage(faces[faceIndex], 0, Datas.height, 36);

        og.drawRegion(GameScreen.balls, currentColor * ballSize, 0, ballSize, ballSize, 0, bowX, bowY, 3);

        og.drawRegion(GameScreen.balls, nextColor * ballSize, 0, ballSize, ballSize, 0, bowX - (ballSize<<1), bowY, 3);

        int size = runnings.size();
        for(int i = 0; i < size; )
        {
            Ball ball = (Ball)runnings.elementAt(i);
            for(int j = 0; j < stays.size(); j ++)
            {
                Ball temp = (Ball)stays.elementAt(j);
                int dx = temp.getX() - ball.getX(), dy = temp.getY() - ball.getY();
                if(dx * dx + dy * dy <= ballSize * ballSize)
                {
                    ball.setRunning(false);
                    stays.addElement(ball);
                    int per = ballSize / 6;
                    if(ball.getX() < temp.getX())
                    {
                        if(ball.getY() < temp.getY() - per)
                        {
                            ball.setX(temp.getX() - (ballSize>>1));
                            ball.setY(temp.getY() - ballSize);
                            if(temp.getCount() == count)
                            {
                                ball.setCount(count - 1);
                            }
                            else
                            {
                                ball.setCount(count);
                            }
                            runnings.removeElementAt(i);
                            break;
                        }
                        else if(ball.getY() > temp.getY() + per)
                        {
                            ball.setX(temp.getX() - ballSize);
                            ball.setY(temp.getY());
                            ball.setCount(temp.getCount());
                            runnings.removeElementAt(i);
                            break;
                        }
                        else
                        {
                            ball.setX(temp.getX() - (ballSize>>1));
                            ball.setY(temp.getY() + ballSize);
                            if(temp.getCount() == count)
                            {
                                ball.setCount(count - 1);
                            }
                            else
                            {
                                ball.setCount(count);
                            }
                            runnings.removeElementAt(i);
                            break;
                        }
                    }
                    else
                    {
                        if(ball.getY() < temp.getY() - per)
                        {
                            ball.setX(temp.getX() + (ballSize>>1));
                            ball.setY(temp.getY() - ballSize);
                            if(temp.getCount() == count)
                            {
                                ball.setCount(count - 1);
                            }
                            else
                            {
                                ball.setCount(count);
                            }
                            runnings.removeElementAt(i);
                            break;
                        }
                        else if(ball.getY() > temp.getY() + per)
                        {
                            ball.setX(temp.getX() + ballSize);
                            ball.setY(temp.getY());
                            ball.setCount(temp.getCount());
                            runnings.removeElementAt(i);
                            break;
                        }
                        else
                        {
                            ball.setX(temp.getX() + (ballSize>>1));
                            ball.setY(temp.getY() + ballSize);
                            if(temp.getCount() == count)
                            {
                                ball.setCount(count - 1);
                            }
                            else
                            {
                                ball.setCount(count);
                            }
                            runnings.removeElementAt(i);
                            break;
                        }
                    }
                }
            }
            int temp = runnings.size();
            if(temp >= size)
            {
                ball.go(og, i);
                temp = runnings.size();
                if(temp >= size)
                {
                    i ++;
                }
            } 
            size = temp;
        }
        size = stays.size();
        for(int i = 0; i < size; i ++)
        {
            Ball ball = (Ball)stays.elementAt(i);
            ball.go(og, i);
        }

        //180 - degree
    }

    class Loading extends Thread
    {
        public void run()
        {
            try
            {
                loading = true;
                if(step == 1)
                {
                    og.setColor(0);
                    og.fillRect(0, 0, Datas.width, Datas.height);
                    balls = Image.createImage("/balls.png");
                    int i = 40, ballsWidth = balls.getWidth(), go = i * ballsWidth / 100;
                    og.drawRegion(balls, 0, 0, go, balls.getHeight(), 0, Datas.width>>1, Datas.height>>1, 3);
                    bg = Image.createImage("/face.png");
                    i = 80;
                    go = i * ballsWidth / 100;
                    og.drawRegion(balls, 0, 0, go, balls.getHeight(), 0, Datas.width>>1, Datas.height>>1, 3);
                    menu = Image.createImage("/menu.png");
                    i = 98;
                    go = i * ballsWidth / 100;
                    og.drawRegion(balls, 0, 0, go, balls.getHeight(), 0, Datas.width>>1, Datas.height>>1, 3);
                }
                else if(step == 2)
                {
                    og.setColor(0);
                    og.fillRect(0, 0, Datas.width, Datas.height);
                    if(balls == null)
                    {
                        balls = Image.createImage("/balls.png");
                    }
                    int i = 40, ballsWidth = balls.getWidth(), go = i * ballsWidth / 100;
                    og.drawRegion(balls, 0, 0, go, balls.getHeight(), 0, Datas.width>>1, Datas.height>>1, 3);

                    cloud0 = Image.createImage("/cloud0.png");
                    i = 50;
                    go = i * ballsWidth / 100;
                    og.drawRegion(balls, 0, 0, go, balls.getHeight(), 0, Datas.width>>1, Datas.height>>1, 3);

                    cloudx[0] = (short)(Utils.getRank(Datas.height>>1));
                    cloudy[0] = (short)(Utils.getRank(Datas.height>>1));
                    cloud1 = Image.createImage("/cloud1.png");
                    i = 60;
                    go = i * ballsWidth / 100;
                    og.drawRegion(balls, 0, 0, go, balls.getHeight(), 0, Datas.width>>1, Datas.height>>1, 3);

                    cloudx[1] = (short) (- cloud1.getWidth()>>1);
                    cloudy[1] = (short)(Utils.getRank((Datas.height>>1) + 50));

                    bg = null;
                    bg = Image.createImage("/bg.png");
                    i = 80;
                    go = i * ballsWidth / 100;
                    og.drawRegion(balls, 0, 0, go, balls.getHeight(), 0, Datas.width>>1, Datas.height>>1, 3);

                    faces = new Image[4];
                    faces[0] = Image.createImage("/normal.png");
                    i = 85;
                    go = i * ballsWidth / 100;
                    og.drawRegion(balls, 0, 0, go, balls.getHeight(), 0, Datas.width>>1, Datas.height>>1, 3);
                    faces[1] = Image.createImage("/normalClose.png");
                    i = 90;
                    go = i * ballsWidth / 100;
                    og.drawRegion(balls, 0, 0, go, balls.getHeight(), 0, Datas.width>>1, Datas.height>>1, 3);

                    faces[2] = Image.createImage("/happy.png");
                    i = 95;
                    go = i * ballsWidth / 100;
                    og.drawRegion(balls, 0, 0, go, balls.getHeight(), 0, Datas.width>>1, Datas.height>>1, 3);
                    
                    faces[3] = Image.createImage("/happyClose.png");
                    i = 99;
                    go = i * ballsWidth / 100;
                    og.drawRegion(balls, 0, 0, go, balls.getHeight(), 0, Datas.width>>1, Datas.height>>1, 3);
                }
                loading = false;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}
