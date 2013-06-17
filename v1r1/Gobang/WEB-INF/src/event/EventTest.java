package event;

import gobang.Leaguer;

public class EventTest implements Runnable {
	@Override
	public void run() {
		for (int i = 0; i < 10000; i++) {
			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			EventManager.INSTANCE.addEvent(new Event("event_" + 100, new Leaguer(
					i, "user" + i, "pass" + i)));
		}
	}
}
