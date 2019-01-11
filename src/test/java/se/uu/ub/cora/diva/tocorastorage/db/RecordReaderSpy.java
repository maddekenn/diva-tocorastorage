package se.uu.ub.cora.diva.tocorastorage.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.sqldatabase.RecordReader;

public class RecordReaderSpy implements RecordReader {

	public String usedTableName;
	public List<String> usedTableNames = new ArrayList<>();
	public List<Map<String, String>> returnedList = new ArrayList<>();;
	public int noOfRecordsToReturn = 1;
	public Map<String, String> usedConditions;
	public List<Map<String, String>> usedConditionsList = new ArrayList<>();
	public boolean returnPredecessors = false;

	@Override
	public List<Map<String, String>> readAllFromTable(String tableName) {
		usedTableName = tableName;
		usedTableNames.add(usedTableName);
		// returnedList = new ArrayList<>();
		for (int i = 0; i < noOfRecordsToReturn; i++) {
			Map<String, String> map = new HashMap<>();
			map.put("someKey" + i, "someValue" + i);
			returnedList.add(map);
		}
		return returnedList;
	}

	@Override
	public Map<String, String> readOneRowFromDbUsingTableAndConditions(String tableName,
			Map<String, String> conditions) {
		usedTableName = tableName;
		usedTableNames.add(usedTableName);
		usedConditions = conditions;
		usedConditionsList.add(usedConditions);
		Map<String, String> map = new HashMap<>();
		map.put("someKey", "someValue");
		// returnedList = new ArrayList<>();
		returnedList.add(map);
		return map;
	}

	@Override
	public List<Map<String, String>> readFromTableUsingConditions(String tableName,
			Map<String, String> conditions) {
		usedTableName = tableName;
		usedTableNames.add(usedTableName);
		usedConditions = conditions;
		usedConditionsList.add(usedConditions);
		if (!returnPredecessors) {
			return Collections.emptyList();
		}
		// returnedList = new ArrayList<>();
		for (int i = 0; i < noOfRecordsToReturn; i++) {
			Map<String, String> map = new HashMap<>();
			map.put("someKey" + i, "someValue" + i);
			returnedList.add(map);
		}
		return returnedList;
	}

}
