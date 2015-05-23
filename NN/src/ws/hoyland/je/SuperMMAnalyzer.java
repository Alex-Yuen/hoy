package ws.hoyland.je;

import java.io.ByteArrayOutputStream;
import java.io.Reader;

import org.apache.lucene.analysis.TokenStream;

import jeasy.analysis.MMAnalyzer;

public class SuperMMAnalyzer extends MMAnalyzer {
	private ByteArrayOutputStream bos;
	public SuperMMAnalyzer(ByteArrayOutputStream bos) {
		super();
		this.bos = bos;
	}

	public SuperMMAnalyzer(int num) {
		super(num);
	}

	public TokenStream tokenStream(String fieldName, Reader reader) {		
//		System.out.println("fn:"+reader);
//		return super.tokenStream(fieldName, reader);
		return new SuperMMFilter(super.tokenStream(fieldName, reader), bos);
	}
}
