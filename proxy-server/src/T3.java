
public class T3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "52756e5570646174653d3126456d61696c3d686f797a68616e67403136332e636f6d2650617373776f72643d31323334353637382667616d656e616d653d26617574686b65793d35343335363838266578743d286e756c6c29266d61633d3744353338343234384238463439373641344239454436423933463338424334265665723d372e322e31323032323426506172746e657269643d302673613d3026696e695665723d372e322e313230363035266b65793d6439626263353261316338326638626633636238363934363065346232663537".toUpperCase();
		//System.out.println(s);
		int i = 0;
		int r = 0;
		while(i<s.length()){
			char c = s.charAt(i);
			r += (int)c;
			i++;
		}
		
		r = r%0x8000;
		//System.out.println(Integer.toHexString(r));
		String salt = Integer.toHexString(r);
		System.out.println(salt);
		//Integer.toHexString(r);
		//s = "12345678";
		String head = s.substring(0, s.length()/2);
		String tail = s.substring(s.length()/2);
		s = head + salt + tail;
		System.out.println(s);
	}

}
