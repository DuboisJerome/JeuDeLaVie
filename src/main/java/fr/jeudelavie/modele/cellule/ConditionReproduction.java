package fr.jeudelavie.modele.cellule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.jeudelavie.modele.operation.Operation;
import fr.jeudelavie.modele.operation.OperationCompare.LongOperationCompare;
import fr.jeudelavie.modele.operation.OperationCompare.OperateurCompare;
import lombok.Getter;

@Getter
public class ConditionReproduction {
	private final TypeEtreVivant typePrecondition;
	private final Map<TypeEtreVivant, List<Operation<Long>>> operations = new HashMap<>();

	public ConditionReproduction(final TypeEtreVivant typePrecondition) {
		this.typePrecondition = typePrecondition;
	}

	public boolean isConditionAAppliquer(final TypeEtreVivant type) {
		return TypeEtreVivant.ANY.equals(this.typePrecondition)
				|| (type == null && this.typePrecondition == null)
				|| (type != null && type.equals(this.typePrecondition));
	}

	public boolean isConditionValide(final Map<TypeEtreVivant, Long> map) {
		return this.operations.entrySet().stream().allMatch(e -> {
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

	private static boolean isConditionValide(final Long qNullable, final List<Operation<Long>> list) {
		final Long q = qNullable == null ? Long.valueOf(0L) : qNullable;
		return list == null || list.stream().allMatch(o -> o.tester(q));
	}

	public void ajouterSousCondition(final TypeEtreVivant typeATester, final OperateurCompare operateur,
			final long quantite) {
		ajouterSousCondition(typeATester, new LongOperationCompare(operateur, quantite));
	}

	public void ajouterSousCondition(final TypeEtreVivant typeATester, final Operation<Long> ope) {
		List<Operation<Long>> liste = this.operations.get(typeATester);
		if (liste == null) {
			liste = new ArrayList<>();
			this.operations.put(typeATester, liste);
		}
		liste.add(ope);
	}
}