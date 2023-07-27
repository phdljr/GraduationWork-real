package com.brother.graduationwork.repository;

import com.brother.graduationwork.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailAndPw(String email, String pw);

    User findByNickname(String nickname);

    @EntityGraph(value = "user.room.orderList", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select u from User u where u.nickname = :nickname")
    User findUserWithJoinedRoom(@Param("nickname") String nickname);

    @EntityGraph(value = "user.room.orderList", type = EntityGraph.EntityGraphType.LOAD)
    List<User> findAllByRoomId(Long roomId);

    User getByNickname(String email);

    User getByEmail(String email);
}
