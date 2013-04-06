package ws.hoyland.cc.random;

public final class b
  implements c
{
  private static long a = 10L;
  private long b;
  private long c;
  private d d;
  private byte[] e;
  private byte[] f;

  public b(d paramd)
  {
    this.d = paramd;
    byte[] arrayOfByte1 = new byte[20];
    this.f = arrayOfByte1;
    this.c = 1L;
    byte[] arrayOfByte2 = new byte[20];
    this.e = arrayOfByte2;
    this.b = 1L;
  }

  private void a()
  {
    long l1 = this.b;
    long l2 = l1 + 1L;
    this.b = l2;
    long l3 = l1;
    int i = 0;
    while (i != 8)
    {
      d locald1 = this.d;
      byte b1 = (byte)(int)l3;
      locald1.a(b1);
      l3 >>>= 8;
      i += 1;
    }
    byte[] arrayOfByte1 = this.e;
    d locald2 = this.d;
    int j = arrayOfByte1.length;
    locald2.a(arrayOfByte1, 0, j);
    byte[] arrayOfByte2 = this.f;
    d locald3 = this.d;
    int k = arrayOfByte2.length;
    locald3.a(arrayOfByte2, 0, k);
    byte[] arrayOfByte3 = this.e;
    int m = this.d.a(arrayOfByte3);
    long l4 = this.b;
    long l5 = a;
    if (l4 % l5 == 0L)
    {
      byte[] arrayOfByte4 = this.f;
      d locald4 = this.d;
      int n = arrayOfByte4.length;
      locald4.a(arrayOfByte4, 0, n);
      long l6 = this.c;
      long l7 = l6 + 1L;
      this.c = l7;
      l3 = l6;
      i = 0;
      while (i != 8)
      {
        d locald5 = this.d;
        byte b2 = (byte)(int)l3;
        locald5.a(b2);
        l3 >>>= 8;
        i += 1;
      }
      byte[] arrayOfByte5 = this.f;
      int i1 = this.d.a(arrayOfByte5);
    }
  }

  public final void a(long paramLong)//set seed
  {
    int i = 0;
    long l = paramLong;
    while (true)
    {
      try
      {
    	while (i != 8)
    	{
	        d locald1 = this.d;
	        byte b1 = (byte)(int)l;
	        locald1.a(b1);
	        l >>>= 8;
	        i += 1;
    	}//continue
        byte[] arrayOfByte1 = this.f;
        d locald2 = this.d;
        int j = arrayOfByte1.length;
        locald2.a(arrayOfByte1, 0, j);
        byte[] arrayOfByte2 = this.f;
        int k = this.d.a(arrayOfByte2);
        return;
      }catch(Exception e){
    	  e.printStackTrace();
      }
      finally
      {
//        localObject = finally;
//        throw localObject;
      }
    }
  }

  public final void a(byte[] paramArrayOfByte)
  {
    try
    {
      d locald1 = this.d;
      int i = paramArrayOfByte.length;
      locald1.a(paramArrayOfByte, 0, i);
      byte[] arrayOfByte1 = this.f;
      d locald2 = this.d;
      int j = arrayOfByte1.length;
      locald2.a(arrayOfByte1, 0, j);
      byte[] arrayOfByte2 = this.f;
      int k = this.d.a(arrayOfByte2);
      return;
    }catch(Exception e){
  	  e.printStackTrace();
    }
    finally
    {
//      localObject = finally;
//      throw localObject;
    }
  }

  public final void b(byte[] paramArrayOfByte)//nextbytes
  {
    int i = paramArrayOfByte.length;
    try
    {
      a();
      i += 0;
      int j = 0;
      int n;
      for (int k = 0; j != i; k = n)
      {
        int m = this.e.length;
        if (k == m)
        {
          a();
          k = 0;
        }
        byte[] arrayOfByte = this.e;
        n = k + 1;
        byte i1 = arrayOfByte[k];
        paramArrayOfByte[j] = i1;
        j += 1;
      }
      return;
    }catch(Exception e){
  	  e.printStackTrace();
    }
    finally
    {
//      localObject = finally;
//      throw localObject;
    }
  }
}

/* Location:           D:\dex2jar\classes.dex.dex2jar.jar
 * Qualified Name:     com.tencent.token.core.encrypt.random.b
 * JD-Core Version:    0.6.2
 */