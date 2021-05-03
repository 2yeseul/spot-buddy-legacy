package kr.co.spotbuddy.modules.block;

import kr.co.spotbuddy.infra.domain.Block;
import kr.co.spotbuddy.infra.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    default boolean isAlreadyBlocked(Member doBlock, Member getBlocked) {
        return existsByDoBlockAndGetBlocked(doBlock, getBlocked);
    }
    boolean existsByDoBlockAndGetBlocked(Member doBlock, Member getBlocked);


    boolean existsByDoBlock(Member doBlock);

    List<Block> findAllByDoBlock(Member doBlock);

    List<Block> findAllByDoBlockOrderByIdDesc(Member doBlock);

    void deleteByDoBlockAndGetBlocked(Member doBlock, Member getBlocked);

}
