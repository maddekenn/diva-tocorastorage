package se.uu.ub.cora.diva.tocorastorage;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.diva.tocorastorage.DivaToCoraConverter;

public class DivaToCoraConverterSpy implements DivaToCoraConverter {

	public String xml;
	public DataGroup convertedDataGroup;

	@Override
	public DataGroup fromXML(String xml) {
		this.xml = xml;
		convertedDataGroup = DataGroup.withNameInData("Converted xml");
		return convertedDataGroup;
	}

}
