package dev.ilionx.workshop.api.visit.controller;

import dev.ilionx.workshop.api.owner.model.Owner;
import dev.ilionx.workshop.api.pet.model.Pet;
import dev.ilionx.workshop.api.visit.model.Visit;
import dev.ilionx.workshop.api.visit.model.request.CreateVisitRequest;
import dev.ilionx.workshop.api.visit.model.request.UpdateVisitRequest;
import dev.ilionx.workshop.api.visit.model.response.VisitResponse;
import dev.ilionx.workshop.support.IntegrationTest;
import io.github.jframe.exception.resource.ErrorResponseResource;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static dev.ilionx.workshop.api.Paths.VISITS;
import static dev.ilionx.workshop.api.Paths.VISIT_BY_ID;
import static dev.ilionx.workshop.common.exception.ApiErrorCode.PET_NOT_FOUND;
import static dev.ilionx.workshop.common.exception.ApiErrorCode.VISIT_NOT_FOUND;
import static io.github.jframe.util.mapper.ObjectMappers.fromJson;
import static io.github.jframe.util.mapper.ObjectMappers.toJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration Test - Visit Global Controller")
class VisitGlobalControllerTest extends IntegrationTest {

    private static final int NON_EXISTENT_VISIT_ID = 999;
    private static final int NON_EXISTENT_PET_ID = 999;

    private static final String VISIT_DESCRIPTION = "Rabies shot";
    private static final LocalDate VISIT_DATE = LocalDate.of(2023, 1, 1);
    private static final String UPDATED_VISIT_DESCRIPTION = "Follow-up checkup";
    private static final LocalDate UPDATED_VISIT_DATE = LocalDate.of(2023, 6, 15);

