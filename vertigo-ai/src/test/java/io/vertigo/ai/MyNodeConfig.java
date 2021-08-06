package io.vertigo.ai;

import io.vertigo.ai.example.heroes.HeroesDefinitionProvider;
import io.vertigo.ai.example.heroes.dao.EraDAO;
import io.vertigo.ai.example.heroes.dao.FactionDAO;
import io.vertigo.ai.example.heroes.dao.HeroeDAO;
import io.vertigo.ai.example.heroes.data.HeroesSmartTypes;
import io.vertigo.ai.example.heroes.data.datageneration.HeroesGenerator;
import io.vertigo.ai.example.iris.ItemDefinitionProvider;
import io.vertigo.ai.example.iris.dao.IrisDAO;
import io.vertigo.ai.example.iris.dao.IrisTrainDAO;
import io.vertigo.ai.example.iris.data.IrisSmartTypes;
import io.vertigo.ai.example.iris.data.datageneration.IrisGenerator;
import io.vertigo.ai.example.iris.loader.IrisDatasetLoader;
import io.vertigo.ai.example.iris.services.IrisServices;
import io.vertigo.ai.example.train.TrainPAO;
import io.vertigo.ai.structure.record.definitions.RecordLoader;
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
	
	public static NodeConfig config(final String object) {
		
		final AIFeatures aiFeatures = new AIFeatures()
				.withAIPredictBackend(
									Param.of("server.name", "http://127.0.0.1:8000/"))
				.withSqlProcessing(
									Param.of("dataSpace", "train"));
		
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

					.withMigration(Param.of("mode", "update"))
					.withLiquibaseDataBaseMigrationPlugin(
							Param.of("masterFile", "/liquibase/master.xml"))
					.withLiquibaseDataBaseMigrationPlugin(
							Param.of("masterFile", "/liquibase/train.xml"),
							Param.of("connectionName", "train"))
					.build());
		
		nodeConfigBuilder.addModule(new DataModelFeatures().build());
		
		nodeConfigBuilder.addModule(new DataStoreFeatures()
					.withCache()
					.withMemoryCache()
					.withEntityStore()
					.withSqlEntityStore(
							Param.of("dataSpace", "train"),
							Param.of("connectionName", "train"))
					.withSqlEntityStore()
					.build());

		if (object=="iris") {
			nodeConfigBuilder.addModule(aiFeatures.build())
					.addModule(ModuleConfig.builder("myApp")
					.addComponent(ItemDefinitionProvider.class)
					.addComponent(IrisDAO.class)
					.addComponent(IrisTrainDAO.class)
					.addComponent(TrainPAO.class)
					.addComponent(IrisGenerator.class)
					.addComponent(RecordLoader.class, IrisDatasetLoader.class)
					.addComponent(IrisServices.class)
					.addDefinitionProvider(DefinitionProviderConfig.builder(ModelDefinitionProvider.class)
							.addDefinitionResource("smarttypes", IrisSmartTypes.class.getName())
							.addDefinitionResource("smarttypes", HeroesSmartTypes.class.getName())
							.addDefinitionResource("dtobjects", "io.vertigo.ai.example.domain.DtDefinitions")
							.build())
					.build());
		} else if (object=="heroes") {
			nodeConfigBuilder.addModule(aiFeatures.build())
				.addModule(ModuleConfig.builder("myApp")
				.addComponent(HeroesDefinitionProvider.class)
				.addComponent(HeroesGenerator.class)
				.addComponent(HeroeDAO.class)
				.addComponent(FactionDAO.class)
				.addComponent(EraDAO.class)
				.addDefinitionProvider(DefinitionProviderConfig.builder(ModelDefinitionProvider.class)
						.addDefinitionResource("smarttypes", IrisSmartTypes.class.getName())
						.addDefinitionResource("smarttypes", HeroesSmartTypes.class.getName())
						.addDefinitionResource("dtobjects", "io.vertigo.ai.example.domain.DtDefinitions")
						.build())
				.build());
		}
		
		return nodeConfigBuilder.build();
	}
}
