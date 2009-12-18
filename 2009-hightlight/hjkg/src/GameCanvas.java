/*
 * GameCanvas.java
 *
 * Created on 2007年3月14日, 上午10:02
 */

import java.util.Timer;
import javax.microedition.lcdui.*;
import java.io.IOException;
import java.util.Random;
import javax.microedition.lcdui.game.Sprite;

/**
 *
 * @author  MissYou
 * @version
 */
public class GameCanvas extends Canvas implements Runnable, CommandListener {
    /**
     * constructor
     */
    private static final int TIMEPERSTAGE = 50;
    private static final int GOLDNUM = 10;
    private static final int STONENUM = 4;
    private static final int GAMEOVER = 0;
    private static final int STARTING = 1;
    private static final int CIRCUMGYRATEING = 2;
    private static final int STRETCHING = 3;
    private static final int BACKING = 4;
    private static final int CATCHING = 5;
    private static final int STAGEPASSING = 6;
    private static final int ALLSTATESPASSED = 7;
    
    private boolean isPaused;
    
    /** Start指令变量 */
    private Command startCmd = new Command("开始游戏", Command.SCREEN, 1);
    /** Exit指令变量 */
    private Command exitCmd = new Command("离开游戏", Command.SCREEN, 5);
    /** Pause指令变量 */
    private Command pauseCmd = new Command("暂停游戏", Command.SCREEN, 1);
    /** Resume指令变量 */
    private Command resumeCmd = new Command("继续游戏", Command.SCREEN, 1);
    /** Quit指令变量 */
    private Command quitCmd = new Command("重新开始", Command.SCREEN, 4);
    
    private GoldMiner goldminer;
    private Gold [] gold = new Gold[GOLDNUM];
    private Stone [] stone = new Stone[STONENUM];
    private int ScreenWidth;
    private int ScreenHeight;
    private Random randomTool = new Random();
    private Hook hook = new Hook();
    private int hookCirState;
    private int Stage;
    private Image BackGround;
    private Gain gain;
    private int AmountofMoney;
    private int StageNum;
    private int [] MinMoney = { 650, 1500, 2500, 3700, 5000, 6500, 8000, 10000 };
    private int LeftTime;
    private int MoneyToAdd;
    private Timer timer = null;
    
    public GameCanvas( GoldMiner goldminer ) {
        this.goldminer = goldminer;
        ScreenWidth = getWidth();
        ScreenHeight = getHeight();
        Stage = STARTING;
        gain = new Gain();
        try {
            BackGround = Image.createImage("/BG.png");
        } catch(IOException ioe){
            System.out.println("Can't load file Background.png");
        }
    }
    
    private void Initialize(){
        hookCirState = 1;
        hook.Initialize();
        AmountofMoney = 0;
        isPaused = false;
        StageNum = 1;
        LeftTime = TIMEPERSTAGE;
        MoneyToAdd = 0;
        IniGold();
        IniStone();
        setCommandListener(this);
        Stage = STARTING;
        addCommand(startCmd);
        addCommand(exitCmd);
    }
    
    private void ChangeStage(){
        StageNum++;
        IniGold();
        IniStone();
        Stage = CIRCUMGYRATEING;
        LeftTime = TIMEPERSTAGE;
        MoneyToAdd = 0;
        hook.Initialize();
    }
    
    private void IniGold(){
        int tempint = 1;
        for( int i = 0; i < GOLDNUM; i++ ){
            if ( i < 2 ){
                gold[i] =  new Gold( 5+randomTool.nextInt(ScreenWidth-10),
                        randomTool.nextInt(ScreenHeight/2-40)+50, tempint );
            } else{
                tempint = randomTool.nextInt(4)+1;
                gold[i] = new Gold(randomTool.nextInt(ScreenWidth-30)+15,
                        randomTool.nextInt(ScreenHeight/2)+ScreenHeight/3+30, tempint );
            }
        }
    }
    
    private void IniStone(){
        for( int i = 0; i < STONENUM; i++ ){
            stone[i] = new Stone(50+randomTool.nextInt(ScreenWidth-100),
                    randomTool.nextInt(ScreenHeight/2-40)+70, randomTool.nextInt(2)+1);
        }
    }
    
    public void doPauseApp(){
        
    }
    
    public void doStartApp(){
        //repaint();
        Thread t = new Thread(this);
        t.start();
    }
    
