package io.vertigo.ai;

import io.vertigo.ai.example.data.ItemDefinitionProvider;
import io.vertigo.ai.example.data.SmartTypes;
import io.vertigo.ai.predict.data.ItemPredictClient;
import io.vertigo.ai.predict.data.TestPredictSmartTypes;
import io.vertigo.ai.predict.data.domain.ItemDatasetLoader;
import io.vertigo.ai.predict.withstore.ItemDatasetStoreLoader;
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
import io.vertigo.database.impl.sql.vendor.postgresql.PostgreSqlDataBase;
import io.vertigo.datamodel.DataModelFeatures;
import io.vertigo.datamodel.impl.smarttype.ModelDefinitionProvider;
import io.vertigo.datastore.DataStoreFeatures;

public final class MyNodeConfig {
	
	public static NodeConfig config(final boolean withDb, final boolean example) {
		
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
		
		if (withDb) {
			nodeConfigBuilder
				.addModule(new DatabaseFeatures()
						.withSqlDataBase()
						.withC3p0(
								Param.of("name", "train"),
								Param.of("dataBaseClass", PostgreSqlDataBase.class.getName()),
								Param.of("jdbcDriver", "org.postgresql.Driver"),
								Param.of("jdbcUrl", "jdbc:postgresql://127.0.0.1:5432/traindb?user=postgres&password=admin"))
								//Param.of("jdbcUrl", "jdbc:postgresql://docker-vertigo.part.klee.lan.net:5432/postgres?user=postgres&password=postgres"))
						.withC3p0(
								Param.of("dataBaseClass", H2DataBase.class.getName()),
								Param.of("jdbcDriver", "org.h2.Driver"),
								Param.of("jdbcUrl", "jdbc:h2:mem:database"))
						.build());
		} else {
			nodeConfigBuilder
			.addModule(new DatabaseFeatures()
					.withSqlDataBase()
					.withC3p0(
							Param.of("dataBaseClass", PostgreSqlDataBase.class.getName()),
							Param.of("jdbcDriver", "org.postgresql.Driver"),
							Param.of("jdbcUrl", "jdbc:postgresql://127.0.0.1:5432/traindb?user=postgres&password=admin"))
							//Param.of("jdbcUrl", "jdbc:postgresql://docker-vertigo.part.klee.lan.net:5432/postgres?user=postgres&password=postgres"))
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
		
		if (example) {
			nodeConfigBuilder.addModule(aiFeatures.build())
					.addModule(ModuleConfig.builder("myApp")
					.addComponent(ItemDefinitionProvider.class)
					.addDefinitionProvider(DefinitionProviderConfig.builder(ModelDefinitionProvider.class)
							.addDefinitionResource("smarttypes", SmartTypes.class.getName())
							.addDefinitionResource("dtobjects", "io.vertigo.ai.example.data.domain.DtDefinitions")
							.build())
					.build());
			
		} else {
			nodeConfigBuilder.addModule(aiFeatures.build())
			.addModule(ModuleConfig.builder("myApp")
					.addComponent(ItemPredictClient.class)
					.addComponent(ItemDatasetLoader.class)
					.addDefinitionProvider(DefinitionProviderConfig.builder(ModelDefinitionProvider.class)
							.addDefinitionResource("smarttypes", TestPredictSmartTypes.class.getName())
							.addDefinitionResource("dtobjects", "io.vertigo.ai.predict.data.DtDefinitions")
							.build())
					.addComponent(withDb ? io.vertigo.ai.predict.withstore.ItemDatasetStoreLoader.class : ItemDatasetStoreLoader.class)
					.addDefinitionProvider(StoreCacheDefinitionProvider.class)
					.build());
		}
		return nodeConfigBuilder.build();
		
	}
}
