package com.fuzzy.function;

@FunctionalInterface
public interface Consumer<T> {

	/**
	 * Performs this operation on the given argument.
	 *
	 * @param t the input argument
	 */
	void accept(T t);
}