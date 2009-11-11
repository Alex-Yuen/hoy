package it.hoyland.sclottery.util;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

public class RMSUtil {
	private String name;
	private Hashtable store;

	public RMSUtil(String name) {
		this.name = name;
		this.store = new Hashtable();
		load();
	}

	public final String getString(String key) {
		return (String) this.store.get(key);
	}

	public final void setString(String key, String value) {
		if (key == null) {
			key = "";
		}
		this.store.put(key, value);
	}

	private void load() {
		try {
			RecordStore recordstore = RecordStore.openRecordStore(this.name, true);
			RecordEnumeration recordenumeration = recordstore.enumerateRecords(null, null, false);

			if (recordstore != null && recordenumeration != null) {
				byte[] record;
				int i = -1;
				String key;
				String value;

				for (; recordenumeration.hasNextElement(); setString(key, value)) {
					record = recordenumeration.nextRecord();
					value = new String(record);
					i = value.indexOf('|');
					key = value.substring(0, i);
					value = value.substring(i + 1);
				}

				recordenumeration.destroy();
				recordstore.closeRecordStore();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public final void save() {
		try {
			RecordStore recordstore = RecordStore .openRecordStore(this.name, true);
			RecordEnumeration recordenumeration = recordstore.enumerateRecords(null, null, false);
			
			if (recordstore != null && recordenumeration != null) {
				byte[] record;
				int i = -1;
				String key;
				String value;
				
				for (; recordenumeration.hasNextElement(); recordstore.deleteRecord(i)) {
					i = recordenumeration.nextRecordId();
				}
			
				for (Enumeration enumeration = this.store.keys(); enumeration.hasMoreElements(); recordstore.addRecord(record, 0, record.length)) {
					key = (String) enumeration.nextElement();
					value = getString(key);
					record = (key + "|" + value).getBytes();
				}

				recordenumeration.destroy();
				recordstore.closeRecordStore();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
