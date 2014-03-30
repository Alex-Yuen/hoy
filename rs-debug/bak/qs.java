import java.io.*;

public class qs extends xqs
{

    qs(OutputStream arg0, int arg1)
    {
        super(arg0, arg1);
    }

    void a(byte arg0[], int arg1, int arg2)
        throws IOException
    {
        super.a(arg0, arg1, arg2);
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        int i;
        try
        {
            i = 3 / 0;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
	void h(byte[] arg0, int arg1, int arg2) throws IOException {
		// TODO Auto-generated method stub
		super.h(arg0, arg1, arg2);
		try
        {
			int i = 3 / 0;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}

	@Override
	void n(byte[] arg0, int arg1, int arg2) throws IOException {
		// TODO Auto-generated method stub
		super.n(arg0, arg1, arg2);
		try
        {
			int i = 3 / 0;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}

	@Override
	void o(byte[] arg0, int arg1, int arg2, int arg3) throws IOException {
		// TODO Auto-generated method stub
		super.o(arg0, arg1, arg2, arg3);
		try
        {
            int i = 3 / 0;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}

	public static void main(String args1[])
    {
    }
}
