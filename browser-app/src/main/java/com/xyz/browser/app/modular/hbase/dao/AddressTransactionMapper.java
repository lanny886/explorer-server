package com.xyz.browser.app.modular.hbase.dao;

import com.xyz.browser.app.modular.hbase.model.AddressTransaction;

public interface AddressTransactionMapper {

    AddressTransaction selectByAddress(String address);

}
