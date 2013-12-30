package ws.hoyland.qqol;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;
import java.util.Map;

import javax.crypto.KeyAgreement;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.math.ec.ECPoint;

import sun.security.ec.NamedCurve;
import ws.hoyland.util.Configuration;
import ws.hoyland.util.Converts;
import ws.hoyland.util.Crypter;

public class Task implements Runnable {
	public final static byte TYPE_0825 = 0x01;

	private byte type;
	private String ip = "183.60.19.100";// 默认IP
	private byte[] ips = new byte[] { (byte) 183, (byte) 60, (byte) 19,
			(byte) 100 };

	//private byte[] buffer;

	private ByteArrayOutputStream baos = null;
	private ByteArrayOutputStream bsofplain = null;
	private Crypter crypter = new Crypter();
	
	private int id = 0;
	private String account = null;
	private String password = null;
	private int status = 1;
	private byte[] seq = null;
	private byte[] encrypt = null;
	private Map<String, byte[]> details = null;
	
	public Task(byte type, String account) {
		this.type = type;
		this.account = account;
		
		this.details = Engine.getInstance().getAcccounts().get(account);
		
		this.id = Integer.parseInt(new String(details.get("id")));
		this.password = new String(details.get("password"));
		this.status = Integer.parseInt(Configuration.getInstance().getProperty("LOGIN_TYPE"));
		this.seq = Util.genKey(2);
	}

	public void setIP(byte[] ips){
		this.ip = (ips[0]&0xFF)+"."+(ips[1]&0xFF)+"."+(ips[2]&0xFF)+"."+(ips[3]&0xFF);
		this.ips = ips;		
	}
	
	@Override
	public void run() {
		// 发送UDP数据
		switch (type) {
		case TYPE_0825:
			try {
				byte[] serverPBK = new byte[] { 0x04, (byte) 0x92, (byte) 0x8D,
						(byte) 0x88, 0x50, 0x67, 0x30, (byte) 0x88,
						(byte) 0xB3, 0x43, 0x26, 0x4E, 0x0C, 0x6B, (byte) 0xAC,
						(byte) 0xB8, 0x49, 0x6D, 0x69, 0x77, (byte) 0x99,
						(byte) 0xF3, 0x72, 0x11, (byte) 0xDE, (byte) 0xB2,
						0x5B, (byte) 0xB7, 0x39, 0x06, (byte) 0xCB, 0x08,
						(byte) 0x9F, (byte) 0xEA, (byte) 0x96, 0x39,
						(byte) 0xB4, (byte) 0xE0, 0x26, 0x04, (byte) 0x98,
						(byte) 0xB5, 0x1A, (byte) 0x99, 0x2D, 0x50,
						(byte) 0x81, (byte) 0x3D, (byte) 0xA8 };
				byte[] key0825 = Util.genKey(0x10);
				
				// ecdhkey 随机生成的公钥，然后key0836x 即为共享秘密，由算法计算得出

				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

				ECParameterSpec ecSpec = NamedCurve
						.getECParameterSpec("secp192k1");

				KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDH",
						"BC");
				keyGen.initialize(ecSpec, new SecureRandom()); // 公私钥 工厂
				KeyPair pair = keyGen.generateKeyPair(); // 生成公私钥
				BCECPublicKey cpk = (BCECPublicKey) pair.getPublic();
				ECPoint.Fp point = (ECPoint.Fp) cpk.getQ();

				byte[] ecdhkey = point.getEncoded(true); // ecdhkey
				// System.out.println(ecdhkey.length);

				java.security.spec.ECPoint sp = ECPointUtil.decodePoint(
						ecSpec.getCurve(), serverPBK);
				KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
				ECPublicKeySpec pubSpec = new ECPublicKeySpec(sp, ecSpec);
				ECPublicKey myECPublicKey = (ECPublicKey) kf
						.generatePublic(pubSpec);

				// System.out.println(((BCECPublicKey)myECPublicKey).getQ().getEncoded().length);

				KeyAgreement agreement = KeyAgreement.getInstance("ECDH", "BC");
				agreement.init(pair.getPrivate());
				agreement.doPhase(myECPublicKey, true);
				//
				byte[] xx = agreement.generateSecret();
				byte[] key0836x = new byte[16]; // key0836x
				for (int i = 0; i < key0836x.length; i++) {
					key0836x[i] = xx[i];
				}

				baos = new ByteArrayOutputStream();
				baos.write(new byte[] { 0x02, 0x34, 0x4B, 0x08, 0x25 });
				baos.write(seq);
				baos.write(Converts.hexStringToByte(Long.toHexString(
						Long.valueOf(account)).toUpperCase()));
				baos.write(new byte[] { 0x03, 0x00, 0x00, 0x00, 0x01, 0x01,
						0x01, 0x00, 0x00, 0x66, (byte) 0xA2, 0x00, 0x00, 0x00,
						0x00 });
				baos.write(key0825);
				// System.err.println(Converts.bytesToHexString(rndkey));
				// System.out.println(Converts.bytesToHexString(baos.toByteArray()));
				// 以下需要加密
				bsofplain = new ByteArrayOutputStream();
				bsofplain.write(new byte[] { 0x00, 0x18, 0x00, 0x16, 0x00,
						0x01, 0x00, 0x00, 0x04, 0x36, 0x00, 0x00, 0x00, 0x01,
						0x00, 0x00, 0x14, (byte) 0x9B });
				bsofplain.write(Converts.hexStringToByte(Long.toHexString(
						Long.valueOf(account)).toUpperCase()));
				bsofplain.write(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x03,
						0x09, 0x00, 0x08, 0x00, 0x01 });
				bsofplain.write(ips);
				// bsofplain.write(new byte[]{
				// 0x00, 0x00, 0x00, 0x00
				// });
				bsofplain.write(new byte[] {
						// 0x00, 0x04, 0x00, 0x36, 0x00, 0x12, 0x00, 0x02, 0x00,
						// 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
						0x00, 0x02, 0x00, 0x36, 0x00, 0x12, 0x00, 0x02, 0x00,
						0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x14, 0x00,
						0x1D, 0x01, 0x02 });
				bsofplain.write(new byte[] { 0x00, 0x19 });
				bsofplain.write(ecdhkey);
				// System.out.println(Converts.bytesToHexString(bsofplain.toByteArray()));
				byte[] encrypt = crypter.encrypt(bsofplain.toByteArray(), key0825);
				// 加密完成
				// byte[] ts = crypter.decrypt(encrypt, rndkey);
				// System.out.println(Converts.bytesToHexString(ts));
				baos.write(encrypt);
				baos.write(new byte[] { 0x03 });
				//buffer = baos.toByteArray();
				
				this.details.put("key0825", key0825);
				this.details.put("ecdhkey", ecdhkey);
				this.details.put("key0836x", key0836x);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

		try {
			DatagramChannel dc = Engine.getInstance().getChannels().get(this.account);
			if(dc==null){
				SocketAddress sa = new InetSocketAddress(ip, 8000);
				dc = DatagramChannel.open();
				dc.configureBlocking(false);
				dc.connect(sa);
				
				Monitor.getInstance().setWakeup(true);
				QQSelector.selector.wakeup();
				dc.register(QQSelector.selector, SelectionKey.OP_READ);
				Monitor.getInstance().setWakeup(false);
				
				Engine.getInstance().getChannels().put(this.account, dc);
			}
						
			dc.write(ByteBuffer.wrap(baos.toByteArray()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
