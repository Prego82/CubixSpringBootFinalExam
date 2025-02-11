package hu.cubix.logistics.BalazsPeregi.mapper;

import org.mapstruct.Mapper;

import hu.cubix.logistics.BalazsPeregi.dto.MilestoneDto;
import hu.cubix.logistics.BalazsPeregi.model.Milestone;

@Mapper(componentModel = "spring")
public interface MilestoneMapper {
	MilestoneDto milestoneToDto(Milestone milestone);

	Milestone dtoToMilestone(MilestoneDto dto);
}
