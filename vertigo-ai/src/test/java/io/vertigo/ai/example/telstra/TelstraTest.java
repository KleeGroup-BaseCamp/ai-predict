package io.vertigo.ai.example.telstra;

import io.vertigo.ai.MyNodeConfig;
import io.vertigo.core.node.config.NodeConfig;

public class TelstraTest extends AbstractTelstraTest {

	@Override
	protected void doSetUp() {
		init();
	}

	@Override
	protected NodeConfig buildNodeConfig() {
		return MyNodeConfig.config("telstra");
	}

}

