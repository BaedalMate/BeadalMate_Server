package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.Menu;
import baedalmate.baedalmate.domain.Order;
import baedalmate.baedalmate.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    public Long createMenu(Order order, Menu menu) {
        order.addMenu(menu);
        menuRepository.save(menu);
        return menu.getId();
    }
}
