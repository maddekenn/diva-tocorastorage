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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.diva.tocorastorage.DivaToCoraPlaceConverter;
import se.uu.ub.cora.diva.tocorastorage.ParseException;

public class DivaToCoraPlaceConverterTest {
	String place22XML = "<place id=\"1\">\n" + "  <pid>alvin-place:22</pid>\n"
			+ "  <dsId>METADATA</dsId>\n" + "  <recordInfo id=\"2\">\n"
			+ "    <externalDs>false</externalDs>\n" + "    <lastAction>UPDATED</lastAction>\n"
			+ "    <created id=\"3\">\n"
			+ "      <date id=\"4\">2014-12-18 20:20:38.346 UTC</date>\n"
			+ "      <dateInStorage id=\"5\">2014-12-18 20:20:39.815 UTC</dateInStorage>\n"
			+ "      <user class=\"seamUser\" id=\"6\">\n"
			+ "        <lastUpdated class=\"sql-timestamp\" id=\"7\">2014-04-17 08:12:52.806</lastUpdated>\n"
			+ "        <id>1</id>\n" + "        <userId>test</userId>\n"
			+ "        <domain>uu</domain>\n" + "        <firstName>Test</firstName>\n"
			+ "        <lastName>Testsson</lastName>\n"
			+ "        <email>test.testsson@ub.uu.se</email>\n" + "      </user>\n"
			+ "      <note>Place created through web gui</note>\n" + "      <type>CREATED</type>\n"
			+ "    </created>\n" + "    <updated id=\"8\">\n"
			+ "      <userAction reference=\"3\"/>\n" + "      <userAction id=\"9\">\n"
			+ "        <date id=\"10\">2014-12-18 20:21:20.880 UTC</date>\n"
			+ "        <user class=\"seamUser\" id=\"11\">\n"
			+ "          <lastUpdated class=\"sql-timestamp\" id=\"12\">2014-04-17 08:12:52.806</lastUpdated>\n"
			+ "          <id>1</id>\n" + "          <userId>test</userId>\n"
			+ "          <domain>uu</domain>\n" + "          <firstName>Test</firstName>\n"
			+ "          <lastName>Testsson</lastName>\n"
			+ "          <email>Stefan.Andersson@ub.uu.se</email>\n" + "        </user>\n"
			+ "        <note>Place updated through web gui</note>\n"
			+ "        <type>UPDATED</type>\n" + "      </userAction>\n" + "    </updated>\n"
			+ "  </recordInfo>\n" + "  <country class=\"country\">\n"
			+ "    <lastUpdated class=\"sql-timestamp\" id=\"14\">2014-04-17 08:12:48.8</lastUpdated>\n"
			+ "    <defaultName>Sverige</defaultName>\n" + "    <localisedNames id=\"15\">\n"
			+ "      <entry>\n" + "        <string>en</string>\n"
			+ "        <string>Sweden</string>\n" + "      </entry>\n" + "    </localisedNames>\n"
			+ "    <alpha2Code>SE</alpha2Code>\n" + "    <alpha3Code>SWE</alpha3Code>\n"
			+ "    <numericalCode>752</numericalCode>\n" + "    <marcCode>sw</marcCode>\n"
			+ "  </country>\n" + "  <regions id=\"16\"/>\n" + "  <defaultPlaceName id=\"17\">\n"
			+ "    <deleted>false</deleted>\n" + "    <name>Linköping</name>\n"
			+ "  </defaultPlaceName>\n" + "  <placeNameForms id=\"18\"/>\n" + "  <identifiers/>\n"
			+ "  <localIdentifiers id=\"19\">\n" + "    <localIdentifier>\n"
			+ "      <type class=\"localIdentifierType\">\n"
			+ "        <lastUpdated class=\"sql-timestamp\">2014-04-17 08:49:50.65</lastUpdated>\n"
			+ "        <defaultName>Waller-id</defaultName>\n" + "        <localisedNames/>\n"
			+ "        <code>waller</code>\n" + "        <id>114</id>\n"
			+ "        <internal>false</internal>\n"
			+ "        <organisationUnitId>2</organisationUnitId>\n" + "      </type>\n"
			+ "      <text>1367</text>\n" + "    </localIdentifier>\n" + "  </localIdentifiers>\n"
			+ "  <longitude>15.62</longitude>\n" + "  <latitude>58.42</latitude>\n" + "</place>";
	String place24XML = "<place id=\"1\">\n" + "  <pid>alvin-place:24</pid>\n"
			+ "  <dsId>METADATA</dsId>\n" + "  <recordInfo id=\"2\">\n"
			+ "    <externalDs>false</externalDs>\n" + "    <lastAction>UPDATED</lastAction>\n"
			+ "    <created id=\"3\">\n"
			+ "      <date id=\"4\">2014-12-18 22:16:44.623 UTC</date>\n"
			+ "      <dateInStorage id=\"5\">2014-12-18 22:16:44.919 UTC</dateInStorage>\n"
			+ "      <user class=\"seamUser\" id=\"6\">\n"
			+ "        <lastUpdated class=\"sql-timestamp\" id=\"7\">2014-04-17 08:12:52.806</lastUpdated>\n"
			+ "        <id>1</id>\n" + "        <userId>test</userId>\n"
			+ "        <domain>uu</domain>\n" + "        <firstName>Test</firstName>\n"
			+ "        <lastName>TEstsson</lastName>\n"
			+ "        <email>test.testsson@ub.uu.se</email>\n" + "      </user>\n"
			+ "      <note>Place created through web gui</note>\n" + "      <type>CREATED</type>\n"
			+ "    </created>\n" + "    <updated id=\"8\">\n"
			+ "      <userAction reference=\"3\"/>\n" + "      <userAction id=\"9\">\n"
			+ "        <date id=\"10\">2014-12-18 22:18:01.276 UTC</date>\n"
			+ "        <dateInStorage id=\"11\">2014-12-18 22:18:01.579 UTC</dateInStorage>\n"
			+ "        <user class=\"seamUser\" id=\"12\">\n"
			+ "          <lastUpdated class=\"sql-timestamp\" id=\"13\">2014-04-17 08:12:52.806</lastUpdated>\n"
			+ "          <id>1</id>\n" + "          <userId>test</userId>\n"
			+ "          <domain>uu</domain>\n" + "          <firstName>Test</firstName>\n"
			+ "          <lastName>Testsson</lastName>\n"
			+ "          <email>test.testsson@ub.uu.se</email>\n" + "        </user>\n"
			+ "        <note>Place updated through web gui</note>\n"
			+ "        <type>UPDATED</type>\n" + "      </userAction>\n"
			+ "      <userAction id=\"14\">\n"
			+ "        <date id=\"15\">2016-02-12 10:29:43.147 UTC</date>\n"
			+ "        <user class=\"seamUser\" id=\"16\">\n"
			+ "          <lastUpdated class=\"sql-timestamp\" id=\"17\">2014-04-17 08:12:53.455</lastUpdated>\n"
			+ "          <id>318</id>\n" + "          <userId>different</userId>\n"
			+ "          <domain>lu</domain>\n" + "          <firstName>Different</firstName>\n"
			+ "          <lastName>Test</lastName>\n"
			+ "          <email>test.testsson@ub.lu.se</email>\n" + "        </user>\n"
			+ "        <note>Place updated through web gui</note>\n"
			+ "        <type>UPDATED</type>\n" + "      </userAction>\n" + "    </updated>\n"
			+ "  </recordInfo>\n" + "  <country class=\"country\">\n"
			+ "    <lastUpdated class=\"sql-timestamp\" id=\"19\">2014-04-17 08:12:48.8</lastUpdated>\n"
			+ "    <defaultName>Sverige</defaultName>\n" + "    <localisedNames id=\"20\">\n"
			+ "      <entry>\n" + "        <string>en</string>\n"
			+ "        <string>Sweden</string>\n" + "      </entry>\n" + "    </localisedNames>\n"
			+ "    <alpha2Code>SE</alpha2Code>\n" + "    <alpha3Code>SWE</alpha3Code>\n"
			+ "    <numericalCode>752</numericalCode>\n" + "    <marcCode>sw</marcCode>\n"
			+ "  </country>\n" + "  <regions id=\"21\"/>\n" + "  <defaultPlaceName id=\"22\">\n"
			+ "    <deleted>false</deleted>\n" + "    <name>Lund</name>\n"
			+ "  </defaultPlaceName>\n" + "  <placeNameForms id=\"23\">\n" + "    <entry>\n"
			+ "      <string>alvin-place:24:0</string>\n" + "      <placeNameForm id=\"24\">\n"
			+ "        <id>alvin-place:24:0</id>\n" + "        <deleted>false</deleted>\n"
			+ "        <name>Londini Gothorum</name>\n" + "        <language id=\"25\">\n"
			+ "          <lastUpdated class=\"sql-timestamp\" id=\"26\">2014-04-17 08:12:51.134</lastUpdated>\n"
			+ "          <defaultName>Latin</defaultName>\n"
			+ "          <localisedNames id=\"27\">\n" + "            <entry>\n"
			+ "              <string>en</string>\n" + "              <string>Latin</string>\n"
			+ "            </entry>\n" + "          </localisedNames>\n"
			+ "          <alpha3Code>lat</alpha3Code>\n" + "          <alpha2Code>la</alpha2Code>\n"
			+ "        </language>\n" + "      </placeNameForm>\n" + "    </entry>\n"
			+ "  </placeNameForms>\n" + "  <identifiers id=\"28\"/>\n"
			+ "  <longitude>13.18</longitude>\n" + "  <latitude>55.7</latitude>\n"
			+ "  <localIdentifiers>\n" + "    <localIdentifier>\n"
			+ "      <type class=\"localIdentifierType\">\n"
			+ "        <lastUpdated class=\"sql-timestamp\">2014-04-17 08:49:50.65</lastUpdated>\n"
			+ "        <defaultName>Waller-id</defaultName>\n" + "        <localisedNames/>\n"
			+ "        <code>waller</code>\n" + "        <id>114</id>\n"
			+ "        <internal>false</internal>\n"
			+ "        <organisationUnitId>2</organisationUnitId>\n"
			+ "        <unique>false</unique>\n" + "      </type>\n" + "      <text>873</text>\n"
			+ "    </localIdentifier>\n" + "  </localIdentifiers>\n" + "</place>\n" + "";
	private DivaToCoraPlaceConverter converter;

