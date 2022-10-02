package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.*;
import baedalmate.baedalmate.domain.embed.Place;
import baedalmate.baedalmate.errors.exceptions.ExistOrderException;
import baedalmate.baedalmate.repository.CategoryRepository;
import baedalmate.baedalmate.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;


@RunWith(SpringRunner.class)
@SpringBootTest
class 모집글참여테스트 {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecruitService recruitService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @Transactional
    @Rollback(true)
    void createOrder() {
        // given
        User user1 = User.builder()
                .nickname("테스트1")
                .role("ROLE_USER")
                .dormitory(Dormitory.SULIM)
                .recruits(new ArrayList<>())
                .orders(new ArrayList<>())
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .nickname("테스트2")
                .role("ROLE_USER")
                .dormitory(Dormitory.SULIM)
                .recruits(new ArrayList<>())
                .orders(new ArrayList<>())
                .build();
        userRepository.save(user2);

        Category category = categoryRepository.findOne((long) 2);

        // when
        Recruit recruit = Recruit.createRecruit(user1, category, 3, 10000, LocalDateTime.now(), Criteria.NUMBER, Dormitory.SULIM,
                Place.createPlace("테스트", "테스트", "테스트", 1, 1), Platform.BAEMIN, 0, "테스트", "테스트",
                true, new ArrayList<ShippingFee>(), new ArrayList<Tag>());

        Long recruitId = recruitService.createRecruit(recruit);

        Menu menu = Menu.createMenu("테스트 메뉴", 1000, 1);

        Order order1 = Order.createOrder(user1);
        Long order1Id = orderService.createOrder(recruit, order1);

        Order order2 = Order.createOrder(user2);
        Long order2Id = orderService.createOrder(recruit, order2);
        try {
            Order order3 = Order.createOrder(user2);
            Long order3Id = orderService.createOrder(recruit, order3);
        } catch (ExistOrderException e) {
            System.out.println(e.getMessage());
        }

        // then
        Assertions.assertThat(recruit.getCurrentPeople()).isEqualTo(2);
    }
}