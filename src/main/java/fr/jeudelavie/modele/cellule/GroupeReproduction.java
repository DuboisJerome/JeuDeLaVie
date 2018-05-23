package fr.jeudelavie.modele.cellule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import fr.jeudelavie.modele.operation.Operation;
import fr.jeudelavie.modele.operation.OperationCompare.LongOperationCompare;
import fr.jeudelavie.modele.operation.OperationCompare.OperateurCompare;
import lombok.Getter;

public class GroupeReproduction {

	@Getter
	private final String nom;

	private final Map<TypeEtreVivant, Integer> typesCreesPonderes = new HashMap<>();

	private final Map<TypeEtreVivant, List<Operation<Long>>> conditionsReproduction = new HashMap<>();

	public GroupeReproduction(final String nom, final TypeEtreVivant typeCree) {
		this.nom = nom;
		this.typesCreesPonderes.put(typeCree, Integer.valueOf(1));
		this.conditionsReproduction.putAll(this.conditionsReproduction);
	}

	public GroupeReproduction(final String nom, final Map<TypeEtreVivant, Integer> typesCreesPonderes) {
		this.nom = nom;
		this.typesCreesPonderes.putAll(typesCreesPonderes);
		this.conditionsReproduction.putAll(this.conditionsReproduction);
	}

	public boolean isApplicable(final EtreVivant etreVivant) {
		if (etreVivant == null || etreVivant.getType() == null) return false;
		final List<Operation<Long>> operations = this.conditionsReproduction.get(etreVivant.getType());
		// si il faut une quantité différente de 0 c'est que ce groupe
		// s'applique à ce type
		return !isConditionValide(0L, operations);
	}

	public boolean isReproductible(final Map<TypeEtreVivant, Long> map) {
		return this.conditionsReproduction.size() > 0 && this.conditionsReproduction.entrySet().stream().allMatch(e -> {
			final TypeEtreVivant type = e.getKey();
			final List<Operation<Long>> list = e.getValue();
			if (TypeEtreVivant.ANY.equals(type)) {
				return map.values().stream().allMatch(v -> isConditionValide(v, list));
			} else if (TypeEtreVivant.ALL.equals(type)) {
				final long q = map.values().stream().filter(l -> l != null).mapToLong(l -> l.longValue()).sum();
				return isConditionValide(Long.valueOf(q), e.getValue());
			} else {
				final Long qNullable = map.get(type);
				return isConditionValide(qNullable, e.getValue());
			}
		});
	}

	private static boolean isConditionValide(final long q, final List<Operation<Long>> list) {
		return isConditionValide(Long.valueOf(q), list);
	}

	private static boolean isConditionValide(final Long qNullable, final List<Operation<Long>> list) {
		final Long q = qNullable == null ? Long.valueOf(0L) : qNullable;
		return list == null || list.stream().allMatch(o -> o.tester(q));
	}

	public TypeEtreVivant seReproduire() {
		final List<TypeEtreVivant> typesCrees = new ArrayList<>();
		this.typesCreesPonderes.entrySet().stream().forEach(e -> {
			IntStream.range(0, e.getValue().intValue()).forEach(i -> typesCrees.add(e.getKey()));
		});
		return typesCrees.get(new Random().nextInt(typesCrees.size()));
	}

	public void ajouterCondition(final TypeEtreVivant typeATester, final OperateurCompare operateur,
			final long quantite) {
		ajouterCondition(typeATester, new LongOperationCompare(operateur, quantite));
	}

	public void ajouterCondition(final TypeEtreVivant typeATester, final Operation<Long> ope) {
		List<Operation<Long>> liste = this.conditionsReproduction.get(typeATester);
		if (liste == null) {
			liste = new ArrayList<>();
			this.conditionsReproduction.put(typeATester, liste);
		}
		liste.add(ope);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof GroupeReproduction)) return false;
		final GroupeReproduction autre = (GroupeReproduction) obj;
		return this.nom.equals(autre.nom);
	}

	@Override
	public int hashCode() {
		return this.nom.hashCode();
	}

	@Override
	public String toString() {
		return this.nom + " -> " + this.typesCreesPonderes.toString();
	}
}
