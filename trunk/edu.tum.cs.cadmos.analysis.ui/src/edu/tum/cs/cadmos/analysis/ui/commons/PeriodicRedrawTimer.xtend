package edu.tum.cs.cadmos.analysis.ui.commons

import org.eclipse.swt.widgets.Control
import edu.tum.cs.cadmos.language.common.Assert

class PeriodicRedrawTimer {

	new(Control clientControl, int periodMillis, int freeCPUMillis, =>Boolean ready) {
		Assert.assertWithinRange(periodMillis, 0, Integer.MAX_VALUE, "periodMillis")
		Assert.assertWithinRange(freeCPUMillis, 0, periodMillis, "freeCPUMillis")
		Assert.assertNotNull(ready, "ready")
		clientControl.display.timerExec(periodMillis,
			[ |
				if(clientControl.disposed) return;
				var t = System.nanoTime
				if (ready.apply) {
					clientControl.redraw
					clientControl.update
				}
				t = System.nanoTime - t
				var nextPeriod = periodMillis - (t / 1_000_000).intValue
				nextPeriod = Math.max(freeCPUMillis, nextPeriod)
				clientControl.display.timerExec(nextPeriod, self)
			])
	}

}
