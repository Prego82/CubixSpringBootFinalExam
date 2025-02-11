package hu.cubix.logistics.BalazsPeregi.mapper;

import org.mapstruct.Mapper;

import hu.cubix.logistics.BalazsPeregi.dto.TransportPlanDto;
import hu.cubix.logistics.BalazsPeregi.model.TransportPlan;

@Mapper(componentModel = "spring")
public interface TransportPlanMapper {

	TransportPlan dtoToTransportPlan(TransportPlanDto dto);

	TransportPlanDto transportPlanToDto(TransportPlan transportPlan);
}
