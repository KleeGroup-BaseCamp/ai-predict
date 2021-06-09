package io.vertigo.ai.example.iris;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertigo.ai.example.iris.data.datageneration.IrisGenerator;
import io.vertigo.ai.structure.record.definitions.DatasetDefinition;
import io.vertigo.commons.impl.transaction.VTransactionManagerImpl;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.resource.ResourceManager;

@Transactional
public abstract class AbstractIrisTestManager {

	DatasetDefinition datasetDefinition;
	
	@Inject
	private IrisGenerator irisGenerator;
	
	private AutoCloseableNode node;
	
	protected final void init(final String datasetName) {
		final DefinitionSpace definitionSpace = node.getDefinitionSpace();

		datasetDefinition = definitionSpace.resolve(datasetName, DatasetDefinition.class);
	}
	
	@BeforeEach
	public final void setUp() {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
		//--
		doSetUp();
	}
	
	@AfterEach
	public final void tearDown() {
		if (node != null) {
			node.close();
		}
	}
	
	protected abstract void doSetUp();

	protected abstract NodeConfig buildNodeConfig();
	
	@Test
	public void createData() {
		irisGenerator.createIrisFromCSV();
	}
}
