package fr.jeudelavie.modele.operation;

@FunctionalInterface
public interface Operation<T> {
	public boolean tester(T obj);
}
