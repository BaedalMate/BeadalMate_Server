package baedalmate.baedalmate.block.dao;

import baedalmate.baedalmate.block.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlockJpaRepository extends JpaRepository<Block, Long> {

    @Modifying(clearAutomatically = true)
    @Query("delete from Block b where b.user.id = :userId and b.target.id = :targetId")
    void deleteByUserIdAndTargetId(@Param("userId") Long userId, @Param("targetId") Long targetId);

    @Query("select b from Block b join fetch b.target where b.user.id = :userId")
    List<Block> findAllByUserIdUsingJoinWithTarget(@Param("userId") Long userId);
}
