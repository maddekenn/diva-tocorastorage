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

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.spider.record.storage.RecordStorage;

public final class DivaToCoraRecordStorage implements RecordStorage {

	private static final String PLACE = "place";
	private HttpHandlerFactory httpHandlerFactory;
	private String baseURL;
	private DivaToCoraConverterFactory converterFactory;

	private DivaToCoraRecordStorage(HttpHandlerFactory httpHandlerFactory,
			DivaToCoraConverterFactory converterFactory, String baseURL) {
		this.httpHandlerFactory = httpHandlerFactory;
		this.converterFactory = converterFactory;
		this.baseURL = baseURL;
	}

	public static DivaToCoraRecordStorage usingHttpHandlerFactoryAndConverterFactoryAndFedoraBaseURL(
			HttpHandlerFactory httpHandlerFactory, DivaToCoraConverterFactory converterFactory,
			String baseURL) {
		return new DivaToCoraRecordStorage(httpHandlerFactory, converterFactory, baseURL);
	}

	@Override
	public DataGroup read(String type, String id) {
		if (PLACE.equals(type)) {
			return readAndConvertPlaceFromFedora(id);
		}
		throw NotImplementedException.withMessage("read is not implemented for type: " + type);
	}

	private DataGroup readAndConvertPlaceFromFedora(String id) {
		HttpHandler httpHandler = createHttpHandlerForPlace(id);
		DivaToCoraConverter toCoraConverter = converterFactory.factor(PLACE);
		return toCoraConverter.fromXML(httpHandler.getResponseText());
	}

	private HttpHandler createHttpHandlerForPlace(String id) {
		String url = baseURL + "objects/" + id + "/datastreams/METADATA/content";
		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		httpHandler.setRequestMethod("GET");
		return httpHandler;
	}

	@Override
	public void create(String type, String id, DataGroup record, DataGroup collectedTerms,
			DataGroup linkList, String dataDivider) {
		throw NotImplementedException.withMessage("create is not implemented");
	}

	@Override
	public void deleteByTypeAndId(String type, String id) {
		throw NotImplementedException.withMessage("deleteByTypeAndId is not implemented");
	}

	@Override
	public boolean linksExistForRecord(String type, String id) {
		throw NotImplementedException.withMessage("linksExistForRecord is not implemented");
	}

	@Override
	public void update(String type, String id, DataGroup record, DataGroup collectedTerms,
			DataGroup linkList, String dataDivider) {
		throw NotImplementedException.withMessage("update is not implemented");
	}

	@Override
	public Collection<DataGroup> readList(String type, DataGroup filter) {
		if (PLACE.equals(type)) {
			return readAndConvertPlaceListFromFedora();
		}
		throw NotImplementedException.withMessage("readList is not implemented for type: " + type);
	}

	private Collection<DataGroup> readAndConvertPlaceListFromFedora() {
		try {
			return tryReadAndConvertPlaceListFromFedora();
		} catch (Exception e) {
			throw ReadFedoraException
					.withMessageAndException("Unable to read list of places: " + e.getMessage(), e);
		}
	}

	private Collection<DataGroup> tryReadAndConvertPlaceListFromFedora() {
		String placeListXML = getPlaceListXMLFromFedora();
		NodeList list = extractNodeListWithPidsFromXML(placeListXML);
		return constructCollectionOfPlacesFromFedora(list);
	}

	private String getPlaceListXMLFromFedora() {
		HttpHandler httpHandler = createHttpHandlerForPlaceList();
		return httpHandler.getResponseText();
	}

	private HttpHandler createHttpHandlerForPlaceList() {
		String url = baseURL
				+ "objects?pid=true&maxResults=100&resultFormat=xml&query=pid%7Ealvin-place:*";
		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		httpHandler.setRequestMethod("GET");
		return httpHandler;
	}

	private NodeList extractNodeListWithPidsFromXML(String placeListXML) {
		XMLXPathParser parser = XMLXPathParser.forXML(placeListXML);
		return parser
				.getNodeListFromDocumentUsingXPath("/result/resultList/objectFields/pid/text()");
	}

	private Collection<DataGroup> constructCollectionOfPlacesFromFedora(NodeList list) {
		Collection<DataGroup> placeList = new ArrayList<>();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			String pid = node.getTextContent();
			placeList.add(readAndConvertPlaceFromFedora(pid));
		}
		return placeList;
	}

	@Override
	public Collection<DataGroup> readAbstractList(String type, DataGroup filter) {
		throw NotImplementedException.withMessage("readAbstractList is not implemented");
	}

	@Override
	public DataGroup readLinkList(String type, String id) {
		throw NotImplementedException.withMessage("readLinkList is not implemented");
	}

	@Override
	public Collection<DataGroup> generateLinkCollectionPointingToRecord(String type, String id) {
		throw NotImplementedException
				.withMessage("generateLinkCollectionPointingToRecord is not implemented");
	}

	@Override
	public boolean recordsExistForRecordType(String type) {
		throw NotImplementedException.withMessage("recordsExistForRecordType is not implemented");
	}

	@Override
	public boolean recordExistsForAbstractOrImplementingRecordTypeAndRecordId(String type,
			String id) {
		throw NotImplementedException.withMessage(
				"recordExistsForAbstractOrImplementingRecordTypeAndRecordId is not implemented");
	}

}
