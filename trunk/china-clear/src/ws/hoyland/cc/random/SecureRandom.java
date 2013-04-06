package ws.hoyland.cc.random;

import java.util.Random;

public class SecureRandom extends Random
{
  //private static 
  protected c generator;

//  static
//  {
//    e locale = new e();
//    b localb = new b(locale);
//    SecureRandom rand = new SecureRandom(localb);
//  }

  public SecureRandom()
  {
    super(0L);
    e locale = new e();
    b localb = new b(locale);
    this.generator = localb;
    long l = System.currentTimeMillis();
    setSeed(l);
  }

  private SecureRandom(c paramc)
  {
    super(0L);
    this.generator = paramc;
  }

  public final void a(byte[] paramArrayOfByte)
  {
    this.generator.a(paramArrayOfByte);
  }

  public final byte[] a(int paramInt)
  {
    byte[] arrayOfByte = new byte[paramInt];
    nextBytes(arrayOfByte);
    return arrayOfByte;
  }

  protected final int next(int paramInt)
  {
    int i = 0;
    int j = (paramInt + 7) / 8;
    byte[] arrayOfByte = new byte[j];
    nextBytes(arrayOfByte);
    int k = i;
    while (i < j)
    {
      int m = k << 8;
      int n = arrayOfByte[i] & 0xFF;
      k = m + n;
      i += 1;
    }
    return (1 << paramInt) - 1 & k;
  }

  public void nextBytes(byte[] paramArrayOfByte)
  {
    this.generator.b(paramArrayOfByte);
  }

  public int nextInt()
  {
    int i = 0;
    byte[] arrayOfByte = new byte[4];
    nextBytes(arrayOfByte);
    int j = i;
    while (i < 4)
    {
      int k = j << 8;
      int m = arrayOfByte[i] & 0xFF;
      j = k + m;
      i += 1;
    }
    return j;
  }

  public void setSeed(long paramLong)
  {
    if (paramLong != 0L)
      this.generator.a(paramLong);
  }
}

/* Location:           D:\dex2jar\classes.dex.dex2jar.jar
 * Qualified Name:     com.tencent.token.core.encrypt.random.SecureRandom
 * JD-Core Version:    0.6.2
 */