package se.uu.ub.cora.diva.tocorastorage.fedora;

import se.uu.ub.cora.diva.tocorastorage.NotImplementedException;

public class DivaFedoraConverterFactoryImp implements DivaFedoraConverterFactory {

	@Override
	public DivaFedoraToCoraConverter factor(String type) {
		if ("person".equals(type)) {
			return new DivaFedoraToCoraPersonConverter();
		}
		throw NotImplementedException.withMessage("No converter implemented for: " + type);
	}

}
