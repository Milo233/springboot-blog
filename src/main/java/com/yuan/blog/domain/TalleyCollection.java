package com.yuan.blog.domain;

import java.util.Date;

public class TalleyCollection {

    // 手上有的钱 未扣除负债
    private double moneyInHand;
    // 欠的钱
    private double debt;
    // 实际有的钱 = moneyInHand - debt
    private double actualMoney;
    // 记账日期
    private Date date;

    public double getMoneyInHand() {
        return moneyInHand;
    }

    public void setMoneyInHand(double moneyInHand) {
        this.moneyInHand = moneyInHand;
    }

    public double getDebt() {
        return debt;
    }

    public void setDebt(double debt) {
        this.debt = debt;
    }

    public double getActualMoney() {
        return actualMoney;
    }

    public void setActualMoney(double actualMoney) {
        this.actualMoney = actualMoney;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
