package baedalmate.baedalmate.recruit.dao;

import baedalmate.baedalmate.recruit.domain.Recruit;
import baedalmate.baedalmate.recruit.dto.MainPageRecruitDto;
import baedalmate.baedalmate.recruit.dto.RecruitDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static baedalmate.baedalmate.recruit.domain.QRecruit.recruit;

@Repository
@RequiredArgsConstructor
public class RecruitCustomRepositoryImpl implements RecruitCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<RecruitDto> findAllUsingJoinOrderByDeadlineDate(Pageable pageable, Long userId) {
        List<RecruitDto> results = jpaQueryFactory
                .select(Projections.constructor(RecruitDto.class,
                        recruit.id,
                        recruit.place.name,
                        recruit.minPeople,
                        recruit.minPrice,
                        recruit.currentPeople,
                        recruit.currentPrice,
                        recruit.criteria,
                        recruit.createDate,
                        recruit.deadlineDate,
                        recruit.user.score,
                        recruit.dormitory,
                        recruit.title,
                        recruit.image,
                        recruit.active))
                .from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .where(recruit.cancel.eq(false), recruit.fail.eq(false))
                .where(recruit.user.blocked.size().eq(0).or(recruit.user.blocked.any().user.id.ne(userId)))
                .where(recruit.user.blocks.size().eq(0).or(recruit.user.blocks.any().target.id.ne(userId).or(recruit.user.blocks.any().target.id.isNull())))
                .orderBy(recruit.deadlineDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(recruit).from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .where(recruit.cancel.eq(false), recruit.fail.eq(false))
                .where(recruit.user.blocked.size().eq(0).or(recruit.user.blocked.any().user.id.ne(userId)))
                .where(recruit.user.blocks.size().eq(0).or(recruit.user.blocks.any().target.id.ne(userId).or(recruit.user.blocks.any().target.id.isNull())))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<RecruitDto> findAllUsingJoinOrderByScore(Pageable pageable, Long userId) {
        List<RecruitDto> results = jpaQueryFactory
                .select(Projections.constructor(RecruitDto.class,
                        recruit.id,
                        recruit.place.name,
                        recruit.minPeople,
                        recruit.minPrice,
                        recruit.currentPeople,
                        recruit.currentPrice,
                        recruit.criteria,
                        recruit.createDate,
                        recruit.deadlineDate,
                        recruit.user.score,
                        recruit.dormitory,
                        recruit.title,
                        recruit.image,
                        recruit.active))
                .from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .where(recruit.cancel.eq(false), recruit.fail.eq(false))
                .where(recruit.user.blocked.size().eq(0).or(recruit.user.blocked.any().user.id.ne(userId)))
                .where(recruit.user.blocks.size().eq(0).or(recruit.user.blocks.any().target.id.ne(userId).or(recruit.user.blocks.any().target.id.isNull())))
                .orderBy(recruit.user.score.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(recruit).from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .where(recruit.cancel.eq(false), recruit.fail.eq(false))
                .where(recruit.user.blocked.size().eq(0).or(recruit.user.blocked.any().user.id.ne(userId)))
                .where(recruit.user.blocks.size().eq(0).or(recruit.user.blocks.any().target.id.ne(userId).or(recruit.user.blocks.any().target.id.isNull())))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<RecruitDto> findAllUsingJoinOrderByView(Pageable pageable, Long userId) {
        List<RecruitDto> results = jpaQueryFactory
                .select(Projections.constructor(RecruitDto.class,
                        recruit.id,
                        recruit.place.name,
                        recruit.minPeople,
                        recruit.minPrice,
                        recruit.currentPeople,
                        recruit.currentPrice,
                        recruit.criteria,
                        recruit.createDate,
                        recruit.deadlineDate,
                        recruit.user.score,
                        recruit.dormitory,
                        recruit.title,
                        recruit.image,
                        recruit.active))
                .from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .where(recruit.cancel.eq(false), recruit.fail.eq(false))
                .where(recruit.user.blocked.size().eq(0).or(recruit.user.blocked.any().user.id.ne(userId)))
                .where(recruit.user.blocks.size().eq(0).or(recruit.user.blocks.any().target.id.ne(userId).or(recruit.user.blocks.any().target.id.isNull())))
                .orderBy(recruit.view.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(recruit).from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .where(recruit.cancel.eq(false), recruit.fail.eq(false))
                .where(recruit.user.blocked.size().eq(0).or(recruit.user.blocked.any().user.id.ne(userId)))
                .where(recruit.user.blocks.size().eq(0).or(recruit.user.blocks.any().target.id.ne(userId).or(recruit.user.blocks.any().target.id.isNull())))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<RecruitDto> findAllByCategoryIdUsingJoinOrderByView(Pageable pageable, Long userId, Long categoryId) {
        List<RecruitDto> results = jpaQueryFactory
                .select(Projections.constructor(RecruitDto.class,
                        recruit.id,
                        recruit.place.name,
                        recruit.minPeople,
                        recruit.minPrice,
                        recruit.currentPeople,
                        recruit.currentPrice,
                        recruit.criteria,
                        recruit.createDate,
                        recruit.deadlineDate,
                        recruit.user.score,
                        recruit.dormitory,
                        recruit.title,
                        recruit.image,
                        recruit.active))
                .from(recruit).distinct()
                .join(recruit.user)
                .join(recruit.category)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .where(recruit.cancel.eq(false), recruit.fail.eq(false))
                .where(recruit.user.blocked.size().eq(0).or(recruit.user.blocked.any().user.id.ne(userId)))
                .where(recruit.user.blocks.size().eq(0).or(recruit.user.blocks.any().target.id.ne(userId).or(recruit.user.blocks.any().target.id.isNull())))
                .where(recruit.category.id.eq(categoryId))
                .orderBy(recruit.view.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(recruit).from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .where(recruit.cancel.eq(false), recruit.fail.eq(false))
                .where(recruit.user.blocked.size().eq(0).or(recruit.user.blocked.any().user.id.ne(userId)))
                .where(recruit.user.blocks.size().eq(0).or(recruit.user.blocks.any().target.id.ne(userId).or(recruit.user.blocks.any().target.id.isNull())))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<RecruitDto> findAllByCategoryIdUsingJoinOrderByDeadlineDate(Pageable pageable, Long userId, Long categoryId) {
        List<RecruitDto> results = jpaQueryFactory
                .select(Projections.constructor(RecruitDto.class,
                        recruit.id,
                        recruit.place.name,
                        recruit.minPeople,
                        recruit.minPrice,
                        recruit.currentPeople,
                        recruit.currentPrice,
                        recruit.criteria,
                        recruit.createDate,
                        recruit.deadlineDate,
                        recruit.user.score,
                        recruit.dormitory,
                        recruit.title,
                        recruit.image,
                        recruit.active))
                .from(recruit).distinct()
                .join(recruit.user)
                .join(recruit.category)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .where(recruit.cancel.eq(false), recruit.fail.eq(false))
                .where(recruit.user.blocked.size().eq(0).or(recruit.user.blocked.any().user.id.ne(userId)))
                .where(recruit.user.blocks.size().eq(0).or(recruit.user.blocks.any().target.id.ne(userId).or(recruit.user.blocks.any().target.id.isNull())))
                .where(recruit.category.id.eq(categoryId))
                .orderBy(recruit.deadlineDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(recruit).from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .where(recruit.cancel.eq(false), recruit.fail.eq(false))
                .where(recruit.user.blocked.size().eq(0).or(recruit.user.blocked.any().user.id.ne(userId)))
                .where(recruit.user.blocks.size().eq(0).or(recruit.user.blocks.any().target.id.ne(userId).or(recruit.user.blocks.any().target.id.isNull())))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<RecruitDto> findAllByCategoryIdUsingJoinOrderByScore(Pageable pageable, Long userId, Long categoryId) {
        List<RecruitDto> results = jpaQueryFactory
                .select(Projections.constructor(RecruitDto.class,
                        recruit.id,
                        recruit.place.name,
                        recruit.minPeople,
                        recruit.minPrice,
                        recruit.currentPeople,
                        recruit.currentPrice,
                        recruit.criteria,
                        recruit.createDate,
                        recruit.deadlineDate,
                        recruit.user.score,
                        recruit.dormitory,
                        recruit.title,
                        recruit.image,
                        recruit.active))
                .from(recruit).distinct()
                .join(recruit.user)
                .join(recruit.category)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .where(recruit.cancel.eq(false), recruit.fail.eq(false))
                .where(recruit.user.blocked.size().eq(0).or(recruit.user.blocked.any().user.id.ne(userId)))
                .where(recruit.user.blocks.size().eq(0).or(recruit.user.blocks.any().target.id.ne(userId).or(recruit.user.blocks.any().target.id.isNull())))
                .where(recruit.category.id.eq(categoryId))
                .orderBy(recruit.user.score.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(recruit).from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .where(recruit.cancel.eq(false), recruit.fail.eq(false))
                .where(recruit.user.blocked.size().eq(0).or(recruit.user.blocked.any().user.id.ne(userId)))
                .where(recruit.user.blocks.size().eq(0).or(recruit.user.blocks.any().target.id.ne(userId).or(recruit.user.blocks.any().target.id.isNull())))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<RecruitDto> findAllByTagUsingJoin(String keyword, Pageable pageable, Long userId) {
        List<RecruitDto> results = jpaQueryFactory
                .select(Projections.constructor(RecruitDto.class,
                        recruit.id,
                        recruit.place.name,
                        recruit.minPeople,
                        recruit.minPrice,
                        recruit.currentPeople,
                        recruit.currentPrice,
                        recruit.criteria,
                        recruit.createDate,
                        recruit.deadlineDate,
                        recruit.user.score,
                        recruit.dormitory,
                        recruit.title,
                        recruit.image,
                        recruit.active))
                .from(recruit).distinct()
                .join(recruit.user)
                .join(recruit.tags)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .where(recruit.cancel.eq(false), recruit.fail.eq(false))
                .where(recruit.tags.any().name.like(Expressions.stringTemplate("'%'")
                                .concat(keyword)
                                .concat(Expressions.stringTemplate("'%'"))))
                .where(recruit.user.blocked.size().eq(0).or(recruit.user.blocked.any().user.id.ne(userId)))
                .where(recruit.user.blocks.size().eq(0).or(recruit.user.blocks.any().target.id.ne(userId).or(recruit.user.blocks.any().target.id.isNull())))
                .orderBy(recruit.user.score.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(recruit).from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .where(recruit.cancel.eq(false), recruit.fail.eq(false))
                .where(recruit.tags.any().name.like(Expressions.stringTemplate("'%'")
                        .concat(keyword)
                        .concat(Expressions.stringTemplate("'%'"))))
                .where(recruit.user.blocked.size().eq(0).or(recruit.user.blocked.any().user.id.ne(userId)))
                .where(recruit.user.blocks.size().eq(0).or(recruit.user.blocks.any().target.id.ne(userId).or(recruit.user.blocks.any().target.id.isNull())))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
