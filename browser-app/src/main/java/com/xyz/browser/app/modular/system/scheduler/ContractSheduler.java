package com.xyz.browser.app.modular.system.scheduler;

import com.xyz.browser.app.core.common.annotion.TimeStat;
import com.xyz.browser.app.modular.hbase.model.Bancor;
import com.xyz.browser.app.modular.hbase.service.BancorService;
import com.xyz.browser.app.modular.hbase.service.ContractService;
import com.xyz.browser.app.modular.system.model.Contract;
import com.xyz.browser.app.modular.system.service.IContractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "guns.scheduler-switch", name = "contract", havingValue = "true")
@Slf4j
public class ContractSheduler {

    @Autowired
    private ContractSheduler self;

    @Autowired
    private IContractService iContractService;

    @Autowired
    private BancorService bancorService;

    @Autowired
    private ContractService contractService;

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void work() {
        self.start();
    }

    @TimeStat
    public void start(){

        try {
//            List<Bancor> bancorList = bancorService.selectBanCor("smartToken");

//            for (Bancor bancor : bancorList) {

            List<Contract> ContractList = iContractService.contractList();

            for (Contract contractInfo : ContractList) {

                String total = iContractService.getTotal(contractInfo.getContract());

                Contract contract = new Contract();
                contract.setTokenAction("create");
                contract.setContract(contractInfo.getContract());
                contract.setTotal(total);
                iContractService.updateContract(contract);


                Map<String, String> map = new HashedMap();
                map.put("tokenAction", "create");
                map.put("contract", contractInfo.getContract());
                map.put("total",total);
                map.put("hash",contractInfo.getHash());
                contractService.updateContractTotal(map);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
