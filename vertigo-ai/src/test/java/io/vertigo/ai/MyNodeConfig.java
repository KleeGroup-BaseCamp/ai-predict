package io.vertigo.ai;

import java.util.Arrays;
import java.util.Iterator;

import io.vertigo.ai.example.heroes.HeroesDefinitionProvider;
import io.vertigo.ai.example.heroes.HeroesPAO;
import io.vertigo.ai.example.heroes.dao.EraDAO;
import io.vertigo.ai.example.heroes.dao.FactionCountDAO;
import io.vertigo.ai.example.heroes.dao.FactionDAO;
import io.vertigo.ai.example.heroes.dao.HeroeDAO;
import io.vertigo.ai.example.heroes.data.HeroesSmartTypes;
import io.vertigo.ai.example.heroes.data.datageneration.HeroesGenerator;
import io.vertigo.ai.example.iris.ItemDefinitionProvider;
import io.vertigo.ai.example.iris.dao.IrisDAO;
import io.vertigo.ai.example.iris.dao.IrisTrainDAO;
import io.vertigo.ai.example.iris.data.IrisSmartTypes;
import io.vertigo.ai.example.iris.data.datageneration.IrisGenerator;
import io.vertigo.ai.example.iris.loader.IrisRecordLoader;
import io.vertigo.ai.example.iris.predict.IrisPredict;
import io.vertigo.ai.example.iris.services.IrisServices;
import io.vertigo.ai.example.telstra.TelstraDefinitionProvider;
import io.vertigo.ai.example.telstra.dao.EventTypeDAO;
import io.vertigo.ai.example.telstra.dao.EventTypeTrainDAO;
import io.vertigo.ai.example.telstra.dao.LocationDAO;
import io.vertigo.ai.example.telstra.dao.LocationTrainDAO;
import io.vertigo.ai.example.telstra.dao.LogFeatureDAO;
import io.vertigo.ai.example.telstra.dao.LogFeatureTrainDAO;
import io.vertigo.ai.example.telstra.dao.ResourceTypeDAO;
import io.vertigo.ai.example.telstra.dao.ResourceTypeTrainDAO;
import io.vertigo.ai.example.telstra.dao.SeverityTypeDAO;
import io.vertigo.ai.example.telstra.dao.SeverityTypeTrainDAO;
import io.vertigo.ai.example.telstra.data.TelstraSmartTypes;
import io.vertigo.ai.example.telstra.data.datageneration.TelstraGenerator;
import io.vertigo.ai.example.telstra.loader.TelstraEventTypeRecordLoader;
import io.vertigo.ai.example.telstra.loader.TelstraLocationRecordLoader;
import io.vertigo.ai.example.telstra.loader.TelstraLogFeatureRecordLoader;
import io.vertigo.ai.example.telstra.loader.TelstraResourceTypeRecordLoader;
import io.vertigo.ai.example.telstra.loader.TelstraSeverityTypeRecordLoader;
import io.vertigo.ai.example.telstra.services.TelstraServices;
import io.vertigo.ai.example.train.TrainPAO;
import io.vertigo.ai.structure.record.definitions.RecordLoader;
import io.vertigo.commons.CommonsFeatures;
import io.vertigo.connectors.httpclient.HttpClientFeatures;
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
import io.vertigo.vega.VegaFeatures;

public final class MyNodeConfig {

	private static final String WS_PORT = "8000";

	public static final class WsDtDefinitions implements Iterable<Class<?>> {
		@Override
		public Iterator<Class<?>> iterator() {
			return Arrays.asList(new Class<?>[] {
				IrisPredict.class
			}).iterator();
		}
	}

