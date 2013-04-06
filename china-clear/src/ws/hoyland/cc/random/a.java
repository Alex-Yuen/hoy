package ws.hoyland.cc.random;

public abstract class a
  implements f
{
  private byte[] a;
  private int b;
  private long c;

  protected a()
  {
    byte[] arrayOfByte = new byte[4];
    this.a = arrayOfByte;
    this.b = 0;
  }

  public final void a()
  {
    long l = this.c << 3;
    a(-128);
    while (this.b != 0)
      a(0);
    a(l);
    c();
  }

  public final void a(byte paramByte)
  {
    byte[] arrayOfByte1 = this.a;
    int i = this.b;
    int j = i + 1;
    this.b = j;
    arrayOfByte1[i] = paramByte;
    int k = this.b;
    int m = this.a.length;
    if (k == m)
    {
      byte[] arrayOfByte2 = this.a;
      a(arrayOfByte2, 0);
      this.b = 0;
    }
    long l = this.c + 1L;
    this.c = l;
  }

  protected abstract void a(long paramLong);

  protected abstract void a(byte[] paramArrayOfByte, int paramInt);

  public final void a(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = paramInt2;
    int j = paramInt1;
    while ((this.b != 0) && (i > 0))
    {
      byte b1 = paramArrayOfByte[j];
      a(b1);
      j += 1;
      i += -1;
    }
    while (true)
    {
      int k = this.a.length;
      if (i <= k)
        break;
      a(paramArrayOfByte, j);
      int m = this.a.length;
      j += m;
      int n = this.a.length;
      i -= n;
      long l1 = this.c;
      long l2 = this.a.length;
      long l3 = l1 + l2;
      this.c = l3;
    }
    while (i > 0)
    {
      byte b2 = paramArrayOfByte[j];
      a(b2);
      j += 1;
      i += -1;
    }
  }

  public void b()
  {
    this.c = 0L;
    this.b = 0;
    int i = 0;
    while (true)
    {
      int j = this.a.length;
      if (i >= j)
        break;
      this.a[i] = 0;
      i += 1;
    }
  }

  protected abstract void c();
}

/* Location:           D:\dex2jar\classes.dex.dex2jar.jar
 * Qualified Name:     com.tencent.token.core.encrypt.random.a
 * JD-Core Version:    0.6.2
 */