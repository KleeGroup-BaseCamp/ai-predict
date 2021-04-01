package io.vertigo.ai;

import io.vertigo.ai.data.ItemPredictClient;
import io.vertigo.ai.data.TestPredictSmartTypes;
import io.vertigo.ai.data.domain.ItemDatasetLoader;
import io.vertigo.ai.withstore.ItemDatasetStoreLoader;
import io.vertigo.commons.CommonsFeatures;
import io.vertigo.core.node.config.BootConfig;
import io.vertigo.core.node.config.DefinitionProviderConfig;
import io.vertigo.core.node.config.ModuleConfig;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.node.config.NodeConfigBuilder;
import io.vertigo.core.param.Param;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.database.DatabaseFeatures;
import io.vertigo.database.impl.sql.vendor.h2.H2DataBase;
import io.vertigo.datamodel.DataModelFeatures;
import io.vertigo.datamodel.impl.smarttype.ModelDefinitionProvider;
import io.vertigo.datastore.DataStoreFeatures;

public final class MyNodeConfig {
	
	public static NodeConfig config(final boolean withDb) {
		
		final AIFeatures aiFeatures = new AIFeatures()
				.withAIPredictBackend(
									Param.of("server.name", "http://127.0.0.1:8000/predict/"));
		
		final NodeConfigBuilder nodeConfigBuilder = NodeConfig.builder()
				.withBoot(BootConfig.builder()
						.withLocales("fr_FR")
						.addPlugin(ClassPathResourceResolverPlugin.class)
						.build())
				.addModule(new CommonsFeatures()
						.withScript()
						.withJaninoScript()
						.build());
		
		if (withDb) {
			nodeConfigBuilder.addModule(new DatabaseFeatures()
					.withSqlDataBase()
					.withC3p0(
							Param.of("dataBaseClass", H2DataBase.class.getName()),
							Param.of("jdbcDriver", "org.h2.Driver"),
							Param.of("jdbcUrl", "jdbc:h2:mem:database"))
					.build());
		}
		
		nodeConfigBuilder.addModule(new DataModelFeatures().build());
		
		if (withDb) {
			nodeConfigBuilder.addModule(new DataStoreFeatures()
					.withCache()
					.withMemoryCache()
					.withEntityStore()
					.withSqlEntityStore()
					.build());
		}
		
		nodeConfigBuilder.addModule(aiFeatures.build())
			.addModule(ModuleConfig.builder("myApp")
					.addComponent(ItemPredictClient.class)
					.addComponent(ItemDatasetLoader.class)
					.addDefinitionProvider(DefinitionProviderConfig.builder(ModelDefinitionProvider.class)
							.addDefinitionResource("smarttypes", TestPredictSmartTypes.class.getName())
							.addDefinitionResource("dtobjects", "io.vertigo.ai.data.DtDefinitions")
							.build())
					.addComponent(withDb ? io.vertigo.ai.withstore.ItemDatasetStoreLoader.class : ItemDatasetStoreLoader.class)
					.addDefinitionProvider(StoreCacheDefinitionProvider.class)
					.build());
		return nodeConfigBuilder.build();
		
	}
}