	public static NodeConfig config(final String configName) {

		final AIFeatures aiFeatures = new AIFeatures()
				.withAIPredictBackend(
						Param.of("server.name", "http://127.0.0.1:8000/"))
				.withSqlProcessing(
						Param.of("dataSpace", "train"),
						Param.of("connectionName", "train"));

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

		final VegaFeatures vegaFeatures = new VegaFeatures()
				.withWebServicesProxyClient();
		final HttpClientFeatures httpClientFeatures = new HttpClientFeatures()
				.withHttpClient(Param.of("urlPrefix", "http://localhost:" + MyNodeConfig.WS_PORT + ""));

		if (configName.equals("iris")) {
			nodeConfigBuilder
			.addModule(httpClientFeatures.build())
			.addModule(vegaFeatures.build())
			.addModule(aiFeatures.build())
			.addModule(ModuleConfig.builder("myApp")
					.addComponent(ItemDefinitionProvider.class)
					.addComponent(IrisDAO.class)
					.addComponent(IrisTrainDAO.class)
					.addComponent(TrainPAO.class)
					.addComponent(IrisGenerator.class)
					.addComponent(RecordLoader.class, IrisRecordLoader.class)
					.addComponent(IrisServices.class)
					.addDefinitionProvider(DefinitionProviderConfig.builder(ModelDefinitionProvider.class)
							.addDefinitionResource("smarttypes", IrisSmartTypes.class.getName()) 
							.addDefinitionResource("smarttypes", HeroesSmartTypes.class.getName()) // FIXME: io.vertigo.ai.example.domain.DtDefinition contient toutes les définitions
							.addDefinitionResource("dtobjects", "io.vertigo.ai.example.domain.DtDefinitions")
							.addDefinitionResource("dtobjects", WsDtDefinitions.class.getName())
							.build())
					.build());
		} else if (configName.equals("heroes")) {
			nodeConfigBuilder
			.addModule(httpClientFeatures.build())
			.addModule(vegaFeatures.build())
			.addModule(aiFeatures.build())
			.addModule(ModuleConfig.builder("myApp")
					.addComponent(HeroesDefinitionProvider.class)
					.addComponent(HeroesGenerator.class)
					.addComponent(HeroeDAO.class)
					.addComponent(FactionDAO.class)
					.addComponent(EraDAO.class)
					.addComponent(HeroesPAO.class)
					.addComponent(FactionCountDAO.class)
					.addDefinitionProvider(DefinitionProviderConfig.builder(ModelDefinitionProvider.class)
							.addDefinitionResource("smarttypes", IrisSmartTypes.class.getName()) // FIXME: io.vertigo.ai.example.domain.DtDefinition contient toutes les définitions
							.addDefinitionResource("smarttypes", HeroesSmartTypes.class.getName()) 
							.addDefinitionResource("dtobjects", "io.vertigo.ai.example.domain.DtDefinitions")
							.build())
					.build());
		} else if (configName.equals("telstra")) {
			nodeConfigBuilder
			.addModule(httpClientFeatures.build())
			.addModule(vegaFeatures.build())
			.addModule(aiFeatures.build())
			.addModule(ModuleConfig.builder("myApp")
					.addComponent(TelstraDefinitionProvider.class)
					.addComponent(TelstraGenerator.class)
					.addComponent(EventTypeDAO.class)
					.addComponent(LocationDAO.class)
					.addComponent(LogFeatureDAO.class)
					.addComponent(ResourceTypeDAO.class)
					.addComponent(SeverityTypeDAO.class)
					.addComponent(LocationTrainDAO.class)
					.addComponent(EventTypeTrainDAO.class)
					.addComponent(LogFeatureTrainDAO.class)
					.addComponent(SeverityTypeTrainDAO.class)
					.addComponent(ResourceTypeTrainDAO.class)
					.addComponent(io.vertigo.ai.example.telstra.train.TrainPAO.class)
					.addComponent(TelstraLocationRecordLoader.class)
					.addComponent(TelstraEventTypeRecordLoader.class)
					.addComponent(TelstraLogFeatureRecordLoader.class)
					.addComponent(TelstraSeverityTypeRecordLoader.class)
					.addComponent(TelstraResourceTypeRecordLoader.class)

					.addComponent(TelstraServices.class)
					.addDefinitionProvider(DefinitionProviderConfig.builder(ModelDefinitionProvider.class)
							.addDefinitionResource("smarttypes", TelstraSmartTypes.class.getName())
							.addDefinitionResource("smarttypes", HeroesSmartTypes.class.getName()) // FIXME: io.vertigo.ai.example.domain.DtDefinition contient toutes les définitions
							.addDefinitionResource("smarttypes", IrisSmartTypes.class.getName()) // FIXME: io.vertigo.ai.example.domain.DtDefinition contient toutes les définitions
							.addDefinitionResource("dtobjects", "io.vertigo.ai.example.domain.DtDefinitions")
							.build())
					.build());
		}

		return nodeConfigBuilder.build();
	}
}
