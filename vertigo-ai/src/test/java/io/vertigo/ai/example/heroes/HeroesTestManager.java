package io.vertigo.ai.example.heroes;

import io.vertigo.ai.MyNodeConfig;
import io.vertigo.core.node.config.NodeConfig;

public final class HeroesTestManager extends AbstractHeroesTestManager {

	private static final String DS_DATASET = "DsHeroes";
	@Override
	protected void doSetUp() {
		init(DS_DATASET);
	}

	@Override
	protected NodeConfig buildNodeConfig() {
		return MyNodeConfig.config("heroes");
	}
}
