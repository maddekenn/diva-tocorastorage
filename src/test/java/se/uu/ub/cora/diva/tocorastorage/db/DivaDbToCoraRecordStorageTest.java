/*
 * Copyright 2018 Uppsala University Library
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.diva.tocorastorage.NotImplementedException;
import se.uu.ub.cora.spider.data.SpiderReadResult;
import se.uu.ub.cora.spider.record.storage.RecordStorage;

public class DivaDbToCoraRecordStorageTest {
	private static final String TABLE_NAME = "divaOrganisation";
	private DivaDbToCoraRecordStorage divaToCoraRecordStorage;
	private DivaDbToCoraConverterFactorySpy converterFactory;
	private RecordReaderFactorySpy recordReaderFactory;

	@BeforeMethod
	public void BeforeMethod() {
		converterFactory = new DivaDbToCoraConverterFactorySpy();
		recordReaderFactory = new RecordReaderFactorySpy();
		divaToCoraRecordStorage = DivaDbToCoraRecordStorage
				.usingRecordReaderFactoryAndConverterFactory(recordReaderFactory, converterFactory);
	}

	@Test
	public void testInit() throws Exception {
		assertNotNull(divaToCoraRecordStorage);
	}

	@Test
	public void divaToCoraRecordStorageImplementsRecordStorage() throws Exception {
		assertTrue(divaToCoraRecordStorage instanceof RecordStorage);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "read is not implemented for type: null")
	public void readThrowsNotImplementedException() throws Exception {
		divaToCoraRecordStorage.read(null, null);
	}

	@Test
	public void testReadOrgansiationFactorDbReader() throws Exception {
		divaToCoraRecordStorage.read(TABLE_NAME, "someId");
		assertTrue(recordReaderFactory.factorWasCalled);
	}

	@Test
	public void testReadOrgansiationTableRequestedFromReader() throws Exception {
		divaToCoraRecordStorage.read(TABLE_NAME, "someId");
		RecordReaderSpy recordReader = recordReaderFactory.factored;
		assertEquals(recordReader.usedTableNames.get(0), TABLE_NAME);
		assertEquals(recordReader.usedTableNames.get(1), "divaOrganisationPredecessors");

		assertEquals(recordReader.usedTableNames.size(), 2);
	}

	@Test
	public void testReadOrganisationConditionsForOrganisationTable() throws Exception {
		divaToCoraRecordStorage.read(TABLE_NAME, "someId");
		RecordReaderSpy recordReader = recordReaderFactory.factored;
		Map<String, String> conditions = recordReader.usedConditionsList.get(0);
		assertEquals(conditions.get("id"), "someId");
	}

	@Test
	public void testReadOrganisationConverterIsFactored() throws Exception {
		divaToCoraRecordStorage.read(TABLE_NAME, "someId");
		DivaDbToCoraConverter divaDbToCoraConverter = converterFactory.factoredConverters.get(0);
		assertNotNull(divaDbToCoraConverter);
	}

	@Test
	public void testReadOrganisationConverterIsCalledWithDataFromDbStorage() throws Exception {
		divaToCoraRecordStorage.read(TABLE_NAME, "someId");
		RecordReaderSpy recordReader = recordReaderFactory.factored;
		DivaDbToCoraConverterSpy divaDbToCoraConverter = (DivaDbToCoraConverterSpy) converterFactory.factoredConverters
				.get(0);
		assertNotNull(divaDbToCoraConverter.mapToConvert);
		assertEquals(recordReader.returnedList.get(0), divaDbToCoraConverter.mapToConvert);
	}

	@Test
	public void testReadOrganisationCallsDatabaseAndReturnsConvertedResultNoPredecessors()
			throws Exception {
		DataGroup convertedOrganisation = divaToCoraRecordStorage.read(TABLE_NAME, "someId");
		RecordReaderSpy recordReader = recordReaderFactory.factored;
		DivaDbToCoraConverterSpy organisationConverter = (DivaDbToCoraConverterSpy) converterFactory.factoredConverters
				.get(0);
		assertEquals(recordReader.returnedList.size(), 1);
		assertEquals(converterFactory.factoredTypes.get(0), "divaOrganisation");
		Map<String, String> firstReadResult = recordReader.returnedList.get(0);
		Map<String, String> firstMapSentToConverter = organisationConverter.mapToConvert;
		assertEquals(firstReadResult, firstMapSentToConverter);

		assertEquals(converterFactory.factoredTypes.size(), 1);

		assertEquals(convertedOrganisation, organisationConverter.convertedDbDataGroup);
	}

	@Test
	public void testReadOrganisationCallsDatabaseAndReturnsConvertedResultWithOnePredecessor()
			throws Exception {
		recordReaderFactory.returnPredecessors = true;
		DataGroup convertedOrganisation = divaToCoraRecordStorage.read(TABLE_NAME, "someId");
		RecordReaderSpy recordReader = recordReaderFactory.factored;
		DivaDbToCoraConverterSpy organisationConverter = (DivaDbToCoraConverterSpy) converterFactory.factoredConverters
				.get(0);
		assertEquals(recordReader.returnedList.size(), 2);
		assertEquals(converterFactory.factoredTypes.get(0), "divaOrganisation");

		Map<String, String> usedConditions = recordReader.usedConditionsList.get(1);
		assertEquals(usedConditions.get("organisation_id"), "someId");

		Map<String, String> firstReadResult = recordReader.returnedList.get(0);
		Map<String, String> mapSentToFirstConverter = organisationConverter.mapToConvert;
		assertEquals(firstReadResult, mapSentToFirstConverter);

		assertEquals(converterFactory.factoredTypes.get(1), "divaOrganisationPredecessor");

		assertReadDataIsSentToConverterUsingReadIndexAndConverterIndex(recordReader, 1, 1);

		assertTrue(convertedOrganisation.containsChildWithNameInData("from Db converter"));

		List<DataGroup> predecessors = convertedOrganisation
				.getAllGroupsWithNameInData("from Db converter");
		assertEquals(predecessors.get(0).getRepeatId(), "0");
		assertEquals(convertedOrganisation, organisationConverter.convertedDbDataGroup);
	}

	private void assertReadDataIsSentToConverterUsingReadIndexAndConverterIndex(
			RecordReaderSpy recordReader, int readerIndex, int converterIndex) {
		DivaDbToCoraConverterSpy predecessorConverter = (DivaDbToCoraConverterSpy) converterFactory.factoredConverters
				.get(converterIndex);

		Map<String, String> firstPredecessorRead = recordReader.returnedList.get(readerIndex);
		Map<String, String> mapSentToSecondConverter = predecessorConverter.mapToConvert;
		assertEquals(firstPredecessorRead, mapSentToSecondConverter);
	}

	@Test
	public void testReadOrganisationCallsDatabaseAndReturnsConvertedResultWithManyPredecessor()
			throws Exception {
		recordReaderFactory.returnPredecessors = true;
		recordReaderFactory.noOfRecordsToReturn = 3;

		DataGroup convertedOrganisation = divaToCoraRecordStorage.read(TABLE_NAME, "someId");
		RecordReaderSpy recordReader = recordReaderFactory.factored;
		DivaDbToCoraConverterSpy organisationConverter = (DivaDbToCoraConverterSpy) converterFactory.factoredConverters
				.get(0);
		assertEquals(recordReader.returnedList.size(), 4);
		assertEquals(converterFactory.factoredTypes.get(0), "divaOrganisation");

		Map<String, String> usedConditions = recordReader.usedConditionsList.get(1);
		assertEquals(usedConditions.get("organisation_id"), "someId");

		Map<String, String> firstReadResult = recordReader.returnedList.get(0);
		Map<String, String> mapSentToFirstConverter = organisationConverter.mapToConvert;
		assertEquals(firstReadResult, mapSentToFirstConverter);

		assertEquals(converterFactory.factoredTypes.get(1), "divaOrganisationPredecessor");

		assertReadDataIsSentToConverterUsingReadIndexAndConverterIndex(recordReader, 1, 1);
		assertReadDataIsSentToConverterUsingReadIndexAndConverterIndex(recordReader, 2, 2);
		assertReadDataIsSentToConverterUsingReadIndexAndConverterIndex(recordReader, 3, 3);

		assertCorrectRepeatIdInPredecessorUsingIndex(convertedOrganisation, 0);
		assertCorrectRepeatIdInPredecessorUsingIndex(convertedOrganisation, 1);
		assertCorrectRepeatIdInPredecessorUsingIndex(convertedOrganisation, 2);

		assertEquals(convertedOrganisation, organisationConverter.convertedDbDataGroup);
	}

	private void assertCorrectRepeatIdInPredecessorUsingIndex(DataGroup convertedOrganisation,
			int index) {
		List<DataGroup> predecessors = convertedOrganisation
				.getAllGroupsWithNameInData("from Db converter");
		assertEquals(predecessors.get(index).getRepeatId(), String.valueOf(index));
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "create is not implemented")
	public void createThrowsNotImplementedException() throws Exception {
		divaToCoraRecordStorage.create(null, null, null, null, null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "deleteByTypeAndId is not implemented")
	public void deleteByTypeAndIdThrowsNotImplementedException() throws Exception {
		divaToCoraRecordStorage.deleteByTypeAndId(null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "linksExistForRecord is not implemented")
	public void linksExistForRecordThrowsNotImplementedException() throws Exception {
		divaToCoraRecordStorage.linksExistForRecord(null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "update is not implemented")
	public void updateThrowsNotImplementedException() throws Exception {
		divaToCoraRecordStorage.update(null, null, null, null, null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "readList is not implemented for type: null")
	public void readListThrowsNotImplementedException() throws Exception {
		divaToCoraRecordStorage.readList(null, null);
	}

	@Test
	public void testReadOrganisationListFactorDbReader() throws Exception {
		divaToCoraRecordStorage.readList(TABLE_NAME, DataGroup.withNameInData("filter"));
		assertTrue(recordReaderFactory.factorWasCalled);
	}

	@Test
	public void testReadOrganisationListCountryTableRequestedFromReader() throws Exception {
		divaToCoraRecordStorage.readList(TABLE_NAME, DataGroup.withNameInData("filter"));
		RecordReaderSpy recordReader = recordReaderFactory.factored;
		assertEquals(recordReader.usedTableName, TABLE_NAME);
	}

	@Test
	public void testReadOrganisationListConverterIsFactored() throws Exception {
		divaToCoraRecordStorage.readList(TABLE_NAME, DataGroup.withNameInData("filter"));
		DivaDbToCoraConverter divaDbToCoraConverter = converterFactory.factoredConverters.get(0);
		assertNotNull(divaDbToCoraConverter);
	}

	@Test
	public void testReadOrganisationListConverterIsCalledWithDataFromDbStorage() throws Exception {
		divaToCoraRecordStorage.readList(TABLE_NAME, DataGroup.withNameInData("filter"));
		RecordReaderSpy recordReader = recordReaderFactory.factored;
		DivaDbToCoraConverterSpy divaDbToCoraConverter = (DivaDbToCoraConverterSpy) converterFactory.factoredConverters
				.get(0);
		assertNotNull(divaDbToCoraConverter.mapToConvert);
		assertEquals(recordReader.returnedList.get(0), divaDbToCoraConverter.mapToConvert);
	}

	@Test
	public void testReadOrganisationListConverteredIsAddedToList() throws Exception {
		SpiderReadResult spiderReadresult = divaToCoraRecordStorage.readList(TABLE_NAME,
				DataGroup.withNameInData("filter"));
		List<DataGroup> readCountryList = spiderReadresult.listOfDataGroups;
		RecordReaderSpy recordReader = recordReaderFactory.factored;
		DivaDbToCoraConverterSpy divaDbToCoraConverter = (DivaDbToCoraConverterSpy) converterFactory.factoredConverters
				.get(0);
		assertEquals(recordReader.returnedList.size(), 1);
		assertEquals(recordReader.returnedList.get(0), divaDbToCoraConverter.mapToConvert);
		assertEquals(readCountryList.get(0), divaDbToCoraConverter.convertedDbDataGroup);
	}

	@Test
	public void testReadOrganisationListConverteredMoreThanOneIsAddedToList() throws Exception {
		recordReaderFactory.noOfRecordsToReturn = 3;
		SpiderReadResult spiderReadResult = divaToCoraRecordStorage.readList(TABLE_NAME,
				DataGroup.withNameInData("filter"));
		List<DataGroup> readOrganisationList = spiderReadResult.listOfDataGroups;
		RecordReaderSpy recordReader = recordReaderFactory.factored;

		assertEquals(recordReader.returnedList.size(), 3);

		DivaDbToCoraConverterSpy divaDbToCoraConverter = (DivaDbToCoraConverterSpy) converterFactory.factoredConverters
				.get(0);
		assertEquals(recordReader.returnedList.get(0), divaDbToCoraConverter.mapToConvert);
		assertEquals(readOrganisationList.get(0), divaDbToCoraConverter.convertedDbDataGroup);

		DivaDbToCoraConverterSpy divaDbToCoraConverter2 = (DivaDbToCoraConverterSpy) converterFactory.factoredConverters
				.get(1);
		assertEquals(recordReader.returnedList.get(1), divaDbToCoraConverter2.mapToConvert);
		assertEquals(readOrganisationList.get(1), divaDbToCoraConverter2.convertedDbDataGroup);

		DivaDbToCoraConverterSpy divaDbToCoraConverter3 = (DivaDbToCoraConverterSpy) converterFactory.factoredConverters
				.get(2);
		assertEquals(recordReader.returnedList.get(2), divaDbToCoraConverter3.mapToConvert);
		assertEquals(readOrganisationList.get(2), divaDbToCoraConverter3.convertedDbDataGroup);

	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "readAbstractList is not implemented")
	public void readAbstractListThrowsNotImplementedException() throws Exception {
		divaToCoraRecordStorage.readAbstractList(null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "readLinkList is not implemented")
	public void readLinkListThrowsNotImplementedException() throws Exception {
		divaToCoraRecordStorage.readLinkList(null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "generateLinkCollectionPointingToRecord is not implemented")
	public void generateLinkCollectionPointingToRecordThrowsNotImplementedException()
			throws Exception {
		divaToCoraRecordStorage.generateLinkCollectionPointingToRecord(null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "recordsExistForRecordType is not implemented")
	public void recordsExistForRecordTypeThrowsNotImplementedException() throws Exception {
		divaToCoraRecordStorage.recordsExistForRecordType(null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "recordExistsForAbstractOrImplementingRecordTypeAndRecordId is not implemented")
	public void recordExistsForAbstractOrImplementingRecordTypeAndRecordIdThrowsNotImplementedException()
			throws Exception {
		divaToCoraRecordStorage.recordExistsForAbstractOrImplementingRecordTypeAndRecordId(null,
				null);
	}
}
