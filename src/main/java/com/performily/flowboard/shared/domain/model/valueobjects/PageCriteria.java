package com.performily.flowboard.shared.domain.model.valueobjects;

/**
 * Page Criteria Value Object
 * @summary
 * Page asked by a client when it reads a list: which page and how many items per page.
 * Pages start at 0, so page 0 with size 20 means "items 1 to 20".
 *
 * Rules: page cannot be negative and size goes from 1 to {@link #MAX_SIZE}.
 * When the client does not send a value, {@link #of(Integer, Integer)} uses
 * page {@value #DEFAULT_PAGE} and size {@value #DEFAULT_SIZE}.
 *
 * @param page the page number, starting at 0
 * @param size the number of items per page
 * @since 1.0.0
 */
public record PageCriteria(int page, int size) {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    /**
     * Compact constructor for PageCriteria.
     *
     * @throws IllegalArgumentException if page is negative or size is out of range
     */
    public PageCriteria {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new IllegalArgumentException("Page size must be between 1 and %d".formatted(MAX_SIZE));
        }
    }

    /**
     * Creates the criteria from optional values, using the defaults for the missing ones.
     *
     * @param page the page number, or null for {@value #DEFAULT_PAGE}
     * @param size the page size, or null for {@value #DEFAULT_SIZE}
     * @return the page criteria
     * @throws IllegalArgumentException if a given value is out of range
     */
    public static PageCriteria of(Integer page, Integer size) {
        return new PageCriteria(page == null ? DEFAULT_PAGE : page, size == null ? DEFAULT_SIZE : size);
    }

    /**
     * Position of the first item of the page in the whole list.
     *
     * @return page * size
     */
    public long offset() {
        return (long) page * size;
    }
}
