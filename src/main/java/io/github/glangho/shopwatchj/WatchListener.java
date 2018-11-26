package io.github.glangho.shopwatchj;

public interface WatchListener {

	public void listen(WatchEvent event);

	public void notifyErrors(Exception e);

	public void notifyResolved();

}
