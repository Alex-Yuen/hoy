package it.hoyland.sclottery;

public class LoginTask implements Task {

	private LotteryMIDlet midlet;
	
	public LoginTask(LotteryMIDlet midlet){
		this.midlet = midlet;
	}
	
	public void execute() {
		switch(this.midlet.getStatus()){
        case 58: // ':'
        case 59: // ';'
        case 60: // '<'
        case 61: // '='
        case 62: // '>'
        case 63: // '?'
        case 64: // '@'
        case 67: // 'C'
        case 68: // 'D'
        case 69: // 'E'
        case 70: // 'F'
        case 71: // 'G'
        case 72: // 'H'
        case 73: // 'I'
        case 74: // 'J'
        case 75: // 'K'
        case 76: // 'L'
        case 77: // 'M'
        case 78: // 'N'
        case 79: // 'O'
        case 80: // 'P'
        case 81: // 'Q'
        case 82: // 'R'
        case 83: // 'S'
        case 84: // 'T'
        case 85: // 'U'
        case 86: // 'V'
        case 87: // 'W'
        case 88: // 'X'
        case 89: // 'Y'
        default:
            break;

        case 90: // 'Z'
        	this.midlet.getDisplay().setCurrent(this.midlet.getMainList());
           // D88.a(a);
            return;

        case 48: // '0'
            return;

        case 49: // '1'
           // D88.b(a);
            return;

        case 50: // '2'
            return;

        case 51: // '3'
           // D88.c(a);
            return;

        case 52: // '4'
          //  D88.d(a);
            return;

        case 53: // '5'
         //   switch(D88.b(a))
        	switch(this.midlet.getSubStatus())
            {
            case 49: // '1'
               // D88.e(a);
                break;

            case 50: // '2'
              //  D88.f(a);
                // fall through

            default:
                return;
            }
            break;

        case 54: // '6'
           // D88.g(a);
            return;

        case 55: // '7'
           // D88.h(a);
            return;

        case 56: // '8'
           // D88.i(a);
            return;

        case 57: // '9'
            //switch(D88.b(a))
        	switch(this.midlet.getSubStatus())
            {
            case 49: // '1'
                //D88.j(a);
                break;

            case 50: // '2'
                //D88.k(a);
                // fall through

            default:
                return;
            }
            break;

        case 65: // 'A'
            //D88.l(a);
            return;

        case 66: // 'B'
            //switch(D88.b(a)) {
        	switch(this.midlet.getSubStatus()){
	            case 49: // '1'
	                //D88.m(a);
	                return;
	
	            case 50: // '2'
	                //D88.n(a);
	                break;
            }
            break;
		}
	}

}
