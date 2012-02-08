package demos.search;

import vitro.grid.Location;

public class ConstantCostFunction<E> implements CostFunction<E> {
	public double value(E e) {
		return 1;
	}
}
