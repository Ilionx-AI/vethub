package dev.ilionx.workshop.api.visit.model.mapper;

import dev.ilionx.workshop.api.vet.model.mapper.VetMapper;
import dev.ilionx.workshop.api.visit.model.Visit;
import dev.ilionx.workshop.api.visit.model.response.VisitResponse;
import io.github.jframe.util.mapper.config.SharedMapperConfig;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting Visit entities to response DTOs.
 */
@Mapper(
    config = SharedMapperConfig.class,
    uses = VetMapper.class
)
public abstract class VisitMapper {

    @Mapping(
        source = "pet.id",
        target = "petId"
    )
    @Mapping(
        source = "vet",
        target = "vet"
    )
    public abstract VisitResponse toResponse(Visit visit);

    public abstract List<VisitResponse> toResponseList(List<Visit> visits);
}
