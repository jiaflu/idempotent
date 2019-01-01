package com.order.order.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class IdempotentHelper {

    @Resource
    private JdbcTemplate jdbcTemplate;

    private String updateSql = "UPDATE `idempotent` SET `called_methods` = ?, `md5` = ?, `sync_method_result` = ?, `create_time` = ?, `update_time`  = ?, `lock_version` = `lock_version` + 1 WHERE `src_app_id` = ? AND `src_bus_code` = ? AND `src_trx_id` = ? AND `app_id` = ? AND `bus_code` = ?  AND `call_seq` = ? AND `handler` = ? AND `lock_version` = ?;";
    private String insertSql = "INSERT INTO `idempotent` (`trx_id`, `create_time`, `update_time`) VALUES (?, ?, ?);";
    private String selectSql = "select * from idempotent where trx_id = ?;";

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    /**
     * get execute result from database
     */
    public IdempotentPo getIdempotentPo(String trxId){

        JdbcTemplate jdbcTemplate = getJdbcTemplate();

        RowMapper<IdempotentPo> rowMapper = new BeanPropertyRowMapper<>(IdempotentPo.class);
        List<IdempotentPo> listQuery = jdbcTemplate.query(
                selectSql,
                rowMapper,
                new Object[]{
                        trxId
                }
        );

        if(listQuery.size() == 1){
            return listQuery.get(0);
        }else if (listQuery.size() == 0){
            return null;
        }else{
            throw new RuntimeException("Unkonw Error!" + listQuery);
        }
    }

    public void saveIdempotentPo(IdempotentPo idempotentPo) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        int update = jdbcTemplate.update(
                insertSql,
                idempotentPo.getTrxId(),
                idempotentPo.getCreateTime(),
                idempotentPo.getUpdateTime()
        );

        if(update != 1){
            throw new RuntimeException("update count exception!" + update);
        }
    }

    public void updateIdempotentPo(IdempotentPo idempotentPo) {

        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        int update = jdbcTemplate.update(
                updateSql,

                idempotentPo.getCreateTime(),
                idempotentPo.getUpdateTime(),
                idempotentPo.getTrxId()
        );

        if (update != 1) {
            throw new RuntimeException("Optimistic Lock Error Occour Or can not find the specific Record!" + idempotentPo);
        }
    }

    public static class IdempotentPo {
        private String trxId;
        private Date createTime;
        private Date updateTime;

        public String getTrxId() {
            return trxId;
        }

        public void setTrxId(String trxId) {
            this.trxId = trxId;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }

        @Override
        public String toString() {
            return "IdempotentPo{" +
                    "trxId='" + trxId + '\'' +
                    ", createTime=" + createTime +
                    ", updateTime=" + updateTime +
                    '}';
        }
    }
}
