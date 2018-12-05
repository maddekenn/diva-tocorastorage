package se.uu.ub.cora.diva.tocorastorage.db;

import java.util.Map;

import se.uu.ub.cora.bookkeeper.data.DataGroup;

public interface DivaDbToCoraConverter {
	DataGroup fromMap(Map<String, String> map);

}
