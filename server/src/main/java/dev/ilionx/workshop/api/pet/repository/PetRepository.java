package dev.ilionx.workshop.api.pet.repository;

import dev.ilionx.workshop.api.pet.model.Pet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Pet} entities.
 */
@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {

    /**
     * Finds all pets belonging to the specified owner.
     *
     * @param ownerId the owner's ID
     * @return list of pets for the given owner
     */
    List<Pet> findByOwnerId(Integer ownerId);

    /**
     * Finds all pets whose name contains the given search term (case-insensitive).
     *
     * @param name the search term to match against pet names
     * @return list of pets whose name contains the search term
     */
    @Query("SELECT p FROM Pet p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Pet> findByNameContainsIgnoreCase(@Param("name") String name);
}
