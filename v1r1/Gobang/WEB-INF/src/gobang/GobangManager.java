package gobang;

import event.EventManager;

public enum GobangManager implements Manager {
	INSTANCE;

	@Override
	public void initialized() {
		LeaguerManager.INSTANCE.initialized();
		CompetitionManager.INSTANCE.initialized();
	}

	@Override
	public void destroyed() {
		EventManager.INSTANCE.destroyed();
		CompetitionManager.INSTANCE.destroyed();
		LeaguerManager.INSTANCE.destroyed();
	}
}