    /**
     * paint
     */
    public void paint(Graphics g) {
        switch(Stage){
            case STARTING:
                Initialize();
                try{
                    g.drawImage( Image.createImage("/Start.png"), ScreenWidth/2,
                            ScreenHeight/2, Graphics.HCENTER|Graphics.VCENTER );
                } catch(IOException ioe){}
                break;
            case CIRCUMGYRATEING :
                paintBackGround(g);
                hook.getHookSprite().paint(g);
                break;
            case STRETCHING:
                paintBackGround(g);
                g.setColor(0x00000000);
                g.drawLine(hook.INIX, hook.INIY, hook.getX(), hook.getY());
                hook.getHookSprite().paint(g);
                break;
            case BACKING :
                paintBackGround(g);
                g.setColor(0x00000000);
                g.drawLine(hook.INIX, hook.INIY, hook.getX(), hook.getY());
                hook.getHookSprite().paint(g);
                break;
            case CATCHING :
                paintBackGround(g);
                g.setColor(0x00000000);
                g.drawLine(hook.INIX, hook.INIY, hook.getX(), hook.getY());
                gain.getGainSprite().setRefPixelPosition(hook.getX(), hook.getY());
                gain.getGainSprite().paint(g);
                break;
            case STAGEPASSING :
                try{
                    g.drawImage( Image.createImage("/GP.png"), ScreenWidth/2,
                            ScreenHeight/2, Graphics.HCENTER|Graphics.VCENTER );
                    g.setColor(254, 255, 0);
                    g.drawString(String.valueOf(MinMoney[StageNum]), 150, 167,
                            Graphics.TOP|Graphics.LEFT);
                } catch(IOException ioe){
                    System.out.println("Can't load GP.png file.");
                }
                break;
            case GAMEOVER :
                try{
                    g.drawImage( Image.createImage("/GameOver.png"), ScreenWidth/2,
                            ScreenHeight/2, Graphics.HCENTER|Graphics.VCENTER );
                } catch(IOException ioe){
                    System.out.println("Can't load GameOver.png file.");
                }
                break;
            case ALLSTATESPASSED :
                try{
                    g.drawImage( Image.createImage("/CM.png"), ScreenWidth/2,
                            ScreenHeight/2, Graphics.HCENTER|Graphics.VCENTER );
                } catch(IOException ioe){
                    System.out.println("Can't load GameOver.png file.");
                }
                break;
        }
    }
    
    private void paintBackGround(Graphics g){
        g.drawImage( BackGround, ScreenWidth/2,
                ScreenHeight/2, Graphics.HCENTER|Graphics.VCENTER );
        g.setColor(0x00FF00FF);
        g.drawString(String.valueOf(AmountofMoney), 45, 12, Graphics.TOP|Graphics.LEFT);
        g.drawString(String.valueOf(StageNum), 207, 26, Graphics.TOP|Graphics.LEFT);
        g.drawString(String.valueOf(MinMoney[StageNum-1]), 63, 27, Graphics.TOP|Graphics.LEFT);
        g.drawString(String.valueOf(LeftTime), 220, 12, Graphics.TOP|Graphics.LEFT);
        for( int j = 0; j< GOLDNUM; j++) {
            gold[j].getTargetSprite().paint(g);
        }
        for( int i = 0; i < STONENUM; i++ ){
            stone[i].getTargetSprite().paint(g);
        }
    }
    
    /**
     * Called when a key is pressed.
     */
    protected  void keyPressed(int keyCode) {
        int gameCode = getGameAction(keyCode);
        if ( Stage == CIRCUMGYRATEING && !(isPaused)){
            if (gameCode == DOWN){
                Stage = STRETCHING;
            }
        }
        
    }
    
    /**
     * Called when a key is released.
     */
    protected  void keyReleased(int keyCode) {
    }
    
    /**
     * Called when a key is repeated (held down).
     */
    protected  void keyRepeated(int keyCode) {
        
    }
    
    /**
     * Called when the pointer is dragged.
     */
    protected  void pointerDragged(int x, int y) {
    }
    
    /**
     * Called when the pointer is pressed.
     */
    protected  void pointerPressed(int x, int y) {
    }
    
    /**
     * Called when the pointer is released.
     */
    protected  void pointerReleased(int x, int y) {
    }
    
    /**
     * Called when action should be handled
     */
    
    public void run(){
        while( true ){
            if (isPaused)
                continue;
            doTimerStart();
            switch(Stage){
                case STARTING :
                    doStarting();
                    break;
                case CIRCUMGYRATEING :
                    doCircumgyrateing();
                    break;
                case STRETCHING :
                    doStretching();
                    break;
                case BACKING :
                    doBacking();
                    break;
                case CATCHING :
                    doBacking();
                    break;
                case STAGEPASSING :
                    doStagepassing();
                    break;
                case GAMEOVER :
                    doGameover();
                    break;
                case ALLSTATESPASSED :
                    doGameover();
                    break;
            }
        }
    }
    
    private void doStarting(){
        
    }
    
    public void SubSec() {
        LeftTime--;
        if ( LeftTime == 0 ){
            if ( AmountofMoney >= MinMoney[StageNum-1] )
                if ( StageNum == MinMoney.length )
                    Stage = ALLSTATESPASSED;
                else
                    Stage = STAGEPASSING;
            else
                Stage = GAMEOVER;
        }
        repaint();
    }
    
