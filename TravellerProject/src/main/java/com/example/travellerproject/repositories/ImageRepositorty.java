package com.example.travellerproject.repositories;

import com.example.travellerproject.model.pojo.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepositorty extends JpaRepository<Image,Long> {
}
