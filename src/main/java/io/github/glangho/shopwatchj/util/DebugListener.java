package io.github.glangho.shopwatchj.util;

import io.github.glangho.shopwatchj.WatchEvent;
import io.github.glangho.shopwatchj.WatchListener;
import io.github.glangho.shopwatchj.WatchStatus;
import io.github.glangho.shopwatchj.shopify.Variant;

public class DebugListener implements WatchListener {

	@Override
	public void listen(WatchEvent event) {
		if (event != null) {
			StringBuilder sb = new StringBuilder();

			String pTitle = event.getProduct().getTitle();
			sb.append(pTitle);

			for (Variant variant : event.getProduct().getVariants()) {
				sb.append("\n");
				sb.append("\t");

				WatchStatus stock = event.getUpdates().get(variant);

				sb.append(variant.getTitle() + " " + stock + " " + variant.getInventoryQuantity());
			}

			System.out.println(sb.toString());
		}
	}

	@Override
	public void notifyErrors(Exception e) {
		e.printStackTrace(System.out);
	}

	@Override
	public void notifyResolved() {
		System.out.println("Everything seems in order.");
	}

}
