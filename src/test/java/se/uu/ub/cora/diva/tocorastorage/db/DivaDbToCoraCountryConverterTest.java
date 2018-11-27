package se.uu.ub.cora.diva.tocorastorage.db;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class DivaDbToCoraCountryConverterTest {

	private DivaDbToCoraOrganisationConverter converter;
	private Map<String, String> rowFromDb;

	@BeforeMethod
	public void beforeMethod() {
		rowFromDb = new HashMap<>();
		rowFromDb.put("organisation_id", "someOrgId");
		// rowFromDb.put("lastupdated", "2014-04-17 10:12:48.8");
		converter = new DivaDbToCoraOrganisationConverter();
	}

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting organisation to Cora organisation: Map does not contain value for organisation_id")
	public void testEmptyMap() {
		rowFromDb = new HashMap<>();
		DataGroup organisation = converter.fromMap(rowFromDb);
		assertNull(organisation);
	}

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting organisation to Cora organisation: Map does not contain value for organisation_id")
	public void testMapWithEmptyValueThrowsError() {
		rowFromDb = new HashMap<>();
		rowFromDb.put("organisation_id", "");
		converter.fromMap(rowFromDb);
	}

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting organisation to Cora organisation: Map does not contain value for organisation_id")
	public void testMapWithNonEmptyValueANDEmptyValueThrowsError() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("organisation_name", "someName");
		rowFromDb.put("organisation_id", "");
		converter.fromMap(rowFromDb);
	}

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting organisation to Cora organisation: Map does not contain value for organisation_id")
	public void mapDoesNotContainOrganisationIdValue() {
		rowFromDb = new HashMap<>();
		rowFromDb.put("organisation_name", "someName");
		DataGroup country = converter.fromMap(rowFromDb);
		// assertEquals(country.getNameInData(), "country");

	}

	@Test
	public void testMinimalValuesReturnsDataGroupWithCorrectRecordInfo() {
		DataGroup organisation = converter.fromMap(rowFromDb);
		assertEquals(organisation.getNameInData(), "divaOrganisation");

		assertCorrectRecordInfoWithId(organisation, "someOrgId");
		// assertEquals(organisation.getFirstAtomicValueWithNameInData("iso31661Alpha2"),
		// "someAlpha2Code");
		// assertEquals(organisation.getChildren().size(), 2);
	}

	// @Test
	// public void testMinimalNullValuesReturnsDataGroupWithCorrectRecordInfo() {
	// rowFromDb.put("defaultname", null);
	// rowFromDb.put("alpha3code", null);
	// rowFromDb.put("numericalcode", null);
	// DataGroup country = converter.fromMap(rowFromDb);
	// assertEquals(country.getNameInData(), "country");
	//
	// assertCorrectRecordInfoWithId(country, "someAlpha2Code");
	// assertEquals(country.getFirstAtomicValueWithNameInData("iso31661Alpha2"),
	// "someAlpha2Code");
	// assertEquals(country.getChildren().size(), 2);
	// }
	//
	private void assertCorrectRecordInfoWithId(DataGroup country, String id) {
		DataGroup recordInfo = country.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), id);

		DataGroup type = recordInfo.getFirstGroupWithNameInData("type");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordType"), "recordType");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordId"), "divaOrganisation");

		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "diva");

		// String tsCreated = recordInfo.getFirstAtomicValueWithNameInData("tsCreated");
		// assertEquals(tsCreated, "2014-04-17 10:12:48.8");
		// String tsUpdated = recordInfo.getFirstAtomicValueWithNameInData("tsUpdated");
		// assertEquals(tsUpdated, "2014-04-17 10:12:48.8");
		//
		// assertCorrectUserInfo(recordInfo);

	}
	//
	// private void assertCorrectUserInfo(DataGroup recordInfo) {
	// DataGroup createdBy = recordInfo.getFirstGroupWithNameInData("createdBy");
	// assertEquals(createdBy.getFirstAtomicValueWithNameInData("linkedRecordType"),
	// "systemOneUser");
	// assertEquals(createdBy.getFirstAtomicValueWithNameInData("linkedRecordId"),
	// "12345");
	// DataGroup updatedBy = recordInfo.getFirstGroupWithNameInData("updatedBy");
	// assertEquals(updatedBy.getFirstAtomicValueWithNameInData("linkedRecordType"),
	// "systemOneUser");
	// assertEquals(updatedBy.getFirstAtomicValueWithNameInData("linkedRecordId"),
	// "12345");
	// }
	//
	// @Test
	// public void testMapContainsValueReturnsDataGroupWithCorrectChildren() {
	// rowFromDb.put("defaultname", "Sverige");
	// rowFromDb.put("alpha3code", "SWE");
	// rowFromDb.put("numericalcode", "752");
	// rowFromDb.put("marccode", "sw");
	// DataGroup country = converter.fromMap(rowFromDb);
	//
	// assertEquals(country.getFirstAtomicValueWithNameInData("iso31661Alpha2"),
	// "someAlpha2Code");
	// DataGroup text = country.getFirstGroupWithNameInData("textId");
	// assertEquals(text.getFirstAtomicValueWithNameInData("linkedRecordType"),
	// "coraText");
	// assertEquals(text.getFirstAtomicValueWithNameInData("linkedRecordId"),
	// "countrysomeAlpha2CodeText");
	// assertEquals(country.getFirstAtomicValueWithNameInData("iso31661Alpha3"),
	// "SWE");
	// assertEquals(country.getFirstAtomicValueWithNameInData("iso31661Numeric"),
	// "752");
	// assertEquals(country.getFirstAtomicValueWithNameInData("marcCountryCode"),
	// "sw");
	//
	// }

}
