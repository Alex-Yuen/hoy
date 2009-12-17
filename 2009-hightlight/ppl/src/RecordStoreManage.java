import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import javax.microedition.io.*;

/**
* ��Ϸ�洢��
*/
public class RecordStoreManage
{
	private RecordStore rs=null;
	
	/********************************************************************************************************/
	/**
	* �����ݲֿ�
	*/
	private RecordStore openRS(String recordStoreName) 
	{
		RecordStore rs = null;
		
		//���Ʋ��ܴ���32���ַ�
		if (recordStoreName.length()>32) return null;
		try 
		{
			rs=RecordStore.openRecordStore(recordStoreName, true);
			return rs;
		} 
		catch (Exception e) 
		{
			System.out.println("�����ݲֿ�ʧ��");
			return null;
		}
	}
	
	/**
	* �ر����ݲֿ�
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
	* ɾ�����ݲֿ�
	*/
	public boolean deleteRS(String recordStoreName) 
	{
		//���Ʋ��ܴ���32���ַ�
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
	* ����Ƿ��м�¼
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
	* ��Ϸ����                                                                                             *
	********************************************************************************************************/
	/**
	* ��ȡ��Ϸ������Ϣ
	*/
	public void readGameSetInfo()
	{
		try 
		{
			//����Ϸ�����ļ�
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
			System.out.println("��ȡ��Ϸ������Ϣʧ��");
		}
	}
	
	/**
	* ������Ϸ������Ϣ
	*/
	public boolean saveGameSetInfo(int type)
	{
		try
		{
			//����Ϸ������Ϣд�������
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
			
			//�������ļ�������
			RecordStore rs=openRS(Paopao.PAOPAO_SETNAME);
			if (rs!=null)
			{
				//�ļ��ﻹû�м�¼������Ӽ�¼
				if (type==0) rs.addRecord(data,0,data.length);
				//���ڼ�¼�����޸ļ�¼
				else rs.setRecord(1,data,0,data.length);
				closeRS(rs);
				System.out.println("�洢��Ϸ������Ϣ�ɹ�");
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			System.out.println("�洢��Ϸ������Ϣʧ��");
			return false;
		}
	}
	
	/*******************************************************************************************************
	* ��Ϸ���а�                                                                                           *
	********************************************************************************************************/
	/**
	* ��ȡ��Ϸ�������а���Ϣ
	*/
	public String readGameRankInfo()
	{
		String rankInfo="";
		byte[] data;
		try 
		{
			//����Ϸ�����ļ�
			RecordStore rs=openRS(Paopao.PAOPAOR_ANKNAME);
			if (rs!=null)
			{
				if (isStore(rs)>0)
				{
					//ʹ��RecordComparator��ö�ٶ����ڵļ�¼��������
					RecordComparator comparator=new RecordComparator()
					{
						public int compare(byte[] first,byte[] second)
						{
							try
							{
								DataInputStream isFirst=new DataInputStream(new ByteArrayInputStream(first));
								DataInputStream isSecond=new DataInputStream(new ByteArrayInputStream(second));
								//�Ӵ�С��˳��
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
								//�޷���ȡ��¼����Ϊ���
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
			System.out.println("��ȡ��Ϸ�������а���Ϣʧ��");
			return rankInfo;
		}
		return rankInfo;
	}
	
	/**
	* �������а���Ϣ
	*/
	public void saveGameRankInfo(int score)
	{
		
		
	}
	
	/*******************************************************************************************************
	* ��Ϸ�ؿ�                                                                                             *
	********************************************************************************************************/
	/**
	* ��ȡָ����Ϸ�ؿ�����
	*/
	public byte[][] readGameStageDate(int stageIndex)
	{
		//���������¼�ؿ�
		byte[][] bytes=new byte[Paopao.PAOPAO_COL][Paopao.PAOPAO_ROW];
		
		try 
		{
			//����Ϸ�ؿ��ļ�
			RecordStore rs=openRS(Paopao.GAME_STAGE_FILE_NAME);
			if (rs!=null)
			{
				//��ȡ�ܹؿ���
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
			System.out.println("��ȡ��Ϸ�ؿ�ʧ��");
		}
		
		return bytes;
	}
	
	/**
	* ��ȡ�ؿ�����
	*/
	public int readStageTotal()
	{
		//����Ϸ�ؿ��ļ�
		RecordStore rs=openRS(Paopao.GAME_STAGE_FILE_NAME);
		if (rs!=null)
		{
			//��ȡ�ܹؿ���
			int stageTotal=isStore(rs);
			closeRS(rs);
			return stageTotal;
		}
		return 0;
	}
	/**
	* �洢��Ϸ�ؿ�
	*/
	public void saveGameStageDate(byte[] bytes)
	{
		try
		{
			//�������ļ�������
			RecordStore rs=openRS(Paopao.GAME_STAGE_FILE_NAME);
			if (rs!=null)
			{
				//��Ӽ�¼
				rs.addRecord(bytes,0,bytes.length);
				
				closeRS(rs);
			}
		}
		catch (Exception e)
		{
			System.out.println("�洢��Ϸ�ؿ�ʧ��");
		}
	}
	
	/**
    * ��ָ���ؿ��ļ��н��ؿ����ݶ�ȡ�������洢��RMS�ļ���
    */
    public void readAndSave()
    {
		//���������¼�ؿ�
		byte[] bytes=new byte[Paopao.STAGE_LENGTH];
		
		try
		{
			DataInputStream dis=new DataInputStream(RecordStoreManage.class.getResourceAsStream("/level.dat"));
			Paopao.gameStageTotal=dis.read();
			byte[] tempBytes=new byte[Paopao.gameStageTotal*100];
			
			//һ���Խ�������ͼ���ݶ���������
			dis.read(tempBytes);
			
			//��������
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
				System.out.println("��ȡ������Ĭ����Ϸ�ؿ����ݳɹ���");
			}
		}
		catch (Exception e)
		{
			System.out.println("�����ļ�����");
		}
	}
	
	/**
	* ��ȡ��Ϸ�ؿ���stageIndexΪҪ��ȡ�Ĺؿ�
	*/
 	public byte[][][] loadGameStage(int stageIndex) 
	{
		//������Ϸ�ؿ�����
		byte[][] bytes=readGameStageDate(stageIndex);
		byte[][][] gameStage=new byte[Paopao.PAOPAO_COL][Paopao.PAOPAO_ROW+4][3];
		
		//�ж���Ҫ��ȡ�Ĺؿ��治����
		if (Paopao.gameStageTotal==0 || Paopao.gameStageTotal<stageIndex)
		{
			//�����ڹؿ��������ȡһ���յĹؿ�
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
			//��ָ���ؿ�����ؿ�������
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
		
		//��ʼ������Ĺҵ��
		for (int i=0;i<Paopao.PAOPAO_COL-1;i++)
		{
			gameStage[i][0][2] = 1;
		}
		
		return gameStage;
	}
	
	/**
	* ��ʼ��gameStageDate����
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
	* �����Ϸ�ؿ����������������Ĺؿ������β��
	*/
	public boolean addGameStageDate(byte[][] bytes)
	{
		try
		{
			//����ά����ת����һά����
			byte tempBytes[]=new byte[Paopao.STAGE_LENGTH];
			int k=0;
			for (int i=0;i<Paopao.PAOPAO_ROW;i++)
			{
				for (int j=0;j<Paopao.PAOPAO_COL;j++)
				{
					tempBytes[k++]=bytes[j][i];
				}
			}
			
			//�������ļ�������
			RecordStore rs=openRS(Paopao.GAME_STAGE_FILE_NAME);
			if (rs!=null)
			{
				//��Ӽ�¼
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
			System.out.println("�����Ϸ�ؿ�ʧ��");
			return false;
		}
		return true;
	}
	
	/**
    * ɾ��ָ����Ϸ�ؿ�
    */
    public boolean delGameStageDate(int stageIndex)
    {
		try
		{
			RecordStore rs=openRS(Paopao.GAME_STAGE_FILE_NAME);
			if (rs!=null)
			{
				//ɾ����¼
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
			System.out.println("ɾ����Ϸ�ؿ�ʧ��");
			return false;
		}
		return true;
	}
	
	/**
    * �޸�ָ����Ϸ�ؿ�
    */
    public boolean setGameStageDate(byte[][] gameStageDate,int stageIndex)
    {
		try
		{
			//����ά����ת����һά����
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
				//�޸ļ�¼
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
			System.out.println("�޸���Ϸ�ؿ�ʧ��");
			return false;
		}
		return true;
	}
}