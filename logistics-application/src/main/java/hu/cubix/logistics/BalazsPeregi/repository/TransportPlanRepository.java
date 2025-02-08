package hu.cubix.logistics.BalazsPeregi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.cubix.logistics.BalazsPeregi.model.TransportPlan;

public interface TransportPlanRepository extends JpaRepository<TransportPlan, Long> {

}
