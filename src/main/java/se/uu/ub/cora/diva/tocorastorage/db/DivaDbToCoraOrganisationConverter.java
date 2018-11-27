package se.uu.ub.cora.diva.tocorastorage.db;

import java.util.Map;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class DivaDbToCoraOrganisationConverter implements DivaDbToCoraConverter {

	private static final String ORGANISATION_ID = "organisation_id";

	@Override
	public DataGroup fromMap(Map<String, String> map) {
		if (!map.containsKey(ORGANISATION_ID) || "".equals(map.get(ORGANISATION_ID))) {
			throw ConversionException.withMessageAndException(
					"Error converting organisation to Cora organisation: Map does not contain value for organisation_id",
					null);
		}
		return createDataGroup(map);

	}

	private DataGroup createDataGroup(Map<String, String> map) {
		DataGroup organisation = DataGroup.withNameInData("divaOrganisation");
		String id = map.get(ORGANISATION_ID);
		DataGroup recordInfo = createRecordInfo(id);
		organisation.addChild(recordInfo);
		return organisation;
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
}
