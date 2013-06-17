package gobang;

public class Leaguer {
	private long id;
	private String name;
	private String password;
	private int score = 1200;
	private LeaguerState state = LeaguerState.WAITING;

	public Leaguer() {
	}

	public Leaguer(long id, String name, String password) {
		this.id = id;
		this.name = name;
		this.password = password;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public LeaguerState getState() {
		return state;
	}

	public void setState(LeaguerState state) {
		this.state = state;
	}
}
