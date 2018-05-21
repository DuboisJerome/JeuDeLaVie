package fr.jeudelavie.modele.cellule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Getter;

public class GroupeReproduction {

	@Getter
	private final String nom;

	private final Map<TypeEtreVivant, Integer> typesCreesPonderes = new HashMap<>();

	private final List<ConditionReproduction> conditionsReproduction = new ArrayList<>();

	public GroupeReproduction(final String nom, final TypeEtreVivant typeCree,
			final List<ConditionReproduction> conditionsReproduction) {
		this.nom = nom;
		this.typesCreesPonderes.put(typeCree, Integer.valueOf(1));
		this.conditionsReproduction.addAll(conditionsReproduction);
	}

	public GroupeReproduction(final String nom, final Map<TypeEtreVivant, Integer> typesCreesPonderes,
			final List<ConditionReproduction> conditionsReproduction) {
		this.nom = nom;
		this.typesCreesPonderes.putAll(typesCreesPonderes);
		this.conditionsReproduction.addAll(conditionsReproduction);
	}

	public boolean isReproductible(final TypeEtreVivant ancienType, final Map<TypeEtreVivant, Long> map) {
		final List<ConditionReproduction> listeConditionsApplicables = this.conditionsReproduction.stream()
				.filter(c -> c.isConditionAAppliquer(ancienType)).collect(Collectors.toList());
		return listeConditionsApplicables.size() > 0
				&& listeConditionsApplicables.stream().allMatch(c -> c.isConditionValide(map));
	}

	public TypeEtreVivant seReproduire() {
		final List<TypeEtreVivant> typesCrees = new ArrayList<>();
		this.typesCreesPonderes.entrySet().stream().forEach(e -> {
			IntStream.range(0, e.getValue().intValue()).forEach(i -> typesCrees.add(e.getKey()));
		});
		return typesCrees.get(new Random().nextInt(typesCrees.size()));
	}

	public ConditionReproduction ajouterCondition(final TypeEtreVivant typePrecondition) {
		final ConditionReproduction c = new ConditionReproduction(typePrecondition);
		this.conditionsReproduction.add(c);
		return c;
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
