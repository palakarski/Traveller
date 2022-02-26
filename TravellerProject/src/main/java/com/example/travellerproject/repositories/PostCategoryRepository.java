package com.example.travellerproject.repositories;

import com.example.travellerproject.model.pojo.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory,Long> {

}
