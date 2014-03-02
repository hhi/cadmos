package edu.tum.cs.cadmos.analysis.schedule;

import java.util.HashMap;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Pair;

import edu.tum.cs.cadmos.analysis.architecture.model.Edge;
import edu.tum.cs.cadmos.analysis.architecture.model.Vertex;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class ScheduleSMTParser {

	public static HashMap<EObject, Pair<String, Integer>> parse(String content,
			DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG) {
		final HashMap<EObject, Pair<String, Integer>> schedule = new HashMap<>();

		if (!content.startsWith("sat")) {
			return null;
		}

		for (Vertex v : softwareComponentDFG.getVertices()) {
			int index = content.indexOf("start" + v.getId());
			if (index >= 0) {
				index += 5 + v.getId().length() + 5;
				while (!Character.isDigit(content.charAt(index))) {
					++index;
				}
				int index2 = index;
				while (Character.isDigit(content.charAt(index2))) {
					++index2;
				}

				int startTime = Integer.parseInt(content.substring(index,
						index2));
				final Pair<String, Integer> p = new Pair<>(null, startTime);
				schedule.put(v.getData(), p);
			}
		}

		for (Vertex v : softwareComponentDFG.getVertices()) {
			int index = content.indexOf("mapping" + v.getId());
			if (index >= 0) {
				index += 7 + v.getId().length() + 5;
				int platEndIndex = content.indexOf(")", index);

				String platform = "";
				while (content.charAt(--platEndIndex) != ' ') {
					platform = content.charAt(platEndIndex) + platform;
				}

				Pair<String, Integer> p = schedule.get(v.getData());
				if (p != null) {
					p = new Pair<>(platform, p.getValue());
					schedule.put(v.getData(), p);
				}
			}
		}

		return schedule;
	}

}
