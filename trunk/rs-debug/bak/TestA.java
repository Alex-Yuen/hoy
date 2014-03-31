import java.lang.reflect.Field;


public class TestA {

	static int i = 29;
	public TestA() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args){
		try {
			Class<?> clazz = TestA.class.getClassLoader().loadClass("TestA");
			Field field = clazz.getDeclaredField("i");
			Integer it = new Integer(-2);
			System.out.println(field.getInt(it));
			System.out.println("l.l ="+it.intValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
