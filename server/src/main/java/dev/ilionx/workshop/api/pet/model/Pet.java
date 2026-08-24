package dev.ilionx.workshop.api.pet.model;

import dev.ilionx.workshop.api.owner.model.Owner;
import dev.ilionx.workshop.api.visit.model.Visit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

/**
 * Entity representing a pet.
 */
@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(
        name = "name",
        nullable = false
    )
    private String name;

    @Past
    @Column(
        name = "birth_date",
        nullable = false
    )
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(
        name = "type_id",
        nullable = false
    )
    private PetType type;

    @ManyToOne
    @JoinColumn(
        name = "owner_id",
        nullable = false
    )
    private Owner owner;

    @OneToMany(
        mappedBy = "pet",
        cascade = CascadeType.ALL,
        fetch = FetchType.EAGER
    )
    private List<Visit> visits = new ArrayList<>();

    @Positive
    @Column(
        name = "weight",
        nullable = true,
        columnDefinition = "DECIMAL(5,2)"
    )
    private Double weight;

    @Column(
        name = "last_vaccine_date",
        nullable = true
    )
    private LocalDate lastVaccineDate;

    @Column(
        name = "allergies",
        nullable = true,
        length = 500
    )
    private String allergies;

    @Column(
        name = "dietary_restrictions",
        nullable = true,
        length = 500
    )
    private String dietaryRestrictions;

    @Column(
        name = "medical_notes",
        nullable = true,
        columnDefinition = "CLOB"
    )
    private String medicalNotes;

}
