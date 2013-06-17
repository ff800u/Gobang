package gobang;

import java.util.HashMap;
import java.util.Map;

public enum LeaguerManager implements Manager {
	INSTANCE;

	private Map<Long, Leaguer> leaguerMap = new HashMap<Long, Leaguer>();

	public Map<String, Object> getAllLeaguer() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(AjaxResKey.ALL_LEAGUERS.getKey(), leaguerMap.values());
		return ret;
	}

	public Map<String, Object> addNewLeaguer(Leaguer newOne) {

		return null;
	}

	public Map<String, Object> loginLeaguer(Leaguer newOne) {

		return null;
	}

	public Map<String, Object> competeWithLeaguer(Leaguer a, Leaguer b) {

		return null;
	}

	@Override
	public void initialized() {
		for (long i = 0; i < 3; i++) {
			Leaguer a = new Leaguer(i, "test" + i, "password" + i);
			leaguerMap.put(i, a);
		}
	}

	@Override
	public void destroyed() {
		// TODO Auto-generated method stub

	}

	private enum AjaxResKey {
		ALL_LEAGUERS("allLeaguers");
		private String keyWord;

		private AjaxResKey(String keyWord) {
			this.keyWord = keyWord;
		}

		public String getKey() {
			return this.keyWord;
		}
	}
}
