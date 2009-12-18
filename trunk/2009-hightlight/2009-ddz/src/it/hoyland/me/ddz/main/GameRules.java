package it.hoyland.me.ddz.main;

public class GameRules
{
	int cards[]=new int[20];
	int num=0;
	public GameRules()	{
	}
	public void setCards(int cd[],int n)
	{
		num=n;
		for(int i=0;i<num;i++)
		{
			cards[i]=cd[num-1-i];
		}
	}
	public int getCardType()
	{
		int type=0;
		if(isRocket())type=1;
		else if(isBomb()!=0)type=2;
		else if(isOne()!=0)type=3;
		else if(isTwo()!=0)type=4;
		else if(isThree()!=0)type=5;
		else if(is3and1()!=0)type=6;
		else if(isOneSeq()!=0)type=7;
		else if(isTwoSeq()!=0)type=8;
		else if(isThreeSeq()!=0)type=9;
		else if(isPlane()!=0)type=10;
		else if(is4and2()!=0)type=11;
		return type;
	}
	public boolean isRocket()
	{
		if(num!=2) return false;
		if(cards[0]==53&&cards[1]==54)
			return true;
		else return false;
	}
	public int isBomb()
	{

        if(num!=4)return 0;
		int n1=int2Value(cards[0]);
		int n2=int2Value(cards[1]);
		int n3=int2Value(cards[2]);
		int n4=int2Value(cards[3]);
		if(n1==n2&&n2==n3&&n3==n4)return n1;
		else return 0;
	}
	public int isOne()
	{
		if(num==1)return int2Value(cards[0]);
		else return 0;
	}
	public int isTwo()
	{
		if(num!=2)return 0;
		int n1=int2Value(cards[0]);
		int n2=int2Value(cards[1]);
		if(n1==n2&&n1!=16)return n1;
		else return 0;
	}
	public int isThree()
	{
		if(num!=3)return 0;
		int n1=int2Value(cards[0]);
		int n2=int2Value(cards[1]);
		int n3=int2Value(cards[2]);
		if(n1==n2&&n2==n3)return n1;
		else return 0;
	}
	public int is3and1()
	{
		if(num!=4&&num!=5)
		{
			return 0;
		}
		int val[]=new int[5];
		for(int i=0;i<num;i++)
			val[i]=int2Value(cards[i]);
		if(val[2]==val[0])
		{
			if(num==4)
			{
				if(val[0]==val[1]&&val[1]==val[2]&&val[2]!=val[3])
					return val[0];
			}
			else if(num==5)
			{
				if(val[0]==val[1]&&val[1]==val[2]&&val[3]==val[4]&&val[2]!=val[3])
					return val[0];
			}
		}
		else if(val[2]==val[num-1])
		{
			if(num==4)
			{
				if(val[0]!=val[1]&&val[1]==val[2]&&val[2]==val[3])
					return val[1];
			}
			if(num==5)
			{
				if(val[0]==val[1]&&val[2]==val[3]&&val[3]==val[4])
					return val[2];
			}
		}
		return 0;
	}
	public int isOneSeq()
	{
		if(num>=5&&num<=12)
		{
			int val[]=new int[12];
			for(int i=0;i<num;i++)
			{
				val[i]=int2Value(cards[i]);
				if(val[i]>=15)return 0;
			}
			for(int i=0;i<num-1;i++)
			{
				if(val[i]!=val[i+1]-1)
					return 0;
			}
			return val[0];
		}
		else return 0;
	}
	public int isTwoSeq()
	{
		if(num%2!=0||num<6)return 0;
		int val[]=new int[20];
		for(int i=0;i<num;i++)
		{
			val[i]=int2Value(cards[i]);
			if(val[i]>=15)return 0;
		}
		for(int i=0;i<num-1;i++)
		{
			if(i%2==0)
			{
				if(val[i]!=val[i+1])return 0;
			}
			else if(i%2==1)
			{
				if(val[i]!=val[i+1]-1)return 0;
			}
		}
		return val[0];
	}
	public int isThreeSeq()
	{
		if(num%3!=0||num<6)return 0;
		int val[]=new int[18];
		for(int i=0;i<num;i++)
		{
			val[i]=int2Value(cards[i]);
			if(val[i]>=15)return 0;
		}
		for(int i=0;i<num-1;i++)
		{
			if(i%3==0)
			{
				if(val[i]!=val[i+1])return 0;
			}
			else if(i%3==1)
			{
				if(val[i]!=val[i+1])return 0;
			}
			else if(i%3==2)
			{
				if(val[i]!=val[i+1]-1)return 0;
			}
		}
		return val[0];
	}
	public int isPlane()
	{
		int val[]=new int[18];
		for(int i=0;i<num;i++)
		{
			val[i]=int2Value(cards[i]);
		}
		int res=0;
		if(num==8)
		{
			//XXXYYYab,aXXXYYYb,abXXXYYY
			int i=0;
			for(i=0;i<=2;i++)
				if(val[i]==val[i+1]&&val[i+1]==val[i+2]&&val[i+2]==val[i+3]-1
				     &&val[i+3]==val[i+4]&&val[i+4]==val[i+5]&&val[i+3]<15)
					break;
			int tmp=i;
			if(tmp==0)
			{
				if(val[6]!=val[7]&&val[6]!=val[3])
					res=val[0];
			}
			else if(tmp==1)
			{
				if(val[0]!=val[1]&&val[7]!=val[4])
					res=val[1];
			}
			else if(tmp==2)
			{
				if(val[0]!=val[1]&&val[1]!=val[2])
					res=val[2];
			}
		}//end of num==8
		else if(num==10)
		{
			//xxxyyyaabb,aaxxxyyybb,aabbxxxyyy
			int i=0;
			for(i=0;i<=4;i++)
				if(val[i]==val[i+1]&&val[i+1]==val[i+2]&&val[i+2]==val[i+3]-1
				     &&val[i+3]==val[i+4]&&val[i+4]==val[i+5]&&val[i+3]<15)
					break;
			int tmp=i;
			if(tmp==0)
			{
				if(val[6]==val[7]&&val[7]!=val[8]&&val[8]==val[9])
					res=val[0];
			}
			else if(tmp==2)
			{
				if(val[0]==val[1]&&val[8]==val[9])
					res=val[2];
			}
			else if(tmp==4)
			{
				if(val[0]==val[1]&&val[1]!=val[2]&&val[2]==val[3])
					res=val[4];
			}
		}//end of num==10
		else if(num==12)
		{
			//xxxyyyzzzabc,axxxyyyzzzbc,abxxxyyyzzzc,abcxxxyyyzzz
			int i=0;
			for(i=0;i<=3;i++)
				if(val[i]==val[i+1]&&val[i+1]==val[i+2]&&val[i+2]==val[i+3]-1
						&&val[i+3]==val[i+4]&&val[i+4]==val[i+5]&&val[i+5]==val[i+6]-1
						&&val[i+6]==val[i+7]&&val[i+7]==val[i+8]&&val[i+6]<15)
					break;
			int tmp=i;
			if(tmp==0)
			{
				if(val[9]!=val[10]&&val[10]!=val[11]&&val[9]!=val[8])
					res=val[0];
			}
			else if(tmp==1)
			{
				if(val[0]!=val[1]&&val[9]!=val[10]&&val[10]!=val[11])
					res=val[1];
			}
			else if(tmp==2)
			{
				if(val[0]!=val[1]&&val[1]!=val[2]&&val[10]!=val[11])
					res=val[2];
			}
			else if(tmp==3)
			{
				if(val[0]!=val[1]&&val[1]!=val[2]&&val[2]!=val[3])
					res=val[3];
			}
		}//end of num==12
		else if(num==15)
		{
			//xxxyyyzzzaabbcc,aaxxxyyyzzzbbcc,aabbxxxyyyzzzcc,aabbccxxxyyyzzz
			int i=0;
			for(i=0;i<=6;i++)
				if(val[i]==val[i+1]&&val[i+1]==val[i+2]&&val[i+2]==val[i+3]-1
						&&val[i+3]==val[i+4]&&val[i+4]==val[i+5]&&val[i+5]==val[i+6]-1
						&&val[i+6]==val[i+7]&&val[i+7]==val[i+8]&&val[i+6]<15)
					break;
			int tmp=i;
			if(tmp==0)
			{
				if(val[9]==val[10]&&val[10]!=val[11]&&val[11]==val[12]&&
						val[12]!=val[13]&&val[13]==val[14])
					res=val[0];
			}
			else if(tmp==2)
			{
				if(val[0]==val[1]&&val[11]==val[12]&&val[12]!=val[13]&&val[13]==val[14])
					res=val[2];
			}
			else if(tmp==4)
			{
				if(val[0]==val[1]&&val[1]!=val[2]&&val[2]==val[3]&&val[13]==val[14])
					res=val[4];
			}
			else if(tmp==6)
			{
				if(val[0]==val[1]&&val[1]!=val[2]&&val[2]==val[3]&&val[3]!=val[4]&&val[4]==val[5])
					res=val[6];
			}
		}//end of num==15
		return res;
	}
	public int is4and2()
	{
		if(num>8)return 0;
		int val[]=new int[8];
		for(int i=0;i<num;i++)
		{
			val[i]=int2Value(cards[i]);
		}
		int res=0;
		if(num==6)
		{
			//XXXXAB,AXXXXB,ABXXXX
			int i=0;
			for(i=0;i<=2;i++)
				if(val[i]==val[i+1]&&val[i+1]==val[i+2]&&val[i+2]==val[i+3])
					break;
			int tmp=i;
			if(tmp==0)
			{
				if(val[4]!=val[5])res=val[0];
			}
			else if(tmp==1)
			{
				res=val[1];
			}
			else if(tmp==2)
			{
				if(val[0]!=val[1])
					res=val[2];
			}
		}//end of num==6
		else if(num==8)
		{
			//xxxxaabb,aaxxxxbb,aabbxxxx
			int i=0;
			for(i=0;i<=4;i++)
				if(val[i]==val[i+1]&&val[i+1]==val[i+2]&&val[i+2]==val[i+3])
					break;
			int tmp=i;
			if(tmp==0)
			{
				if(val[4]==val[5]&&val[5]!=val[6]&&val[6]==val[7])
					res=val[0];
			}
			else if(tmp==2)
			{
				if(val[0]==val[1]&&val[6]==val[7])
					res=val[2];
			}
			else if(tmp==4)
			{
				if(val[0]==val[1]&&val[1]!=val[2]&&val[2]==val[3])
					res=val[4];
			}
		}
		return res;
	}
	public int int2Value(int x)
	{
		if(x==53)return 16;
		if(x==54)return 17;
		int val=0;
		if(x%4==0)
			val=x/4+2;
		else
			val=x/4+3;
		return val;
	}
}