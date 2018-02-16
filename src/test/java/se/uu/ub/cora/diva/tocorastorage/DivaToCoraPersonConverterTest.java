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

import java.util.Collection;
import java.util.Iterator;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAttribute;
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
		DataGroup type = recordInfo.getFirstGroupWithNameInData("type");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordType"), "recordType");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordId"), "person");

		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "diva");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "authority-person:11685");

		DataGroup createdBy = recordInfo.getFirstGroupWithNameInData("createdBy");
		assertEquals(createdBy.getFirstAtomicValueWithNameInData("linkedRecordType"), "user");
		assertEquals(createdBy.getFirstAtomicValueWithNameInData("linkedRecordId"), "12345");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("tsCreated"),
				"2016-09-02 10:59:47.428");

		DataGroup updatedBy = recordInfo.getFirstGroupWithNameInData("updatedBy");
		assertEquals(updatedBy.getFirstAtomicValueWithNameInData("linkedRecordType"), "user");
		assertEquals(updatedBy.getFirstAtomicValueWithNameInData("linkedRecordId"), "12345");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("tsUpdated"),
				"2018-02-08 10:16:19.538");

		DataGroup defaultName = personDataGroup
				.getAllGroupsWithNameInDataAndAttributes("name",
						DataAttribute.withNameInDataAndValue("type", "authorized"))
				.iterator().next();
		assertEquals(defaultName.getAttribute("type"), "authorized");

		assertCorrectName(defaultName, "Test", "Testsson", "Grosshandlare", null);

		Collection<DataGroup> alternativeNames = personDataGroup
				.getAllGroupsWithNameInDataAndAttributes("name",
						DataAttribute.withNameInDataAndValue("type", "alternative"));

		Iterator<DataGroup> alternativeNamesIterator = alternativeNames.iterator();
		assertCorrectName(alternativeNamesIterator.next(), "Karl", "Erixon", null, null);
		assertCorrectName(alternativeNamesIterator.next(), "Karl", "Erixon", null, null);

	}

	private void assertCorrectName(DataGroup defaultName, String givenNameValue,
			String familyNameValue, String additionValue, String numberValue) {
		assertCorrectNamePartUsingNameGroupAndAttributeNameAndValue(defaultName, "givenName",
				givenNameValue);
		assertCorrectNamePartUsingNameGroupAndAttributeNameAndValue(defaultName, "familyName",
				familyNameValue);
		assertCorrectNamePartUsingNameGroupAndAttributeNameAndValue(defaultName, "addition",
				additionValue);
		assertCorrectNamePartUsingNameGroupAndAttributeNameAndValue(defaultName, "number",
				numberValue);
	}

	private void assertCorrectNamePartUsingNameGroupAndAttributeNameAndValue(DataGroup name,
			String attributeName, String nameValue) {
		Collection<DataGroup> givenNames = name.getAllGroupsWithNameInDataAndAttributes("namePart",
				DataAttribute.withNameInDataAndValue("type", attributeName));
		if (nameValue == null) {
			assertEquals(givenNames.size(), 0);
		} else {
			assertEquals(givenNames.size(), 1);
			DataGroup givenName = givenNames.iterator().next();
			assertEquals(givenName.getFirstAtomicValueWithNameInData("value"), nameValue);
		}
	}

}
