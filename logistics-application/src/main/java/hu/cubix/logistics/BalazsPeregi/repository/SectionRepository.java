package hu.cubix.logistics.BalazsPeregi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.cubix.logistics.BalazsPeregi.model.Address;
import hu.cubix.logistics.BalazsPeregi.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {

}
