package se.uu.ub.cora.diva.tocorastorage.fedora;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.diva.tocorastorage.fedora.DivaFedoraToCoraConverter;

public class DivaFedoraToCoraConverterSpy implements DivaFedoraToCoraConverter {

	public String xml;
	public DataGroup convertedDataGroup;

	@Override
	public DataGroup fromXML(String xml) {
		this.xml = xml;
		convertedDataGroup = DataGroup.withNameInData("Converted xml");
		return convertedDataGroup;
	}

}
