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
package se.uu.ub.cora.diva.tocorastorage.fedora;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static se.uu.ub.cora.diva.tocorastorage.fedora.DivaToCoraPersonConverterTestHelper.assertCorrectCreatedByUsingRecordInfoAndUserId;
import static se.uu.ub.cora.diva.tocorastorage.fedora.DivaToCoraPersonConverterTestHelper.assertCorrectIdUsingRecordInfoAndId;
import static se.uu.ub.cora.diva.tocorastorage.fedora.DivaToCoraPersonConverterTestHelper.assertCorrectTsCreatedUsingRecordInfoAndTsCreated;
import static se.uu.ub.cora.diva.tocorastorage.fedora.DivaToCoraPersonConverterTestHelper.assertCorrectTsUpdatedUsingRecordInfoAndTsUpdated;
import static se.uu.ub.cora.diva.tocorastorage.fedora.DivaToCoraPersonConverterTestHelper.assertCorrectUpdatedByUsingRecordInfoAndUserId;
import static se.uu.ub.cora.diva.tocorastorage.fedora.DivaToCoraPersonConverterTestHelper.assertRecordInfoPersonInDiva;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.diva.tocorastorage.ParseException;

public class DivaToCoraPersonConverterTest {

	private DivaToCoraPersonConverter converter;

	@BeforeMethod
	public void beforeMethod() {
		converter = new DivaToCoraPersonConverter();
	}

