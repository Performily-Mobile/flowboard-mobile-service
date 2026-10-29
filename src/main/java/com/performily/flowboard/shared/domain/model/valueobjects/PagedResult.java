package com.performily.flowboard.shared.domain.model.valueobjects;

import java.util.List;
import java.util.function.Function;

/**
 * Paged Result Value Object
 * @summary
 * One page of a list together with the data the client needs to ask for the next
 * one: the page number, the page size and how many items there are in total.
 *
 * Rules: items is never null (an empty page has an empty list), page cannot be
 * negative, size is at least 1 and totalItems cannot be negative.
 *
 * @param items      the items of this page
 * @param page       the page number, starting at 0
 * @param size       the page size that was asked
 * @param totalItems the number of items in the whole list
 * @param <T>        the type of the items
 * @since 1.0.0
 */
public record PagedResult<T>(List<T> items, int page, int size, long totalItems) {
    /**
     * Compact constructor for PagedResult.
     *
     * @throws IllegalArgumentException if a value is out of range
     */
    public PagedResult {
        items = items == null ? List.of() : List.copyOf(items);
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Page size must be at least 1");
        }
        if (totalItems < 0) {
            throw new IllegalArgumentException("Total items cannot be negative");
        }
    }

    /**
     * Creates a result for the given criteria.
     *
     * @param items      the items of this page
     * @param criteria   the {@link PageCriteria} that was asked
     * @param totalItems the number of items in the whole list
     * @param <T>        the type of the items
     * @return the paged result
     */
    public static <T> PagedResult<T> of(List<T> items, PageCriteria criteria, long totalItems) {
        return new PagedResult<>(items, criteria.page(), criteria.size(), totalItems);
    }

    /**
     * Number of pages of the whole list. An empty list has 0 pages.
     *
     * @return the total number of pages
     */
    public int totalPages() {
        return (int) ((totalItems + size - 1) / size);
    }

    /**
     * Checks whether there is a page after this one.
     *
     * @return true when page + 1 is lower than the total number of pages
     */
    public boolean hasNext() {
        return page + 1 < totalPages();
    }

    /**
     * Converts the items keeping the page data. Used, for example, to turn domain
     * objects into REST resources.
     *
     * @param mapper the function that converts each item
     * @param <R>    the new type of the items
     * @return a new result with the converted items
     */
    public <R> PagedResult<R> map(Function<? super T, ? extends R> mapper) {
        return new PagedResult<>(items.stream().<R>map(mapper).toList(), page, size, totalItems);
    }
}
