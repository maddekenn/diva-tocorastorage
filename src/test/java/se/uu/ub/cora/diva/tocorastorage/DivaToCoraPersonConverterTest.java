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
package se.uu.ub.cora.diva.tocorastorage;

import static org.testng.Assert.assertEquals;
import static se.uu.ub.cora.diva.tocorastorage.DivaToCoraPersonConverterTestHelper.assertCorrectCreatedByUsingRecordInfoAndUserId;
import static se.uu.ub.cora.diva.tocorastorage.DivaToCoraPersonConverterTestHelper.assertCorrectIdUsingRecordInfoAndId;
import static se.uu.ub.cora.diva.tocorastorage.DivaToCoraPersonConverterTestHelper.assertCorrectName;
import static se.uu.ub.cora.diva.tocorastorage.DivaToCoraPersonConverterTestHelper.assertCorrectTsCreatedUsingRecordInfoAndTsCreated;
import static se.uu.ub.cora.diva.tocorastorage.DivaToCoraPersonConverterTestHelper.assertCorrectTsUpdatedUsingRecordInfoAndTsUpdated;
import static se.uu.ub.cora.diva.tocorastorage.DivaToCoraPersonConverterTestHelper.assertCorrectUpdatedByUsingRecordInfoAndUserId;
import static se.uu.ub.cora.diva.tocorastorage.DivaToCoraPersonConverterTestHelper.assertRecordInfoPersonInDiva;
import static se.uu.ub.cora.diva.tocorastorage.DivaToCoraPersonConverterTestHelper.getAlternativeNamesFromPersonDataGroup;
import static se.uu.ub.cora.diva.tocorastorage.DivaToCoraPersonConverterTestHelper.getDefaultNameFromPersonDataGroup;

import java.util.Collection;
import java.util.Iterator;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataGroup;

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
		assertEquals(personDataGroup.getNameInData(), "authority");
		DataGroup recordInfo = personDataGroup.getFirstGroupWithNameInData("recordInfo");
		assertRecordInfoPersonInDiva(recordInfo);

		assertCorrectIdUsingRecordInfoAndId(recordInfo, "authority-person:11685");

		assertCorrectCreatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsCreatedUsingRecordInfoAndTsCreated(recordInfo, "2016-09-02 10:59:47.428");

		assertCorrectUpdatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsUpdatedUsingRecordInfoAndTsUpdated(recordInfo, "2018-02-08 10:16:19.538");

		DataGroup defaultName = getDefaultNameFromPersonDataGroup(personDataGroup);
		assertEquals(defaultName.getAttribute("type"), "authorized");

		assertCorrectName(defaultName, "Test", "Testsson", "Grosshandlare", null, null);

		Collection<DataGroup> alternativeNames = getAlternativeNamesFromPersonDataGroup(
				personDataGroup);
		assertEquals(alternativeNames.size(), 3);

		Iterator<DataGroup> alternativeNamesIterator = alternativeNames.iterator();
		assertCorrectName(alternativeNamesIterator.next(), "Karl", "Erixon", null, "III", "0");
		assertCorrectName(alternativeNamesIterator.next(), "Test", "Testsson", null, null, "1");
		assertCorrectName(alternativeNamesIterator.next(), "Test2", "Testsson2", "Sir", "IV", "2");

	}

	@Test
	public void convertFromXMLPerson10000() throws Exception {
		DataGroup personDataGroup = converter
				.fromXML(DivaToCoraPersonConverterTestData.person10000XML);
		assertEquals(personDataGroup.getNameInData(), "authority");
		DataGroup recordInfo = personDataGroup.getFirstGroupWithNameInData("recordInfo");
		assertRecordInfoPersonInDiva(recordInfo);

		assertCorrectIdUsingRecordInfoAndId(recordInfo, "authority-person:10000");

		assertCorrectCreatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsCreatedUsingRecordInfoAndTsCreated(recordInfo, "2018-02-19 10:10:43.448");

		assertCorrectUpdatedByUsingRecordInfoAndUserId(recordInfo, "12345");
		assertCorrectTsUpdatedUsingRecordInfoAndTsUpdated(recordInfo, "2018-02-08 10:16:19.538");

		DataGroup defaultName = getDefaultNameFromPersonDataGroup(personDataGroup);
		assertEquals(defaultName.getAttribute("type"), "authorized");

		assertCorrectName(defaultName, "Sven", "Svensson", "Grosshandlare", "VI", null);

		Collection<DataGroup> alternativeNames = getAlternativeNamesFromPersonDataGroup(
				personDataGroup);
		assertEquals(alternativeNames.size(), 1);

		Iterator<DataGroup> alternativeNamesIterator = alternativeNames.iterator();
		assertCorrectName(alternativeNamesIterator.next(), "Sven", "Karlsson", null, null, "0");

	}

}
