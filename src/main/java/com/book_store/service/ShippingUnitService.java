package com.book_store.service;

import com.book_store.entity.ShippingUnit;
import com.book_store.repository.ShippingUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingUnitService {
    @Autowired
    private ShippingUnitRepository shippingUnitRepository;

    public List<ShippingUnit> getAllShippingUnits() {
        return shippingUnitRepository.findAll();

    }
}
