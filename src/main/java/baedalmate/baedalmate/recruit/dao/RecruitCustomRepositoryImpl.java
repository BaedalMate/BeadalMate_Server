package baedalmate.baedalmate.recruit.dao;

import baedalmate.baedalmate.errors.exceptions.InvalidPageException;
import baedalmate.baedalmate.recruit.domain.Recruit;
import baedalmate.baedalmate.recruit.dto.HostedRecruitDto;
import baedalmate.baedalmate.recruit.dto.MainPageRecruitDto;
import baedalmate.baedalmate.recruit.dto.ParticipatedRecruitDto;
import baedalmate.baedalmate.recruit.dto.RecruitDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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

    private OrderSpecifier<?> recruitSort(Pageable pageable) {
        String sort = pageable.getSort().toString();
        if (sort.contains("score")) {
            return recruit.user.score.desc();
        } else if (sort.contains("deadlineDate")) {
            return recruit.deadlineDate.asc();
        } else if (sort.contains("view")) {
            return recruit.view.desc();
        } else if (sort.contains("createDate")) {
            return recruit.createDate.desc();
        } else {
            throw new InvalidPageException("Wrong sort parameter.");
        }
    }

    private BooleanExpression exceptClosedRecruit(Boolean exceptClose) {
        if(exceptClose) {
            return recruit.active.eq(true);
        }
        return null;
    }

    private BooleanExpression recruitCategory(Long categoryId) {
        if(categoryId == null) {
            return null;
        }
        return recruit.category.id.eq(categoryId);
    }

    @Override
    public Page<RecruitDto> findAllUsingJoin(Pageable pageable, Long userId, Long categoryId, Boolean exceptClose) {
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
                .where(exceptClosedRecruit(exceptClose))
                .where(recruitCategory(categoryId))
                .where(recruit.user.blocked.size().eq(0).or(recruit.user.blocked.any().user.id.ne(userId)))
                .where(recruit.user.blocks.size().eq(0).or(recruit.user.blocks.any().target.id.ne(userId).or(recruit.user.blocks.any().target.id.isNull())))
                .orderBy(recruitSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(recruit).from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .where(recruit.cancel.eq(false), recruit.fail.eq(false))
                .where(exceptClosedRecruit(exceptClose))
                .where(recruitCategory(categoryId))
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
                .where(recruit.cancel.eq(false), recruit.fail.eq(false), recruit.active.eq(true))
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
                .join(recruit.tags)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .where(recruit.cancel.eq(false), recruit.fail.eq(false), recruit.active.eq(true))
                .where(recruit.tags.any().name.like(Expressions.stringTemplate("'%'")
                        .concat(keyword)
                        .concat(Expressions.stringTemplate("'%'"))))
                .where(recruit.user.blocked.size().eq(0).or(recruit.user.blocked.any().user.id.ne(userId)))
                .where(recruit.user.blocks.size().eq(0).or(recruit.user.blocks.any().target.id.ne(userId).or(recruit.user.blocks.any().target.id.isNull())))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    public Page<ParticipatedRecruitDto> findAllParticipatedRecruitDtoByUserIdUsingJoin(Pageable pageable, Long userId) {
        List<ParticipatedRecruitDto> results = jpaQueryFactory
                .select(Projections.constructor(ParticipatedRecruitDto.class,
                        recruit.id,
                        recruit.place.name,
                        recruit.criteria,
                        recruit.createDate,
                        recruit.deadlineDate,
                        recruit.dormitory,
                        recruit.title,
                        recruit.image,
                        recruit.active,
                        recruit.cancel,
                        recruit.fail))
                .from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.orders)
                .where(recruit.user.id.ne(userId))
                .where(recruit.orders.any().user.id.eq(userId))
                .orderBy(recruit.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(recruit).from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.orders)
                .where(recruit.user.id.eq(userId))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    public Page<HostedRecruitDto> findAllHostedRecruitDtoByUserIdUsingJoin(Pageable pageable, Long userId) {
        List<HostedRecruitDto> results = jpaQueryFactory
                .select(Projections.constructor(HostedRecruitDto.class,
                        recruit.id,
                        recruit.place.name,
                        recruit.criteria,
                        recruit.createDate,
                        recruit.deadlineDate,
                        recruit.dormitory,
                        recruit.title,
                        recruit.image,
                        recruit.active,
                        recruit.cancel,
                        recruit.fail))
                .from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.orders)
                .where(recruit.user.id.eq(userId))
                .orderBy(recruit.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(recruit).from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.user.blocks)
                .leftJoin(recruit.user.blocked)
                .from(recruit).distinct()
                .join(recruit.user)
                .leftJoin(recruit.orders)
                .where(recruit.user.id.eq(userId))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
