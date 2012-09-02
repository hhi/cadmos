/*--------------------------------------------------------------------------+
|                                                                          |
| Copyright 2008-2012 Technische Universitaet Muenchen                     |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+--------------------------------------------------------------------------*/

package edu.tum.cs.cadmos.common;

import java.util.Collection;

/**
 * Assertions that build a possible error message "lazily", that is, only if
 * conditions <b>do not hold</b>.
 * <p>
 * Usually, lazy message-construction reduces the overhead associated with
 * message-construction in the "normal" case, where the condition is
 * <i>true</i>. Additionally, this may reduce potential
 * {@link NullPointerException}s and other errors that can happen during
 * message-construction, although the condition may be <code>true</code>.
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 */
public class Assert {

	/**
	 * Asserts that the given <i>condition</i> is <code>true</code> and throws
	 * an {@link AssertionError} otherwise.
	 * <p>
	 * Note, that the message string for the assertion error will only be built
	 * and formatted if the condition does not hold.
	 * 
	 * @throws AssertionError
	 *             if <i>condition</i> is <code>false</code>.
	 */
	public static void assertTrue(boolean condition, String message,
			Object... args) {
		if (!condition) {
			throw new AssertionError(String.format(message, args));
		}
	}

	/**
	 * Asserts that the given <i>object</i> is <code>null</code> and throws an
	 * {@link AssertionError} otherwise.
	 * <p>
	 * The message will be formatted with the given <i>objectName</i> if the
	 * <i>object</i> is not <code>null</code>.
	 * <p>
	 * Example: <code>assertNull(x, "x");</code> throws an assertion error with
	 * message "Expected 'x' to be null".
	 * 
	 * @throws AssertionError
	 *             if the <i>object</i> is not <code>null</code>.
	 */
	public static void assertNull(Object object, String objectName) {
		if (object != null) {
			throw new AssertionError(String.format("Expected '%s' to be null",
					objectName));
		}
	}

	/**
	 * Asserts that the given <i>object</i> is not <code>null</code> and throws
	 * an {@link AssertionError} otherwise.
	 * <p>
	 * The message will be formatted with the given <i>objectName</i> if the
	 * <i>object</i> is <code>null</code>.
	 * <p>
	 * Example: <code>assertNotNull(null, "x");</code> throws an assertion error
	 * with message "Expected 'x' to be not null".
	 * 
	 * @throws AssertionError
	 *             if the <i>object</i> is <code>null</code>.
	 */
	public static void assertNotNull(Object object, String objectName) {
		if (object == null) {
			throw new AssertionError(String.format(
					"Expected '%s' to be not null", objectName));
		}
	}

	/**
	 * Asserts that the given <i>element</i> is not contained in the given
	 * <i>collection</i> and throws an {@link AssertionError} otherwise.
	 * <p>
	 * The message will be formatted with the given <i>elementName</i> and
	 * <i>collectionName</i> if the <i>element</i> is contained in the
	 * <i>collection</i>.
	 * <p>
	 * Example: <code>assertNotContainedIn(e, c, "e", "c");</code> throws an
	 * assertion error with message
	 * "Element 'e' (e) is already present in collection 'c' ([..., e, ...])".
	 * 
	 * @param <E>
	 * 
	 * @throws AssertionError
	 *             if the <i>element</i> is contained in the <i>collection</i>.
	 */
	public static <E> void assertNotContainedIn(E element,
			Collection<E> collection, String elementName, String collectionName) {
		if (collection.contains(element)) {
			throw new AssertionError(
					String.format(
							"Element '%s' (%s) is already present in collection '%s' (%s)",
							elementName, element, collectionName, collection));
		}
	}

	/**
	 * Asserts that the given <i>element</i> is contained in the given
	 * <i>collection</i> and throws an {@link AssertionError} otherwise.
	 * <p>
	 * The message will be formatted with the given <i>elementName</i> and
	 * <i>collectionName</i> if the <i>element</i> is contained in the
	 * <i>collection</i>.
	 * <p>
	 * Example: <code>assertContainedIn(e, c, "e", "c");</code> throws an
	 * assertion error with message
	 * "Element 'e' (e) is not present in collection 'c' ([...])".
	 * 
	 * @param <E>
	 * 
	 * @throws AssertionError
	 *             if the <i>element</i> is not contained in the
	 *             <i>collection</i>.
	 */
	public static <E> void assertContainedIn(E element,
			Collection<E> collection, String elementName, String collectionName) {
		if (collection.contains(element)) {
			throw new AssertionError(String.format(
					"Element '%s' (%s) is not present in collection '%s' (%s)",
					elementName, element, collectionName, collection));
		}
	}

	/**
	 * Asserts that the given <i>instance</i> is an instance of the given
	 * <i>superClass</i>, that is "
	 * <code><i>instance</i> <b>instanceof</b> <i>superClass</i></code>" holds,
	 * and throws an {@link AssertionError} otherwise.
	 * 
	 * @throws AssertionError
	 *             if the given <i>instance</i> is not an instance of the given
	 *             <i>superClass</i>.
	 */
	public static void assertInstanceOf(Object instance, Class<?> superClass,
			String instanceName) {
		if (instance == null
				|| !superClass.isAssignableFrom(instance.getClass())) {
			throw new AssertionError(
					String.format(
							"Expected '%s'(%s) to be an instance of '%s', but was '%s'",
							instanceName, instance, superClass,
							instance == null ? "null" : instance.getClass()));
		}
	}

	/**
	 * Asserts that the given <i>value</i> is within the range given by
	 * [<i>min</i>..<i>max</i>] and throws an {@link AssertionError} otherwise.
	 * 
	 * @throws AssertionError
	 *             if <i>value</i> is less than <i>min</i> or greater than
	 *             <i>max</i>.
	 */
	public static void assertWithinRange(double value, double min, double max,
			String valueName) {
		if (value < min || value > max) {
			throw new AssertionError(String.format(
					"Expected '%s' to be within [%s..%s], but was %s",
					valueName, min, max, value));
		}
	}

	/**
	 * Asserts that invocation of this statement fails. <br>
	 * Useful e.g. in <i>default</i> cases of switch statements to indicate an
	 * uncovered case.
	 * 
	 * @throws AssertionError
	 *             on invocation.
	 */
	public static void fails(String message, Object... args) {
		throw new AssertionError(String.format(message, args));
	}

}