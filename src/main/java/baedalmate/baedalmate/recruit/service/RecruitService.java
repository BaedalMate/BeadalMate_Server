package baedalmate.baedalmate.recruit.service;

import baedalmate.baedalmate.category.dao.CategoryJpaRepository;
import baedalmate.baedalmate.category.domain.Category;
import baedalmate.baedalmate.category.domain.CategoryImage;
import baedalmate.baedalmate.category.service.CategoryImageService;
import baedalmate.baedalmate.chat.domain.ChatRoom;
import baedalmate.baedalmate.chat.service.ChatRoomService;
import baedalmate.baedalmate.errors.exceptions.InvalidPageException;
import baedalmate.baedalmate.errors.exceptions.InvalidParameterException;
import baedalmate.baedalmate.order.dao.OrderJpaRepository;
import baedalmate.baedalmate.order.domain.Menu;
import baedalmate.baedalmate.order.domain.Order;
import baedalmate.baedalmate.recruit.dao.RecruitRepository;
import baedalmate.baedalmate.recruit.dao.TagJpaRepository;
import baedalmate.baedalmate.recruit.domain.*;
import baedalmate.baedalmate.recruit.dao.RecruitJpaRepository;
import baedalmate.baedalmate.recruit.domain.embed.Place;
import baedalmate.baedalmate.recruit.dto.*;
import baedalmate.baedalmate.user.dao.UserJpaRepository;
import baedalmate.baedalmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitService {

    private final RecruitJpaRepository recruitJpaRepository;
    private final RecruitRepository recruitRepository;
    private final UserJpaRepository userJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryImageService categoryImageService;
    private final ChatRoomService chatRoomService;

    @Transactional
    public Long createRecruit(Long userId, CreateRecruitDto createRecruitDto) {
        // 유저조회
        User user = userJpaRepository.findById(userId).get();

        // 태그 생성
        List<Tag> tags;
        if(createRecruitDto.getTags().size()>4) {
            throw new InvalidParameterException("Number of tag must be less than 5");
        }
        if(createRecruitDto.getTags().size()>0) {
            tags = createRecruitDto
                    .getTags()
                    .stream()
                    .map(t -> {
                        if(t.getTagname().length()>8) {
                            throw new InvalidParameterException("Length of tag must be less than 9");
                        }
                        return Tag.createTag(t.getTagname());
                    })
                    .collect(Collectors.toList());
        } else {
            tags = new ArrayList<>();
        }

        // 배달비 생성
        List<ShippingFee> shippingFees;
        if(createRecruitDto.isFreeShipping()) { // 무료배달이면 shippingFees는 빈 ArrayList
            shippingFees = new ArrayList<>();
        }
        else {
            shippingFees = createRecruitDto.getShippingFee().stream()
                    .map(c -> ShippingFee.createShippingFee(c.getShippingFee(), c.getLowerPrice(), c.getUpperPrice()))
                    .collect(Collectors.toList());
        }

        // place 생성
        PlaceDto placeDto = createRecruitDto.getPlace();
        Place place = Place.createPlace(placeDto.getName(), placeDto.getAddressName(), placeDto.getRoadAddressName(), placeDto.getX(), placeDto.getY());

        // Category 조회
        Category category = categoryJpaRepository.findById(createRecruitDto.getCategoryId()).get();

        // 랜덤 카테고리 이미지 조회
        CategoryImage categoryImage = categoryImageService.getRandomCategoryImage(category);

        // 모집글 등록자 order 생성
        List<Menu> menus = createRecruitDto.getMenu().stream()
                .map(m -> Menu.createMenu(m.getName(), m.getPrice(), m.getQuantity()))
                .collect(Collectors.toList());
        Order order = Order.createOrder(user, menus);
        List<Order> orders = new ArrayList<Order>();
        orders.add(order);

        // recruit 생성
        Recruit recruit = Recruit.createRecruit(
                user,
                category,
                createRecruitDto.getMinPeople(),
                createRecruitDto.getMinPrice(),
                createRecruitDto.getDeadlineDate(),
                createRecruitDto.getCriteria(),
                createRecruitDto.getDormitory(),
                place,
                createRecruitDto.getPlatform(),
                createRecruitDto.getCoupon(),
                createRecruitDto.getTitle(),
                createRecruitDto.getDescription(),
                categoryImage.getName(),
                createRecruitDto.isFreeShipping(),
                shippingFees,
                tags,
                orders
        );
        // recruit 영속화 (cascade -> order, tag, shippingfee, menu 전부 영속화)
        recruitJpaRepository.save(recruit);

        // current price 갱신
        int price = 0;
        for(MenuDto menuDto : createRecruitDto.getMenu()) {
            price += menuDto.getPrice();
        }
        recruitJpaRepository.updateCurrentPrice(price, recruit.getId());

        // current people 갱신
        recruitJpaRepository.updateCurrentPeople(recruit.getId());

        // chat room 생성
        ChatRoom chatRoom = ChatRoom.createChatRoom(recruit);
        chatRoomService.save(user, chatRoom);
        return recruit.getId();
    }

    public RecruitDetailDto findOne(User user, Long recruitId) {

        // Recruit 조회
        Recruit recruit = recruitRepository.findByIdUsingJoin(recruitId);

        // PlaceDto 생성
        Place place = recruit.getPlace();
        PlaceDto placeDto = new PlaceDto(
                place.getName(),
                place.getAddressName(),
                place.getRoadAddressName(),
                place.getX(),
                place.getY()
        );

        // ShippingFeeDetail 생성
        List<ShippingFeeDto> shippingFeeDetails = recruit.getShippingFees()
                .stream().map(s -> new ShippingFeeDto(
                                s.getShippingFee(),
                                s.getLowerPrice(),
                                s.getUpperPrice()
                        )
                )
                .collect(Collectors.toList());

        boolean host = recruit.getUser().getId() == user.getId() ? true : false;
        boolean participate = false;
        List<Order> orders = orderJpaRepository.findAllByRecruitId(recruitId);
        for(Order order : orders) {
            if(order.getUser().getId() == user.getId()) {
                participate = true;
            }
        }

        User hostUser = recruit.getUser();
        return new RecruitDetailDto(
                recruit.getId(),
                recruit.getImage(),
                recruit.getTitle(),
                recruit.getDescription(),
                placeDto,
                recruit.getPlatform().name(),
                recruit.getDeadlineDate(),
                recruit.getMinShippingFee(),
                shippingFeeDetails,
                recruit.getCoupon(),
                recruit.getCurrentPeople(),
                recruit.getMinPeople(),
                recruit.getDormitory().getName(),
                hostUser.getNickname(),
                hostUser.getScore(),
                hostUser.getProfileImage(),
                hostUser.getDormitoryName(),
                recruit.isActive(),
                host,
                participate
        );
    }

    @Transactional
    public int updateView(Long recruitId) {
        return recruitJpaRepository.updateView(recruitId);
    }

    public List<RecruitDto> findAllRecruitDto(Pageable pageable) {
        String sort = pageable.getSort().toString();
        Pageable p = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        List<Recruit> recruitList;
        if(sort.contains("score")) {
            recruitList = recruitRepository.findAllUsingJoinOrderByScore(p);
        }
        else if(sort.contains("deadlineDate")) {
            recruitList = recruitRepository.findAllUsingJoinOrderByDeadlineDate(p);
        }
        else if(sort.contains("view")) {
            recruitList = recruitRepository.findAllUsingJoinOrderByView(p);
        }
        else {
            throw new InvalidPageException("Wrong sort parameter.");
        }
        return recruitList.stream().map(r -> new RecruitDto(
                r.getId(),
                r.getPlace().getName(),
                r.getMinPeople(),
                r.getMinPrice(),
                r.getCurrentPeople(),
                r.getCurrentPrice(),
                r.getCriteria(),
                r.getCreateDate(),
                r.getDeadlineDate(),
                r.getUser().getScore(),
                r.getDormitory().getName(),
                r.getTitle(),
                r.getImage()
        )).collect(Collectors.toList());
    }

    public List<MainPageRecruitDto> findAllMainPageRecruitDto(Pageable pageable) {
        String sort = pageable.getSort().toString();
        Pageable p = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        List<Recruit> recruitList;
        if(sort.contains("score")) {
            recruitList = recruitRepository.findAllUsingJoinOrderByScore(p);
        }
        else if(sort.contains("deadlineDate")) {
            recruitList = recruitRepository.findAllUsingJoinOrderByDeadlineDate(p);
        }
        else if(sort.contains("view")) {
            recruitList = recruitRepository.findAllUsingJoinOrderByView(p);
        }
        else {
            throw new InvalidPageException("Wrong sort parameter.");
        }
        return recruitList.stream().map(r -> new MainPageRecruitDto(
                r.getId(),
                r.getPlace().getName(),
                r.getMinPeople(),
                r.getCurrentPeople(),
                r.getMinPrice(),
                r.getCreateDate(),
                r.getDeadlineDate(),
                r.getUser().getNickname(),
                r.getUser().getScore(),
                r.getDormitory().getName(),
                r.getMinShippingFee(),
                r.getImage()
        )).collect(Collectors.toList());
    }

    @Transactional
    public int updateCurrentPeople(Recruit recruit) {
        int currentPeople = recruitJpaRepository.updateCurrentPeople(recruit.getId());

        // 인원수 검사
        if (recruit.getMinPeople() <= recruit.updateCurrentPeople() && recruit.getCriteria() == Criteria.NUMBER) {
            recruit.setActive(false);
        }
        return currentPeople;
    }

    public List<MainPageRecruitDtoWithTag> findAllWithTag(Dormitory dormitory, Pageable pageable) {
        return recruitRepository.findAllWithTagsUsingJoinOrderByDeadlineDate(dormitory, pageable)
                .stream().map(r -> new MainPageRecruitDtoWithTag(
                        r.getId(),
                        r.getPlace().getName(),
                        r.getMinPrice(),
                        r.getCreateDate(),
                        r.getDeadlineDate(),
                        r.getUser().getNickname(),
                        r.getUser().getScore(),
                        r.getDormitory().getName(),
                        r.getMinShippingFee(),
                        r.getTags().stream().map(t -> new TagDto(t.getName())).collect(Collectors.toList()),
                        r.getImage()
                )).collect(Collectors.toList());
    }

    public List<RecruitDto> findAllByCategory(Long categoryId, Pageable pageable) {
        String sort = pageable.getSort().toString();
        List<Recruit> recruitList;
        if(sort.contains("score")) {
            recruitList = recruitRepository.findAllByCategoryUsingJoinOrderByScore(categoryId, pageable);
        }
        if(sort.contains("deadlineDate")) {
            recruitList = recruitRepository.findAllByCategoryUsingJoinOrderByDeadlineDate(categoryId, pageable);
        }
        if(sort.contains("view")) {
            recruitList = recruitRepository.findAllByCategoryUsingJoinOrderByView(categoryId, pageable);
        }
        else {
            throw new InvalidPageException("Wrong sort parameter.");
        }
        return recruitList.stream()
                .map(r -> new RecruitDto(
                        r.getId(),
                        r.getPlace().getName(),
                        r.getMinPeople(),
                        r.getMinPrice(),
                        r.getCurrentPeople(),
                        r.getCurrentPrice(),
                        r.getCriteria(),
                        r.getCreateDate(),
                        r.getDeadlineDate(),
                        r.getUser().getScore(),
                        r.getDormitory().getName(),
                        r.getTitle(),
                        r.getImage()
        )).collect(Collectors.toList());
    }
}
