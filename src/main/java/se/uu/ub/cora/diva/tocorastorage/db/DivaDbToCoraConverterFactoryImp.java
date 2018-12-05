package se.uu.ub.cora.diva.tocorastorage.db;

import se.uu.ub.cora.diva.tocorastorage.NotImplementedException;

public class DivaDbToCoraConverterFactoryImp implements DivaDbToCoraConverterFactory {

	@Override
	public DivaDbToCoraConverter factor(String type) {
		if ("divaOrganisation".equals(type)) {
			return new DivaDbToCoraOrganisationConverter();
		}
		throw NotImplementedException.withMessage("No converter implemented for: " + type);
	}

}
