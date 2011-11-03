package edu.tum.cs.cadmos.commons.core;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.tum.cs.cadmos.commons.core.Assert;

public class TestAssert {

	@Test
	public void test_assertTrue_OK() {
		Assert.assertTrue(true, "%s", "");
	}

	@Test
	public void test_assertTrue_ERR() {
		try {
			Assert.assertTrue(false, "%s", "ERROR");
			throw new Error(
					"Expected Assert.assertTrue(false, ...) to throw an AssertionError");
		} catch (final AssertionError e) {
			junit.framework.Assert.assertEquals("ERROR", e.getMessage());
		}
	}

	protected static class ClassA {
		/* Only for testing purposes of assertInstanceOf(...). */
	}

	protected static class ClassB extends ClassA {
		/* Only for testing purposes of assertInstanceOf(...). */
	}

	@Test
	public void test_assertInstanceOf_OK() {
		final ArrayList<Integer> list = new ArrayList<>();
		Assert.assertInstanceOf(list, List.class, "list");
	}

	@Test
	public void test_assertInstanceOf_B_A_OK() {
		final ClassB b = new ClassB();
		Assert.assertInstanceOf(b, ClassA.class, "b");
	}

	@Test
	public void test_assertInstanceOf_A_B_ERR() {
		final ClassA a = new ClassA();
		try {
			Assert.assertInstanceOf(a, ClassB.class, "a");
			throw new Error(
					"Expected Assert.assertInstanceOf(a, ClassB...) to throw an AssertionError");
		} catch (final AssertionError e) {
			/* As expected. */
		}
	}

}
