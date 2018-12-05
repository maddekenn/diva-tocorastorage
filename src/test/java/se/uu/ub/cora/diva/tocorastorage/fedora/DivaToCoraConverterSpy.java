package se.uu.ub.cora.diva.tocorastorage.fedora;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.diva.tocorastorage.fedora.DivaToCoraConverter;

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
