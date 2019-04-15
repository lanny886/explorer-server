package com.xyz.browser.app.modular.hbase.service;

import com.xyz.browser.app.modular.hbase.model.AddressTransaction;

public interface AddressTransactionService {

    AddressTransaction selectByAddress(String address);

}
