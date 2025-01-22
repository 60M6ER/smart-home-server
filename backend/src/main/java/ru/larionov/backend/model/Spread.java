package ru.larionov.backend.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Data
@Slf4j
public class Spread {
    private UUID id;
    private PairCurrency firstPair;
    private TypeOrder firstTypeOrder;
    private OrderBook firstOrderBook;
    private double firstPrice;
    private double firstAmount;
    private PairCurrency secondPair;
    private TypeOrder secondTypeOrder;
    private OrderBook secondOrderBook;
    private double secondPrice;
    private double secondAmount;
    private PairCurrency thirdPair;
    private TypeOrder thirdTypeOrder;
    private OrderBook thirdOrderBook;
    private double thirdPrice;
    private double thirdAmount;
    private TypeSpread typeSpread;
    private int currentIndex;
    private FeeInformation feeInformation;
    private FeeInformation secondFeeInformation;
    private Date dateCreate;
    private Date dateFinish;
    private SpreadState state;
    private double USDT_amount_start;
    private double USDT_amount_end;
    private double profit;
    private double profitPercent;
    private double maxProfit;
    private double maxProfitPercent;

    public Spread() {
        currentIndex = 0;
        state = SpreadState.CREATED;
        dateCreate = new Date();
    }

    public Spread(TypeSpread typeSpread) {
        this.typeSpread = typeSpread;
        currentIndex = 0;
        state = SpreadState.CREATED;
        dateCreate = new Date();
    }

    public void addChain(ChainPairs chainPair, OrderBook orderBook) {
        if (firstPair == null) {
            firstPair = chainPair.getBasePair();
            firstTypeOrder = chainPair.getTypeOrder();
            firstOrderBook = orderBook;
        } else if (secondPair == null) {
            secondPair = chainPair.getBasePair();
            secondTypeOrder = chainPair.getTypeOrder();
            secondOrderBook = orderBook;
        } else {
            thirdPair = chainPair.getBasePair();
            thirdTypeOrder = chainPair.getTypeOrder();
            thirdOrderBook = orderBook;
        }
    }

    public void calculate() {
        OrderBookRow firstRow = firstOrderBook.getBestPriceByTypeOrder(firstTypeOrder, 1);
        OrderBookRow secondRow = secondOrderBook.getBestPriceByTypeOrder(secondTypeOrder, 1);
        double a = (secondRow.getPrice() - firstRow.getPrice()) / firstRow.getPrice()
                - (feeInformation.getTaker() + feeInformation.getMaker());
        if (a > 0) {
            state = SpreadState.WORKING;
            USDT_amount_start = secondRow.getAmount();
            USDT_amount_end = USDT_amount_start * (1 + a);
            profit = USDT_amount_start * a;
            profitPercent = a * 100;
            if (maxProfit < profit) {
                maxProfit = profit;
                maxProfitPercent = profitPercent;
            }
        } else {
            state = SpreadState.FINISHED;
            dateFinish = new Date();
        }
    }

    public void updateWithAnother(Spread spread) {
        firstOrderBook = spread.getFirstOrderBook();
        secondOrderBook = spread.getSecondOrderBook();
        thirdOrderBook = spread.getThirdOrderBook();
        calculate();
    }

    private double getSecondAmountUSDT() {
        if (secondTypeOrder == TypeOrder.BUY) {
            return secondPrice * secondAmount * firstPrice;
        } else {
            return secondAmount * firstPrice;
        }
    }

    private double calculateOperationAmount(double amountBaseCurrency, double price, TypeOrder typeOrder) {
        double a;
        if (typeOrder == TypeOrder.BUY){
            // Покупаем
            a = amountBaseCurrency / price;
        } else {
            // Продаем
            a = amountBaseCurrency * price;
        }
        return a - (a * feeInformation.getTaker());
    }

    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        if (typeSpread == TypeSpread.TRIANGLE) {

            sb.append(firstPair.getVendor())
                    .append(": ");
            if (firstPair != null){
                sb.append(firstPair);
            }
            if (secondPair != null) {
                sb.append(" | ").append(secondPair);
            }
            if (thirdPair != null) {
                sb.append(" | ").append(thirdPair);
            }
        } else {
            sb.append(firstPair.getVendor())
                    .append(": ");
            if (firstPair != null){
                sb.append(firstPair);
            }
            if (secondPair != null) {
                sb.append(" | ");
                sb.append(secondPair.getVendor())
                        .append(": ")
                        .append(secondPair);
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getDescription());
        sb.append("\n");
        sb.append(String.format("Будет использованно: %.3f USDT. Доход составит: %.3f USDT ( %.2f процентов).",
                USDT_amount_start,
                USDT_amount_end - USDT_amount_start,
                (USDT_amount_end - USDT_amount_start) * 100 / USDT_amount_start));

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Spread spread = (Spread) o;

        return new EqualsBuilder().append(firstPair, spread.firstPair).append(firstTypeOrder, spread.firstTypeOrder).append(secondPair, spread.secondPair).append(secondTypeOrder, spread.secondTypeOrder).append(thirdPair, spread.thirdPair).append(thirdTypeOrder, spread.thirdTypeOrder).append(typeSpread, spread.typeSpread).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(firstPair).append(firstTypeOrder).append(secondPair).append(secondTypeOrder).append(thirdPair).append(thirdTypeOrder).append(typeSpread).toHashCode();
    }
}
