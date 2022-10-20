package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.ShippingFee;
import baedalmate.baedalmate.repository.ShippingFeeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShippingFeeService {
    private final ShippingFeeJpaRepository shippingFeeJpaRepository;

    public List<ShippingFee> findByRecruitId(Long recruitId) {
        return shippingFeeJpaRepository.findAllByRecruitIdUsingJoin(recruitId);
    }
}
