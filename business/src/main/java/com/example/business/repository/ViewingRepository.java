package com.example.business.repository;

import com.example.business.model.Viewing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewingRepository extends JpaRepository<Viewing, Long> {
}
