package baedalmate.baedalmate.block.service;

import baedalmate.baedalmate.block.dao.BlockJpaRepository;
import baedalmate.baedalmate.block.domain.Block;
import baedalmate.baedalmate.block.dto.BlockedUserDto;
import baedalmate.baedalmate.block.dto.BlockedUserListDto;
import baedalmate.baedalmate.errors.exceptions.InvalidApiRequestException;
import baedalmate.baedalmate.user.dao.UserJpaRepository;
import baedalmate.baedalmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    public BlockedUserListDto getBlockList(Long userId) {
        List<Block> blockList = blockJpaRepository.findAllByUserIdUsingJoinWithTarget(userId);
        List<BlockedUserDto> blockedUserDtos = blockList.stream()
                .map(b -> new BlockedUserDto(b.getTarget().getId(), b.getTarget().getNickname(), b.getTarget().getProfileImage()))
                .collect(Collectors.toList());
        return new BlockedUserListDto(blockedUserDtos);
    }
}
