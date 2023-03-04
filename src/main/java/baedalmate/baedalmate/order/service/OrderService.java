package baedalmate.baedalmate.order.service;

import baedalmate.baedalmate.category.dto.MessageDto;
import baedalmate.baedalmate.chat.dao.ChatRoomJpaRepository;
import baedalmate.baedalmate.chat.dao.MessageJpaRepository;
import baedalmate.baedalmate.chat.domain.ChatRoom;
import baedalmate.baedalmate.chat.domain.Message;
import baedalmate.baedalmate.chat.domain.MessageType;
import baedalmate.baedalmate.errors.exceptions.ExistOrderException;
import baedalmate.baedalmate.errors.exceptions.InvalidApiRequestException;
import baedalmate.baedalmate.fcm.event.CloseEvent;
import baedalmate.baedalmate.fcm.event.MenuEvent;
import baedalmate.baedalmate.fcm.event.ParticipateEvent;
import baedalmate.baedalmate.notification.dao.NotificationJpaRepository;
import baedalmate.baedalmate.notification.domain.Notification;
import baedalmate.baedalmate.order.dto.OrderDto;
import baedalmate.baedalmate.order.dao.OrderJpaRepository;
import baedalmate.baedalmate.order.dto.MenuDto;
import baedalmate.baedalmate.order.dto.OrderAndChatRoomIdDto;
import baedalmate.baedalmate.recruit.dao.MenuJpaRepository;
import baedalmate.baedalmate.recruit.dao.RecruitJpaRepository;
import baedalmate.baedalmate.recruit.domain.Criteria;
import baedalmate.baedalmate.order.domain.Menu;
import baedalmate.baedalmate.order.domain.Order;
import baedalmate.baedalmate.recruit.domain.Recruit;
import baedalmate.baedalmate.user.dao.FcmJpaRepository;
import baedalmate.baedalmate.user.dao.UserJpaRepository;
import baedalmate.baedalmate.user.domain.Fcm;
import baedalmate.baedalmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final UserJpaRepository userJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final RecruitJpaRepository recruitJpaRepository;
    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final MessageJpaRepository messageJpaRepository;
    private final MenuJpaRepository menuJpaRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final FcmJpaRepository fcmJpaRepository;
    private final NotificationJpaRepository notificationJpaRepository;
    private final SimpMessageSendingOperations sendingOperations;

    public List<Order> findByRecruitId(Long recruitId) {
        return orderJpaRepository.findAllByRecruitIdUsingJoin(recruitId);
    }

    @Transactional
    public void updateOrder(Long userId, OrderDto orderDto) {
        Order order = orderJpaRepository.findByUserIdAndRecruitIdUsingJoin(userId, orderDto.getRecruitId());
        if (order == null) {
            throw new InvalidApiRequestException("User is not participant");
        }
        Recruit recruit = order.getRecruit();

        // current price 갱신
        int previousPrice = 0;
        List<Menu> menus = order.getMenus();
        for (Menu menu : menus) {
            previousPrice += menu.getPrice();
        }
        int currentPrice = 0;
        for (MenuDto menuDto : orderDto.getMenu()) {
            currentPrice += menuDto.getPrice();
        }

        menuJpaRepository.deleteByOrderId(order.getId());

        for (MenuDto m : orderDto.getMenu()) {
            Menu menu = Menu.createMenu(m.getName(), m.getPrice(), m.getQuantity());
            menu.setOrder(order);
            menuJpaRepository.save(menu);
        }
        recruitJpaRepository.updateCurrentPrice(recruit.getCurrentPrice() - previousPrice + currentPrice, recruit.getId());
        // 마감 기준 체크
        if (recruit.getCriteria() == Criteria.PRICE && recruit.getCurrentPrice() >= recruit.getMinPrice()) {
            recruitJpaRepository.setActiveFalse(recruit.getId(), LocalDateTime.now());
        }
        List<Long> userIdList = new ArrayList<>();
        if (recruit.getUser().getId() != userId) {
            userIdList.add(recruit.getUser().getId());
        }
        List<Fcm> fcmList = fcmJpaRepository.findAllByUserIdListAndAllowRecruitTrue(userIdList);
        List<Notification> notifications = fcmList.stream().map(f -> f.getUser()).distinct()
                .map(u -> Notification.createNotification(
                        recruit.getTitle(),
                        "참가자가 메뉴를 변경했습니다.",
                        recruit.getImage(),
                        recruit.getChatRoom().getId(),
                        u))
                .collect(Collectors.toList());
        notificationJpaRepository.saveAll(notifications);
        eventPublisher.publishEvent(new MenuEvent(
                recruit.getChatRoom().getId(),
                recruit.getTitle(),
                "참가자가 메뉴를 변경했습니다.",
                recruit.getImage(),
                fcmList));
    }

    @Transactional
    public void deleteOrder(Long userId, Long recruitId) {
        Order order = orderJpaRepository.findByUserIdAndRecruitIdUsingJoin(userId, recruitId);

        Recruit recruit = order.getRecruit();
        // 현재 인원 감소
        int price = 0;
        List<Menu> menus = order.getMenus();
        for (Menu menu : menus) {
            price += menu.getPrice();
        }
        // 현재 금액 감소
        recruit.setCurrentPeople(recruit.getCurrentPeople() - 1);
        recruit.setCurrentPrice(recruit.getCurrentPrice() - price);
        recruitJpaRepository.save(recruit);
        // order 삭제
        orderJpaRepository.delete(order);
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(recruit.getUser().getId());
        List<Fcm> fcmList = fcmJpaRepository.findAllByUserIdListAndAllowRecruitTrue(userIdList);
        List<Notification> notifications = fcmList.stream().map(f -> f.getUser()).distinct()
                .map(u -> Notification.createNotification(
                        recruit.getTitle(),
                        "참가자가 모집을 나갔습니다.",
                        recruit.getImage(),
                        recruit.getChatRoom().getId(),
                        u))
                .collect(Collectors.toList());
        notificationJpaRepository.saveAll(notifications);
        eventPublisher.publishEvent(new ParticipateEvent(
                recruit.getChatRoom().getId(),
                recruit.getTitle(),
                "참가자가 모집을 나갔습니다.",
                recruit.getImage(),
                fcmList));
    }

    @Transactional
    public OrderAndChatRoomIdDto createOrder(Long userId, OrderDto orderDto) {

        // User 조회
        User user = userJpaRepository.findById(userId).get();

        // Recruit 조회
        Recruit recruit = recruitJpaRepository.findById(orderDto.getRecruitId()).get();

        // 취소 또는 비활성 검사
        if (recruit.isCancel()) {
            throw new InvalidApiRequestException("Already canceled recruit");
        }
        if (!recruit.isActive()) {
            throw new InvalidApiRequestException("Already closed recruit");
        }
        // 중복 검사
        List<Order> orders = recruit.getOrders();
        for (Order order : orders) {
            if (order.getUser().getId() == userId) {
                throw new ExistOrderException();
            }
        }

        List<User> users = orders.stream().map(o -> o.getUser()).collect(Collectors.toList());

        // order 생성
        List<Menu> menus = orderDto.getMenu().stream()
                .map(m -> Menu.createMenu(m.getName(), m.getPrice(), m.getQuantity()))
                .collect(Collectors.toList());
        Order order = Order.createOrder(user, menus);

        recruit.addOrder(order);
        orderJpaRepository.save(order);

        // current price 갱신
        int price = 0;
        for (MenuDto menuDto : orderDto.getMenu()) {
            price += menuDto.getPrice() * menuDto.getQuantity();
        }
        recruit.setCurrentPrice(recruit.getCurrentPrice() + price);

        // current people 갱신
        recruit.setCurrentPeople(recruit.getCurrentPeople() + 1);

        users.add(user);
        // 마감 기준 체크
        if (recruit.getCriteria() == Criteria.NUMBER && recruit.getCurrentPeople() == recruit.getMinPeople()) {
            recruit.setActive(false);
            recruit.setDeactivateDate(LocalDateTime.now());
            List<Long> userIdList = users.stream().map(u -> u.getId()).collect(Collectors.toList());
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
        } else if (recruit.getCriteria() == Criteria.PRICE && recruit.getCurrentPrice() >= recruit.getMinPrice()) {
            recruit.setActive(false);
            recruit.setDeactivateDate(LocalDateTime.now());
            List<Long> userIdList = users.stream().map(u -> u.getId()).collect(Collectors.toList());
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
        recruitJpaRepository.save(recruit);

        // 입장 메세지 생성
        ChatRoom chatRoom = chatRoomJpaRepository.findByRecruitId(orderDto.getRecruitId());

        Message message = Message.createMessage(MessageType.ENTER, "", user, chatRoom, null);
        messageJpaRepository.save(message);
        MessageDto messageDto = new MessageDto(
                chatRoom.getId(),
                userId,
                null,
                null,
                message.getId(),
                "",
                MessageType.ENTER,
                null,
                null);
        sendingOperations.convertAndSend("/topic/chat/room/" + messageDto.getRoomId(), messageDto);
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(recruit.getUser().getId());
        List<Fcm> fcmList = fcmJpaRepository.findAllByUserIdListAndAllowRecruitTrue(userIdList);
        List<Notification> notifications = fcmList.stream().map(f -> f.getUser()).distinct()
                .map(u -> Notification.createNotification(
                        recruit.getTitle(),
                        "모집에 참여하였습니다.",
                        recruit.getImage(),
                        recruit.getChatRoom().getId(),
                        u))
                .collect(Collectors.toList());
        notificationJpaRepository.saveAll(notifications);
        eventPublisher.publishEvent(new ParticipateEvent(
                recruit.getChatRoom().getId(),
                recruit.getTitle(),
                "모집에 참여하였습니다.",
                recruit.getImage(),
                fcmList));
        return new OrderAndChatRoomIdDto(order.getId(), chatRoom.getId());
    }
}
