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

	private static final String ADDITION = "addition";
	private static final String FAMILY_NAME = "familyName";
	private static final String NUMBER = "number";
	private static final String GIVEN_NAME = "givenName";
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
		createAlternativeNamesAndAddToPerson(person);

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
		possiblyCreateNamePartAndAddToNameUsingAttributeNameAndValue(defaultName, GIVEN_NAME,
				getDefaultNamePartFromXML("firstname"));
		possiblyCreateNamePartAndAddToNameUsingAttributeNameAndValue(defaultName, FAMILY_NAME,
				getDefaultNamePartFromXML("lastname"));
		possiblyCreateNamePartAndAddToNameUsingAttributeNameAndValue(defaultName, ADDITION,
				getDefaultNamePartFromXML(ADDITION));
		possiblyCreateNamePartAndAddToNameUsingAttributeNameAndValue(defaultName, NUMBER,
				getDefaultNamePartFromXML(NUMBER));
	}

	private String getDefaultNamePartFromXML(String xmlTagName) {
		return getStringFromDocumentUsingXPath(
				"/authorityPerson/defaultName/" + xmlTagName + "/text()");
	}

	private void possiblyCreateNamePartAndAddToNameUsingAttributeNameAndValue(DataGroup defaultName,
			String attributeNameInData, String value) {
		if (valueContainsData(value)) {
			createNamePartAndAddToNameUsingAttributeNameAndValue(defaultName, attributeNameInData,
					value);
		}
	}

	private boolean valueContainsData(String value) {
		return !"".equals(value);
	}

	private void createNamePartAndAddToNameUsingAttributeNameAndValue(DataGroup defaultName,
			String attributeNameInData, String value) {
		DataGroup givenNamePart = DataGroup.withNameInData("namePart");
		defaultName.addChild(givenNamePart);
		givenNamePart.addAttributeByIdWithValue("type", attributeNameInData);
		givenNamePart.addChild(DataAtomic.withNameInDataAndValue("value", value));
	}

	private void createAlternativeNamesAndAddToPerson(DataGroup person) {
		NodeList list = parser
				.getNodeListFromDocumentUsingXPath("/authorityPerson/alternativeNames/nameForm");
		createAndAddAllAlternativeNamesToPersonUsingNodeListAndPerson(list, person);
	}

	private void createAndAddAllAlternativeNamesToPersonUsingNodeListAndPerson(NodeList list,
			DataGroup person) {
		for (int i = 0; i < list.getLength(); i++) {
			Node nameForm = list.item(i);
			addAlternativeNameToPersonUsingNodeAndPersonAndRepeatId(nameForm, person,
					String.valueOf(i));
		}
	}

	private void addAlternativeNameToPersonUsingNodeAndPersonAndRepeatId(Node nameForm,
			DataGroup person, String repeatId) {
		DataGroup alternativeName = DataGroup.withNameInData("name");
		person.addChild(alternativeName);
		alternativeName.addAttributeByIdWithValue("type", "alternative");
		alternativeName.setRepeatId(repeatId);
		createAlternativeNameUsingNameFormNodeAndAddToAlternativeNames(nameForm, alternativeName);
	}

	private void createAlternativeNameUsingNameFormNodeAndAddToAlternativeNames(Node nameForm,
			DataGroup alternativeName) {
		possiblyCreateNamePartAndAddToNameUsingAttributeNameAndValue(alternativeName, GIVEN_NAME,
				getAlternativeNamePartFromXMLUsingNodeAndXPathPart(nameForm, "firstname"));
		possiblyCreateNamePartAndAddToNameUsingAttributeNameAndValue(alternativeName, FAMILY_NAME,
				getAlternativeNamePartFromXMLUsingNodeAndXPathPart(nameForm, "lastname"));
		possiblyCreateNamePartAndAddToNameUsingAttributeNameAndValue(alternativeName, ADDITION,
				getAlternativeNamePartFromXMLUsingNodeAndXPathPart(nameForm, ADDITION));
		possiblyCreateNamePartAndAddToNameUsingAttributeNameAndValue(alternativeName, NUMBER,
				getAlternativeNamePartFromXMLUsingNodeAndXPathPart(nameForm, NUMBER));
	}

	private String getAlternativeNamePartFromXMLUsingNodeAndXPathPart(Node nameForm,
			String xmlTagName) {
		return parser.getStringFromDocumentUsingNodeAndXPath(nameForm,
				"./" + xmlTagName + "/text()");
	}
}
