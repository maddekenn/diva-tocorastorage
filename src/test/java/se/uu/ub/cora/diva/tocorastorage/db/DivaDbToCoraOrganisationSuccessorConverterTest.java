package se.uu.ub.cora.diva.tocorastorage.db;

import static org.testng.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class DivaDbToCoraOrganisationSuccessorConverterTest {
	private DivaDbToCoraOrganisationSuccessorConverter converter;
	private Map<String, String> rowFromDb;

	@BeforeMethod
	public void beforeMethod() {
		rowFromDb = new HashMap<>();
		rowFromDb.put("id", "someOrgId");
		rowFromDb.put("predecessorid", "somePredecessorId");
		converter = new DivaDbToCoraOrganisationSuccessorConverter();

	}

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting organisation successor to Cora organisation successor: Map does not contain mandatory values for organisation id and prdecessor id")
	public void testEmptyMap() {
		rowFromDb = new HashMap<>();
		DataGroup organisation = converter.fromMap(rowFromDb);
		assertNull(organisation);
	}

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting organisation successor to Cora organisation successor: Map does not contain mandatory values for organisation id and prdecessor id")
	public void testMapWithEmptyValueForOrganisationIdThrowsError() {
		rowFromDb = new HashMap<>();
		rowFromDb.put("id", "");
		converter.fromMap(rowFromDb);
	}

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting organisation successor to Cora organisation successor: Map does not contain mandatory values for organisation id and prdecessor id")
	public void testMapWithMissingPredecessorIdThrowsError() {
		rowFromDb = new HashMap<>();
		rowFromDb.put("id", "someOrgId");
		converter.fromMap(rowFromDb);
	}

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting organisation successor to Cora organisation successor: Map does not contain mandatory values for organisation id and prdecessor id")
	public void testMapWithEmptyValueForPredecessorIdThrowsError() {
		rowFromDb = new HashMap<>();
		rowFromDb.put("id", "someOrgId");
		rowFromDb.put("predecessorid", "");
		converter.fromMap(rowFromDb);
	}

	// @Test
	// public void testMinimalValuesReturnsDataGroupWithCorrectStructure() {
	// DataGroup predecessor = converter.fromMap(rowFromDb);
	// assertEquals(predecessor.getNameInData(), "formerName");
	// DataGroup linkedOrganisation =
	// predecessor.getFirstGroupWithNameInData("organisationLink");
	//
	// assertEquals(linkedOrganisation.getFirstAtomicValueWithNameInData("linkedRecordType"),
	// "divaOrganisation");
	// assertEquals(linkedOrganisation.getFirstAtomicValueWithNameInData("linkedRecordId"),
	// "somePredecessorId");
	// assertFalse(predecessor.containsChildWithNameInData("organisationComment"));
	// }
	//
	// @Test
	// public void
	// testMinimalValuesWithEmptyValueForDescriptionReturnsDataGroupWithCorrectStructure()
	// {
	// rowFromDb.put("description", "");
	// DataGroup predecessor = converter.fromMap(rowFromDb);
	// assertEquals(predecessor.getNameInData(), "formerName");
	// DataGroup linkedOrganisation =
	// predecessor.getFirstGroupWithNameInData("organisationLink");
	//
	// assertEquals(linkedOrganisation.getFirstAtomicValueWithNameInData("linkedRecordType"),
	// "divaOrganisation");
	// assertEquals(linkedOrganisation.getFirstAtomicValueWithNameInData("linkedRecordId"),
	// "somePredecessorId");
	// assertFalse(predecessor.containsChildWithNameInData("organisationComment"));
	// }
	//
	// @Test
	// public void testCompleteValuesReturnsDataGroupWithCorrectStructure() {
	// rowFromDb.put("description", "some description text");
	// DataGroup predecessor = converter.fromMap(rowFromDb);
	// assertEquals(predecessor.getNameInData(), "formerName");
	// DataGroup linkedOrganisation =
	// predecessor.getFirstGroupWithNameInData("organisationLink");
	//
	// assertEquals(linkedOrganisation.getFirstAtomicValueWithNameInData("linkedRecordType"),
	// "divaOrganisation");
	// assertEquals(linkedOrganisation.getFirstAtomicValueWithNameInData("linkedRecordId"),
	// "somePredecessorId");
	//
	// assertEquals(predecessor.getFirstAtomicValueWithNameInData("organisationComment"),
	// "some description text");
	//
	// }

}
