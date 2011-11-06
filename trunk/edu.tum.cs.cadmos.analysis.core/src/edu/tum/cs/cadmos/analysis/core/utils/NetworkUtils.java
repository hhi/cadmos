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

package edu.tum.cs.cadmos.analysis.core.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.tum.cs.cadmos.analysis.core.Network;
import edu.tum.cs.cadmos.core.model.IAtomicComponent;
import edu.tum.cs.cadmos.core.model.IChannel;

/**
 * A set of helper and utility methods for common network operations.
 * 
 * @author wolfgang.schwitzer
 * @author nvpopa@gmail.com
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash: 9784A0106717D734AA5BB7657F88D3A9
 */
public class NetworkUtils {

	/**
	 * Returns the union of all components that are the source or destination of
	 * any of the given <i>channels</i>.
	 */
	public static Collection<IAtomicComponent> getComponents(
			Collection<IChannel> channels) {
		final Collection<IAtomicComponent> components = new HashSet<>();
		for (final IChannel ch : channels) {
			components.add((IAtomicComponent) ch.getSrc());
			components.add((IAtomicComponent) ch.getDst());
		}
		return components;
	}

	/**
	 * Returns an arbitrary component from the given <i>network</i> or
	 * <code>null</code> if the given <i>network</i> has no components.
	 */
	public static IAtomicComponent getAnyComponent(Network network) {
		for (final IAtomicComponent comp : network.getAllComponents()) {
			return comp;
		}
		return null;
	}

	/**
	 * Returns the path described by the given list of <i>channels</i> as text.
	 * <p>
	 * Example: <code>[Channel(AB), Channel(BC)]</code> yields
	 * <code>"A->B->C"</code>
	 */
	public static String channelPathToString(List<IChannel> channels) {
		final StringBuilder s = new StringBuilder();
		for (int i = 0; i < channels.size(); i++) {
			s.append(channels.get(i).getSrc());
			s.append("->");
		}
		s.append(channels.get(channels.size() - 1).getDst());
		return s.toString();
	}

	/**
	 * Returns the path described by the given list of <i>components </i> as
	 * text.
	 * <p>
	 * Example: <code>[A, B, C]</code> yields <code>"A->B->C"</code>
	 */
	public static String componentPathToString(List<IAtomicComponent> components) {
		final StringBuilder s = new StringBuilder();
		for (int i = 0; i < components.size() - 1; i++) {
			s.append(components.get(i));
			s.append("->");
		}
		s.append(components.get(components.size() - 1));
		return s.toString();
	}

	/**
	 * Returns the component with the given unique <i>id</i> within the given
	 * <i>network</i> or <code>null</code> if no such component exists.
	 * 
	 * @throws IllegalArgumentException
	 *             if id is <code>null</code>.
	 */
	public static IAtomicComponent getComponent(Network network, Object id) {
		if (id == null) {
			throw new IllegalArgumentException("Expected id to be not null");
		}
		for (final IAtomicComponent component : network.getAllComponents()) {
			if (component.getId() != null && component.getId().equals(id)) {
				return component;
			}
		}
		return null;
	}

	/**
	 * Returns the channel with the given unique <i>id</i> within the given
	 * <i>network</i> or <code>null</code> if no such channel exists.
	 * 
	 * @throws IllegalArgumentException
	 *             if id is <code>null</code>.
	 */
	public static IChannel getEdge(Network network, Object id) {
		if (id == null) {
			throw new IllegalArgumentException("Expected id to be not null");
		}
		for (final IChannel ch : network.getAllChannels()) {
			if (ch.getId() != null && ch.getId().equals(id)) {
				return ch;
			}
		}
		return null;
	}

	/** Returns the given <i>network</i> as formatted string. */
	public static String toString(Network network) {
		final StringBuilder s = new StringBuilder();
		for (final IAtomicComponent component : network.getAllComponents()) {
			s.append(component.toString());
			s.append("\n");
			for (final IChannel channel : component.getOutgoing()) {
				s.append("\t");
				s.append(channel.toString());
				s.append(" -> ");
				s.append(channel.getDst().getName());
				s.append("\n");
			}
		}
		return s.toString();
	}

}
