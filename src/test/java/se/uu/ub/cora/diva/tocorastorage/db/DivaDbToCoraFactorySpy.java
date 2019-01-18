package se.uu.ub.cora.diva.tocorastorage.db;

public class DivaDbToCoraFactorySpy implements DivaDbToCoraFactory {
	public boolean factorWasCalled = false;
	public DivaDbToCoraSpy factored;
	public String type;

	@Override
	public DivaDbToCora factor(String type) {
		factorWasCalled = true;
		this.type = type;
		factored = new DivaDbToCoraSpy();
		return factored;
	}

}
