package se.uu.ub.cora.diva.tocorastorage.db;

public class DivaDbToCoraFactorySpy implements DivaDbToCoraFactory {
	public boolean factorWasCalled = false;
	public DivaDbToCoraSpy factored;

	@Override
	public DivaDbToCora factor() {
		factorWasCalled = true;
		factored = new DivaDbToCoraSpy();
		return factored;
	}

}
