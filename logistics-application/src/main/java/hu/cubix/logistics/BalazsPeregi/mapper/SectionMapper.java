package hu.cubix.logistics.BalazsPeregi.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import hu.cubix.logistics.BalazsPeregi.dto.SectionDto;
import hu.cubix.logistics.BalazsPeregi.model.Section;

@Mapper(componentModel = "spring")
public interface SectionMapper {
	@Mapping(target = "transportPlan", ignore = true)
	Section dtoToSection(SectionDto dto);

	SectionDto sectionToDto(Section section);

	List<Section> dtosToSections(List<SectionDto> dtos);

	List<SectionDto> sectionsToDtos(List<Section> sections);

}
