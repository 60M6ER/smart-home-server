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
    private double firstPrice;
    private double firstAmount;
    private PairCurrency secondPair;
    private TypeOrder secondTypeOrder;
    private double secondPrice;
    private double secondAmount;
    private PairCurrency thirdPair;
    private TypeOrder thirdTypeOrder;
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
        if (typeSpread == TypeSpread.TRIANGLE) {
            if (firstPair == null) {
                firstPair = chainPair.getBasePair();
                firstTypeOrder = chainPair.getTypeOrder();
                OrderBookRow bestPriceByTypeOrder = orderBook.getBestPriceByTypeOrder(firstTypeOrder, 0);
                firstPrice = bestPriceByTypeOrder.getPrice();
                firstAmount = bestPriceByTypeOrder.getAmount();
                USDT_amount_start = firstAmount * firstPrice;
            } else if (secondPair == null) {
                secondPair = chainPair.getBasePair();
                secondTypeOrder = chainPair.getTypeOrder();
                OrderBookRow bestPriceByTypeOrder = orderBook.getBestPriceByTypeOrder(secondTypeOrder, 0);
                secondPrice = bestPriceByTypeOrder.getPrice();
                secondAmount = bestPriceByTypeOrder.getAmount();
                USDT_amount_start = Math.min(USDT_amount_start, getSecondAmountUSDT());
            } else {
                thirdPair = chainPair.getBasePair();
                thirdTypeOrder = chainPair.getTypeOrder();
                OrderBookRow bestPriceByTypeOrder = orderBook.getBestPriceByTypeOrder(thirdTypeOrder, 0);
                thirdPrice = bestPriceByTypeOrder.getPrice();
                thirdAmount = bestPriceByTypeOrder.getAmount();
                USDT_amount_start = Math.min(USDT_amount_start, thirdAmount * thirdPrice);
            }
        } else {
            if (firstPair == null) {
                firstPair = chainPair.getBasePair();
                firstTypeOrder = chainPair.getTypeOrder();
                OrderBookRow bestPriceByTypeOrder = orderBook.getBestPriceByTypeOrder(firstTypeOrder, 0);
                firstPrice = bestPriceByTypeOrder.getPrice();
                firstAmount = bestPriceByTypeOrder.getAmount();
                USDT_amount_start = firstAmount * firstPrice;
            } else {
                secondPair = chainPair.getBasePair();
                secondTypeOrder = chainPair.getTypeOrder();
                OrderBookRow bestPriceByTypeOrder = orderBook.getBestPriceByTypeOrder(secondTypeOrder, 0);
                secondPrice = bestPriceByTypeOrder.getPrice();
                secondAmount = bestPriceByTypeOrder.getAmount();
                USDT_amount_start = Math.min(USDT_amount_start, secondAmount * secondPrice);
            }
        }
        if (USDT_amount_start >= 1 && typeSpread == TypeSpread.TRIANGLE && thirdPair != null && feeInformation != null) {
            USDT_amount_end = calculateOperationAmount(USDT_amount_start, firstPrice, firstTypeOrder);
            USDT_amount_end = calculateOperationAmount(USDT_amount_end, secondPrice, secondTypeOrder);
            USDT_amount_end = calculateOperationAmount(USDT_amount_end, thirdPrice, thirdTypeOrder);
        } else if (typeSpread == TypeSpread.BETWEEN_EXCHANGES && secondPair != null && feeInformation != null) {
            USDT_amount_end = calculateOperationAmount(USDT_amount_start, firstPrice, firstTypeOrder);
            USDT_amount_end = calculateOperationAmount(USDT_amount_end, secondPrice, secondTypeOrder);
        }
        if (USDT_amount_start > 0 && USDT_amount_end > 0) {
            profit = USDT_amount_end - USDT_amount_start;
            profitPercent = (USDT_amount_end - USDT_amount_start) * 100 / USDT_amount_start;
            maxProfit = profit;
            maxProfitPercent = profitPercent;
        }
    }

    public void updateWithAnother(Spread spread) {
        firstPrice = spread.getFirstPrice();
        firstAmount = spread.getFirstAmount();
        secondPrice = spread.getSecondPrice();
        secondAmount = spread.getSecondAmount();
        if (typeSpread == TypeSpread.TRIANGLE) {
            thirdPrice = spread.getThirdPrice();
            thirdAmount = spread.getThirdAmount();
        }
        USDT_amount_start = spread.getUSDT_amount_start();
        USDT_amount_end = spread.getUSDT_amount_end();
        if (USDT_amount_start > 0 && USDT_amount_end > 0) {
            profit = USDT_amount_end - USDT_amount_start;
            profitPercent = (USDT_amount_end - USDT_amount_start) * 100 / USDT_amount_start;
            if (profitPercent > maxProfitPercent) {
                maxProfit = profit;
                maxProfitPercent = profitPercent;
            }
        }
        if (profit <= 0) {
            dateFinish = spread.getDateCreate();
            state = SpreadState.FINISHED;
        }
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
