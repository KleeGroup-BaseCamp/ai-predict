package io.vertigo.ai;

import io.vertigo.ai.example.iris.IrisPAO;
import io.vertigo.ai.example.iris.ItemDefinitionProvider;
import io.vertigo.ai.example.iris.dao.IrisDAO;
import io.vertigo.ai.example.iris.data.IrisSmartTypes;
import io.vertigo.ai.example.iris.data.datageneration.IrisGenerator;
import io.vertigo.commons.CommonsFeatures;
import io.vertigo.commons.impl.transaction.VTransactionManagerImpl;
import io.vertigo.core.node.config.BootConfig;
import io.vertigo.core.node.config.DefinitionProviderConfig;
import io.vertigo.core.node.config.ModuleConfig;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.node.config.NodeConfigBuilder;
import io.vertigo.core.param.Param;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.database.DatabaseFeatures;
import io.vertigo.database.impl.sql.vendor.h2.H2DataBase;
import io.vertigo.database.impl.sql.vendor.postgresql.PostgreSqlDataBase;
import io.vertigo.datamodel.DataModelFeatures;
import io.vertigo.datamodel.impl.smarttype.ModelDefinitionProvider;
import io.vertigo.datastore.DataStoreFeatures;

public final class MyNodeConfig {
	
	public static NodeConfig config(final boolean withDb, final boolean iris) {
		
		final AIFeatures aiFeatures = new AIFeatures()
				.withAIPredictBackend(
									Param.of("server.name", "http://127.0.0.1:8000/"));
		
		final NodeConfigBuilder nodeConfigBuilder = NodeConfig.builder()
				.withBoot(BootConfig.builder()
						.withLocales("fr_FR")
						.addPlugin(ClassPathResourceResolverPlugin.class)
						.build())
				.addModule(new CommonsFeatures()
						.withScript()
						.withJaninoScript()
						.build());
		
		nodeConfigBuilder
			.addModule(new DatabaseFeatures()
					.withSqlDataBase()
					.withC3p0(
							Param.of("name", "train"),
							Param.of("dataBaseClass", PostgreSqlDataBase.class.getName()),
							Param.of("jdbcDriver", "org.postgresql.Driver"),
							Param.of("jdbcUrl", "jdbc:postgresql://127.0.0.1:5432/traindb?user=postgres&password=admin"))
					.withC3p0(
							Param.of("dataBaseClass", H2DataBase.class.getName()),
							Param.of("jdbcDriver", "org.h2.Driver"),
							Param.of("jdbcUrl", "jdbc:h2:mem:database"))
					.build());
		
		nodeConfigBuilder.addModule(new DataModelFeatures().build());
		
		nodeConfigBuilder.addModule(new DataStoreFeatures()
					.withCache()
					.withMemoryCache()
					.withEntityStore()
					.withSqlEntityStore()
					.build());

		nodeConfigBuilder.addModule(aiFeatures.build())
				.addModule(ModuleConfig.builder("myApp")
				.addComponent(ItemDefinitionProvider.class)
				.addComponent(IrisPAO.class)
				.addComponent(IrisDAO.class)
				.addComponent(IrisGenerator.class)
				.addDefinitionProvider(DefinitionProviderConfig.builder(ModelDefinitionProvider.class)
						.addDefinitionResource("smarttypes", IrisSmartTypes.class.getName())
						.addDefinitionResource("dtobjects", "io.vertigo.ai.example.domain.DtDefinitions")
						.build())
				.build());
		
		return nodeConfigBuilder.build();
		
	}
}
