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

package edu.tum.cs.cadmos.commons.ui.rendering;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link SWTRenderer}.
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public class TestSWTRenderer {

	protected Thread uiThread;

	protected Display display;

	protected Shell shell;

	protected Canvas canvas;

	protected Object ready;

	protected SWTRenderer renderer;

	@Before
	public void setup() throws Exception {
		uiThread = new Thread() {
			@Override
			public void run() {
				display = new Display();
				shell = new Shell(display);
				shell.setSize(640, 480);
				shell.setLayout(new FillLayout());
				canvas = new Canvas(shell, SWT.DOUBLE_BUFFERED);
				renderer = new SWTRenderer(canvas);

				shell.open();
				synchronized (ready) {
					ready.notifyAll();
				}

				while (!shell.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}
			}
		};
		ready = new Object();
		uiThread.start();
		synchronized (ready) {
			ready.wait();
		}
	}

	@Test
	public void testCommands() throws Exception {
		final Image image = new Image(display, 100, 100);
		final int pixel = image.getImageData().getPixel(0, 0);
		image.getImageData().palette.getRGB(pixel);

		final RenderingContext ctx = new RenderingContext();
		final Rect union = new Rect(0, 0, 2, 2);
		final Rect box = new Rect(1, 1, 8, 8);
		final Rect area = new Rect(0, 0, 10, 10);
		ctx.setFillColor(DefaultColors.WHITE).setNoDraw();
		ctx.paintRectangle(null, area);
		ctx.setArrows(EArrowStyle.NONE, EArrowStyle.NORMAL);
		ctx.setDrawColor(DefaultColors.BLACK);
		ctx.setSolidDraw();
		for (int i = 1; i < 10; i++) {
			ctx.drawConnector(null, new Rect(3, 3, 6, 6), 1, 1, 1, 1,
					"Connector-" + i, -.3f * i);
		}
		ctx.setFillColor(DefaultColors.BLUE);
		ctx.paintRoundedRectangle(null, union.move(2, 2), .2f, .2f);
		ctx.paintRoundedRectangle(null, union.move(5, 5), .2f, .2f);
		ctx.setDrawColor(DefaultColors.VERY_DARK_BLUE);
		ctx.setFillColor(DefaultColors.WHITE);
		ctx.setArrows(EArrowStyle.NORMAL, EArrowStyle.NONE);
		ctx.drawLine(null, box.topLine());
		ctx.setArrows(EArrowStyle.NONE, EArrowStyle.STEALTH);
		ctx.drawLine(null, box.bottomLine());
		renderer.setContext(ctx);
		redrawCanvas();
		Thread.sleep(15000);
		closeShell();
	}

	private void closeShell() {
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				shell.close();
			}
		});
	}

	private void redrawCanvas() {
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				canvas.redraw();
			}
		});
	}
}
