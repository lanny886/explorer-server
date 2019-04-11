package com.xyz.browser.app.modular.hbase.service.impl;

import com.xyz.browser.app.modular.hbase.dao.AddressTransactionMapper;
import com.xyz.browser.app.modular.hbase.model.AddressTransaction;
import com.xyz.browser.app.modular.hbase.service.AddressTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressTransactionServiceImpl implements AddressTransactionService {

    @Autowired
    private AddressTransactionMapper addressTransactionMapper;

    @Override
    public AddressTransaction selectByAddress(String address) {
        return addressTransactionMapper.selectByAddress(address);
    }
}
