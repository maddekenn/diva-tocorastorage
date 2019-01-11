package se.uu.ub.cora.diva.tocorastorage.db;

import se.uu.ub.cora.sqldatabase.RecordReader;
import se.uu.ub.cora.sqldatabase.RecordReaderFactory;

public class RecordReaderFactorySpy implements RecordReaderFactory {

	public boolean factorWasCalled;
	public RecordReaderSpy factored;
	public int noOfRecordsToReturn = 1;
	public boolean returnPredecessors = false;

	@Override
	public RecordReader factor() {
		factorWasCalled = true;
		factored = new RecordReaderSpy();
		factored.returnPredecessors = returnPredecessors;
		factored.noOfRecordsToReturn = noOfRecordsToReturn;
		return factored;
	}

}
