package fr.jeudelavie.modele.operation;

public class OperationCompare<T extends Comparable<T>> implements Operation<T> {
	public enum OperateurCompare {
		SUP, SUP_EGAL, INF, INF_EGAL, EGAL;
	}

	private final OperateurCompare ope;
	private final T valeurComparaison;

	public OperationCompare(final OperateurCompare ope, final T valeurComparaison) {
		super();
		this.ope = ope;
		this.valeurComparaison = valeurComparaison;
	}

	@Override
	public boolean tester(final T obj) {
		final int compare = obj.compareTo(this.valeurComparaison);
		switch (this.ope) {
		case EGAL:
			return compare == 0;
		case INF:
			return compare < 0;
		case INF_EGAL:
			return compare <= 0;
		case SUP:
			return compare > 0;
		case SUP_EGAL:
			return compare >= 0;
		default:
			return false;
		}
	}

	public static class LongOperationCompare extends OperationCompare<Long> {
		public LongOperationCompare(final OperateurCompare ope, final Long l) {
			super(ope, l);
		}

		public LongOperationCompare(final OperateurCompare ope, final long l) {
			this(ope, Long.valueOf(l));
		}

		public boolean tester(final long l) {
			return this.tester(Long.valueOf(l));
		}
	}
}