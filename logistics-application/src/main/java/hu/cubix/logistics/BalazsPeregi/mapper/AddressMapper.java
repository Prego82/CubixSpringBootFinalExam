package hu.cubix.logistics.BalazsPeregi.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import hu.cubix.logistics.BalazsPeregi.dto.AddressDto;
import hu.cubix.logistics.BalazsPeregi.model.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

	Address dtoToAddress(AddressDto address);

	AddressDto addressToDto(Address address);

	List<AddressDto> addressesToDtos(List<Address> addresses);

	List<Address> addressDtosToAddresses(List<AddressDto> dtos);

}
