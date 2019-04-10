package com.xyz.browser.app.modular.kafka;

import com.alibaba.fastjson.JSONObject;
import com.xyz.browser.app.core.util.SpringUtil;
import com.xyz.browser.app.modular.system.model.Contract;
import com.xyz.browser.app.modular.system.service.IContractService;
import com.xyz.browser.app.modular.system.service.impl.IContractServiceImpl;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

public class ContractMsgListener implements MessageListener<String, String> {

    private IContractService IContractService = (IContractService) SpringUtil.getBean(IContractServiceImpl.class);

    @Override
    public void onMessage(ConsumerRecord<String, String> data) {

        try {
            JSONObject json = JSONObject.parseObject(data.value());
            Contract contract = new Contract();
            contract.setTotal(json.getString("total"));
            contract.setDecimal(json.get("decimal").toString());
            contract.setName(json.getString("name"));
            contract.setSymbol(json.getString("symbol"));
            contract.setAsset(json.getString("asset"));
            contract.setHash(json.getString("hash"));
            contract.setBlockNumber(json.getString("blockNumber"));
            contract.setContract(json.getString("contract"));
            contract.setTokenStandard(json.getString("tokenStandard"));
            contract.setTokenAction(json.getString("tokenAction"));
            contract.setTfrom(json.getString("from"));
            contract.setTto(json.getString("to"));
            IContractService.insertRtData(contract);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
