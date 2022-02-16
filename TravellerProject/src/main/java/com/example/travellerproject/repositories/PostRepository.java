package com.example.travellerproject.repositories;

import com.example.travellerproject.model.dto.post.RequestPostDTO;
import com.example.travellerproject.model.pojo.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
}
