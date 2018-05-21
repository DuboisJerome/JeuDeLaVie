package fr.jeudelavie.util;

public class PublicObservable extends java.util.Observable {
	@Override
	public synchronized void setChanged() {
		super.setChanged();
	}

	@Override
	public synchronized void clearChanged() {
		super.clearChanged();
	}
}
