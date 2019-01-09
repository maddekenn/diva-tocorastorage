package se.uu.ub.cora.diva.tocorastorage.db;

import java.util.Map;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class DivaDbToCoraOrganisationConverter implements DivaDbToCoraConverter {

	private static final String ORGANISATION_ID = "id";
	private static final String ALTERNATIVE_NAME = "alternative_name";
	private Map<String, String> dbRow;
	private DataGroup organisation;

	@Override
	public DataGroup fromMap(Map<String, String> dbRow) {
		this.dbRow = dbRow;
		if (organisationIsEmpty()) {
			throw ConversionException.withMessageAndException(
					"Error converting organisation to Cora organisation: Map does not contain value for "
							+ ORGANISATION_ID,
					null);
		}
		return createDataGroup();

	}

	private boolean organisationIsEmpty() {
		return !dbRow.containsKey(ORGANISATION_ID) || "".equals(dbRow.get(ORGANISATION_ID));
	}

	private DataGroup createDataGroup() {
		createAndAddOrganisationWithRecordInfo();
		createAndAddName();
		createAndAddAlternativeName();
		createAndAddOrganisationType();
		createAndAddEligibility();
		possiblyCeateAndAddAddress();
		possiblyCreateAndAddOrganisationNumber();
		possiblyCreateAndAddOrganisationCode();
		possiblyCreateAndAddURL();
		return organisation;
	}

	private void createAndAddOrganisationWithRecordInfo() {
		organisation = DataGroup.withNameInData("organisation");
		String id = dbRow.get(ORGANISATION_ID);
		DataGroup recordInfo = createRecordInfo(id);
		organisation.addChild(recordInfo);
	}

	private void createAndAddName() {
		String divaOrganisationName = dbRow.get("defaultname");
		organisation.addChild(
				DataAtomic.withNameInDataAndValue("organisationName", divaOrganisationName));
	}

	private void createAndAddAlternativeName() {
		DataGroup alternativeNameDataGroup = DataGroup.withNameInData("alternativeName");
		alternativeNameDataGroup.addChild(DataAtomic.withNameInDataAndValue("language", "en"));
		String alternativeName = dbRow.get(ALTERNATIVE_NAME);
		alternativeNameDataGroup
				.addChild(DataAtomic.withNameInDataAndValue("organisationName", alternativeName));
		organisation.addChild(alternativeNameDataGroup);
	}

	private void createAndAddOrganisationType() {
		organisation.addChild(DataAtomic.withNameInDataAndValue("organisationType", "unit"));
	}

	private void createAndAddEligibility() {
		String eligible = dbRow.get("not_eligible");
		String coraEligible = isEligible(eligible) ? "yes" : "no";
		organisation.addChild(DataAtomic.withNameInDataAndValue("eligible", coraEligible));
	}

	private boolean isEligible(String eligible) {
		return "f".equals(eligible);
	}

	private DataGroup createRecordInfo(String id) {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", id));
		createAndAddType(recordInfo);
		createAndAddDataDivider(recordInfo);
		return recordInfo;
	}

	private void createAndAddType(DataGroup recordInfo) {
		DataGroup type = DataGroup.withNameInData("type");
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "divaOrganisation"));
		recordInfo.addChild(type);
	}

	private void createAndAddDataDivider(DataGroup recordInfo) {
		DataGroup dataDivider = DataGroup.withNameInData("dataDivider");
		dataDivider.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "system"));
		dataDivider.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "diva"));
		recordInfo.addChild(dataDivider);
	}

	private void possiblyCeateAndAddAddress() {
		possiblyAddAtomicValueUsingKeyAndNameInData("city", "city");
		possiblyAddAtomicValueUsingKeyAndNameInData("street", "street");
		possiblyAddAtomicValueUsingKeyAndNameInData("box", "box");
		possiblyAddAtomicValueUsingKeyAndNameInData("postnumber", "postcode");
		possiblyAddAtomicValueUsingKeyAndNameInData("country_code", "country");
	}

	private void possiblyAddAtomicValueUsingKeyAndNameInData(String key, String nameInData) {
		if (dbRow.containsKey(key)) {
			String value = dbRow.get(key);
			organisation.addChild(DataAtomic.withNameInDataAndValue(nameInData, value));
		}
	}

	private void possiblyCreateAndAddOrganisationNumber() {
		possiblyAddAtomicValueUsingKeyAndNameInData("orgnumber", "organisationNumber");
	}

	private void possiblyCreateAndAddOrganisationCode() {
		possiblyAddAtomicValueUsingKeyAndNameInData("organisation_code", "organisationCode");
	}

	private void possiblyCreateAndAddURL() {
		possiblyAddAtomicValueUsingKeyAndNameInData("organisation_homepage", "URL");
	}

}