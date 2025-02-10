package hu.cubix.logistics.BalazsPeregi.service.specification;

import org.springframework.data.jpa.domain.Specification;

import hu.cubix.logistics.BalazsPeregi.model.Address;
import hu.cubix.logistics.BalazsPeregi.model.Address_;

public class AddressSpecification {

	public static Specification<Address> cityLike(String cityPrefix) {
		return (root, cq, cb) -> cb.like(cb.lower(root.get(Address_.city)), cityPrefix.toLowerCase() + "%");
	}

	public static Specification<Address> streetLike(String streetPrefix) {
		return (root, cq, cb) -> cb.like(cb.lower(root.get(Address_.street)), streetPrefix.toLowerCase() + "%");
	}

	public static Specification<Address> hasCountryCode(String countryCode) {
		return (root, cq, cb) -> cb.equal(root.get(Address_.country), countryCode);
	}

	public static Specification<Address> hasZip(String zipCode) {
		return (root, cq, cb) -> cb.equal(root.get(Address_.zip), zipCode);
	}

}
