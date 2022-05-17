package com.clone.instagram.repository;

import com.clone.instagram.entity.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    private EntityManager em;


    @Test
    void mainStory() {
//        @Query("select p from Post p" +
//                " left join fetch p.user u" +
//                " where u.id in (select f.toUser from Follow f where f.fromUser = :sessionId)"
//        )
//        Page<Post> mainStory(Long sessionId, Pageable pageable);

        List<Post> resultList = em.createQuery("select p from Post p " +
                        " left join fetch p.user u" +
                        " where u.id in (select f.toUser.id from Follow f where f.fromUser.id = :id)", Post.class)
                .setParameter("id", 2L)
                .getResultList();

        System.out.println("resultList = " + resultList);


    }
}