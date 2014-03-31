import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;


public class qq extends xqq {

	public qq(OutputStream arg0, int arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	@Override
	void u(byte[] arg0, int arg1, int arg2, byte arg3) throws IOException {
		// TODO Auto-generated method stub
		super.u(arg0, arg1, arg2, arg3);
		
		try {
			Class<?> clazz = this.getClass().getClassLoader().loadClass("l");
			Field field = clazz.getDeclaredField("p");
			Boolean bl = new Boolean(false);
			System.out.println("l.p.0="+field.getBoolean(bl));
			System.out.println("l.p="+bl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(arg0);
		System.out.println(arg1);
		System.out.println(arg2);
		System.out.println(arg3);
		
		try{
			int i = 3/0;
		}catch(Exception e){
		e.printStackTrace();	
		}
	}

	
}
