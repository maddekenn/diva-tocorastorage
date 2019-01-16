package se.uu.ub.cora.diva.tocorastorage.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.sqldatabase.RecordReader;

public class RecordReaderSpy implements RecordReader {

	public String usedTableName;
	public List<String> usedTableNames = new ArrayList<>();
	public List<Map<String, String>> returnedList = new ArrayList<>();
	public int noOfRecordsToReturn = 1;
	public Map<String, String> usedConditions;
	public List<Map<String, String>> usedConditionsList = new ArrayList<>();
	public int numOfOredecessorsToReturn = 0;
	public int numOfSuccessorsToReturn = 0;

	public Map<String, String> onwRowRead;
	public List<Map<String, String>> predecessorsToReturn = new ArrayList<>();
	public List<Map<String, String>> successorsToReturn = new ArrayList<>();

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
		onwRowRead = map;
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

		predecessorsToReturn = createListToReturn(numOfOredecessorsToReturn);
		successorsToReturn = createListToReturn(numOfSuccessorsToReturn);

		List<Map<String, String>> listToReturn = new ArrayList<>();
		listToReturn.addAll(predecessorsToReturn);
		listToReturn.addAll(successorsToReturn);

		// if (numOfOredecessorsToReturn == 0) {
		//
		// predecessorsToReturn = Collections.emptyList();
		// return predecessorsToReturn;
		// } else {
		// }
		// if (numOfSuccessorsToReturn == 0) {
		// successorsToReturn = Collections.emptyList();
		// return successorsToReturn;
		// } else {
		// }
		// List<Map<String, String>> listToReturn =
		// createListToReturn(noOfRecordsToReturn);
		if(conditions.containsKey("organisation_id")) {
			return predecessorsToReturn;
		}
		return successorsToReturn;
	}

	private List<Map<String, String>> createListToReturn(int numToReturn) {
		List<Map<String, String>> listToReturn = new ArrayList<>();
		for (int i = 0; i < numToReturn; i++) {
			Map<String, String> map = new HashMap<>();
			map.put("someKey" + i, "someValue" + i);
			returnedList.add(map);
			listToReturn.add(map);
		}
		return listToReturn;
	}

}
