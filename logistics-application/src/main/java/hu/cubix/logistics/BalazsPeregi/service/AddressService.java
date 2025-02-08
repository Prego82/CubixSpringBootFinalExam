package hu.cubix.logistics.BalazsPeregi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.cubix.logistics.BalazsPeregi.model.Address;
import hu.cubix.logistics.BalazsPeregi.repository.AddressRepository;
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

}
