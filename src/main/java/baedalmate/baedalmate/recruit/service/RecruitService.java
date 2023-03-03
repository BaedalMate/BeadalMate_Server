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
import baedalmate.baedalmate.fcm.event.CancelEvent;
import baedalmate.baedalmate.fcm.event.CloseEvent;
import baedalmate.baedalmate.fcm.event.FailEvent;
import baedalmate.baedalmate.notification.dao.NotificationJpaRepository;
import baedalmate.baedalmate.notification.domain.Notification;
import baedalmate.baedalmate.order.dao.OrderJpaRepository;
import baedalmate.baedalmate.order.dao.OrderRepository;
import baedalmate.baedalmate.order.domain.Menu;
import baedalmate.baedalmate.order.domain.Order;
import baedalmate.baedalmate.recruit.dao.*;
import baedalmate.baedalmate.recruit.domain.*;
import baedalmate.baedalmate.recruit.domain.embed.Place;
import baedalmate.baedalmate.recruit.dto.*;
import baedalmate.baedalmate.user.dao.FcmJpaRepository;
import baedalmate.baedalmate.user.dao.UserJpaRepository;
import baedalmate.baedalmate.user.domain.Fcm;
import baedalmate.baedalmate.user.domain.User;
import baedalmate.baedalmate.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
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
    private final ApplicationEventPublisher eventPublisher;
    private final MenuJpaRepository menuJpaRepository;
    private final FcmJpaRepository fcmJpaRepository;
    private final NotificationJpaRepository notificationJpaRepository;

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

        int shippingFee = recruit.getShippingFee();
        int shippingFeePerParticipant = (int) Math.ceil((double) shippingFee / participants.size());
        int paymentPrice = myTotal.get() + shippingFeePerParticipant;
        return new ParticipantsMenuDto(total.get(), participants.size(), participants, myTotal.get(), shippingFee, shippingFeePerParticipant, paymentPrice);
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
        // Recruit 조회
        if (updateRecruitDto.getMinPeople() <= 1) {
            throw new InvalidApiRequestException("Number of min people must be more than 1");
        }
        Recruit recruit = recruitRepository.findByIdUsingJoinWithOrder(recruitId);
        if (recruit.getCurrentPeople() > 1) {
            throw new InvalidApiRequestException("Someone is participating");
        }
        // 유저조회
        User user = userJpaRepository.findById(userId).get();
        Category category = categoryJpaRepository.findById(updateRecruitDto.getCategoryId()).get();

        if (recruit.getUser().getId() != user.getId()) {
            throw new InvalidApiRequestException("Not host");
        }
        // 배달비 예외
        if (updateRecruitDto.getFreeShipping().equals(true) && updateRecruitDto.getShippingFee() != null) {
            throw new InvalidApiRequestException("Free shipping is true but shipping fee is not null");
        }
        if (updateRecruitDto.getFreeShipping().equals(false) && updateRecruitDto.getShippingFee() == null) {
            throw new InvalidApiRequestException("Free shipping is false but shipping fee is null");
        }
        // 태그 예외
        if (updateRecruitDto.getTags().size() > 4) {
            throw new InvalidParameterException("Number of tag must be less than 5");
        }
        if (updateRecruitDto.getTags().size() == 0) {
            throw new InvalidParameterException("Number of tag must be more than 0");
        }
        category.addRecruit(recruit);
        CategoryImage categoryImage = categoryImageService.getRandomCategoryImage(category);
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
        if(!updateRecruitDto.getFreeShipping()){
            recruit.setShippingFee(updateRecruitDto.getShippingFee());
        }
        recruit.setImage(categoryImage.getName());
        recruit.getTags().clear();
        for (TagDto t : updateRecruitDto.getTags()) {
            if (t.getTagname().length() > 8) {
                throw new InvalidParameterException("Length of tag must be less than 9");
            }
            Tag tag = Tag.createTag(t.getTagname());
            recruit.addTag(tag);
        }
        Order order = orderJpaRepository.findByUserIdAndRecruitIdUsingJoin(userId, recruitId);
        order.getMenus().clear();
        int price = 0;
        for (MenuDto m : updateRecruitDto.getMenu()) {
            price += m.getPrice() * m.getQuantity();
            Menu menu = Menu.createMenu(m.getName(), m.getPrice(), m.getQuantity());
            order.addMenu(menu);
        }
        if (price >= updateRecruitDto.getMinPrice()) {
            throw new InvalidApiRequestException("Current price is bigger than min price");
        }
        recruit.setShippingFee(updateRecruitDto.getFreeShipping().equals(true) ? 0 : updateRecruitDto.getShippingFee());
        recruit.setCurrentPrice(price);
    }

    @Transactional
    public void closeBySchedule() {
        List<Recruit> closedRecruitList = recruitJpaRepository.findAllByDeadlineDateAndCriteriaDate(LocalDateTime.now());
        List<Recruit> failedRecruitList = recruitJpaRepository.findAllByDeadlineDateAndCriteriaNotDate(LocalDateTime.now());
        for (Recruit r : closedRecruitList) {
            if (r.getOrders().size() == 1) {
                failedRecruitList.add(r);
                continue;
            }
            List<Long> userIdList = r.getOrders().stream().map(o -> o.getUser().getId()).collect(Collectors.toList());
            List<Fcm> fcmList = fcmJpaRepository.findAllByUserIdListAndAllowRecruitTrue(userIdList);
            List<Notification> notifications = fcmList.stream().map(f -> f.getUser()).distinct()
                    .map(u -> Notification.createNotification(
                            r.getTitle(),
                            "모집이 마감되었습니다.",
                            r.getImage(),
                            r.getChatRoom().getId(),
                            u))
                    .collect(Collectors.toList());
            notificationJpaRepository.saveAll(notifications);
            eventPublisher.publishEvent(new CloseEvent(
                    r.getChatRoom().getId(),
                    r.getTitle(),
                    "모집이 마감되었습니다.",
                    r.getImage(),
                    fcmList));
        }
        for (Recruit r : failedRecruitList) {
            List<Long> userIdList = r.getOrders().stream().map(o -> o.getUser().getId()).collect(Collectors.toList());
            List<Fcm> fcmList = fcmJpaRepository.findAllByUserIdListAndAllowRecruitTrue(userIdList);
            List<Notification> notifications = fcmList.stream().map(f -> f.getUser()).distinct()
                    .map(u -> Notification.createNotification(
                            r.getTitle(),
                            "모집에 실패하였습니다.",
                            r.getImage(),
                            r.getChatRoom().getId(),
                            u))
                    .collect(Collectors.toList());
            notificationJpaRepository.saveAll(notifications);
            eventPublisher.publishEvent(new FailEvent(
                    r.getChatRoom().getId(),
                    r.getTitle(),
                    "모집에 실패하였습니다.",
                    r.getImage(),
                    fcmList));
        }
        recruitJpaRepository.setActiveFalseFromRecruitExceedTime(LocalDateTime.now());
        recruitJpaRepository.setFailTrueAndActiveFalseFromRecruitExceedTime(LocalDateTime.now());
    }

    @Transactional
    public void cancel(Long recruitId, Long userId) {
        // 유저조회
        User user = userJpaRepository.findById(userId).get();
        // Recruit 조회
        Recruit recruit = recruitRepository.findByIdUsingJoinWithOrder(recruitId);

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
        recruitJpaRepository.setCancelTrueAndActiveFalse(recruitId, LocalDateTime.now());
        orderJpaRepository.deleteById(recruit.getOrders().get(0).getId());
        List<Long> userIdList = recruit.getOrders().stream().map(o -> o.getUser().getId()).collect(Collectors.toList());
        List<Fcm> fcmList = fcmJpaRepository.findAllByUserIdListAndAllowRecruitTrue(userIdList);
        List<Notification> notifications = fcmList.stream().map(f -> f.getUser()).distinct()
                .map(u -> Notification.createNotification(
                        recruit.getTitle(),
                        "모집이 취소되었습니다.",
                        recruit.getImage(),
                        recruit.getChatRoom().getId(),
                        u))
                .collect(Collectors.toList());
        notificationJpaRepository.saveAll(notifications);
        eventPublisher.publishEvent(new CancelEvent(
                recruit.getChatRoom().getId(),
                recruit.getTitle(),
                "모집이 취소되었습니다.",
                recruit.getImage(),
                fcmList));
    }

    @Transactional
    public void close(Long recruitId, Long userId) {
        // 유저조회
        User user = userJpaRepository.findById(userId).get();
        // Recruit 조회
        Recruit recruit = recruitRepository.findByIdUsingJoinWithOrder(recruitId);

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
        recruitJpaRepository.setActiveFalse(recruitId, LocalDateTime.now());
        List<Long> userIdList = recruit.getOrders().stream().map(o -> o.getUser().getId()).collect(Collectors.toList());
        List<Fcm> fcmList = fcmJpaRepository.findAllByUserIdListAndAllowRecruitTrue(userIdList);
        List<Notification> notifications = fcmList.stream().map(f -> f.getUser()).distinct()
                .map(u -> Notification.createNotification(
                        recruit.getTitle(),
                        "모집이 마감되었습니다.",
                        recruit.getImage(),
                        recruit.getChatRoom().getId(),
                        u))
                .collect(Collectors.toList());
        notificationJpaRepository.saveAll(notifications);
        eventPublisher.publishEvent(new CloseEvent(
                recruit.getChatRoom().getId(),
                recruit.getTitle(),
                "모집이 마감되었습니다.",
                recruit.getImage(),
                fcmList));
    }

    @Transactional
    public Long create(Long userId, CreateRecruitDto createRecruitDto) {
        if (createRecruitDto.getMinPeople() <= 1) {
            throw new InvalidApiRequestException("Number of min people must be more than 1");
        }
        // 유저조회
        User user = userJpaRepository.findById(userId).get();

        // 태그 생성
        List<Tag> tags;
        if (createRecruitDto.getTags().size() > 4) {
            throw new InvalidParameterException("Number of tag must be less than 5");
        }
        if (createRecruitDto.getTags().size() == 0) {
            throw new InvalidParameterException("Number of tag must be more than 0");
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

        // 배달비 예외
        if (createRecruitDto.getFreeShipping().equals(true) && createRecruitDto.getShippingFee() != null) {
            throw new InvalidApiRequestException("Free shipping is true but shipping fee is not null");
        }
        if (createRecruitDto.getFreeShipping().equals(false) && createRecruitDto.getShippingFee() == null) {
            throw new InvalidApiRequestException("Free shipping is false but shipping fee is null");
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

        // current price 갱신
        int price = 0;
        for (MenuDto menuDto : createRecruitDto.getMenu()) {
            price += menuDto.getPrice() * menuDto.getQuantity();
        }
        if (price >= createRecruitDto.getMinPrice()) {
            throw new InvalidApiRequestException("Current price is bigger than min price");
        }

        int shippingFee = createRecruitDto.getFreeShipping().equals(true) ? 0 : createRecruitDto.getShippingFee();
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
                createRecruitDto.getTitle(),
                createRecruitDto.getDescription(),
                categoryImage.getName(),
                createRecruitDto.getFreeShipping(),
                shippingFee,
                price,
                tags,
                orders
        );
        // recruit 영속화 (cascade -> order, tag, shippingfee, menu 전부 영속화)
        recruitJpaRepository.save(recruit);

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
                recruit.getPlatform(),
                recruit.getDeadlineDate(),
                recruit.getTitle(),
                recruit.getDescription(),
                recruit.isFreeShipping(),
                recruit.getShippingFee(),
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

        List<TagDto> tags = recruit.getTags().stream().map(t -> new TagDto(t.getName()))
                .collect(Collectors.toList());
        return new RecruitDetailDto(
                recruit.getId(),
                recruit.getImage(),
                recruit.getTitle(),
                recruit.getDescription(),
                placeDto,
                recruit.getPlatform().name(),
                recruit.getDeadlineDate(),
                recruit.getShippingFee(),
                recruit.getCurrentPeople(),
                recruit.getMinPeople(),
                recruit.getCurrentPrice(),
                recruit.getMinPrice(),
                recruit.getDormitory().getName(),
                recruit.isActive(),
                recruit.isCancel(),
                host,
                participate,
                userInfo,
                tags,
                recruit.getChatRoom().getId()
        );
    }

    @Transactional
    public int updateView(Long recruitId) {
        return recruitJpaRepository.updateView(recruitId);
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
                r.getShippingFee(),
                r.getImage(),
                r.isActive()
        )).collect(Collectors.toList());
    }

    public List<MainPageRecruitDtoWithTag> findAllWithTag(Long userId, Pageable pageable) {
        User user = userJpaRepository.findById(userId).get();
        if (user.getDormitory() == null) {
            throw new InvalidApiRequestException("Set dormitory.");
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
                                    r.getShippingFee(),
                                    subList.stream().map(t -> new TagDto(t.getName())).collect(Collectors.toList()),
                                    r.getImage(),
                                    r.isActive());
                        }
                ).collect(Collectors.toList());
    }

    public Page<RecruitDto> findAllByCategory(Long userId, Long categoryId, Pageable pageable, Boolean exceptClose) {
        return recruitJpaRepository.findAllUsingJoin(pageable, userId, categoryId, exceptClose);
    }
}
