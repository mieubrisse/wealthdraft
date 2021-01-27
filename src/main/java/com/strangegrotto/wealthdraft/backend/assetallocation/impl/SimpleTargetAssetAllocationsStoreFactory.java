package com.strangegrotto.wealthdraft.backend.assetallocation.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Preconditions;
import com.google.common.escape.ArrayBasedEscaperMap;
import com.strangegrotto.wealthdraft.AbstractYmlBackedStoreFactory;
import com.strangegrotto.wealthdraft.backend.assetallocation.api.types.TargetAssetAllocation;
import com.strangegrotto.wealthdraft.backend.filters.api.FiltersStore;

import java.math.BigDecimal;
import java.util.List;

public class SimpleTargetAssetAllocationsStoreFactory extends AbstractYmlBackedStoreFactory<
        SerTargetAssetAllocations,
        SerTargetAssetAllocations,
        SimpleTargetAssetAllocationsStore> {
    private final FiltersStore filtersStore;

    public SimpleTargetAssetAllocationsStoreFactory(ObjectMapper baseMapper, FiltersStore filtersStore) {
        super(baseMapper);
        this.filtersStore = filtersStore;
    }

    @Override
    protected void configureMapper(ObjectMapper mapper) { }

    @Override
    protected JavaType getDeserializationType(TypeFactory typeFactory) {
        return typeFactory.constructType(SerTargetAssetAllocations.class);
    }

    @Override
    protected SerTargetAssetAllocations postprocess(SerTargetAssetAllocations targetAssetAllocations) {
        return targetAssetAllocations;
    }

    @Override
    protected void validate(SerTargetAssetAllocations targetAssetAllocations) {
        var filters = this.filtersStore.getFilters();
        var targets = targetAssetAllocations.getTargets();

        for (int i = 0; i < targets.size(); i++) {
            var target = targets.get(i);

            var numeratorFilterName = target.getNumeratorFilterId();
            Preconditions.checkState(
                    filters.containsKey(numeratorFilterName),
                    "Numerator filter '%s' in allocation target #%s is not defined",
                    numeratorFilterName,
                    i
            );

            var denominatorFilterNameOpt = target.getDenominatorFilterIdOpt();
            if (denominatorFilterNameOpt.isPresent()) {
                var denominatorFilterName = denominatorFilterNameOpt.get();
                Preconditions.checkState(
                        filters.containsKey(denominatorFilterName),
                        "Denominator filter '%s' in allocation target #%s is not defined",
                        denominatorFilterName,
                        i
                );
            }

            var targetFraction = target.getFraction();
            Preconditions.checkState(
                    targetFraction.compareTo(BigDecimal.ZERO) >= 0,
                    "Fraction for allocation target #%s is less than zero",
                    i);
            Preconditions.checkState(
                    targetFraction.compareTo(BigDecimal.ONE) <= 0,
                    "Fraction for allocation target #%s is greater than 1.0",
                    i);
        }
    }

    @Override
    protected SimpleTargetAssetAllocationsStore buildResult(SerTargetAssetAllocations targetAssetAllocations) {
        List<TargetAssetAllocation> result = List.copyOf(targetAssetAllocations.getTargets());
        return new SimpleTargetAssetAllocationsStore(result);
    }
}