    // ========================= LIST =========================
    @Test
    @DisplayName("Should return all visits when visits exist")
    void shouldReturnAllVisitsWhenVisitsExist() throws Exception {
        // Given: An owner with a pet and a visit exists in the database
        final Owner owner = aSavedOwner();
        final Pet pet = aSavedPet(owner);
        final Visit visit = aSavedVisit(pet);

        // When: Requesting all visits
        // Then: Should return 200 with list containing the visit
        mockMvc.perform(get(VISITS))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(equalTo(visit.getId()))))
            .andExpect(jsonPath("$[0].date", is(notNullValue())))
            .andExpect(jsonPath("$[0].description", is(equalTo(VISIT_DESCRIPTION))))
            .andExpect(jsonPath("$[0].petId", is(equalTo(pet.getId()))));
    }

    @Test
    @DisplayName("Should return empty list when no visits exist")
    void shouldReturnEmptyListWhenNoVisitsExist() throws Exception {
        // Given: No visits exist in the database

        // When: Requesting all visits
        // Then: Should return 200 with empty array
        mockMvc.perform(get(VISITS))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    // ========================= GET BY ID =========================
    @Test
    @DisplayName("Should return visit when valid visit ID exists")
    void shouldReturnVisitWhenValidVisitIdExists() throws Exception {
        // Given: An owner with a pet and a visit exists in the database
        final Owner owner = aSavedOwner();
        final Pet pet = aSavedPet(owner);
        final Visit visit = aSavedVisit(pet);

        // When: Requesting visit by ID
        // Then: Should return 200 with visit details
        mockMvc.perform(get(VISIT_BY_ID, visit.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(equalTo(visit.getId()))))
            .andExpect(jsonPath("$.date", is(notNullValue())))
            .andExpect(jsonPath("$.description", is(equalTo(VISIT_DESCRIPTION))))
            .andExpect(jsonPath("$.petId", is(equalTo(pet.getId()))))
            .andExpect(jsonPath("$.vet", is(notNullValue())));
    }

    @Test
    @DisplayName("Should return not found when visit ID does not exist")
    void shouldReturnNotFoundWhenVisitIdDoesNotExist() throws Exception {
        // Given: No visit exists with ID 999

        // When: Requesting non-existent visit
        final ResultActions response = mockMvc.perform(get(VISIT_BY_ID, NON_EXISTENT_VISIT_ID));

        // Then: Should return 404 with error message
        response.andExpect(status().isNotFound());

        final String content = response.andReturn().getResponse().getContentAsString();
        final ErrorResponseResource error = fromJson(content, ErrorResponseResource.class);
        assertThat(error.getErrorMessage(), is(equalTo(VISIT_NOT_FOUND.getReason())));
    }

    // ========================= CREATE =========================
    @Test
    @DisplayName("Should create visit when valid data provided")
    void shouldCreateVisitWhenValidDataProvided() throws Exception {
        // Given: An owner with a pet exists and a valid create visit request with petId in body
        final Owner owner = aSavedOwner();
        final Pet pet = aSavedPet(owner);
        final CreateVisitRequest request = aCreateVisitRequestWithPet(pet.getId());

        // When: Creating the visit
        final String responseBody = mockMvc.perform(
            post(VISITS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            // Then: Should return 201 with visit response
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(notNullValue())))
            .andExpect(jsonPath("$.date", is(notNullValue())))
            .andExpect(jsonPath("$.description", is(equalTo(VISIT_DESCRIPTION))))
            .andExpect(jsonPath("$.petId", is(equalTo(pet.getId()))))
            .andExpect(jsonPath("$.vet", is(notNullValue())))
            .andReturn()
            .getResponse()
            .getContentAsString();

        // And: The returned visit should be valid
        final VisitResponse visitResponse = fromJson(responseBody, VisitResponse.class);
        assertThat(visitResponse.getId(), is(notNullValue()));
        assertThat(visitResponse.getDescription(), is(equalTo(VISIT_DESCRIPTION)));
        assertThat(visitResponse.getPetId(), is(equalTo(pet.getId())));
    }

    @Test
    @DisplayName("Should return not found when pet does not exist for create visit")
    void shouldReturnNotFoundWhenPetDoesNotExistForCreateVisit() throws Exception {
        // Given: A create visit request with non-existent pet ID
        final CreateVisitRequest request = aCreateVisitRequestWithPet(NON_EXISTENT_PET_ID);

        // When: Creating visit for non-existent pet
        final ResultActions response = mockMvc.perform(
            post(VISITS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        );

        // Then: Should return 404 with error message
        response.andExpect(status().isNotFound());

        final String content = response.andReturn().getResponse().getContentAsString();
        final ErrorResponseResource error = fromJson(content, ErrorResponseResource.class);
        assertThat(error.getErrorMessage(), is(equalTo(PET_NOT_FOUND.getReason())));
    }

    // ========================= UPDATE =========================
    @Test
    @DisplayName("Should update visit when valid data provided")
    void shouldUpdateVisitWhenValidDataProvided() throws Exception {
        // Given: An existing visit in the database
        final Owner owner = aSavedOwner();
        final Pet pet = aSavedPet(owner);
        final Visit visit = aSavedVisit(pet);
        final UpdateVisitRequest request = anUpdateVisitRequest();

        // When: Updating the visit
        // Then: Should return 200 OK with updated visit
        mockMvc.perform(
            put(VISIT_BY_ID, visit.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(equalTo(visit.getId()))))
            .andExpect(jsonPath("$.petId", is(equalTo(pet.getId()))));
    }

    @Test
    @DisplayName("Should return not found when visit does not exist for update")
    void shouldReturnNotFoundWhenVisitDoesNotExistForUpdate() throws Exception {
        // Given: An update request for non-existent visit
        final UpdateVisitRequest request = anUpdateVisitRequest();

        // When: Updating non-existent visit
        final ResultActions response = mockMvc.perform(
            put(VISIT_BY_ID, NON_EXISTENT_VISIT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        );

        // Then: Should return 404 with error message
        response.andExpect(status().isNotFound());

        final String content = response.andReturn().getResponse().getContentAsString();
        final ErrorResponseResource error = fromJson(content, ErrorResponseResource.class);
        assertThat(error.getErrorMessage(), is(equalTo(VISIT_NOT_FOUND.getReason())));
    }

    // ========================= DELETE =========================
    @Test
    @DisplayName("Should delete visit when visit exists")
    void shouldDeleteVisitWhenVisitExists() throws Exception {
        // Given: An existing visit in the database
        final Owner owner = aSavedOwner();
        final Pet pet = aSavedPet(owner);
        final Visit visit = aSavedVisit(pet);

        // When: Deleting the visit
        // Then: Should return 204 no content
        mockMvc.perform(delete(VISIT_BY_ID, visit.getId()))
            .andExpect(status().isNoContent());

        // And: The visit should no longer exist
        mockMvc.perform(get(VISIT_BY_ID, visit.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return not found when visit does not exist for delete")
    void shouldReturnNotFoundWhenVisitDoesNotExistForDelete() throws Exception {
        // Given: No visit exists with ID 999

        // When: Deleting non-existent visit
        final ResultActions response = mockMvc.perform(delete(VISIT_BY_ID, NON_EXISTENT_VISIT_ID));

        // Then: Should return 404 with error message
        response.andExpect(status().isNotFound());

        final String content = response.andReturn().getResponse().getContentAsString();
        final ErrorResponseResource error = fromJson(content, ErrorResponseResource.class);
        assertThat(error.getErrorMessage(), is(equalTo(VISIT_NOT_FOUND.getReason())));
    }
}
