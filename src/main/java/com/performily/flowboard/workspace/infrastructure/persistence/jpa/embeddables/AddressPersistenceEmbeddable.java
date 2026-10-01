package com.performily.flowboard.workspace.infrastructure.persistence.jpa.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Address Persistence Embeddable
 * @summary
 * Persistence representation for the Address value object.
 *
 * @since 1.0.0
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressPersistenceEmbeddable {
    @Column(name = "address_street", nullable = false, length = 150)
    private String street;

    @Column(name = "address_district", nullable = false, length = 60)
    private String district;

    @Column(name = "address_province", nullable = false, length = 60)
    private String province;

    @Column(name = "address_department", nullable = false, length = 60)
    private String department;
}