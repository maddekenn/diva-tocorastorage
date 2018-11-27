package se.uu.ub.cora.diva.tocorastorage.db;

import java.util.List;
import java.util.Map;

import se.uu.ub.cora.sqldatabase.RecordReader;

public class RecordReaderSpy implements RecordReader {

	public String usedTableName;

	@Override
	public List<Map<String, String>> readAllFromTable(String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> readOneRowFromDbUsingTableAndConditions(String tableName,
			Map<String, String> conditions) {
		usedTableName = tableName;

		return null;
	}

}
