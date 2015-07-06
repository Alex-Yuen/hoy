package net.xland.aqq.service;

import net.xland.util.Converts;

public class PacketContent {
	private String content;
	
	public PacketContent(String content){
		this.content = content;
	}

	public byte[] toByteArray(){
		return Converts.hexStringToByte(this.content.replaceAll(" ", ""));
	}
}
