package ws.hoyland.je;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

public class SuperMMFilter extends TokenStream {
	private TokenStream stream;
	private ByteArrayOutputStream bos;
	public SuperMMFilter(TokenStream stream, ByteArrayOutputStream bos){
//		return TokenStream;
//		super(stream);
		this.stream = stream;
		this.bos = bos;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Token next() throws IOException {
		Token token = stream.next();
//		token.startOffset();
		if(token!=null){
			byte[] bs = new byte[token.endOffset()-this.bos.size()];
			bs[bs.length-1] = 1;
//			System.out.println(bs.length);
			this.bos.write(bs);
		}
		return token;
	}
}
