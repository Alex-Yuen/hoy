package mobi.samov.client.net;

/**
 * <p>Title: CardClient</p>
 *
 * <p>Description: lizhenpeng</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: LP&P</p>
 *
 * @author lipeng
 * @version 1.0
 */
import java.io.*;
public class DataHead
{
  public DataHead()
  {
  }
  byte[] getBytes()
  {
    byte[] data = null;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try
    {
      dos.writeChar(command);
      dos.writeChar(size);
    }
    catch(Exception e)
    {
    }
    data = bos.toByteArray();
    return data;
  }
  void FillData(byte[]data,int index)
  {
    ByteArrayInputStream bis = new ByteArrayInputStream(data,index,4);
    DataInputStream dis = new DataInputStream(bis);
    try
    {
      command=dis.readChar();
      size=dis.readChar();
    }
    catch(Exception e)
    {
    }
  }
  /**
   * 数据包头部命令字
   */
  public char command;
  /**
   * 数据包头大小
   */
  public char size;
}
