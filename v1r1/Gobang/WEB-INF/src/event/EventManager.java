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
 * ʹ����ѯ�������¼����ͣ���̨�����¼��ַ���֧�ֶ�session�㲥
 * 
 * @author aliang
 * 
 */
public enum EventManager {
	INSTANCE;
	/** �Ự���ỰӦ��ȡ�¼�����ʼindex ӳ��� */
	private Map<String, Integer> pageid2Index;

	/** �¼����� */
	private List<EventNode> eventCache;

	/**
	 * �Ѿ���������¼������������µĻỰ���ɣ� ��Ҫ��ֵ�������ûỰ�ܹ���ȡ�ĵĳ�ʼ�¼�λ��<br>
	 * ��Ҫÿ�λỰȡ�¼����ض�λ�������¼������β<br>
	 * �����¼���Ҫ��ֵ��Ϊindex
	 */
	private int cacheTotalEventNumber;

	/** Ϊÿ���Ự����һ����ʱ�������Ựδ��ʱ����ѯ�¼�����ʱ��ע������ѯ�ʸ� */
	private Map<String, Timer> pageid2Timer;

	/** ʮ�벻����ѯ�¼�����Ϊ�¼����Ӷ�ʧ��ע������ѯ�ʸ� */
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
	 * ע���¼����շ�
	 * 
	 * @param pageid
	 *            �ỰID
	 */
	public void registerPage(String pageid) {
		// ���ӻỰ���ûỰ��ʼ�¼�index ��ӳ��
		pageid2Index.put(pageid, cacheTotalEventNumber);
		LogRecoder.EVENT.warn("register event polling. pageid : " + pageid
				+ " , event start index : " + pageid2Index.get(pageid)); // ��ʼ���Ự��ʱ����
		resetPageWatch(pageid);
	}

	/**
	 * ע���¼����շ�
	 * 
	 * @param pageid
	 *            �ỰID
	 */
	public void unregisterPage(long pageid) {
		pageid2Index.remove(pageid);
	}

	public synchronized void addEvent(Event event) {
		eventCache.add(new EventNode(cacheTotalEventNumber++, event));
	}

	/**
	 * ��ȡ�¼��б���������£���ȡ������������һ��
	 * 
	 * @param pageid
	 *            �ỰID
	 * @return �ûỰ��Ӧ���¼��б�
	 */
	public synchronized List<EventNode> getEvents(String pageid) {
		Integer index = pageid2Index.get(pageid);
		if (index == null) {
			return null;
		}
		/*
		 * ÿ�οͻ�����ȡ�¼����͸���һ�γ�ʱ���� �������SESSION_TIME_OUT��û����ȡ����Ϊ���پ����¼����ӣ��Ƴ���ע��
		 */
		resetPageWatch(pageid);
		// ��ȡ�¼��б�
		List<EventNode> list = getEventList(index);
		// �����´��¼����index
		pageid2Index.put(pageid, cacheTotalEventNumber);
		// �Ƴ�����session���Ѿ�ȡ�ߵ��¼�
		clearOldEvent();
		// �����¼��б�
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
		 * �Ự��ʱ����
		 * 
		 * @param pageid
		 *            �ỰID
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
