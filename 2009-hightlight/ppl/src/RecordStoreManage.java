import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import javax.microedition.io.*;

/**
* 游戏存储类
*/
public class RecordStoreManage
{
	private RecordStore rs=null;
	
	/********************************************************************************************************/
	/**
	* 打开数据仓库
	*/
	private RecordStore openRS(String recordStoreName) 
	{
		RecordStore rs = null;
		
		//名称不能大于32个字符
		if (recordStoreName.length()>32) return null;
		try 
		{
			rs=RecordStore.openRecordStore(recordStoreName, true);
			return rs;
		} 
		catch (Exception e) 
		{
			System.out.println("打开数据仓库失败");
			return null;
		}
	}
	
	/**
	* 关闭数据仓库
	*/
	private void closeRS(RecordStore rs)
	{
		try
		{
			rs.closeRecordStore();
		}
		catch (RecordStoreNotOpenException e)
		{
			e.printStackTrace();
		}
		catch (RecordStoreException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	* 删除数据仓库
	*/
	public boolean deleteRS(String recordStoreName) 
	{
		//名称不能大于32个字符
		if (recordStoreName.length()>32) return false;
		try 
		{
			RecordStore.deleteRecordStore(recordStoreName);
		} 
		catch (Exception e) 
		{
			return false;
		}
		return true;
	}
	
	/**
	* 检查是否有记录
	*/
	private int isStore(RecordStore rs)
	{
		try 
		{
			return rs.getNumRecords();
		} 
		catch (Exception e) 
		{
			return 0;
		}
	}
	
	/*******************************************************************************************************
	* 游戏设置                                                                                             *
	********************************************************************************************************/
	/**
	* 读取游戏配置信息
	*/
	public void readGameSetInfo()
	{
		try 
		{
			//打开游戏设置文件
			RecordStore rs=openRS(Paopao.PAOPAO_SETNAME);
			if (rs!=null)
			{
				if (isStore(rs)>0)
				{
					byte[] data=rs.getRecord(1);
					DataInputStream dis=new DataInputStream(new ByteArrayInputStream(data));
					Paopao.playerName=dis.readUTF();
					Paopao.backImageIndex=dis.readInt();
					Paopao.acousticEffect=dis.readInt();
					Paopao.backgrounMusic=dis.readInt();
					Paopao.netLinkType=dis.readInt();
					Paopao.bluetooth=dis.readInt();
					Paopao.gameStageInitializtion=dis.readInt();
					Paopao.stageIndex=dis.readInt();
					dis.close();
				}
				else
				{
					saveGameSetInfo(0);
				}
				closeRS(rs);
			}
		} 
		catch (Exception e) 
		{
			System.out.println("读取游戏配置信息失败");
		}
	}
	
	/**
	* 保存游戏配置信息
	*/
	public boolean saveGameSetInfo(int type)
	{
		try
		{
			//将游戏配置信息写入输出流
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			DataOutputStream dos=new DataOutputStream(baos);
			dos.writeUTF(Paopao.playerName);
			dos.writeInt(Paopao.backImageIndex);
			dos.writeInt(Paopao.acousticEffect);
			dos.writeInt(Paopao.backgrounMusic);
			dos.writeInt(Paopao.netLinkType);
			dos.writeInt(Paopao.bluetooth);
			dos.writeInt(Paopao.gameStageInitializtion);
			dos.writeInt(Paopao.stageIndex);
			dos.close();
			byte[] data=baos.toByteArray();
			
			//打开配置文件并保存
			RecordStore rs=openRS(Paopao.PAOPAO_SETNAME);
			if (rs!=null)
			{
				//文件里还没有记录，则添加记录
				if (type==0) rs.addRecord(data,0,data.length);
				//存在记录，则修改记录
				else rs.setRecord(1,data,0,data.length);
				closeRS(rs);
				System.out.println("存储游戏配置信息成功");
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			System.out.println("存储游戏配置信息失败");
			return false;
		}
	}
	
	/*******************************************************************************************************
	* 游戏排行榜                                                                                           *
	********************************************************************************************************/
	/**
	* 读取游戏本地排行榜信息
	*/
	public String readGameRankInfo()
	{
		String rankInfo="";
		byte[] data;
		try 
		{
			//打开游戏设置文件
			RecordStore rs=openRS(Paopao.PAOPAOR_ANKNAME);
			if (rs!=null)
			{
				if (isStore(rs)>0)
				{
					//使用RecordComparator对枚举对象内的记录进行排序
					RecordComparator comparator=new RecordComparator()
					{
						public int compare(byte[] first,byte[] second)
						{
							try
							{
								DataInputStream isFirst=new DataInputStream(new ByteArrayInputStream(first));
								DataInputStream isSecond=new DataInputStream(new ByteArrayInputStream(second));
								//从大到小的顺序
								String firstName=isFirst.readUTF();
								int firstScore=isFirst.readInt();
								String secondName=isSecond.readUTF();
								int secondScore=isSecond.readInt();
								if (firstScore!=secondScore)
								{
									return firstScore>secondScore ? RecordComparator.PRECEDES:RecordComparator.FOLLOWS;
								}
								isFirst.close();
								isSecond.close();
							}
							catch (IOException ex)
							{
								//无法读取记录，认为相等
								return RecordComparator.EQUIVALENT;
							}
							return RecordComparator.EQUIVALENT;
						}
					};
					
					DataInputStream dis=null;
					RecordEnumeration renum=rs.enumerateRecords(null,comparator,false);
					while(renum.hasNextElement())   
					{   
						int id=renum.nextRecordId();
						data=rs.getRecord(id);
						dis=new DataInputStream(new ByteArrayInputStream(data));
						rankInfo=dis.readUTF()+"        ";
						rankInfo=rankInfo+dis.readInt()+"\n";
					}
					renum.destroy();
					
					
					
					dis.close();
				}
				closeRS(rs);
			}
		} 
		catch (Exception e) 
		{
			System.out.println("读取游戏本地排行榜信息失败");
			return rankInfo;
		}
		return rankInfo;
	}
	
	/**
	* 保存排行榜信息
	*/
	public void saveGameRankInfo(int score)
	{
		
		
	}
	
	/*******************************************************************************************************
	* 游戏关卡                                                                                             *
	********************************************************************************************************/
	/**
	* 读取指定游戏关卡数据
	*/
	public byte[][] readGameStageDate(int stageIndex)
	{
		//设置数组记录关卡
		byte[][] bytes=new byte[Paopao.PAOPAO_COL][Paopao.PAOPAO_ROW];
		
		try 
		{
			//打开游戏关卡文件
			RecordStore rs=openRS(Paopao.GAME_STAGE_FILE_NAME);
			if (rs!=null)
			{
				//读取总关卡数
				if ((Paopao.gameStageTotal=isStore(rs))>0)
				{
					byte[] data=rs.getRecord(stageIndex);
					int k=0;
					for (int i=0;i<Paopao.PAOPAO_ROW;i++)
					{
						for (int j=0;j<Paopao.PAOPAO_COL;j++)
						{
							bytes[j][i]=data[k++];
						}
					}
				}
				else
				{
					return null;
				}
				closeRS(rs);
			}
		}
		catch (Exception e) 
		{
			System.out.println("读取游戏关卡失败");
		}
		
		return bytes;
	}
	
	/**
	* 读取关卡总数
	*/
	public int readStageTotal()
	{
		//打开游戏关卡文件
		RecordStore rs=openRS(Paopao.GAME_STAGE_FILE_NAME);
		if (rs!=null)
		{
			//读取总关卡数
			int stageTotal=isStore(rs);
			closeRS(rs);
			return stageTotal;
		}
		return 0;
	}
	/**
	* 存储游戏关卡
	*/
	public void saveGameStageDate(byte[] bytes)
	{
		try
		{
			//打开配置文件并保存
			RecordStore rs=openRS(Paopao.GAME_STAGE_FILE_NAME);
			if (rs!=null)
			{
				//添加记录
				rs.addRecord(bytes,0,bytes.length);
				
				closeRS(rs);
			}
		}
		catch (Exception e)
		{
			System.out.println("存储游戏关卡失败");
		}
	}
	
	/**
    * 从指定关卡文件中将关卡数据读取出来并存储到RMS文件中
    */
    public void readAndSave()
    {
		//设置数组记录关卡
		byte[] bytes=new byte[Paopao.STAGE_LENGTH];
		
		try
		{
			DataInputStream dis=new DataInputStream(RecordStoreManage.class.getResourceAsStream("/level.dat"));
			Paopao.gameStageTotal=dis.read();
			byte[] tempBytes=new byte[Paopao.gameStageTotal*100];
			
			//一次性将整个地图数据读入数组中
			dis.read(tempBytes);
			
			//保存数据
			int k=0;
			for (int i=0;i<tempBytes.length/Paopao.STAGE_LENGTH;i++)
			{
				for (int j=0;j<Paopao.STAGE_LENGTH;j++)
				{
					bytes[j]=tempBytes[i*Paopao.STAGE_LENGTH+j];
					
					/*System.out.print(bytes[j]+",");
					if ((j+1)%10==0) System.out.println();
					if ((j+1)%100==0) System.out.println();*/
				}
				saveGameStageDate(bytes);
				System.out.println("读取并保存默认游戏关卡数据成功！");
			}
		}
		catch (Exception e)
		{
			System.out.println("保存文件错误");
		}
	}
	
	/**
	* 读取游戏关卡，stageIndex为要读取的关卡
	*/
 	public byte[][][] loadGameStage(int stageIndex) 
	{
		//读出游戏关卡数据
		byte[][] bytes=readGameStageDate(stageIndex);
		byte[][][] gameStage=new byte[Paopao.PAOPAO_COL][Paopao.PAOPAO_ROW+4][3];
		
		//判断所要读取的关卡存不存在
		if (Paopao.gameStageTotal==0 || Paopao.gameStageTotal<stageIndex)
		{
			//不存在关卡数据则读取一个空的关卡
			byte[][] tempBytes=initializtionGameStageDate();
			
			int k=0,l=0;
			for(int i=0;i<Paopao.PAOPAO_ROW;i++)
			{
				for(int j=0;j<Paopao.PAOPAO_COL;j++)
				{
					gameStage[j][i][0] = tempBytes[k++][l];
				}
				k=0;
				l++;
			}
			
		}
		else
		{
			//将指定关卡读入关卡数组中
			int k=0,l=0;
			for(int i=0;i<Paopao.PAOPAO_ROW;i++)
			{
				for(int j=0;j<Paopao.PAOPAO_COL;j++)
				{
					gameStage[j][i][0] = bytes[k++][l];
				}
				k=0;
				l++;
			}
		}
		
		//初始化数组的挂点层
		for (int i=0;i<Paopao.PAOPAO_COL-1;i++)
		{
			gameStage[i][0][2] = 1;
		}
		
		return gameStage;
	}
	
	/**
	* 初始化gameStageDate数组
	*/
	private byte[][] initializtionGameStageDate()
	{
		byte[][] gameStageDate=new byte[Paopao.PAOPAO_COL][Paopao.PAOPAO_ROW];
		for (int i=0;i<Paopao.PAOPAO_ROW;i++)
		{
			for (int j=0;j<Paopao.PAOPAO_COL;j++)
			{
				if (i%2!=0 && j==Paopao.PAOPAO_COL-1)
				{
					gameStageDate[j][i]=1;
				}
				else
				{
					gameStageDate[j][i]=0;
				}
			}
		}
		return gameStageDate;
	}
	
	/**
	* 添加游戏关卡，本方法将新增的关卡添加在尾部
	*/
	public boolean addGameStageDate(byte[][] bytes)
	{
		try
		{
			//将二维数组转换成一维数组
			byte tempBytes[]=new byte[Paopao.STAGE_LENGTH];
			int k=0;
			for (int i=0;i<Paopao.PAOPAO_ROW;i++)
			{
				for (int j=0;j<Paopao.PAOPAO_COL;j++)
				{
					tempBytes[k++]=bytes[j][i];
				}
			}
			
			//打开配置文件并保存
			RecordStore rs=openRS(Paopao.GAME_STAGE_FILE_NAME);
			if (rs!=null)
			{
				//添加记录
				rs.addRecord(tempBytes,0,tempBytes.length);
				closeRS(rs);
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			System.out.println("添加游戏关卡失败");
			return false;
		}
		return true;
	}
	
	/**
    * 删除指定游戏关卡
    */
    public boolean delGameStageDate(int stageIndex)
    {
		try
		{
			RecordStore rs=openRS(Paopao.GAME_STAGE_FILE_NAME);
			if (rs!=null)
			{
				//删除记录
				rs.deleteRecord(stageIndex);
				closeRS(rs);
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			System.out.println("删除游戏关卡失败");
			return false;
		}
		return true;
	}
	
	/**
    * 修改指定游戏关卡
    */
    public boolean setGameStageDate(byte[][] gameStageDate,int stageIndex)
    {
		try
		{
			//将二维数组转换成一维数组
			byte tempBytes[]=new byte[Paopao.STAGE_LENGTH];
			int k=0;
			for (int i=0;i<Paopao.PAOPAO_ROW;i++)
			{
				for (int j=0;j<Paopao.PAOPAO_COL;j++)
				{
					tempBytes[k++]=gameStageDate[j][i];
				}
			}
			
			RecordStore rs=openRS(Paopao.GAME_STAGE_FILE_NAME);
			if (rs!=null)
			{
				//修改记录
				rs.setRecord(stageIndex,tempBytes,0,tempBytes.length);
				closeRS(rs);
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			System.out.println("修改游戏关卡失败");
			return false;
		}
		return true;
	}
}