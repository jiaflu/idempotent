package com.wallet.wallet.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wallet.wallet.impl.WalletPayMoneyService.WalletPayRequestVO;
import com.wallet.wallet.impl.WalletPayMoneyService.WalletPayResponseVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
@RequestMapping(value = "/wallet")
public class WalletController {

    public static final String APPID = "wallet-service";

    @Autowired
    private WalletService walletService;
    @Autowired
    private IdempotentHelper idempotentHelper;

    @RequestMapping(value = "/payMoney")
    public String payMoney(@RequestParam int userId, @RequestParam int money, @RequestParam String trxId) {
        String AppTrxId = APPID + trxId;
        WalletPayRequestVO param = new WalletPayRequestVO();
        param.setUserId(userId);
        param.setPayAmount((long) money);

        if (idempotentHelper.getIdempotentPo(AppTrxId) == null) {
            // 非重复请求
            IdempotentHelper.IdempotentPo idempotentPo = new IdempotentHelper.IdempotentPo();
            idempotentPo.setTrxId(AppTrxId);
            idempotentPo.setCreateTime(new Date());
            idempotentPo.setUpdateTime(new Date());
            idempotentHelper.saveIdempotentPo(idempotentPo);

            walletService.doTryPay(param);
            walletService.doConfirmPay(param);
        }

        return "ok";
    }


}
