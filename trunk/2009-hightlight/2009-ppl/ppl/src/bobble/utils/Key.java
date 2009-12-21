package bobble.utils;
public interface Key{
	final static int NUM0					= (1 << 0);
	final static int NUM1					= (1 << 1);
	final static int NUM2					= (1 << 2);
	final static int NUM3					= (1 << 3);
	final static int NUM4					= (1 << 4);
	final static int NUM5					= (1 << 5);
	final static int NUM6					= (1 << 6);
	final static int NUM7					= (1 << 7);
	final static int NUM8					= (1 << 8);
	final static int NUM9					= (1 << 9);
	
	final static int UP					= (1 << 10);
	final static int DOWN					= (1 << 11);
	final static int LEFT					= (1 << 12);
	final static int RIGHT					= (1 << 13);
	final static int SELECT					= (1 << 14);
	
	final static int SOFT_L					= (1 << 15);
	final static int SOFT_R					= (1 << 16);
	final static int SOFT_M					= (1 << 17);
	
	final static int STAR					= (1 << 18);
	final static int POUND					= (1 << 19);
	
	final static int SEND					= (1 << 20);
	final static int END					= (1 << 21);
	
	//////////
	final static int COMBO1			        = NUM2 |NUM5;
	final static int MENU_SELECT			= SELECT |NUM5;
	final static int MENU_SOFT_L			= SOFT_L;
	final static int MENU_SOFT_R			= SOFT_R;
	final static int MENU_UP				= NUM2 | UP;
	final static int MENU_DOWN				= NUM8 | DOWN;
	final static int MENU_LEFT				= NUM4 | LEFT;
	final static int MENU_RIGHT				= NUM6 | RIGHT;
	
	final static int MOVE_UP				= NUM2 | UP;
	final static int MOVE_DOWN				= NUM8 | DOWN;
	final static int MOVE_LEFT				= NUM4 | LEFT;
	final static int MOVE_RIGHT				= NUM6 | RIGHT;
	
//	final static int JUMP					= NUM2 | UP;
	final static int JUMP_LEFT				= NUM1;
	final static int JUMP_RIGHT				= NUM3;
	
	
	final static int COMBAT					= NUM5 | SELECT;
	final static int COMBAT_IN				= NUM5 | SELECT | NUM0;
	final static int COMBAT_OUT				= NUM0;
	final static int COMBAT_BLOCK			= MOVE_UP;
	
	final static int SHOW_INGAME_MENU		= SOFT_R;
	final static int SKIP_TASK				= SOFT_R;
}
