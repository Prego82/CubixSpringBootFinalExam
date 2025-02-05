package hu.cubix.logistics.BalazsPeregi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.cubix.logistics.BalazsPeregi.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
