/**
* 精灵链表类
*/
public class MyLine
{
	/**
	* 内部类，定义链表节点的属性
	*/
	private class Node
	{
		private int layerID;
		private Node next;
		private int paopao_row;
		private int paopao_col;
		private int paopaoColor;
		
		/**
		* 构造函数，建立链接节点
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
	
	//链接头
	private Node head=null;
	
	/**
	* 链接构造函数
	*/
	public MyLine()
	{
		super();
	}
	
	/**
	* 清空链表
	*/
	public void setNodeNull()
	{
		head=null;
	}
	
	/**
	* 头节点前插入节点
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
	* 删除节点
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
	* 修改泡泡颜色
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
	* 查找当前位置是否存在泡泡，存在则返回泡泡所在节点的layerID，不存在则返回-1
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
	* 添加图层后，更新相关图层索引号
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
	* 删除图层后，更新相关图层索引号
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
	* 查找出当前图层中所有的泡泡颜色
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
	* 检查链表是否为空
	*/
	public boolean isNull()
	{
		if (head==null) return true;
		else return false;
	}
	
	/**
	* 打印当前链表中泡泡的图层ID以及所在的行与列坐标
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