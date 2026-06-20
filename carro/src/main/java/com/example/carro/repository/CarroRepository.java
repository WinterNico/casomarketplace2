package com.example.carro.repository;

import com.example.carro.model.Carro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarroRepository extends JpaRepository<Carro, Long> {
    List<Carro> findByUserId(Long userId);
    Optional<Carro> findByUserIdAndProductId(Long userId, Long productId);
}
