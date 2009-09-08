package mobi.samov.client.game;


import java.io.DataInputStream;

public class StrCode {
	 final  String FILE_NAME_STRING = "/string.bin";//
	 final  String FILE_NAME_STRING2 = "/string2.bin";//道具介绍
	 final  String FILE_NAME_STRING3 = "/string3.bin";//充值介绍
	 final  String FILE_NAME_STRING4 = "/string4.bin";//充值介绍
    public final int MAX_COMMON_STR_ID   = 1024;
    String[] s_commonStrings;
    String[][] s_commonStrings2;
//    private String[] s_levelStrings;
	
    public void LoadStrings(int blockID,String str)
    {
        try
        {
            DataInputStream dis = new DataInputStream(str.getClass().
                getResourceAsStream(str));
            LocateToBlock(dis, blockID);
            int stringCount = ReadUnsignedVarshort(dis);
            String[] strings = new String[stringCount];
            for (int i = 0; i < stringCount; i++)
            {
            		strings[i] = dis.readUTF();
               
            }
            if (blockID == 0)
            {
                s_commonStrings = strings;
            }
            dis.close();
            dis = null;
        }
        catch (Exception e)
        {
        }
    }
    /**
     * 读取数组
     * @param blockID
     * @param str
     */
    public void LoadStrings2(int blockID,String str,int len)
    {
        try
        {
            DataInputStream dis = new DataInputStream(str.getClass().
                getResourceAsStream(str));
            LocateToBlock(dis, blockID);
            int stringCount = ReadUnsignedVarshort(dis);
           s_commonStrings2 = new String[stringCount][len];
      //      String[][] strings = new String[stringCount][4];
            for (int i = 0; i < stringCount; i++)
            {
            	for (int j = 0; j < len; j++) {
            		s_commonStrings2[i][j] = dis.readUTF(); 
				}
            }
            dis.close();
            dis = null;
        }
        catch (Exception e)
        {
        }
    }
    public int LocateToBlock(DataInputStream dis, int blockID) throws Exception
    {
	    int blockCount = ReadUnsignedVarshort(dis);
	    dis.skip(blockID * 2);
	    int offset = ReadUnsignedShort(dis);
	    int size = ReadUnsignedShort(dis) - offset;
	    dis.skip( (blockCount - blockID - 1) * 2);
	    dis.skip(offset);
	    return size;
    }
    public static int ReadUnsignedShort(DataInputStream dis) throws Exception
    {
        return dis.readUnsignedShort();
    }
    public short ReadUnsignedVarshort(DataInputStream dis) throws Exception
    { 
	    int data1 = dis.readUnsignedByte();
	    if ( (data1 & 0x80) == 0)
	    {
	        return (short) (data1 & 0x7F);
	    }
	    else
	    {
	        int data2 = ReadUnsignedByte(dis);
	        return (short) ( ( (data1 & 0x7F) << 8) + data2);
	    }
    }    
    public int ReadUnsignedByte(DataInputStream dis) throws Exception
    { 
    	return dis.readUnsignedByte();
    }
    public void FreeStr()
    {
    	 s_commonStrings = null;
    	 s_commonStrings2 = null;
    }
    public String GetString(int stringID,String str)
    {
    	LoadStrings(0,str);
    	String s = null;
    	if(s_commonStrings==null)
    		System.out.println("StrCode.GetString()");
        if (stringID <= MAX_COMMON_STR_ID)
        {
            s = ""+s_commonStrings[stringID];
        }
        s_commonStrings = null;
        return s;
//        else
//        {
//            return s_levelStrings[stringID - MAX_COMMON_STR_ID - 1];
//        }
    }
}
