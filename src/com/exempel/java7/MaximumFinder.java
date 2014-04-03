package com.exempel.java7;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
/**
 * @author syedshahul
 * @see : http://www.javacodegeeks.com/2013/02/java-7-forkjoin-framework-example
 * .html
 */
public class MaximumFinder extends RecursiveTask<Integer> {

	private static final int SEQUENTIAL_THRESHOLD = 5;

	private final Integer[] data;
	private final int start;
	private final int end;

	public MaximumFinder(Integer[] data, int start, int end) {
		this.data = data;
		this.start = start;
		this.end = end;
	}

	public MaximumFinder(Integer[] data) {
		this(data, 0, data.length);
	}

	@Override
	protected Integer compute() {
		final int length = end - start;
		if (length < SEQUENTIAL_THRESHOLD) {
			return computeDirectly();
		}
		final int split = length / 2;
		final MaximumFinder left = new MaximumFinder(data, start, start + split);
		left.fork();
		final MaximumFinder right = new MaximumFinder(data, start + split, end);
		return Math.max(right.compute(), left.join());
	}

	private Integer computeDirectly() {
		System.out.println(Thread.currentThread() + " computing array position : " +
				                    start + " to " + end);
		int max = Integer.MIN_VALUE;
		for (int i = start; i < end; i++) {
			if (data[i] > max) {
				max = data[i];
			}
		}
		return max;
	}

	public static void main(String[] args) {
		// create a random data set
		final Integer[] data = new Integer[1000];
		final Random random = new Random();
		for (int i = 0; i < data.length; i++) {
			data[i] = random.nextInt(100);
		}
		System.out.println("Total object : "+data.length+"\nData : "+Arrays.asList
				(data)
				.toString());
		// submit the task to the pool
		final ForkJoinPool pool = new ForkJoinPool(4);
		final MaximumFinder finder = new MaximumFinder(data);
		System.out.println("Maximum number : "+pool.invoke(finder));
	}
}
