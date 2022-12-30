package baedalmate.baedalmate.recruit.service;

import baedalmate.baedalmate.category.dao.CategoryJpaRepository;
import baedalmate.baedalmate.category.domain.Category;
import baedalmate.baedalmate.category.domain.CategoryImage;
import baedalmate.baedalmate.category.service.CategoryImageService;
import baedalmate.baedalmate.chat.domain.ChatRoom;
import baedalmate.baedalmate.chat.service.ChatRoomService;
import baedalmate.baedalmate.errors.exceptions.*;
import baedalmate.baedalmate.order.dao.OrderJpaRepository;
import baedalmate.baedalmate.order.domain.Menu;
import baedalmate.baedalmate.order.domain.Order;
import baedalmate.baedalmate.recruit.dao.RecruitRepository;
import baedalmate.baedalmate.recruit.dao.ShippingFeeJpaRepository;
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

    public List<RecruitDto> findAllByTag(String keyword, Pageable pageable) {
        List<Recruit> recruits = recruitRepository.findAllByTagUsingJoin(keyword, pageable);
        return recruits.stream()
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
        List<ParticipantDto> participants = orderJpaRepository.findAllByRecruitIdUsingJoin(recruitId)
                .stream().map(o -> {
                    if (o.getUser().getId() == userId) {
                        participate.set(true);
                    }
                    return new ParticipantDto(o.getUser().getId(), o.getUser().getNickname(), o.getUser().getProfileImage());
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
        Recruit recruit = recruitRepository.findByIdUsingJoin(recruitId);

        boolean host = recruit.getUser().getId() == user.getId() ? true : false;

        if (!host) {
            throw new InvalidApiRequestException("Not host");
        }

        if (recruit.getCurrentPeople() > 1) {
            throw new InvalidApiRequestException("Someone is participating");
        }

        if (updateRecruitDto.getCategoryId() != null) {
            Category category = categoryJpaRepository.findById(updateRecruitDto.getCategoryId()).get();
            recruit.setCategory(category);
        }
        if (updateRecruitDto.getCategoryId() != null) {
            PlaceDto placeDto = updateRecruitDto.getPlace();
            Place place = Place.createPlace(placeDto.getName(), placeDto.getAddressName(), placeDto.getRoadAddressName(), placeDto.getX(), placeDto.getY());
            recruit.setPlace(place);
        }
        if (updateRecruitDto.getDormitory() != null) {
            recruit.setDormitory(updateRecruitDto.getDormitory());
        }
        if (updateRecruitDto.getCriteria() != null) {
            recruit.setCriteria(updateRecruitDto.getCriteria());
        }
        if (updateRecruitDto.getMinPrice() != null) {
            recruit.setMinPrice(updateRecruitDto.getMinPrice());
        }
        if (updateRecruitDto.getMinPeople() != null) {
            recruit.setMinPeople(updateRecruitDto.getMinPeople());
        }
        if (updateRecruitDto.getFreeShipping() != null) {
            recruit.setFreeShipping(updateRecruitDto.getFreeShipping());
            if (recruit.isFreeShipping()) {
                shippingFeeJpaRepository.deleteByRecruitId(recruitId);
            }
        }
        if (updateRecruitDto.getShippingFee() != null) {
            shippingFeeJpaRepository.deleteByRecruitId(recruitId);
            if (!recruit.isFreeShipping()) {
                for (ShippingFeeDto sf : updateRecruitDto.getShippingFee()) {
                    ShippingFee shippingFee = ShippingFee.createShippingFee(sf.getShippingFee(), sf.getLowerPrice(), sf.getUpperPrice());
                    shippingFee.setRecruit(recruit);
                    shippingFeeJpaRepository.save(shippingFee);
                }
            }
        }
        if (updateRecruitDto.getPlatform() != null) {
            recruit.setPlatform(updateRecruitDto.getPlatform());
        }
        if (updateRecruitDto.getDeadlineDate() != null) {
            recruit.setDeadlineDate(updateRecruitDto.getDeadlineDate());
        }
        if (updateRecruitDto.getTitle() != null) {
            recruit.setTitle(updateRecruitDto.getTitle());
        }
        if (updateRecruitDto.getDescription() != null) {
            recruit.setDescription(updateRecruitDto.getDescription());
        }
        if (updateRecruitDto.getTags() != null) {
            tagJpaRepository.deleteByRecruitId(recruitId);
            if (updateRecruitDto.getTags().size() > 4) {
                throw new InvalidParameterException("Number of tag must be less than 5");
            }
            if (updateRecruitDto.getTags().size() > 0) {
                for (TagDto tagDto : updateRecruitDto.getTags()) {
                    Tag tag = Tag.createTag(tagDto.getTagname());
                    tag.setRecruit(recruit);
                    tagJpaRepository.save(tag);
                }
            }
        }

        recruitJpaRepository.save(recruit);
    }

    @Transactional
    public void closeBySchedule() {
        recruitJpaRepository.setCancelTrueFromRecruitExceedTime(LocalDateTime.now());
        recruitJpaRepository.setActiveFalseFromRecruitExceedTime(LocalDateTime.now());
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
        if(!recruit.isActive()) {
            throw new InvalidApiRequestException("Already closed recruit");
        }
        recruitJpaRepository.setActiveFalse(recruitId);
        recruitJpaRepository.setCancelTrue(recruitId);
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
        if(recruit.isCancel()){
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
                hostUser.getNickname(),
                hostUser.getScore(),
                hostUser.getProfileImage(),
                hostUser.getDormitoryName(),
                recruit.isActive(),
                recruit.isCancel(),
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
        if (sort.contains("score")) {
            recruitList = recruitRepository.findAllUsingJoinOrderByScore(p);
        } else if (sort.contains("deadlineDate")) {
            recruitList = recruitRepository.findAllUsingJoinOrderByDeadlineDate(p);
        } else if (sort.contains("view")) {
            recruitList = recruitRepository.findAllUsingJoinOrderByView(p);
        } else {
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
        if (sort.contains("score")) {
            recruitList = recruitRepository.findAllUsingJoinOrderByScore(p);
        } else if (sort.contains("deadlineDate")) {
            recruitList = recruitRepository.findAllUsingJoinOrderByDeadlineDate(p);
        } else if (sort.contains("view")) {
            recruitList = recruitRepository.findAllUsingJoinOrderByView(p);
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
                r.getImage()
        )).collect(Collectors.toList());
    }

    public List<MainPageRecruitDtoWithTag> findAllWithTag(Dormitory dormitory, Pageable pageable) {
        return recruitRepository.findAllWithTagsUsingJoinOrderByDeadlineDate(dormitory, pageable)
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
                                    r.getImage());
                        }
                ).collect(Collectors.toList());
    }

    public List<RecruitDto> findAllByCategory(Long categoryId, Pageable pageable) {
        String sort = pageable.getSort().toString();
        List<Recruit> recruitList;
        if (sort.contains("score")) {
            recruitList = recruitRepository.findAllByCategoryUsingJoinOrderByScore(categoryId, pageable);
        } else if (sort.contains("deadlineDate")) {
            recruitList = recruitRepository.findAllByCategoryUsingJoinOrderByDeadlineDate(categoryId, pageable);
        } else if (sort.contains("view")) {
            recruitList = recruitRepository.findAllByCategoryUsingJoinOrderByView(categoryId, pageable);
        } else {
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