	@Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting person to Cora person: Can not read xml: "
			+ "The element type \"pid\" must be terminated by the matching end-tag \"</pid>\".")
	public void parseExceptionShouldBeThrownOnMalformedXML() throws Exception {
		String xml = "<pid></notPid>";
		converter.fromXML(xml);
	}

	@Test
	public void convertFromXML() throws Exception {
		DataGroup personDataGroup = converter
				.fromXML(DivaToCoraPersonConverterTestData.person11685XML);
		assertEquals(personDataGroup.getNameInData(), "person");
		DataGroup recordInfo = personDataGroup.getFirstGroupWithNameInData("recordInfo");
		assertRecordInfoPersonInDiva(recordInfo);

		assertCorrectIdUsingRecordInfoAndId(recordInfo, "authority-person:11685");

		assertCorrectCreatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsCreatedUsingRecordInfoAndTsCreated(recordInfo, "2016-09-02 10:59:47.428");

		assertCorrectUpdatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsUpdatedUsingRecordInfoAndTsUpdated(recordInfo, "2018-02-08 10:16:19.538");

		DataGroup name = personDataGroup.getFirstGroupWithNameInData("authorizedName");
		assertCorrectName(name, "Testsson", "Test", null);

		List<DataGroup> allGroupsWithNameInData = personDataGroup
				.getAllGroupsWithNameInData("alternativeName");

		assertCorrectName(allGroupsWithNameInData.get(0), "Erixon", "Karl", "0");
		assertCorrectName(allGroupsWithNameInData.get(1), "Testsson", "Test", "1");
		assertCorrectName(allGroupsWithNameInData.get(2), "Testsson2", "Test2", "2");
		assertEquals(allGroupsWithNameInData.size(), 3);

	}

	private void assertCorrectName(DataGroup dataGroup, String expectedLastName,
			String expectedFirstName, String repeatId) {
		String lastName = dataGroup.getFirstAtomicValueWithNameInData("familyName");
		assertEquals(lastName, expectedLastName);
		String firstName = dataGroup.getFirstAtomicValueWithNameInData("givenName");
		assertEquals(firstName, expectedFirstName);
		if (repeatId != null) {
			assertEquals(dataGroup.getRepeatId(), repeatId);
		}
	}

	@Test
	public void convertFromXMLPerson10000() throws Exception {
		DataGroup personDataGroup = converter
				.fromXML(DivaToCoraPersonConverterTestData.person10000XML);
		assertEquals(personDataGroup.getNameInData(), "person");
		DataGroup recordInfo = personDataGroup.getFirstGroupWithNameInData("recordInfo");
		assertRecordInfoPersonInDiva(recordInfo);

		assertCorrectIdUsingRecordInfoAndId(recordInfo, "authority-person:10000");

		assertCorrectCreatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsCreatedUsingRecordInfoAndTsCreated(recordInfo, "2018-02-19 10:10:43.448");

		assertCorrectUpdatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsUpdatedUsingRecordInfoAndTsUpdated(recordInfo, "2018-02-08 10:16:19.538");

		DataGroup name = personDataGroup.getFirstGroupWithNameInData("authorizedName");
		assertCorrectName(name, "Svensson", "Sven", null);

		List<DataGroup> allGroupsWithNameInData = personDataGroup
				.getAllGroupsWithNameInData("alternativeName");

		assertCorrectName(allGroupsWithNameInData.get(0), "Karlsson", "Sven", "0");
		assertEquals(allGroupsWithNameInData.size(), 1);

	}

	@Test
	public void convertFromXMLPersonNoFirstName() throws Exception {
		DataGroup personDataGroup = converter
				.fromXML(DivaToCoraPersonConverterTestData.personNoFirstNameXML);
		assertEquals(personDataGroup.getNameInData(), "person");
		DataGroup recordInfo = personDataGroup.getFirstGroupWithNameInData("recordInfo");
		assertRecordInfoPersonInDiva(recordInfo);

		assertCorrectIdUsingRecordInfoAndId(recordInfo, "authority-person:10000");

		assertCorrectCreatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsCreatedUsingRecordInfoAndTsCreated(recordInfo, "2018-02-19 10:10:43.448");

		assertCorrectUpdatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsUpdatedUsingRecordInfoAndTsUpdated(recordInfo, "2018-02-08 10:16:19.538");

		DataGroup name = personDataGroup.getFirstGroupWithNameInData("authorizedName");
		assertFalse(name.containsChildWithNameInData("givenName"));
		assertEquals(name.getFirstAtomicValueWithNameInData("familyName"), "Svensson");

	}

	@Test
	public void convertFromXMLPersonNoLastName() throws Exception {
		DataGroup personDataGroup = converter
				.fromXML(DivaToCoraPersonConverterTestData.personNoLastNameXML);
		assertEquals(personDataGroup.getNameInData(), "person");
		DataGroup recordInfo = personDataGroup.getFirstGroupWithNameInData("recordInfo");
		assertRecordInfoPersonInDiva(recordInfo);

		assertCorrectIdUsingRecordInfoAndId(recordInfo, "authority-person:10000");

		assertCorrectCreatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsCreatedUsingRecordInfoAndTsCreated(recordInfo, "2018-02-19 10:10:43.448");

		assertCorrectUpdatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsUpdatedUsingRecordInfoAndTsUpdated(recordInfo, "2018-02-08 10:16:19.538");

		DataGroup name = personDataGroup.getFirstGroupWithNameInData("authorizedName");
		assertFalse(name.containsChildWithNameInData("familyName"));
		assertEquals(name.getFirstAtomicValueWithNameInData("givenName"), "Sven");

	}

	@Test
	public void convertFromXMLPersonNoName() throws Exception {
		DataGroup personDataGroup = converter
				.fromXML(DivaToCoraPersonConverterTestData.personNoNameXML);
		assertEquals(personDataGroup.getNameInData(), "person");
		DataGroup recordInfo = personDataGroup.getFirstGroupWithNameInData("recordInfo");
		assertRecordInfoPersonInDiva(recordInfo);

		assertCorrectIdUsingRecordInfoAndId(recordInfo, "authority-person:10000");

		assertCorrectCreatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsCreatedUsingRecordInfoAndTsCreated(recordInfo, "2018-02-19 10:10:43.448");

		assertCorrectUpdatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsUpdatedUsingRecordInfoAndTsUpdated(recordInfo, "2018-02-08 10:16:19.538");
		assertFalse(personDataGroup.containsChildWithNameInData("authorizedName"));
	}

	@Test
	public void convertFromXMLPersonNoFirstNameInAlternativeName() throws Exception {
		DataGroup personDataGroup = converter
				.fromXML(DivaToCoraPersonConverterTestData.personNoFirstNameAlternativeXML);
		assertEquals(personDataGroup.getNameInData(), "person");
		DataGroup recordInfo = personDataGroup.getFirstGroupWithNameInData("recordInfo");
		assertRecordInfoPersonInDiva(recordInfo);

		assertCorrectIdUsingRecordInfoAndId(recordInfo, "authority-person:10000");

		assertCorrectCreatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsCreatedUsingRecordInfoAndTsCreated(recordInfo, "2018-02-19 10:10:43.448");

		assertCorrectUpdatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsUpdatedUsingRecordInfoAndTsUpdated(recordInfo, "2018-02-08 10:16:19.538");

		List<DataGroup> allGroupsWithNameInData = personDataGroup
				.getAllGroupsWithNameInData("alternativeName");

		DataGroup alternativeName = allGroupsWithNameInData.get(0);
		assertFalse(alternativeName.containsChildWithNameInData("givenName"));
		assertEquals(alternativeName.getFirstAtomicValueWithNameInData("familyName"), "Karlsson");

	}

	@Test
	public void convertFromXMLPersonNoLastNameInAlternativeName() throws Exception {
		DataGroup personDataGroup = converter
				.fromXML(DivaToCoraPersonConverterTestData.personNoLastNameAlternativeXML);
		assertEquals(personDataGroup.getNameInData(), "person");
		DataGroup recordInfo = personDataGroup.getFirstGroupWithNameInData("recordInfo");
		assertRecordInfoPersonInDiva(recordInfo);

		assertCorrectIdUsingRecordInfoAndId(recordInfo, "authority-person:10000");

		assertCorrectCreatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsCreatedUsingRecordInfoAndTsCreated(recordInfo, "2018-02-19 10:10:43.448");

		assertCorrectUpdatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsUpdatedUsingRecordInfoAndTsUpdated(recordInfo, "2018-02-08 10:16:19.538");

		List<DataGroup> allGroupsWithNameInData = personDataGroup
				.getAllGroupsWithNameInData("alternativeName");

		DataGroup alternativeName = allGroupsWithNameInData.get(0);
		assertFalse(alternativeName.containsChildWithNameInData("familyName"));
		assertEquals(alternativeName.getFirstAtomicValueWithNameInData("givenName"), "Sven");

	}

	@Test
	public void convertFromXMLPersonNoAlternativeName() throws Exception {
		DataGroup personDataGroup = converter
				.fromXML(DivaToCoraPersonConverterTestData.personNoAlternativeNameXML);
		assertEquals(personDataGroup.getNameInData(), "person");
		DataGroup recordInfo = personDataGroup.getFirstGroupWithNameInData("recordInfo");
		assertRecordInfoPersonInDiva(recordInfo);

		assertCorrectIdUsingRecordInfoAndId(recordInfo, "authority-person:10000");

		assertCorrectCreatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsCreatedUsingRecordInfoAndTsCreated(recordInfo, "2018-02-19 10:10:43.448");

		assertCorrectUpdatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsUpdatedUsingRecordInfoAndTsUpdated(recordInfo, "2018-02-08 10:16:19.538");

		assertFalse(personDataGroup.containsChildWithNameInData("alternativeName"));

	}

}
