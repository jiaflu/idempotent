package com.wallet.wallet.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.wallet.wallet.impl.WalletPayMoneyService.WalletPayRequestVO;
import com.wallet.wallet.impl.WalletPayMoneyService.WalletPayResponseVO;

import javax.annotation.Resource;

@Component
public class WalletService {

	@Resource
	private JdbcTemplate jdbcTemplate;

	public WalletPayResponseVO doTryPay(WalletPayRequestVO param) {
		int update = jdbcTemplate.update("update `wallet` set freeze_amount = freeze_amount + ? where user_id = ? and (total_amount - freeze_amount) >= ?;",
				param.getPayAmount(),param.getUserId(),param.getPayAmount());

		if(update != 1){
			throw new RuntimeException("can not find specific user id or have not enought money");
		}

		WalletPayResponseVO walletPayTccMethodResult = new WalletPayResponseVO();
		walletPayTccMethodResult.setFreezeAmount(param.getPayAmount());
		return walletPayTccMethodResult;
	}

	public void doConfirmPay(WalletPayRequestVO param) {
		int update = jdbcTemplate.update("update `wallet` set freeze_amount = freeze_amount - ?, total_amount = total_amount - ? where user_id = ?;",
				param.getPayAmount(),param.getPayAmount(),param.getUserId());

		if(update != 1){
			throw new RuntimeException("unknow Exception!");
		}
	}

	public void doCancelPay(WalletPayRequestVO param) {
		int update = jdbcTemplate.update("update `wallet` set freeze_amount = freeze_amount - ? where user_id = ?;",
				param.getPayAmount(),param.getUserId());
		if(update != 1){
			throw new RuntimeException("unknow Exception!");
		}
	}



}
