package com.performily.flowboard.shared.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Resource for one page of a list, with the data needed to ask for the next page.
 *
 * @param items      the items of the page
 * @param page       the page number, starting at 0
 * @param size       the page size
 * @param totalItems the number of items in the whole list
 * @param totalPages the number of pages
 * @param hasNext    whether there is a page after this one
 * @param <T>        the type of the items
 */
@Schema(name = "PageResponse", description = "One page of a list")
public record PageResource<T>(
        @Schema(description = "Items of the page") List<T> items,
        @Schema(description = "Page number, starting at 0", example = "0") int page,
        @Schema(description = "Page size", example = "20") int size,
        @Schema(description = "Number of items in the whole list", example = "87") long totalItems,
        @Schema(description = "Number of pages", example = "5") int totalPages,
        @Schema(description = "Whether there is a next page", example = "true") boolean hasNext
) {
}
