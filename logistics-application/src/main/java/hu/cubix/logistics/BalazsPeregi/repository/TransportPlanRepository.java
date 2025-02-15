package hu.cubix.logistics.BalazsPeregi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hu.cubix.logistics.BalazsPeregi.model.TransportPlan;

public interface TransportPlanRepository extends JpaRepository<TransportPlan, Long> {

	@EntityGraph("TransportPlan.Full")
	@Query("SELECT tp FROM TransportPlan tp WHERE id=:id")
	Optional<TransportPlan> findByIdWithFullData(long id);

}
