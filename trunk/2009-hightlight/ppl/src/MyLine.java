/**
* ����������
*/
public class MyLine
{
	/**
	* �ڲ��࣬��������ڵ������
	*/
	private class Node
	{
		private int layerID;
		private Node next;
		private int paopao_row;
		private int paopao_col;
		private int paopaoColor;
		
		/**
		* ���캯�����������ӽڵ�
		*/
		public Node(int layerID,int paopao_row,int paopao_col,int paopaoColor)
		{
			this.layerID=layerID;
			this.paopao_row=paopao_row;
			this.paopao_col=paopao_col;
			this.paopaoColor=paopaoColor;
			next=null;
		}
	}
	
	//����ͷ
	private Node head=null;
	
	/**
	* ���ӹ��캯��
	*/
	public MyLine()
	{
		super();
	}
	
	/**
	* �������
	*/
	public void setNodeNull()
	{
		head=null;
	}
	
	/**
	* ͷ�ڵ�ǰ����ڵ�
	*/
	public void insertNode(int layerID,int paopao_row,int paopao_col,int paopaoColor,int ranAddNum,boolean isRanAddPP)
	{
		if (head==null)
		{
			head=new Node(layerID,paopao_row,paopao_col,paopaoColor);
		}
		else
		{
			Node current=head;
			head=new Node(layerID,paopao_row,paopao_col,paopaoColor);
			head.next=current;
		}
		if (isRanAddPP)
		{
			addLayerID(layerID,ranAddNum);
		}
		//printNode();
	}
	
	/**
	* ɾ���ڵ�
	*/
	public boolean delNode(int layerID)
	{
		Node current=head;
		Node parent;
		if (head==null)
		{
			return false;
		}
		else if (head.layerID==layerID)
		{
			head=head.next;
			minusLayerID(layerID);
			//printNode();
			return true;
		}
		else
		{
			parent=head;
			current=head.next;
			while (current!=null)
			{
				if (current.layerID!=layerID)
				{
					parent=current;
					current=current.next;
				}
				else
				{
					parent.next=current.next;
					minusLayerID(layerID);
					//printNode();
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	* �޸�������ɫ
	*/
	public void setNode(int layerID,int paopaoColor)
	{
		Node current=head;
		while (current!=null)
		{
			if (current.layerID!=layerID)
			{
				current=current.next;
			}
			else
			{
				current.paopaoColor=paopaoColor;
				break;
			}
		}
		//printNode();
	}
	
	/**
	* ���ҵ�ǰλ���Ƿ�������ݣ������򷵻��������ڽڵ��layerID���������򷵻�-1
	*/
	public int findNode(int paopao_row,int paopao_col)
	{
		Node current=head;
		while (current!=null)
		{
			if (current.paopao_row==paopao_row && current.paopao_col==paopao_col)
			{
				return current.layerID;
			}
			else
			{
				current=current.next;
			}
		}
		return -1;
	}
	
	/**
	* ���ͼ��󣬸������ͼ��������
	*/
	private void addLayerID(int layerID,int ranAddNum)
	{
		Node current=head.next;
		while (current!=null)
		{
			//System.out.println("layerID:"+layerID+" "+current.layerID);
			current.layerID+=ranAddNum;
			current=current.next;
		}
	}
	
	/**
	* ɾ��ͼ��󣬸������ͼ��������
	*/
	private void minusLayerID(int layerID)
	{
		Node current=head;
		while (current!=null)
		{
			if (current.layerID>layerID)
			{
				current.layerID--;
			}
			current=current.next;
		}
	}
	
	/**
	* ���ҳ���ǰͼ�������е�������ɫ
	*/
	public int[] getPaopaoColor()
	{
		Node current=head;
		int paopaoColor[]=new int[8];
		boolean isSame=false;
		int n=0;
		while (current!=null)
		{
			for (int i=0;i<8;i++)
			{
				if (paopaoColor[i]==0) continue;
				if (paopaoColor[i]==current.paopaoColor) 
				{
					isSame=true;
					continue;
				}
			}
			if (!isSame)
			{
				paopaoColor[n++]=current.paopaoColor;
			}
			else
			{
				isSame=false;
			}
			if (n==8) break;
			current=current.next;
		}
		return paopaoColor;
	}
	
	/**
	* ��������Ƿ�Ϊ��
	*/
	public boolean isNull()
	{
		if (head==null) return true;
		else return false;
	}
	
	/**
	* ��ӡ��ǰ���������ݵ�ͼ��ID�Լ����ڵ�����������
	*/
	private void printNode()
	{
		Node current=head;
		while (current!=null)
		{
			System.out.print(current.layerID+"("+current.paopao_row+" "+current.paopao_col+" "+current.paopaoColor+")  ");
			current=current.next;
		}
		System.out.println("end");
	}
}