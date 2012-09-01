package edu.tum.cs.cadmos.common;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestListUtils {

	@Test
	public void test_concat() {
		final List<Integer> list1 = Arrays.asList(1, 2);
		final List<Integer> list2 = Arrays.asList(3, 4);
		final List<Integer> result = ListUtils.concat(list1, list2);
		assertThat(result.size(), is(4));
		assertThat("expecting all items in order", result, hasItems(1, 2, 3, 4));
		assertThat("expecting new result", result, not(sameInstance(list1)));
		assertThat("expecting new result", result, not(sameInstance(list2)));
		assertTrue("expecting modifiable result", result.add(5));
	}

	@Test
	public void test_filter() {
		final List<Object> list = new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(3f);
		list.add(4f);
		list.add("5");
		list.add("6");
		final List<Integer> integers = ListUtils.filter(list, Integer.class);
		assertThat(integers.size(), is(2));
		assertThat(integers, hasItems(1, 2));
		final List<Float> floats = ListUtils.filter(list, Float.class);
		assertThat(floats.size(), is(2));
		assertThat(floats, hasItems(3f, 4f));
		final List<Double> doubles = ListUtils.filter(list, Double.class);
		assertThat(doubles.size(), is(0));
	}
}
