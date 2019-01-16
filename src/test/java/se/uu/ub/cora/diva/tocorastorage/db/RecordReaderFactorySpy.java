package se.uu.ub.cora.diva.tocorastorage.db;

import se.uu.ub.cora.sqldatabase.RecordReader;
import se.uu.ub.cora.sqldatabase.RecordReaderFactory;

public class RecordReaderFactorySpy implements RecordReaderFactory {

	public boolean factorWasCalled;
	public RecordReaderSpy factored;
	public int noOfRecordsToReturn = 1;
	// public boolean returnPredecessors = false;
	public int numOfPredecessorsToReturn = 0;
	public int numOfSuccessorsToReturn = 0;

	@Override
	public RecordReader factor() {
		factorWasCalled = true;
		factored = new RecordReaderSpy();
		factored.numOfOredecessorsToReturn = numOfPredecessorsToReturn;
		factored.numOfSuccessorsToReturn = numOfSuccessorsToReturn;
		factored.noOfRecordsToReturn = noOfRecordsToReturn;
		return factored;
	}

}
