package com.order.order.impl;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class OrderService {

	public static final String APPID = "order-service";

	@Resource
	private JdbcTemplate jdbcTemplate;
	@Resource
	private IdempotentHelper idempotentHelper;

	@Transactional
	public String buySomething(int userId, long money, String trxId) throws IOException {

		String AppTrxId = APPID + trxId;

		if (idempotentHelper.getIdempotentPo(AppTrxId) == null) {
			// 非重复请求
			IdempotentHelper.IdempotentPo idempotentPo = new IdempotentHelper.IdempotentPo();
			idempotentPo.setTrxId(AppTrxId);
			idempotentPo.setCreateTime(new Date());
			idempotentPo.setUpdateTime(new Date());
			idempotentHelper.saveIdempotentPo(idempotentPo);
			int id = saveOrderRecord(userId, money);
			payMoney(userId, money, trxId);
		} else {
			// 重复请求
			payMoney(userId, money, trxId);
		}

		return null;
	}


	private Integer saveOrderRecord(final int userId, final long money) {

		final String INSERT_SQL = "INSERT INTO `order` (`order_id`, `user_id`, `money`, `create_time`) VALUES (NULL, ?, ?, ?);";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
		    new PreparedStatementCreator() {
		    	@Override
		        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		            PreparedStatement ps =
		                connection.prepareStatement(INSERT_SQL, new String[] {"id"});
		            ps.setInt(1, userId);
		            ps.setLong(2, money);
		            ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));
		            return ps;
		        }
		    },
		    keyHolder);

		return keyHolder.getKey().intValue();
	}

	public String payMoney(int userId, long money, String trxId) throws IOException {

		HttpClient client = new HttpClient();
		String posturi = "http://localhost:8082/wallet/payMoney?userId=" + String.valueOf(userId)
				+ "&money=" + String.valueOf(money) + "&trxId=" + trxId;
		System.out.println(posturi);
		HttpMethod method = new PostMethod(posturi);
		//((PostMethod) method).addParameter("userId", String.valueOf(userId));
		//((PostMethod) method).addParameter("money", String.valueOf(money));

		client.executeMethod(method);

		BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
		StringBuffer stringBuffer = new StringBuffer();
		String str = "";
		while((str = reader.readLine())!=null){
			stringBuffer.append(str);
		}
		String ts = stringBuffer.toString();

		// 打印服务器返回的状态
		System.out.println(method.getStatusLine());
		// 打印返回的信息
		System.out.println(method.getResponseBodyAsString());
		// 释放连接
		method.releaseConnection();

		return ts;
	}

}
