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

package edu.tum.cs.cadmos.matlab.core.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import edu.tum.cs.cadmos.core.model.ICompositeComponent;
import edu.tum.cs.commons.logging.SimpleLogger;
import edu.tum.cs.simulink.builder.SimulinkModelBuilder;
import edu.tum.cs.simulink.builder.SimulinkModelBuildingException;
import edu.tum.cs.simulink.model.SimulinkBlock;
import edu.tum.cs.simulink.model.SimulinkModel;
import edu.tum.cs.simulink.model.SimulinkPortBase;

/**
 * A TestSimulinkUtils.
 * 
 * @author
 * @version $Rev: $
 * @version $Author: $
 * @version $Date: $
 * @ConQAT.Rating RED Hash:
 */
public class TestSimulinkUtils {

	public SimulinkModel getModelFromFile(String filename) {

		final String path = "SimulinkModels/" + filename;
		final File f = new File(getClass().getResource(path).getFile());

		final SimulinkModelBuilder build = new SimulinkModelBuilder(f,
				new SimpleLogger());
		SimulinkModel model = null;
		try {
			model = build.buildModel();
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final SimulinkModelBuildingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return model;
	}

	@Test
	public void test_DelayPath() {

		SimulinkModel m = getModelFromFile("MemoryConqatTo2.mdl");

		for (final SimulinkBlock b : m.getSubBlocks()) {
			System.out.println(b.getParameter("SampleTime"));
		}

		Map<SimulinkPortBase, Integer> list = SimulinkUtils.getDelayPaths(m
				.getSubBlock("In1").getOutLines().iterator().next(), 0);

		Assert.assertTrue(list.size() == 1);
		System.out.println(list);

		m = getModelFromFile("ForkMemory3Edges323.mdl");
		list = SimulinkUtils.getDelayPaths(m.getSubBlock("In1").getOutLines()
				.iterator().next(), 0);
		System.out.println(list);

		ICompositeComponent c = MatlabSimulinkTranslation
				.translateSimulinkModel(m);

		System.out.println(c);

		System.out.println(c.getAllChannels().isEmpty());
		System.out.println(c.getAllPorts().toList());

		m = getModelFromFile("SubSystem1Block.mdl");
		c = MatlabSimulinkTranslation.translateSimulinkModel(m);

		System.out.println(c);
		System.out.println("Channels: "
				+ c.getChildren().iterator().next().getAllPorts().iterator()
						.next().getIncoming());

	}
}
