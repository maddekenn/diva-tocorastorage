package se.uu.ub.cora.diva.tocorastorage.fedora;

import se.uu.ub.cora.diva.tocorastorage.NotImplementedException;
import se.uu.ub.cora.httphandler.HttpHandlerFactoryImp;

public class DivaFedoraConverterFactoryImp implements DivaFedoraConverterFactory {

	private String fedoraURL;

	public static DivaFedoraConverterFactoryImp usingFedoraURL(String fedoraURL) {
		return new DivaFedoraConverterFactoryImp(fedoraURL);
	}

	private DivaFedoraConverterFactoryImp(String fedoraURL) {
		this.fedoraURL = fedoraURL;
	}

	@Override
	public DivaFedoraToCoraConverter factorToCoraConverter(String type) {
		if ("person".equals(type)) {
			return new DivaFedoraToCoraPersonConverter();
		}
		throw NotImplementedException.withMessage("No converter implemented for: " + type);
	}

	@Override
	public DivaCoraToFedoraConverter factorToFedoraConverter(String type) {
		if ("person".equals(type)) {
			HttpHandlerFactoryImp httpHandlerFactory = new HttpHandlerFactoryImp();
			return DivaCoraToFedoraPersonConverter
					.usingHttpHandlerFactoryAndFedoraUrl(httpHandlerFactory, fedoraURL);
		}
		throw NotImplementedException.withMessage("No converter implemented for: " + type);
	}

	public String getFedoraURL() {
		// needed for tests
		return fedoraURL;
	}

}
