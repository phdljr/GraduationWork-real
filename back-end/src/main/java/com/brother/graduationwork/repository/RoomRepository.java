package com.brother.graduationwork.repository;

import com.brother.graduationwork.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RoomRepository extends JpaRepository<Room, Long> {

    Room findByTitle(String title);

    @Query("select m from Room m where m.title like CONCAT('%',:title,'%')")
    Page<Room> findAllByTitleLike(@Param("title") String title, Pageable pageable);

    @Query("select m from Room m where m.gatheringPlace like CONCAT('%',:title,'%')")
    Page<Room> findAllByGatheringPlaceLike(@Param("title") String title, Pageable pageable);
}
