package fr.jeudelavie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import fr.jeudelavie.modele.Parametres;
import fr.jeudelavie.modele.cellule.ConditionReproduction;
import fr.jeudelavie.modele.cellule.TypeEtreVivant;
import fr.jeudelavie.modele.operation.OperationCompare.OperateurCompare;

public class TypeEtreVivantTest {

	@Test
	public void testCombinaison() {
		final int nbEtat = 1;
		final int nbVoisines = 8;

		final long res = rec2(nbEtat, nbVoisines, 0);
	}

	private long rec2(final int nbEtat, final int nbVoisines, final long total) {
		final long res;
		if (total < nbVoisines) {
			res = IntStream.range(0, nbEtat).parallel().mapToLong(etat -> {
				final long nextTotal = total + 1;
				return rec2(nbEtat, nbVoisines, nextTotal);
			}).sum() + 1;
		} else if (total == nbVoisines) {
			res = 1L;
		} else {
			res = 0L;
		}
		return res;
	}

	@Test
	public void testCoupleCisGenre() {
		final TypeEtreVivant t1 = new TypeEtreVivant("T1");
		final TypeEtreVivant t2 = new TypeEtreVivant("T2");

		final ConditionReproduction cond = new ConditionReproduction(TypeEtreVivant.ANY);
		cond.ajouterSousCondition(t1, OperateurCompare.SUP_EGAL, 1);
		cond.ajouterSousCondition(t2, OperateurCompare.SUP_EGAL, 1);

		final List<ConditionReproduction> listeConditions = new ArrayList<>();
		listeConditions.add(cond);

		final Map<TypeEtreVivant, Integer> map = new HashMap<>();
		map.put(t1, Integer.valueOf(1));
		map.put(t2, Integer.valueOf(1));
		final Parametres p = new Parametres();
		p.ajouterGroupeReproduction("CisGenre", map, listeConditions);

		// t1 avec t2
		Collection<TypeEtreVivant> typesPresent = new ArrayList<>();
		typesPresent.add(t1);
		testNeDoitPasPouvoirSeReproduire(p, typesPresent);
		typesPresent.add(t2);
		testDoitPouvoirSeReproduire(p, typesPresent);

		// t2 avec t1
		typesPresent = new ArrayList<>();
		typesPresent.add(t2);
		testNeDoitPasPouvoirSeReproduire(p, typesPresent);
		typesPresent.add(t1);
		testDoitPouvoirSeReproduire(p, typesPresent);
	}

	@Test
	public void testCoupleHomosexuel() {
		final TypeEtreVivant t1 = new TypeEtreVivant("T1");

		final ConditionReproduction cond = new ConditionReproduction(TypeEtreVivant.ANY);
		cond.ajouterSousCondition(t1, OperateurCompare.SUP_EGAL, 2);

		final List<ConditionReproduction> listeConditions = new ArrayList<>();
		listeConditions.add(cond);

		final Parametres s = new Parametres();
		s.ajouterGroupeReproduction("Homosexuel", t1, listeConditions);

		// t1 avec t1
		final Collection<TypeEtreVivant> typesPresent = new ArrayList<>();
		typesPresent.add(t1);
		testNeDoitPasPouvoirSeReproduire(s, typesPresent);
		typesPresent.add(t1);
		testDoitPouvoirSeReproduire(s, typesPresent);
	}

	@Test
	public void testTuple() {
		final TypeEtreVivant t1 = new TypeEtreVivant("T1");
		final TypeEtreVivant t2 = new TypeEtreVivant("T2");
		final TypeEtreVivant t3 = new TypeEtreVivant("T3");

		final ConditionReproduction cond = new ConditionReproduction(TypeEtreVivant.ANY);
		cond.ajouterSousCondition(t1, OperateurCompare.SUP_EGAL, 1);
		cond.ajouterSousCondition(t2, OperateurCompare.SUP_EGAL, 1);
		cond.ajouterSousCondition(t3, OperateurCompare.SUP_EGAL, 1);

		final List<ConditionReproduction> listeConditions = new ArrayList<>();
		listeConditions.add(cond);

		final Map<TypeEtreVivant, Integer> map = new HashMap<>();
		map.put(t1, Integer.valueOf(1));
		map.put(t2, Integer.valueOf(1));
		map.put(t3, Integer.valueOf(1));
		final Parametres s = new Parametres();
		s.ajouterGroupeReproduction("Tuple1", map, listeConditions);

		// t1 avec t2 et t3
		Collection<TypeEtreVivant> typesPresent = new ArrayList<>();
		typesPresent.add(t1);
		testNeDoitPasPouvoirSeReproduire(s, typesPresent);
		typesPresent.add(t2);
		testNeDoitPasPouvoirSeReproduire(s, typesPresent);
		typesPresent.add(t3);
		testDoitPouvoirSeReproduire(s, typesPresent);

		// t2 avec t1 et t3
		typesPresent = new ArrayList<>();
		typesPresent.add(t2);
		testNeDoitPasPouvoirSeReproduire(s, typesPresent);
		typesPresent.add(t1);
		testNeDoitPasPouvoirSeReproduire(s, typesPresent);
		typesPresent.add(t3);
		testDoitPouvoirSeReproduire(s, typesPresent);

		// t3 avec t1 et t2
		typesPresent = new ArrayList<>();
		typesPresent.add(t3);
		testNeDoitPasPouvoirSeReproduire(s, typesPresent);
		typesPresent.add(t1);
		testNeDoitPasPouvoirSeReproduire(s, typesPresent);
		typesPresent.add(t2);
		testDoitPouvoirSeReproduire(s, typesPresent);

	}

	private static void testDoitPouvoirSeReproduire(final Parametres p, final Collection<TypeEtreVivant> col) {
		final TypeEtreVivant cree = p.seReproduire(null, get(col));
		final boolean isOk = cree != null;
		Assert.assertTrue(cree
				+ "<="
				+ p.getListeGroupes().get(0)
				+ " ne peut pas se reproduire avec "
				+ col
				+ " alors qu'il devrait pouvoir", isOk);
	}

	private static void testNeDoitPasPouvoirSeReproduire(final Parametres p, final Collection<TypeEtreVivant> col) {
		final TypeEtreVivant cree = p.seReproduire(null, get(col));
		final boolean isOk = cree == null;
		Assert.assertTrue(cree
				+ "<="
				+ p.getListeGroupes().get(0)
				+ " peut se reproduire avec "
				+ col
				+ " alors qu'il NE devrait PAS pouvoir", isOk);
	}

	private static Map<TypeEtreVivant, Long> get(final Collection<TypeEtreVivant> c) {
		return c.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}
}
