package com.performily.flowboard.shared.interfaces.rest.transform;

import com.performily.flowboard.shared.domain.model.valueobjects.PagedResult;
import com.performily.flowboard.shared.interfaces.rest.resources.PageResource;

import java.util.function.Function;

/**
 * Assembler that converts a PagedResult into a PageResource.
 */
public final class PageResourceFromPagedResultAssembler {

    private PageResourceFromPagedResultAssembler() {
    }

    /**
     * Converts a page of domain objects into a page of resources.
     *
     * @param result the {@link PagedResult} to convert
     * @param mapper function that converts each item into its resource
     * @param <T>    domain item type
     * @param <R>    resource item type
     * @return the {@link PageResource}
     */
    public static <T, R> PageResource<R> toResourceFromPagedResult(PagedResult<T> result, Function<T, R> mapper) {
        var page = result.map(mapper);
        return new PageResource<>(page.items(), page.page(), page.size(), page.totalItems(),
                page.totalPages(), page.hasNext());
    }
}
