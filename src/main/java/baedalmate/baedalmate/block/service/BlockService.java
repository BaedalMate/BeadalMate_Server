package baedalmate.baedalmate.block.service;

import baedalmate.baedalmate.block.dao.BlockJpaRepository;
import baedalmate.baedalmate.block.domain.Block;
import baedalmate.baedalmate.errors.exceptions.InvalidApiRequestException;
import baedalmate.baedalmate.user.dao.UserJpaRepository;
import baedalmate.baedalmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BlockService {

    private final UserJpaRepository userJpaRepository;
    private final BlockJpaRepository blockJpaRepository;

    @Transactional
    public void block(Long userId, Long targetId) {
        User user = userJpaRepository.findByIdUsingJoinWithBlock(userId);
        if(user.getBlocks().stream().anyMatch(b -> b.getTarget().getId() == targetId)) {
            throw new InvalidApiRequestException("Already blocked");
        }
        User target = userJpaRepository.findById(targetId).get();
        Block block = Block.createBlock(user, target);
        blockJpaRepository.save(block);
    }

    @Transactional
    public void unblock(Long userId, Long targetId) {
        User user = userJpaRepository.findByIdUsingJoinWithBlock(userId);
        if(!user.getBlocks().stream().anyMatch(b -> b.getTarget().getId() == targetId)) {
            throw new InvalidApiRequestException("Target is not blocked");
        }
        blockJpaRepository.deleteByUserIdAndTargetId(userId, targetId);
    }
}
