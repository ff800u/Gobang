package event;

public class Event {
	private String eventid;
	private Object data;

	public Event(String eventid, Object data) {
		this.eventid = eventid;
		this.data = data;
	}

	public String getEventid() {
		return eventid;
	}

	public void setEventid(String eventid) {
		this.eventid = eventid;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