    private void doCircumgyrateing(){
        removeCommand(startCmd);
        addCommand(quitCmd);
        addCommand(pauseCmd);
        if ((hook.getHookSprite().getFrame()==8&&(hookCirState==1||hookCirState==2))||
                (hook.getHookSprite().getFrame()==0&&(hookCirState==3||hookCirState==4))){
            hookCirState++;
            if ( hookCirState == 2 )
                hook.getHookSprite().setTransform(Sprite.TRANS_ROT270);
            if ( hookCirState == 4 )
                hook.getHookSprite().setTransform(Sprite.TRANS_NONE);
            if ( hookCirState == 5 )
                hookCirState = 1;
        }
        if ( hookCirState == 1 || hookCirState == 2 ){
            hook.getHookSprite().nextFrame();
        } else{
            hook.getHookSprite().prevFrame();
        }
        if (hookCirState == 2&&hook.getHookSprite().getFrame()==8||
                hookCirState == 4&&hook.getHookSprite().getFrame()==0)
            return;
        repaint();
        try{
            Thread.sleep(200);
        } catch ( InterruptedException e ){}
    }
    
    private void doStretching(){
        hook.extReSetXY( hookCirState==1||hookCirState==4 );
        if ( hook.getX() > ScreenWidth || hook.getX() <  0
                || hook.getY() < 0 || hook.getY() > ScreenHeight ){
            Stage = BACKING;
        } else{
            if(Checkcollides() == true)
                Stage = CATCHING;
            else{
                try{
                    Thread.sleep(50);
                } catch ( InterruptedException e ){}
                repaint();
            }
        }
    }
    
    private void doBacking(){
        hook.backReSetXY( hookCirState==1||hookCirState==4 );
        if ( hook.getY() < hook.INIY ){
            hook.getHookSprite().setRefPixelPosition(hook.INIX, hook.INIY);
            hook.setnowSpeed(hook.SPEED);
            hook.getHookSprite().setVisible(true);
            AmountofMoney += MoneyToAdd;
            MoneyToAdd = 0;
            Stage = CIRCUMGYRATEING;
        } else{
            try{
                Thread.sleep(50);
            } catch ( InterruptedException e ){}
            repaint();
        }
    }
    
    private void doStagepassing(){
        try{
            Thread.sleep(2000);
        } catch ( InterruptedException e ){}
        ChangeStage();
    }
    
    private void doGameover(){        
        removeCommand(resumeCmd);
        removeCommand(pauseCmd);
        removeCommand(quitCmd);
        try{
            Thread.sleep(2000);
        } catch ( InterruptedException e ){}
        System.out.println("asghadhagasg");
        Stage = STARTING;
    }
    
    private boolean Checkcollides(){
        for( int i = 0; i < GOLDNUM; i++ ){
            if (hook.getHookSprite().collidesWith(gold[i].getTargetSprite(), true)){
                gold[i].getTargetSprite().setVisible(false);
                hook.getHookSprite().setVisible(false);
                hook.setnowSpeed(hook.SPEED-gold[i].getSize());
                MoneyToAdd = gold[i].getRelValue();
                return true;
            }
        }
        for ( int i = 0; i < STONENUM; i++ ){
            if (hook.getHookSprite().collidesWith(stone[i].getTargetSprite(), true)){
                stone[i].getTargetSprite().setVisible(false);
                hook.getHookSprite().setVisible(false);
                hook.setnowSpeed(hook.SPEED-2*stone[i].getSize());
                MoneyToAdd = stone[i].getRelValue();
                return true;
            }
        }
        return false;
    }
    
    /** 启动计时器的方法 */
    private void doTimerStart() {
        //计时器不是null的时候就启动计时器
        if(timer == null) {
            GameTimer gameTimer = new GameTimer(this);
            timer = new Timer();
            timer.schedule(gameTimer, 1000, 1000);
        }
    }
    
    /** 停止计时器的方法 */
    private void doTimerStop() {
        //计时器不是null的时候就停止计时器
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    /** 进行暂停或重新开始的方法 */
    private void doPauseOrResume() {
        if(isPaused) {
            //暂停时
            //重新开始
            isPaused = false;
            doTimerStart();
        }else {
            //不是暂停时
            //进行暂停
            isPaused = true;
            doTimerStop();
        }
        repaint();
    }
    
    public void commandAction(Command c, Displayable s) {
        if(c == startCmd) {//Start指令
            //进行游戏初始化来开始游戏
            Stage = CIRCUMGYRATEING;
            
        }else if(c == exitCmd) {//Exit指令
            //调用出NumberSliderPuzzle的doExit方法来将MIDlet本身结束
            goldminer.doExit();
            
        }else if(c == pauseCmd) {//Pause指令
            //交换Pause与Resume指令，调用Pause、Resume处理方法
            removeCommand(pauseCmd);
            addCommand(resumeCmd);
            doPauseOrResume();
            
        }else if(c == resumeCmd) {//Resume指令
            //交换Resume与Pause指令，调用Pause、Resume处理方法
            removeCommand(resumeCmd);
            addCommand(pauseCmd);
            doPauseOrResume();
            
        }else if(c == quitCmd) {//Quit指令
            //停止计时器
            doTimerStop();
            //删除指令
            removeCommand(pauseCmd);
            removeCommand(resumeCmd);
            removeCommand(quitCmd);
            //显示标题
            Stage = STARTING;
            repaint();
        }
    }
    
}
