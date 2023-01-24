package baedalmate.baedalmate.recruit.service;

import baedalmate.baedalmate.block.dao.BlockJpaRepository;
import baedalmate.baedalmate.block.domain.Block;
import baedalmate.baedalmate.category.dao.CategoryJpaRepository;
import baedalmate.baedalmate.category.domain.Category;
import baedalmate.baedalmate.category.domain.CategoryImage;
import baedalmate.baedalmate.category.service.CategoryImageService;
import baedalmate.baedalmate.chat.domain.ChatRoom;
import baedalmate.baedalmate.chat.service.ChatRoomService;
import baedalmate.baedalmate.errors.exceptions.*;
import baedalmate.baedalmate.order.dao.OrderJpaRepository;
import baedalmate.baedalmate.order.dao.OrderRepository;
import baedalmate.baedalmate.order.domain.Menu;
import baedalmate.baedalmate.order.domain.Order;
import baedalmate.baedalmate.recruit.dao.*;
import baedalmate.baedalmate.recruit.domain.*;
import baedalmate.baedalmate.recruit.domain.embed.Place;
import baedalmate.baedalmate.recruit.dto.*;
import baedalmate.baedalmate.user.dao.UserJpaRepository;
import baedalmate.baedalmate.user.domain.User;
import baedalmate.baedalmate.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RecruitService {

    private final RecruitJpaRepository recruitJpaRepository;
    private final RecruitRepository recruitRepository;
    private final UserJpaRepository userJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryImageService categoryImageService;
    private final ChatRoomService chatRoomService;
    private final ShippingFeeJpaRepository shippingFeeJpaRepository;
    private final OrderRepository orderRepository;
    private final BlockJpaRepository blockJpaRepository;
    private final MenuJpaRepository menuJpaRepository;

    public Page<HostedRecruitDto> findHostedRecruit(Long userId, Pageable pageable) {
        Page<HostedRecruitDto> hostedRecruitDtos = recruitJpaRepository.findAllHostedRecruitDtoByUserIdUsingJoin(pageable, userId);
        return hostedRecruitDtos;
    }

    public Page<ParticipatedRecruitDto> findParticipatedRecruit(Long userId, Pageable pageable) {
        Page<ParticipatedRecruitDto> participatedRecruits = recruitJpaRepository.findAllParticipatedRecruitDtoByUserIdUsingJoin(pageable, userId);
        return participatedRecruits;
    }

    public Page<RecruitDto> findAllByTag(Long userId, String keyword, Pageable pageable) {
        Page<RecruitDto> recruits = recruitJpaRepository.findAllByTagUsingJoin(keyword, pageable, userId);
        return recruits;
    }

    public MyMenuDto getMyMenu(Long userId, Long recruitId) {
        Order order;
        try {
            order = orderJpaRepository.findByUserIdAndRecruitIdUsingJoin(userId, recruitId);
        } catch (ResourceNotFoundException e) {
            throw new InvalidApiRequestException("User is not participant");
        }
        AtomicInteger price = new AtomicInteger();
        List<MenuDto> menus = order.getMenus().stream().map(m -> {
                    price.addAndGet(m.getPrice() * m.getQuantity());
                    return new MenuDto(m.getName(), m.getPrice(), m.getQuantity());
                })
                .collect(Collectors.toList());
        return new MyMenuDto(userId, menus, price.get());
    }

    public ParticipantsMenuDto getMenu(Long userId, Long recruitId) {
        AtomicBoolean participate = new AtomicBoolean(false);
        AtomicInteger total = new AtomicInteger(0);
        AtomicInteger myTotal = new AtomicInteger();
        Recruit recruit = recruitRepository.findByIdUsingJoin(recruitId);
        List<ParticipantMenuDto> participants = orderJpaRepository.findAllByRecruitIdUsingJoin(recruitId)
                .stream().map(o -> {
                    if (o.getUser().getId() == userId) {
                        participate.set(true);
                    }
                    AtomicInteger t = new AtomicInteger(0);
                    List<MenuDto> menu = o.getMenus().stream().map(m -> {
                        t.addAndGet(m.getPrice() * m.getQuantity());
                        return new MenuDto(m.getName(), m.getPrice(), m.getQuantity());
                    }).collect(Collectors.toList());
                    total.addAndGet(t.get());
                    if (o.getUser().getId() == userId) {
                        participate.set(true);
                        myTotal.set(t.get());
                    }
                    return new ParticipantMenuDto(
                            o.getUser().getId(),
                            o.getUser().getNickname(),
                            o.getUser().getProfileImage(),
                            menu,
                            t.get());
                })
                .collect(Collectors.toList());
        if (participate.get() == false) {
            throw new InvalidApiRequestException("User is not participant");
        }

        int shippingFee = 0;
        if (!recruit.isFreeShipping()) {
            for (ShippingFee sf : recruit.getShippingFees()) {
                if (sf.getLowerPrice() <= total.get()) {
                    shippingFee = sf.getShippingFee();
                }
            }
        }
        int shippingFeePerParticipant = (int) Math.ceil((double) shippingFee / participants.size());
        int paymentPrice = myTotal.get() + shippingFeePerParticipant - (int) Math.floor((double) recruit.getCoupon() / participants.size());
        return new ParticipantsMenuDto(total.get(), participants.size(), participants, myTotal.get(), shippingFee, shippingFeePerParticipant, recruit.getCoupon(), paymentPrice);
    }

    public ParticipantsDto getParticipants(Long userId, Long recruitId) {
        AtomicBoolean participate = new AtomicBoolean(false);
        List<Block> blocks = blockJpaRepository.findAllByUserIdUsingJoinWithTarget(userId);
        List<ParticipantDto> participants = orderJpaRepository.findAllByRecruitIdUsingJoin(recruitId)
                .stream().map(o -> {
                    if (o.getUser().getId() == userId) {
                        participate.set(true);
                    }
                    boolean block = false;
                    if (blocks.stream().anyMatch(b -> b.getTarget().getId() == o.getUser().getId())) {
                        block = true;
                    }
                    return new ParticipantDto(o.getUser().getId(), o.getUser().getNickname(), o.getUser().getProfileImage(), block);
                })
                .collect(Collectors.toList());
        if (participate.get() == false) {
            throw new InvalidApiRequestException("User is not participant");
        }
        return new ParticipantsDto(recruitId, participants);
    }

    @Transactional
    public void update(Long userId, Long recruitId, UpdateRecruitDto updateRecruitDto) {
        // 유저조회
        User user = userJpaRepository.findById(userId).get();
        // Recruit 조회
        Recruit recruit = recruitRepository.findByIdUsingJoinWithOrder(recruitId);

        Order order = orderJpaRepository.findByUserIdAndRecruitIdUsingJoin(userId, recruitId);

        boolean host = recruit.getUser().getId() == user.getId() ? true : false;

        if (!host) {
            throw new InvalidApiRequestException("Not host");
        }

        if (recruit.getCurrentPeople() > 1) {
            throw new InvalidApiRequestException("Someone is participating");
        }

        Category category = categoryJpaRepository.findById(updateRecruitDto.getCategoryId()).get();
        recruit.setCategory(category);
        PlaceDto placeDto = updateRecruitDto.getPlace();
        Place place = Place.createPlace(placeDto.getName(), placeDto.getAddressName(), placeDto.getRoadAddressName(), placeDto.getX(), placeDto.getY());
        recruit.setPlace(place);
        recruit.setDormitory(updateRecruitDto.getDormitory());
        recruit.setCriteria(updateRecruitDto.getCriteria());
        recruit.setMinPrice(updateRecruitDto.getMinPrice());
        recruit.setMinPeople(updateRecruitDto.getMinPeople());
        recruit.setFreeShipping(updateRecruitDto.getFreeShipping());

        recruit.setPlatform(updateRecruitDto.getPlatform());
        recruit.setDeadlineDate(updateRecruitDto.getDeadlineDate());
        recruit.setTitle(updateRecruitDto.getTitle());
        recruit.setDescription(updateRecruitDto.getDescription());
        recruitJpaRepository.save(recruit);
        if (updateRecruitDto.getTags().size() > 4) {
            throw new InvalidParameterException("Number of tag must be less than 5");
        }
        if (updateRecruitDto.getTags().size() > 0) {
            tagJpaRepository.deleteByRecruitId(recruitId);
            List<Tag> tags = updateRecruitDto.getTags().stream()
                    .map(t -> {

                        Tag tag = Tag.createTag(t.getTagname());
                        tag.setRecruit(recruit);
                        return tag;
                    })
                    .collect(Collectors.toList());
            tagJpaRepository.saveAll(tags);
        }
        shippingFeeJpaRepository.deleteByRecruitId(recruitId);
        List<ShippingFee> shippingFees = updateRecruitDto.getShippingFee().stream()
                .map(sf -> {
                    ShippingFee shippingFee = ShippingFee.createShippingFee(
                            sf.getShippingFee(),
                            sf.getLowerPrice(),
                            sf.getUpperPrice());
                    shippingFee.setRecruit(recruit);
                    return shippingFee;
                })
                .collect(Collectors.toList());
        shippingFeeJpaRepository.saveAll(shippingFees);

        menuJpaRepository.deleteByOrderId(order.getId());
        List<Menu> menus = updateRecruitDto.getMenu().stream()
                .map(m -> {
                    Menu menu = Menu.createMenu(m.getName(), m.getPrice(), m.getQuantity());
                    menu.setOrder(order);
                    return menu;
                })
                .collect(Collectors.toList());
        menuJpaRepository.saveAll(menus);

    }

    @Transactional
    public void closeBySchedule() {
//        recruitJpaRepository.setCancelTrueFromRecruitExceedTime(LocalDateTime.now());
        recruitJpaRepository.setActiveFalseFromRecruitExceedTime(LocalDateTime.now());
        recruitJpaRepository.setFailTrueAndActiveFalseFromRecruitExceedTime(LocalDateTime.now());
    }

    @Transactional
    public void cancel(Long recruitId, Long userId) {
        // 유저조회
        User user = userJpaRepository.findById(userId).get();
        // Recruit 조회
        Recruit recruit = recruitRepository.findByIdUsingJoin(recruitId);

        boolean host = recruit.getUser().getId() == user.getId() ? true : false;

        if (!host) {
            throw new InvalidApiRequestException("Not host");
        }
        if (recruit.isCancel()) {
            throw new InvalidApiRequestException("Already canceled recruit");
        }
        if (!recruit.isActive()) {
            throw new InvalidApiRequestException("Already closed recruit");
        }
//        recruitJpaRepository.setActiveFalse(recruitId);
        recruitJpaRepository.setCancelTrueAndActiveFalse(recruitId);
    }

    @Transactional
    public void close(Long recruitId, Long userId) {
        // 유저조회
        User user = userJpaRepository.findById(userId).get();
        // Recruit 조회
        Recruit recruit = recruitRepository.findByIdUsingJoin(recruitId);

        boolean host = recruit.getUser().getId() == user.getId() ? true : false;

        if (!host) {
            throw new InvalidApiRequestException("Not host");
        }
        if (recruit.isCancel()) {
            throw new InvalidApiRequestException("Already canceled recruit");
        }
        if (!recruit.isActive()) {
            throw new InvalidApiRequestException("Already closed recruit");
        }
        recruitJpaRepository.setActiveFalse(recruitId);
    }

    @Transactional
    public Long create(Long userId, CreateRecruitDto createRecruitDto) {
        // 유저조회
        User user = userJpaRepository.findById(userId).get();

        // 태그 생성
        List<Tag> tags;
        if (createRecruitDto.getTags().size() > 4) {
            throw new InvalidParameterException("Number of tag must be less than 5");
        }
        if (createRecruitDto.getTags().size() > 0) {
            tags = createRecruitDto
                    .getTags()
                    .stream()
                    .map(t -> {
                        if (t.getTagname().length() > 8) {
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
        if (createRecruitDto.getFreeShipping()) { // 무료배달이면 shippingFees는 빈 ArrayList
            shippingFees = new ArrayList<>();
        } else {
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
                createRecruitDto.getFreeShipping(),
                shippingFees,
                tags,
                orders
        );
        // recruit 영속화 (cascade -> order, tag, shippingfee, menu 전부 영속화)
        recruitJpaRepository.save(recruit);

        // current price 갱신
        int price = 0;
        for (MenuDto menuDto : createRecruitDto.getMenu()) {
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

    public RecruitDetailForModifyDto getRecruitDetail(Long userId, Long recruitId) {
        // Recruit 조회
        Recruit recruit = recruitRepository.findByIdUsingJoin(recruitId);

        if (recruit.getUser().getId() != userId) {
            throw new InvalidApiRequestException("Not host");
        }

        Order order = orderJpaRepository.findByUserIdAndRecruitIdUsingJoin(userId, recruitId);
        List<MenuDto> menuDtos = order.getMenus().stream()
                .map(m -> new MenuDto(m.getName(), m.getPrice(), m.getQuantity())).collect(Collectors.toList());
        return new RecruitDetailForModifyDto(
                recruitId,
                recruit.getCategory().getId(),
                new PlaceDto(
                        recruit.getPlace().getName(),
                        recruit.getPlace().getAddressName(),
                        recruit.getPlace().getRoadAddressName(),
                        recruit.getPlace().getX(),
                        recruit.getPlace().getY()),
                recruit.getDormitory(),
                recruit.getCriteria(),
                recruit.getMinPrice(),
                recruit.getMinPeople(),
                recruit.getShippingFees().stream()
                        .map(sf -> new ShippingFeeDto(sf.getShippingFee(), sf.getLowerPrice(), sf.getUpperPrice()))
                        .collect(Collectors.toList()),
                recruit.getCoupon(),
                recruit.getPlatform(),
                recruit.getDeadlineDate(),
                recruit.getTitle(),
                recruit.getDescription(),
                recruit.isFreeShipping(),
                menuDtos,
                recruit.getTags().stream().map(t -> new TagDto(t.getName())).collect(Collectors.toList())
        );
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
        List<Order> orders = orderJpaRepository.findAllByRecruitIdUsingJoin(recruitId);
        for (Order order : orders) {
            if (order.getUser().getId() == user.getId()) {
                participate = true;
            }
        }

        User hostUser = recruit.getUser();
        UserInfoDto userInfo = new UserInfoDto(
                hostUser.getId(),
                hostUser.getNickname(),
                hostUser.getProfileImage(),
                hostUser.getDormitoryName(),
                hostUser.getScore()
        );

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
                recruit.getCurrentPrice(),
                recruit.getMinPrice(),
                recruit.getDormitory().getName(),
                recruit.isActive(),
                recruit.isCancel(),
                host,
                participate,
                userInfo
        );
    }

    @Transactional
    public int updateView(Long recruitId) {
        return recruitJpaRepository.updateView(recruitId);
    }

    public Page<RecruitDto> findAllRecruitDto(Long userId, Pageable pageable) {
        String sort = pageable.getSort().toString();
        Pageable p = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        List<Recruit> recruitList;
        log.debug("get using queryDsl");
        Page<RecruitDto> recruits;
        if (sort.contains("score")) {
            recruits = recruitJpaRepository.findAllUsingJoinOrderByScore(pageable, userId);
        } else if (sort.contains("deadlineDate")) {
            log.debug("get using JpaRepository");
            recruits = recruitJpaRepository.findAllUsingJoinOrderByDeadlineDate(pageable, userId);
        } else if (sort.contains("view")) {
            recruits = recruitJpaRepository.findAllUsingJoinOrderByView(pageable, userId);
        } else if (sort.contains("createDate")) {
            recruits = recruitJpaRepository.findAllUsingJoinOrderByCreateDate(pageable, userId);
        } else {
            throw new InvalidPageException("Wrong sort parameter.");
        }
        return recruits;
    }

    public List<MainPageRecruitDto> findAllMainPageRecruitDto(Long userId, Pageable pageable) {
        String sort = pageable.getSort().toString();
        Pageable p = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        List<Recruit> recruitList;
        if (sort.contains("score")) {
            recruitList = recruitRepository.findAllUsingJoinOrderByScore(p, userId);
        } else if (sort.contains("deadlineDate")) {
            recruitList = recruitRepository.findAllUsingJoinOrderByDeadlineDate(p, userId);
        } else if (sort.contains("view")) {
            recruitList = recruitRepository.findAllUsingJoinOrderByView(p, userId);
        } else if (sort.contains("createDate")) {
            recruitList = recruitRepository.findAllUsingJoinOrderByCreateDate(p, userId);
        } else {
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
                r.getImage(),
                r.isActive()
        )).collect(Collectors.toList());
    }

    public List<MainPageRecruitDtoWithTag> findAllWithTag(Long userId, Pageable pageable) {
        User user = userJpaRepository.findById(userId).get();
        if (user.getDormitory() == null) {
            throw new ResourceNotFoundException();
        }
        return recruitRepository.findAllWithTagsUsingJoinOrderByDeadlineDate(user.getDormitory(), pageable, userId)
                .stream().map(r -> {
                            List<Tag> tags = r.getTags();
                            Collections.shuffle(tags);
                            int size = tags.size() >= 2 ? 2 : tags.size();
                            List<Tag> subList = new ArrayList<Tag>(tags.subList(0, size));
                            return new MainPageRecruitDtoWithTag(
                                    r.getId(),
                                    r.getPlace().getName(),
                                    r.getMinPrice(),
                                    r.getCreateDate(),
                                    r.getDeadlineDate(),
                                    r.getUser().getNickname(),
                                    r.getUser().getScore(),
                                    r.getDormitory().getName(),
                                    r.getMinShippingFee(),
                                    subList.stream().map(t -> new TagDto(t.getName())).collect(Collectors.toList()),
                                    r.getImage(),
                                    r.isActive());
                        }
                ).collect(Collectors.toList());
    }

    public Page<RecruitDto> findAllByCategory(Long userId, Long categoryId, Pageable pageable) {
        String sort = pageable.getSort().toString();
        Page<RecruitDto> recruitList;
        if (sort.contains("score")) {
            recruitList = recruitJpaRepository.findAllByCategoryIdUsingJoinOrderByScore(pageable, userId, categoryId);
        } else if (sort.contains("deadlineDate")) {
            recruitList = recruitJpaRepository.findAllByCategoryIdUsingJoinOrderByDeadlineDate(pageable, userId, categoryId);
        } else if (sort.contains("view")) {
            recruitList = recruitJpaRepository.findAllByCategoryIdUsingJoinOrderByView(pageable, userId, categoryId);
        } else if (sort.contains("createDate")) {
            recruitList = recruitJpaRepository.findAllByCategoryIdUsingJoinOrderByCreateDate(pageable, userId, categoryId);
        } else {
            throw new InvalidPageException("Wrong sort parameter.");
        }
        return recruitList;
    }
}
