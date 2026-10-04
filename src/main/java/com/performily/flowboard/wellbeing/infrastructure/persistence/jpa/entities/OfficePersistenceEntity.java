package com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "offices")
public class OfficePersistenceEntity {

    public OfficePersistenceEntity() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 80)
    private String name;

    @Column(name = "area", length = 60)
    private String area;

    @Column(name = "address", nullable = false, length = 150)
    private String address;

    @Column(name = "floor", nullable = false, length = 30)
    private String floor;

    @Column(name = "reference", length = 150)
    private String reference;

    @Column(name = "active", nullable = false)
    private boolean active;

    public Long getId() { return id; }
    public void setId(Long v) { id = v; }

    public String getName() { return name; }
    public void setName(String v) { name = v; }

    public String getArea() { return area; }
    public void setArea(String v) { area = v; }

    public String getAddress() { return address; }
    public void setAddress(String v) { address = v; }

    public String getFloor() { return floor; }
    public void setFloor(String v) { floor = v; }

    public String getReference() { return reference; }
    public void setReference(String v) { reference = v; }

    public boolean isActive() { return active; }
    public void setActive(boolean v) { active = v; }
}
