package fr.jeudelavie.modele.operation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import lombok.Getter;

public abstract class OperationComposite<T> implements Operation<T> {

	@Getter
	protected final List<Operation<T>> operations = new ArrayList<>();

	public boolean isEmpty() {
		return CollectionUtils.isEmpty(this.operations);
	}

	public static class OperationAND<T> extends OperationComposite<T> {
		@Override
		public boolean tester(final T t) {
			return !isEmpty() && this.operations.stream().allMatch(o -> o.tester(t));
		}
	}

	public static class OperationOR<T> extends OperationComposite<T> {
		@Override
		public boolean tester(final T t) {
			return !isEmpty() && this.operations.stream().anyMatch(o -> o.tester(t));
		}
	}

}
