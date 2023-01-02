package baedalmate.baedalmate.block.domain;

import baedalmate.baedalmate.recruit.domain.Recruit;
import baedalmate.baedalmate.user.domain.User;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Block {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "block_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private User target;

    //== constructor ==//
    public Block() {}

    public Block(User user, User target) {
        this.user = user;
        this.target = target;
    }

    //== 생성 메서드 ==//
    public static Block createBlock(User user, User target) {
        Block block = new Block();
        block.setTarget(target);
        user.addBlock(block);
        return block;
    }

    //== setter ==//
    public void setUser(User user) {
        this.user = user;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    //== 연관관계 편의 메서드 ==//
}
