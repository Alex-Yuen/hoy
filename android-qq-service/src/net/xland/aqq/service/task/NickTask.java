package net.xland.aqq.service.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.xland.aqq.service.PacketContent;
import net.xland.aqq.service.PacketSender;
import net.xland.aqq.service.Task;
import net.xland.util.Converts;
import net.xland.util.XLandUtil;

public class NickTask extends Task {
	private String nick = null;
	private static Logger logger = LogManager.getLogger(PacketSender.class.getName());
	
	public NickTask(String sid, String nick) {
		this.sid = sid;
		this.nick = nick;
	}

	@Override
	public void run() {
		try{
			this.session = this.server.getSession(this.sid);
			this.session.put("x-cmd", "nick");

			byte[] ecdhkey = (byte[])this.session.get("x-ek");
			byte[] sharekey = (byte[])this.session.get("x-sk");
			byte[] flag = (byte[])this.session.get("x-flag");
			byte[] xkey = (byte[])this.session.get("x-xkey");
			byte[] bf = (byte[])this.session.get("x-bf"); //bind flag
			byte[] codekey = (byte[])this.session.get("x-ck");
			String mobile = (String)session.get("x-mobile");
			
			String password = XLandUtil.genPassword(); 
			this.session.put("x-pwd", password);
			
			logger.info(sid+" [SET-PASSWORD] "+password);
			
			bos.reset();
			bos.write(new PacketContent("1B").toByteArray());
			bos.write(("86"+mobile).getBytes());
			bos.write(new PacketContent("2D 38 36 2D").toByteArray());
			bos.write(bf);
			bos.write((byte)password.length());
			bos.write(password.getBytes());
			bos.write(new PacketContent("05 05 01 0E").toByteArray());
			bos.write(("86-"+mobile).getBytes());
			bos.write(new PacketContent("05 0A").toByteArray());
			bos.write(new PacketContent("71 71 70 61 73 73 70 6F 72 74").toByteArray());
			bos.write(new PacketContent("06").toByteArray());
			bos.write((byte)this.nick.length());
			bos.write(this.nick.getBytes());
			bos.write(new PacketContent("08 01 01 0F 04 00 00 00 00").toByteArray());
			content = cryptor.encrypt(bos.toByteArray(), codekey); //第一次加密
			
			bos.reset();
			bos.write(new PacketContent("00").toByteArray());
			bos.write(Converts.short2Byte((short)(content.length+0x2E)));
			bos.write(new PacketContent("00 00 00 00 00 00 00 5F 00 00 00").toByteArray());
			bos.write(Converts.int2Byte((int)(System.currentTimeMillis()/1000)));
			bos.write(new PacketContent("02").toByteArray());
			bos.write(Converts.short2Byte((short)(content.length+0x2A)));
			bos.write(new PacketContent("00 01 00 06 04 00 00 00 00").toByteArray());
			bos.write(new PacketContent("1B").toByteArray());
			bos.write(("86"+mobile).getBytes());
			bos.write(new PacketContent("2D 38 36 2D").toByteArray());
			bos.write(bf);
			bos.write((byte)content.length);
			bos.write(content);
			bos.write(new PacketContent("03").toByteArray());			
			content = cryptor.encrypt(bos.toByteArray(), sharekey); //第二次加密
			
			bos.reset();
			bos.write(new PacketContent("00 00 00 74 00 00").toByteArray());
			bos.write(Converts.short2Byte(this.seq));
			bos.write(new PacketContent("20 02 9D EC 20 02 9D EC").toByteArray());
			bos.write(new PacketContent("67 00 00 00 00 00 00 00 00 00 00 00 ").toByteArray());
			bos.write(new PacketContent("00 00 00 04").toByteArray());
			bos.write(new PacketContent("00 00 00 15").toByteArray());
			bos.write(new PacketContent("77 74 6C 6F 67 69 6E 2E 74 72 61 6E 73 5F 65 6D 70").toByteArray());
			bos.write(new PacketContent("00 00 00 08").toByteArray());
			bos.write(flag);
			bos.write(new PacketContent("00 00 00 13").toByteArray());
			bos.write(new PacketContent("30 30 30 30 30 30 30 30 30 30 30 30 30 30 30").toByteArray());
			bos.write(new PacketContent("00 00 00 04").toByteArray());
			bos.write(new PacketContent("00 20").toByteArray());
			bos.write(new PacketContent("7C 33 31 30 32 36 30 30 30 30 30 30 30 30 30 30").toByteArray());
			bos.write(new PacketContent("7C 41 35 2E 37 2E 32 2E 31 34 38 33 32 31").toByteArray());
			bos.write(new PacketContent("00 00").toByteArray());
			bos.write(Converts.short2Byte((short)(content.length+0x50)));
			bos.write(new PacketContent("02").toByteArray());
			bos.write(Converts.short2Byte((short)(content.length+0x4C)));
			bos.write(new PacketContent("1F 41 08 12").toByteArray());
			bos.write(new PacketContent("00 01").toByteArray());
			bos.write(new PacketContent("00 00 00 00").toByteArray());
			bos.write(new PacketContent("03 07 00 00 00").toByteArray());
			bos.write(new PacketContent("00 02").toByteArray());
			bos.write(new PacketContent("00 00 00 00").toByteArray());
			bos.write(new PacketContent("00 00 00 00").toByteArray());
			bos.write(new PacketContent("01 01").toByteArray());
			bos.write(xkey);
			bos.write(new PacketContent("01 02").toByteArray());
			bos.write(new PacketContent("00 19").toByteArray());
			bos.write(ecdhkey);
			bos.write(content);
			bos.write(new PacketContent("03").toByteArray());
			content = cryptor.encrypt(bos.toByteArray(), outterkey); //第三次加密
			
			bos.reset();
			bos.write(new PacketContent("00 00").toByteArray());
			bos.write(Converts.short2Byte((short)(content.length+0x13)));
			bos.write(new PacketContent("00 00 00 08 02 00 00 00 04 00 00 00").toByteArray());
			bos.write(new PacketContent("00 05 30").toByteArray());
			bos.write(content);			
			content = bos.toByteArray();
			
			submit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
