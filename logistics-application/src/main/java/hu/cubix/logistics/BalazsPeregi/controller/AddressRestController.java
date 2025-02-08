package hu.cubix.logistics.BalazsPeregi.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import hu.cubix.logistics.BalazsPeregi.dto.AddressDto;
import hu.cubix.logistics.BalazsPeregi.mapper.AddressMapper;
import hu.cubix.logistics.BalazsPeregi.model.Address;
import hu.cubix.logistics.BalazsPeregi.service.AddressService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/addresses")
public class AddressRestController {

	@Autowired
	private AddressService addressService;

	@Autowired
	private AddressMapper addressMapper;

	@PostMapping
	public ResponseEntity<AddressDto> addAddress(@RequestBody @Valid AddressDto newAddress) {
		if (newAddress.getId() != null) {
			return ResponseEntity.badRequest().build();
		}
		Address address = addressMapper.dtoToAddress(newAddress);
		Address savedAddress = addressService.save(address);
		if (savedAddress == null) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(addressMapper.addressToDto(savedAddress));
	}

	@GetMapping
	public List<AddressDto> findAll() {
		return addressMapper.addressesToDtos(addressService.findAll());
	}

	@GetMapping("{id}")
	public AddressDto findById(@PathVariable long id) {
		Optional<Address> address = addressService.findById(id);
		return addressMapper.addressToDto(address.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
	}

	@DeleteMapping("{id}")
	public void deleteById(@PathVariable long id) {
		addressService.deleteById(id);
	}

	@PutMapping("{id}")
	public ResponseEntity<AddressDto> update(@PathVariable long id, @RequestBody @Valid AddressDto updatedAddress) {
		if (updatedAddress.getId() != id) {
			return ResponseEntity.badRequest().build();
		}
		Address updated = addressService.update(addressMapper.dtoToAddress(updatedAddress));
		if (updated == null) {
			return ResponseEntity.badRequest().build();
		} else {
			return ResponseEntity.ok(addressMapper.addressToDto(updated));
		}
	}
}
