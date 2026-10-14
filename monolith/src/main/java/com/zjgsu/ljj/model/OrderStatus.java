package com.zjgsu.ljj.model;

/**
 * 订单状态：本周只实现下单（待付款）环节，完整状态流转留待后续课程。
 */
public enum OrderStatus {
    /** 待付款 */
    CREATED,
    /** 已支付 */
    PAID,
    /** 已取消 */
    CANCELLED
}