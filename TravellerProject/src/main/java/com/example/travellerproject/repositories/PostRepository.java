package com.example.travellerproject.repositories;

import com.example.travellerproject.model.dto.post.RequestPostDTO;
import com.example.travellerproject.model.pojo.Post;
import com.example.travellerproject.model.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findPostByUserIsNot(User user);
}
