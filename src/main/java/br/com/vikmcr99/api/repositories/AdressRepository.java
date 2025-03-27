package br.com.vikmcr99.api.repositories;

import br.com.vikmcr99.api.domain.adress.Adress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdressRepository extends JpaRepository<Adress, UUID> {
}
