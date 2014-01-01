package ws.hoyland.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CopiedIterator implements Iterator<String> {
	private Iterator<?> iterator = null;

	public CopiedIterator(Iterator<?> itr) {
		List<String> list = new LinkedList<String>();
		while (itr.hasNext()) {
			list.add((String)itr.next());
		}
		this.iterator = list.iterator();
	}

	public boolean hasNext() {
		return this.iterator.hasNext();
	}

	public void remove( ) {
	  throw new UnsupportedOperationException("This is a read-only iterator.");
	}

	public String next() {
		return (String)this.iterator.next();
	}
}