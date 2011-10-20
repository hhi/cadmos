package edu.tum.cs.cadmos.core.utils;

import org.junit.Test;

import edu.tum.cs.cadmos.commons.Assert;

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

}
