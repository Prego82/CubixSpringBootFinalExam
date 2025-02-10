package hu.cubix.logistics.BalazsPeregi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import hu.cubix.logistics.BalazsPeregi.model.Address;
import hu.cubix.logistics.BalazsPeregi.repository.AddressRepository;
import hu.cubix.logistics.BalazsPeregi.service.specification.AddressSpecification;
import jakarta.transaction.Transactional;

@Service
public class AddressService {

	@Autowired
	private AddressRepository addressRepo;

	public Address save(Address address) {
		return addressRepo.save(address);
	}

	public List<Address> findAll() {
		return addressRepo.findAll();
	}

	public Optional<Address> findById(long id) {
		return addressRepo.findById(id);
	}

	public void deleteById(long id) {
		addressRepo.deleteById(id);
	}

	@Transactional
	public Address update(Address address) {
		if (!addressRepo.existsById(address.getId())) {
			return null;
		}
		return addressRepo.save(address);
	}

	public Page<Address> dynamicSearch(Address address, Pageable pageable) {
		String cityPrefix = address.getCity();
		String streetPrefix = address.getStreet();
		String countryCode = address.getCountry();
		String zipCode = address.getZip();
		Specification<Address> specs = Specification.where(null);

		if (StringUtils.hasLength(cityPrefix)) {
			specs = specs.and(AddressSpecification.cityLike(cityPrefix));
		}
		if (StringUtils.hasLength(streetPrefix)) {
			specs = specs.and(AddressSpecification.streetLike(streetPrefix));
		}
		if (StringUtils.hasLength(countryCode)) {
			specs = specs.and(AddressSpecification.hasCountryCode(countryCode));
		}
		if (StringUtils.hasLength(zipCode)) {
			specs = specs.and(AddressSpecification.hasZip(zipCode));
		}
		return addressRepo.findAll(specs, pageable);
	}

}
