package io.vertigo.ai.example.iris;

import io.vertigo.core.node.config.discovery.ModuleDiscoveryFeatures;

public class IrisFeature extends ModuleDiscoveryFeatures<IrisFeature> {

	protected IrisFeature(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getPackageRoot() {
		// TODO Auto-generated method stub
		return this.getClass().getPackage().getName();
	}

	
}
