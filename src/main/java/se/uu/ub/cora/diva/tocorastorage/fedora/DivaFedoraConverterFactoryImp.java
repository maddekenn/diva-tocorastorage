package se.uu.ub.cora.diva.tocorastorage.fedora;

import se.uu.ub.cora.diva.tocorastorage.NotImplementedException;

public class DivaFedoraConverterFactoryImp implements DivaFedoraConverterFactory {

	@Override
	public DivaFedoraToCoraConverter factorToCoraConverter(String type) {
		if ("person".equals(type)) {
			return new DivaFedoraToCoraPersonConverter();
		}
		throw NotImplementedException.withMessage("No converter implemented for: " + type);
	}

	@Override
	public DivaCoraToFedoraConverter factorToFedoraConverter(String type) {
		// TODO Auto-generated method stub
		return null;
	}

}
