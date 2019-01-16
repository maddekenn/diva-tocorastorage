package se.uu.ub.cora.diva.tocorastorage.db;

import java.util.Map;

public class DivaDbToCoraOrganisationAncestryConverter {
	protected static final String PREDECESSOR_ID = "predecessorid";
	private static final String ORGANISATION_ID = "id";
	protected Map<String, String> dbRow;

	protected boolean mandatoryValuesAreMissing() {
		return organisationIdIsMissing() || predecessorIdIsMissing();
	}

	private boolean organisationIdIsMissing() {
		return !dbRow.containsKey(ORGANISATION_ID) || "".equals(dbRow.get(ORGANISATION_ID));
	}

	private boolean predecessorIdIsMissing() {
		return !dbRow.containsKey(PREDECESSOR_ID) || "".equals(dbRow.get(PREDECESSOR_ID));
	}
}