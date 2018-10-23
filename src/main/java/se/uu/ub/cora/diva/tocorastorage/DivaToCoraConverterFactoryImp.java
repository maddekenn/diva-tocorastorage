package se.uu.ub.cora.diva.tocorastorage;

public class DivaToCoraConverterFactoryImp implements DivaToCoraConverterFactory {

	@Override
	public DivaToCoraConverter factor(String type) {
		if ("person".equals(type)) {
			return new DivaToCoraPersonConverter();
		}
		throw NotImplementedException.withMessage("No converter implemented for: " + type);
	}

}
