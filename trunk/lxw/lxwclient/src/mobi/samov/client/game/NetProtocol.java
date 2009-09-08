package mobi.samov.client.game;

public class NetProtocol
{
  //客户端发向服务器
//  public static final char LOGIN = 0x0001;
//  public static final char REQUEST_STARTGAME = 0x0002;
//  public static final char POST_CARD = 0x0005;
//  public static final char REJECT_CARD = 0x0006;
  //服务器发向客户端
  public static final byte QUICK = 1;   //快速游戏
  public static final byte LOGIN = 2;   //登陆
  public static final byte SCENES = 3;
  public static final byte SCENE = 4;
  public static final byte ROOM = 5;
  public static final byte DESK = 6;
  public static final byte SIT = 7;
  public static final byte LEFT = 8;
  public static final byte READY = 9;
  public static final byte UNREADY = 10;
  public static final byte IN = 11;
  public static final byte OUT = 12;
  public static final byte INFO = 13;
  public static final byte DEAL = 14;
  public static final byte DROP = 15;
  public static final byte QGAME = 16;
  public static final byte BUY = 17;
  public static final byte QUIT = 18;
}
