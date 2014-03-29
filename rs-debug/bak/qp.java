import java.io.OutputStream;


public class qp extends xqp {

	qp(OutputStream arg0, int arg1) {
		super(arg0, arg1);
		System.out.println("inner qp..............................>>>>>>>>>>>>>>>>>>>>>>>>>>"+arg0.toString()+":"+arg1);
		// TODO Auto-generated constructor stub
		try{
			int k = 3/0;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
