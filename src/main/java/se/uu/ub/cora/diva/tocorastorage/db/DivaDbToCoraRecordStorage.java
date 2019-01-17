/*
 * Copyright 2018, 2019 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.diva.tocorastorage.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.diva.tocorastorage.NotImplementedException;
import se.uu.ub.cora.spider.data.SpiderReadResult;
import se.uu.ub.cora.spider.record.storage.RecordStorage;
import se.uu.ub.cora.sqldatabase.RecordReader;
import se.uu.ub.cora.sqldatabase.RecordReaderFactory;

public class DivaDbToCoraRecordStorage implements RecordStorage {

	private RecordReaderFactory recordReaderFactory;
	private DivaDbToCoraConverterFactory converterFactory;
	private RecordReader recordReader;

	private DivaDbToCoraRecordStorage(RecordReaderFactory recordReaderFactory,
			DivaDbToCoraConverterFactory converterFactory) {
		this.recordReaderFactory = recordReaderFactory;
		this.converterFactory = converterFactory;
	}

	public static DivaDbToCoraRecordStorage usingRecordReaderFactoryAndConverterFactory(
			RecordReaderFactory recordReaderFactory,
			DivaDbToCoraConverterFactory converterFactory) {
		return new DivaDbToCoraRecordStorage(recordReaderFactory, converterFactory);
	}

	@Override
	public DataGroup read(String type, String id) {
		if ("divaOrganisation".equals(type)) {
			recordReader = recordReaderFactory.factor();
			DataGroup organisation = readAndConvertOrganisationFromDb(type, id);

			tryToReadAndConvertPredecessors(id, organisation);
			Map<String, String> conditions = new HashMap<>();
			conditions.put("predecessor_id", id);
			List<Map<String, String>> successors = recordReader
					.readFromTableUsingConditions("divaOrganisationPredecessors", conditions);
			if (successors != null && !successors.isEmpty()) {
				int repeatId = 0;
				for (Map<String, String> successorsValues : successors) {
					DivaDbToCoraConverter successorsConverter = converterFactory
							.factor("divaOrganisationSuccessor");
					DataGroup convertedSuccessor = successorsConverter.fromMap(successorsValues);
					convertedSuccessor.setRepeatId(String.valueOf(repeatId));
					organisation.addChild(convertedSuccessor);
					repeatId++;
				}
			}
			return organisation;

		}
		throw NotImplementedException.withMessage("read is not implemented for type: " + type);
	}

	private void tryToReadAndConvertPredecessors(String id, DataGroup organisation) {
		Map<String, String> conditions = new HashMap<>();
		conditions.put("organisation_id", id);
		List<Map<String, String>> predecessors = recordReader
				.readFromTableUsingConditions("divaOrganisationPredecessors", conditions);

		possiblyConvertPredecessors(organisation, predecessors);
	}

	private DataGroup readAndConvertOrganisationFromDb(String type, String id) {
		Map<String, String> readRow = readOneRowFromDbUsingTypeAndId(type, id);
		return convertOneMapFromDbToDataGroup(type, readRow);
	}

	private Map<String, String> readOneRowFromDbUsingTypeAndId(String type, String id) {
		Map<String, String> conditions = new HashMap<>();
		conditions.put("id", id);
		return recordReader.readOneRowFromDbUsingTableAndConditions(type, conditions);
	}

	private DataGroup convertOneMapFromDbToDataGroup(String type, Map<String, String> readRow) {
		DivaDbToCoraConverter dbToCoraConverter = converterFactory.factor(type);
		return dbToCoraConverter.fromMap(readRow);
	}

	private void possiblyConvertPredecessors(DataGroup organisation,
			List<Map<String, String>> predecessors) {
		if (organisationHasPredecessors(predecessors)) {
			convertAndAddPredecessors(organisation, predecessors);
		}
	}

	private boolean organisationHasPredecessors(List<Map<String, String>> predecessors) {
		return predecessors != null && !predecessors.isEmpty();
	}

	private void convertAndAddPredecessors(DataGroup organisation,
			List<Map<String, String>> predecessors) {
		int repeatId = 0;
		for (Map<String, String> predecessorValues : predecessors) {
			convertAndAddPredecessor(organisation, repeatId, predecessorValues);
			repeatId++;
		}
	}

	private void convertAndAddPredecessor(DataGroup organisation, int repeatId,
			Map<String, String> predecessorValues) {
		DivaDbToCoraConverter predecessorConverter = converterFactory
				.factor("divaOrganisationPredecessor");
		DataGroup predecessor = predecessorConverter.fromMap(predecessorValues);
		predecessor.setRepeatId(String.valueOf(repeatId));
		organisation.addChild(predecessor);
	}

	@Override
	public void create(String type, String id, DataGroup record, DataGroup collectedTerms,
			DataGroup linkList, String dataDivider) {
		throw NotImplementedException.withMessage("create is not implemented");
	}

	@Override
	public void deleteByTypeAndId(String type, String id) {
		throw NotImplementedException.withMessage("deleteByTypeAndId is not implemented");
	}

	@Override
	public boolean linksExistForRecord(String type, String id) {
		throw NotImplementedException.withMessage("linksExistForRecord is not implemented");
	}

	@Override
	public void update(String type, String id, DataGroup record, DataGroup collectedTerms,
			DataGroup linkList, String dataDivider) {
		throw NotImplementedException.withMessage("update is not implemented");

	}

	@Override
	public SpiderReadResult readList(String type, DataGroup filter) {
		if ("divaOrganisation".equals(type)) {
			List<Map<String, String>> rowsFromDb = readAllFromDb(type);
			return createSpiderReadResultFromDbData(type, rowsFromDb);
		}
		throw NotImplementedException.withMessage("readList is not implemented for type: " + type);
	}

	private List<Map<String, String>> readAllFromDb(String type) {
		RecordReader recordReader = recordReaderFactory.factor();
		return recordReader.readAllFromTable(type);
	}

	private SpiderReadResult createSpiderReadResultFromDbData(String type,
			List<Map<String, String>> rowsFromDb) {
		SpiderReadResult spiderReadResult = new SpiderReadResult();
		spiderReadResult.listOfDataGroups = convertListOfMapsFromDbToDataGroups(type, rowsFromDb);
		return spiderReadResult;
	}

	private List<DataGroup> convertListOfMapsFromDbToDataGroups(String type,
			List<Map<String, String>> readAllFromTable) {
		List<DataGroup> convertedList = new ArrayList<>();
		for (Map<String, String> map : readAllFromTable) {
			DataGroup convertedGroup = convertOneMapFromDbToDataGroup(type, map);
			convertedList.add(convertedGroup);
		}
		return convertedList;
	}

	@Override
	public SpiderReadResult readAbstractList(String type, DataGroup filter) {
		throw NotImplementedException.withMessage("readAbstractList is not implemented");
	}

	@Override
	public DataGroup readLinkList(String type, String id) {
		throw NotImplementedException.withMessage("readLinkList is not implemented");
	}

	@Override
	public Collection<DataGroup> generateLinkCollectionPointingToRecord(String type, String id) {
		throw NotImplementedException
				.withMessage("generateLinkCollectionPointingToRecord is not implemented");
	}

	@Override
	public boolean recordsExistForRecordType(String type) {
		throw NotImplementedException.withMessage("recordsExistForRecordType is not implemented");
	}

	@Override
	public boolean recordExistsForAbstractOrImplementingRecordTypeAndRecordId(String type,
			String id) {
		throw NotImplementedException.withMessage(
				"recordExistsForAbstractOrImplementingRecordTypeAndRecordId is not implemented");
	}

}
