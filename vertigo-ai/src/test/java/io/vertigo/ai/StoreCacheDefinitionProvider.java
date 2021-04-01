package io.vertigo.ai;

import java.util.Collections;
import java.util.List;

import io.vertigo.ai.data.domain.iris.IrisItem;
import io.vertigo.core.node.definition.Definition;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.SimpleDefinitionProvider;
import io.vertigo.datamodel.structure.util.DtObjectUtil;
import io.vertigo.datastore.cache.definitions.CacheDefinition;
import io.vertigo.datastore.impl.entitystore.cache.CacheData;

public class StoreCacheDefinitionProvider implements SimpleDefinitionProvider {

	@Override
	public List<? extends Definition> provideDefinitions(final DefinitionSpace definitionSpace) {
		return Collections.singletonList(new CacheDefinition(CacheData.getContext(DtObjectUtil.findDtDefinition(IrisItem.class)), true, 1000, 3600, 3600 / 2, true));
	}

}