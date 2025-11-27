package com.andrew.smartfitnessassistant.repository;

import com.andrew.smartfitnessassistant.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<QuestionEntity, UUID> {
    List<QuestionEntity> findAllByOrderByPosition();


    @Query(value = "SELECT q from QuestionEntity q LEFT JOIN FETCH q.answers WHERE q.position = :position")
    QuestionEntity findByPosition(@Param("position") Integer position);
}
