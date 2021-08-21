package io.vertigo.ai.example.iris;

import io.vertigo.core.node.config.discovery.ModuleDiscoveryFeatures;

public class IrisFeature extends ModuleDiscoveryFeatures<IrisFeature> {

	protected IrisFeature(String name) {
		super(name);
	}

	@Override
	protected String getPackageRoot() {
		return this.getClass().getPackage().getName();
	}

	
}
