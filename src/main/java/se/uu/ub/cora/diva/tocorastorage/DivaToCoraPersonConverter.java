/*
 * Copyright 2018 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.diva.tocorastorage;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class DivaToCoraPersonConverter implements DivaToCoraConverter {

	private XMLXPathParser parser;

	@Override
	public DataGroup fromXML(String xml) {
		try {
			parser = XMLXPathParser.forXML(xml);
			return tryToCreateDataGroupFromDocument();
		} catch (Exception e) {
			throw ParseException.withMessageAndException(
					"Error converting person to Cora person: " + e.getMessage(), e);
		}
	}

	private DataGroup tryToCreateDataGroupFromDocument() {
		DataGroup person = DataGroup.withNameInData("authority");
		createRecordInfoAndAddToPerson(person);

		createDefaultNameAndAddToPerson(person);
		createAlternativeNameAndAddToPerson(person);

		return person;
	}

	private void createRecordInfoAndAddToPerson(DataGroup place) {
		DataGroup recordInfo = DivaToCoraRecordInfoConverter.createRecordInfo(parser);
		place.addChild(recordInfo);
	}

	private String getStringFromDocumentUsingXPath(String xpathString) {
		return parser.getStringFromDocumentUsingXPath(xpathString);
	}

	private void createDefaultNameAndAddToPerson(DataGroup person) {
		DataGroup defaultName = DataGroup.withNameInData("name");
		person.addChild(defaultName);
		defaultName.addAttributeByIdWithValue("type", "authorized");
		createName(defaultName);
	}

	private void createName(DataGroup defaultName) {
		createNamePartAndAddToNameUsingAttributeNameAndXMLTag(defaultName, "givenName",
				getDefaultNamePartFromXML("firstname"));
		createNamePartAndAddToNameUsingAttributeNameAndXMLTag(defaultName, "familyName",
				getDefaultNamePartFromXML("lastname"));
		createNamePartAndAddToNameUsingAttributeNameAndXMLTag(defaultName, "addition",
				getDefaultNamePartFromXML("addition"));
		// createNamePartAndAddToNameUsingAttributeNameAndXMLTag(defaultName, "number",
		// "number");
	}

	private String getDefaultNamePartFromXML(String xmlTagName) {
		return getStringFromDocumentUsingXPath(
				"/authorityPerson/defaultName/" + xmlTagName + "/text()");
	}

	private void createNamePartAndAddToNameUsingAttributeNameAndXMLTag(DataGroup defaultName,
			String attributeNameInData, String xmlTagName) {
		DataGroup givenNamePart = DataGroup.withNameInData("namePart");
		defaultName.addChild(givenNamePart);
		givenNamePart.addAttributeByIdWithValue("type", attributeNameInData);
		givenNamePart.addChild(DataAtomic.withNameInDataAndValue("value", xmlTagName));
	}

	// private String getLastTsUpdatedFromDocument() {
	// NodeList list = parser.getNodeListFromDocumentUsingXPath(
	// "/authorityPerson/recordInfo/events/event/timestamp/text()");
	// Node item = getTheLastTsUpdatedAsItShouldBeTheLatest(list);
	// return item.getTextContent();
	// }

	private void createAlternativeNameAndAddToPerson(DataGroup person) {
		NodeList list = parser
				.getNodeListFromDocumentUsingXPath("/authorityPerson/alternativeNames/nameForm");
		Node item = list.item(0);
		NodeList childNodes = item.getChildNodes();
		String textContent = childNodes.item(0).getTextContent();
		String nodeName = childNodes.item(0).getNodeName();
		DataGroup defaultName = DataGroup.withNameInData("name");
		person.addChild(defaultName);
		defaultName.addAttributeByIdWithValue("type", "alternative");
		createAlternativeName(defaultName);
	}

	private void createAlternativeName(DataGroup defaultName) {
		createNamePartAndAddToNameUsingAttributeNameAndXMLTag(defaultName, "givenName",
				getAlternativeNamePartFromXML("firstname"));
		createNamePartAndAddToNameUsingAttributeNameAndXMLTag(defaultName, "familyName",
				getAlternativeNamePartFromXML("lastname"));
		// createNamePartAndAddToNameUsingAttributeNameAndXMLTag(defaultName,
		// "addition",
		// getAlternativeNamePartFromXML("addition"));
		// createNamePartAndAddToNameUsingAttributeNameAndXMLTag(defaultName, "number",
		// "number");
	}

	private String getAlternativeNamePartFromXML(String xmlTagName) {
		return getStringFromDocumentUsingXPath(
				"/authorityPerson/alternativeNames/nameForm/" + xmlTagName + "/text()");
	}
}