	@BeforeMethod
	public void beforeMethod() {
		converter = new DivaToCoraPlaceConverter();
	}

	@Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting place to Cora place: Can not read xml: "
			+ "The element type \"pid\" must be terminated by the matching end-tag \"</pid>\".")
	public void parseExceptionShouldBeThrownOnMalformedXML() throws Exception {
		String xml = "<pid></notPid>";
		converter.fromXML(xml);
	}

	@Test
	public void convertFromXML() throws Exception {
		DataGroup placeDataGroup = converter.fromXML(place22XML);
		assertEquals(placeDataGroup.getNameInData(), "authority");
		DataGroup recordInfo = placeDataGroup.getFirstGroupWithNameInData("recordInfo");
		DataGroup type = recordInfo.getFirstGroupWithNameInData("type");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordType"), "recordType");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordId"), "place");

		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "alvin");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "alvin-place:22");

		DataGroup createdBy = recordInfo.getFirstGroupWithNameInData("createdBy");
		assertEquals(createdBy.getFirstAtomicValueWithNameInData("linkedRecordType"), "user");
		assertEquals(createdBy.getFirstAtomicValueWithNameInData("linkedRecordId"), "12345");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("tsCreated"),
				"2014-12-18 20:20:38.346");

		DataGroup updatedBy = recordInfo.getFirstGroupWithNameInData("updatedBy");
		assertEquals(updatedBy.getFirstAtomicValueWithNameInData("linkedRecordType"), "user");
		assertEquals(updatedBy.getFirstAtomicValueWithNameInData("linkedRecordId"), "12345");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("tsUpdated"),
				"2014-12-18 20:21:20.880");

		DataGroup defaultName = placeDataGroup.getFirstGroupWithNameInData("name");
		assertEquals(defaultName.getAttribute("type"), "authorized");
		DataGroup defaultNamePart = defaultName.getFirstGroupWithNameInData("namePart");
		assertEquals(defaultNamePart.getAttribute("type"), "defaultName");
		assertEquals(defaultNamePart.getFirstAtomicValueWithNameInData("value"), "Linköping");

		DataGroup coordinates = placeDataGroup.getFirstGroupWithNameInData("coordinates");
		assertEquals(coordinates.getFirstAtomicValueWithNameInData("latitude"), "58.42");
		assertEquals(coordinates.getFirstAtomicValueWithNameInData("longitude"), "15.62");

	}

	@Test
	public void convertFromXML24() throws Exception {
		DataGroup placeDataGroup = converter.fromXML(place24XML);
		assertEquals(placeDataGroup.getNameInData(), "authority");
		DataGroup recordInfo = placeDataGroup.getFirstGroupWithNameInData("recordInfo");
		DataGroup type = recordInfo.getFirstGroupWithNameInData("type");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordType"), "recordType");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordId"), "place");

		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "alvin");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "alvin-place:24");

		DataGroup createdBy = recordInfo.getFirstGroupWithNameInData("createdBy");
		assertEquals(createdBy.getFirstAtomicValueWithNameInData("linkedRecordType"), "user");
		assertEquals(createdBy.getFirstAtomicValueWithNameInData("linkedRecordId"), "12345");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("tsCreated"),
				"2014-12-18 22:16:44.623");

		DataGroup updatedBy = recordInfo.getFirstGroupWithNameInData("updatedBy");
		assertEquals(updatedBy.getFirstAtomicValueWithNameInData("linkedRecordType"), "user");
		assertEquals(updatedBy.getFirstAtomicValueWithNameInData("linkedRecordId"), "12345");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("tsUpdated"),
				"2016-02-12 10:29:43.147");

		DataGroup defaultName = placeDataGroup.getFirstGroupWithNameInData("name");
		assertEquals(defaultName.getAttribute("type"), "authorized");
		DataGroup defaultNamePart = defaultName.getFirstGroupWithNameInData("namePart");
		assertEquals(defaultNamePart.getAttribute("type"), "defaultName");
		assertEquals(defaultNamePart.getFirstAtomicValueWithNameInData("value"), "Lund");

		DataGroup coordinates = placeDataGroup.getFirstGroupWithNameInData("coordinates");
		assertEquals(coordinates.getFirstAtomicValueWithNameInData("latitude"), "55.7");
		assertEquals(coordinates.getFirstAtomicValueWithNameInData("longitude"), "13.18");

	}
}
