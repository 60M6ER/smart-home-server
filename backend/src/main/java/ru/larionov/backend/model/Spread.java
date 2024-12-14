package ru.larionov.backend.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

@Data
@Slf4j
public class Spread {
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

    @Override
    public String toString() {
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
                sb.append("\n");
                sb.append(String.format("Будет использованно: %.3f USDT. Доход составит: %.3f USDT ( %.2f процентов).",
                        USDT_amount_start,
                        USDT_amount_end - USDT_amount_start,
                        (USDT_amount_end - USDT_amount_start) * 100 / USDT_amount_start));
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
                sb.append("\n");
                sb.append(String.format("Будет использованно: %.3f USDT. Доход составит: %.3f USDT ( %.2f процентов).",
                        USDT_amount_start,
                        USDT_amount_end - USDT_amount_start,
                        (USDT_amount_end - USDT_amount_start) * 100 / USDT_amount_start));
            }
        }

        return sb.toString();
    }
}
