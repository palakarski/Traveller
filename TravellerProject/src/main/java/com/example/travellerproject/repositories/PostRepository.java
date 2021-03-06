package com.example.travellerproject.repositories;

import com.example.travellerproject.model.pojo.Post;
import com.example.travellerproject.model.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findPostByUserIsNot(User user);

    @Query(value = "select * from posts as p " +
            "join subscribers as s on p.owner_id = s.subscribed_for_user_id " +
            "join users as u on u.id = s.subscriber_id " +
            "where u.id =?1",
            nativeQuery = true)
    List<Post> getNewsFeed(long userId);

    @Query(value = "select * from posts as p " +
            "join subscribers as s on p.owner_id = s.subscribed_for_user_id " +
            "join users as u on u.id = s.subscriber_id " +
            "where u.id =?1 "+
            "order by p.created_at desc",
            nativeQuery = true)
    List<Post> getNewsFeedSortedByDate(long userId);


    @Query(value = "select * from posts as p " +
            "join subscribers as s on p.owner_id = s.subscribed_for_user_id " +
            "join users as u on u.id = s.subscriber_id " +
            "where u.id =?1 "+
            "order by category_id ",
            nativeQuery = true)
    List<Post> getNewsFeedSortedByCategory(long userId);

    @Query(value = "select *, count(l.post_id) as likes from posts as p " +
            "join subscribers as s on p.owner_id = s.subscribed_for_user_id " +
            "join users as u on u.id = s.subscriber_id " +
            "left join users_like_posts as l on p.id = l.post_id " +
            "where u.id =?1 " +
            "group by p.id "+
            "order by likes desc",
            nativeQuery = true)
    List<Post> getNewsFeedSortedByLikes(long userId);


    @Query(value = "select * from posts as p " +
            "join users as u on u.id = p.owner_id "+
            "where not p.owner_id=?1 ",
            nativeQuery = true)
    List<Post> getForeignPost(long userId);


    @Query(value = "select * from posts as p " +
            "join users as u on u.id = p.owner_id " +
            "where not( p.owner_id=?1) " +
            "order by p.category_id",
            nativeQuery = true)
    List<Post> getAllForeignPostSortedByCategory(long userId);


    @Query(value = "select * from posts as p " +
            "join users as u on u.id = p.owner_id " +
            "where not( p.owner_id=?1) " +
            "order by p.created_at desc",
            nativeQuery = true)
    List<Post> getAllForeignPostSortedByDate(long userId);

    @Query(value = "select * ,count(l.post_id) as likes from posts as p " +
            "join users as u on u.id = p.owner_id " +
            "left join users_like_posts as l on p.id = l.post_id " +
            "where not p.owner_id = ?1 " +
            "group by p.id " +
            "order by likes desc",
            nativeQuery = true)
    List<Post> getAllForeignPostSortedByLikes(long userId);

    Page<Post> findPostsByUserIsNotOrderByLikersDesc(Pageable pageable, User user);
}
