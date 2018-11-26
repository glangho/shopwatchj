package io.github.glangho.shopwatchj;

public interface WatchListener {

	public void listen(WatchEvent event);

	public void handle(Exception e);

}
