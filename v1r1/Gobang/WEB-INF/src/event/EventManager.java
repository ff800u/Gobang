package event;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import log.LogRecoder;

/**
 * 使用轮询来进行事件推送，后台进行事件分发，支持多session广播
 * 
 * @author aliang
 * 
 */
public enum EventManager {
	INSTANCE;
	/** 会话到会话应该取事件的起始index 映射表 */
	private Map<String, Integer> pageid2Index;

	/** 事件缓存 */
	private List<EventNode> eventCache;

	/**
	 * 已经缓存过的事件的总数，当新的会话生成， 需要该值索引到该会话能够获取的的初始事件位置<br>
	 * 需要每次会话取事件后，重定位索引到事件缓存队尾<br>
	 * 新增事件需要该值作为index
	 */
	private int cacheTotalEventNumber;

	/** 为每个会话保存一个定时器，当会话未按时来轮询事件，则定时器注销其轮询资格 */
	private Map<String, Timer> pageid2Timer;

	/** 十秒不来轮询事件，认为事件连接丢失，注销其轮询资格 */
	private static final long PAGE_TIME_OUT = 10 * 1000;

	private EventManager() {
		initialized();
	}

	private void initialized() {
		pageid2Index = Collections
				.synchronizedMap(new HashMap<String, Integer>());
		eventCache = Collections.synchronizedList(new LinkedList<EventNode>());
		cacheTotalEventNumber = 0;
		pageid2Timer = Collections
				.synchronizedMap(new HashMap<String, Timer>());

		test();
	}

	private void test() {
		Thread t = new Thread(new EventTest());
		t.setName("add event");
		t.setDaemon(true);
		t.start();
	}

	public void destroyed() {
		pageid2Index.clear();
		eventCache.clear();
		cacheTotalEventNumber = 0;
		for (Timer timer : pageid2Timer.values()) {
			timer.cancel();
		}
		pageid2Timer.clear();
	}

	/**
	 * 注册事件接收方
	 * 
	 * @param pageid
	 *            会话ID
	 */
	public void registerPage(String pageid) {
		// 增加会话到该会话初始事件index 的映射
		pageid2Index.put(pageid, cacheTotalEventNumber);
		LogRecoder.EVENT.warn("register event polling. pageid : " + pageid
				+ " , event start index : " + pageid2Index.get(pageid)); // 初始化会话超时监听
		resetPageWatch(pageid);
	}

	/**
	 * 注销事件接收方
	 * 
	 * @param pageid
	 *            会话ID
	 */
	public void unregisterPage(long pageid) {
		pageid2Index.remove(pageid);
	}

	public synchronized void addEvent(Event event) {
		eventCache.add(new EventNode(cacheTotalEventNumber++, event));
	}

	/**
	 * 获取事件列表，正常情况下，将取至缓存队列最后一个
	 * 
	 * @param pageid
	 *            会话ID
	 * @return 该会话对应的事件列表
	 */
	public synchronized List<EventNode> getEvents(String pageid) {
		Integer index = pageid2Index.get(pageid);
		if (index == null) {
			return null;
		}
		/*
		 * 每次客户端来取事件，就更新一次超时监听 如果超过SESSION_TIME_OUT还没有来取，认为不再具有事件连接，移除其注册
		 */
		resetPageWatch(pageid);
		// 获取事件列表
		List<EventNode> list = getEventList(index);
		// 设置下次事件起点index
		pageid2Index.put(pageid, cacheTotalEventNumber);
		// 移除所有session都已经取走的事件
		clearOldEvent();
		// 返回事件列表
		return list;
	}

	private void resetPageWatch(String pageid) {
		Timer oldTimer = pageid2Timer.get(pageid);
		if (oldTimer != null) {
			oldTimer.cancel();
		}
		Timer newTimer = new Timer();
		newTimer.schedule(new PageTimeoutTask(pageid), PAGE_TIME_OUT);
		pageid2Timer.put(pageid, newTimer);
	}

	private List<EventNode> getEventList(int index) {
		if (eventCache.isEmpty()) {
			return new LinkedList<EventNode>();
		}
		EventNode firstEvent = eventCache.get(0);
		int firstIndex = firstEvent.getIndex();
		if (index <= firstIndex) {
			return new LinkedList<EventNode>(eventCache);
		}
		if (index >= firstIndex + eventCache.size()) {
			return new LinkedList<EventNode>();
		}
		int offsetOfIndex = index - firstIndex;
		return new LinkedList<EventNode>(eventCache.subList(offsetOfIndex,
				eventCache.size()));
	}

	private void clearOldEvent() {
		if (eventCache.isEmpty()) {
			return;
		}
		List<Integer> indexList = new LinkedList<Integer>(pageid2Index.values());
		Collections.sort(indexList);
		if (!indexList.isEmpty()) {
			int offsetOfNextList = indexList.get(0)
					- eventCache.get(0).getIndex();
			eventCache = eventCache
					.subList(offsetOfNextList, eventCache.size());
		}
	}

	private class PageTimeoutTask extends TimerTask {
		private String pageid;

		/**
		 * 会话超时任务
		 * 
		 * @param pageid
		 *            会话ID
		 */
		public PageTimeoutTask(String pageid) {
			super();
			LogRecoder.EVENT.error("page : " + pageid + " time out.");
			this.pageid = pageid;
		}

		@Override
		public void run() {
			pageid2Index.remove(pageid);
		}
	}
}
