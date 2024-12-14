package ru.larionov.backend.model;


import jdk.jfr.Frequency;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.processing.Generated;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Data
public class ChainPairs {

    private PairCurrency basePair;

    private String baseCurrency;
    private boolean between;


    private TypeOrder typeOrder;

    private ChainPairs parentChain;

    private int level;

    private int countLevels;


    private List<ChainPairs> children;

    public ChainPairs(PairCurrency pairCurrency, String baseCurrency) {
        this.basePair = pairCurrency;
        this.baseCurrency = baseCurrency;
        this.children = new LinkedList<>();
        this.level = 0;
        this.countLevels = 1;
    }

    public ChainPairs(PairCurrency pairCurrency, String baseCurrency, ChainPairs parentChain) {
        this.basePair = pairCurrency;
        this.baseCurrency = baseCurrency;
        this.parentChain = parentChain;
        this.children = new LinkedList<>();
        this.level = parentChain.level + 1;
        this.countLevels = 1;
        parentChain.upCountLevels(this.level);
    }

    private void upCountLevels(int level) {
        if (parentChain != null) {
            parentChain.upCountLevels(level);
        }
        this.countLevels = level + 1;
    }

    public void scanChildren (List<PairCurrency> pairs) {
        children = new ArrayList<>();
        if (between) {
            Optional<PairCurrency> first = pairs.stream()
                    .filter(pair -> pair.getVendor() != basePair.getVendor()
                            && basePair.getBaseCurrency().equals(pair.getBaseCurrency())
                            && basePair.getQuoteCurrency().equals(pair.getQuoteCurrency())
                    )
                    .findFirst();
            if (first.isPresent()) {
                String bCurrency = first.get().getBaseCurrency().equals(getSecondCurrency()) ?
                        first.get().getBaseCurrency() :
                        first.get().getQuoteCurrency();
                ChainPairs newChain = new ChainPairs(first.get(), bCurrency, this);
                newChain.typeOrder = newChain.getBaseCurrency().equals(newChain.getBasePair().getQuoteCurrency()) ?
                        TypeOrder.BUY :
                        TypeOrder.SELL;
                children.add(newChain);
            }
        } else {
            pairs.forEach(pair -> {
                if (level < 1) {
                    if (!pair.getQuoteCurrency().equals(baseCurrency)
                            && !pair.getBaseCurrency().equals(baseCurrency)
                            && !basePair.equals(pair)
                            && basePair.getVendor() == pair.getVendor()
                            && (pair.getBaseCurrency().equals(getSecondCurrency())
                            || pair.getQuoteCurrency().equals(getSecondCurrency()))) {
                        String bCurrency = pair.getBaseCurrency().equals(getSecondCurrency()) ?
                                pair.getBaseCurrency() :
                                pair.getQuoteCurrency();
                        ChainPairs newChain = new ChainPairs(pair, bCurrency, this);
                        newChain.typeOrder = newChain.getBaseCurrency().equals(newChain.getBasePair().getQuoteCurrency()) ?
                                TypeOrder.BUY :
                                TypeOrder.SELL;
                        newChain.scanChildren(pairs);
                        children.add(newChain);
                    }
                } else {
                    String startBaseCurrency = parentChain.getBaseCurrency();
                    if (basePair.getVendor() == pair.getVendor()
                            && (pair.getBaseCurrency().equals(startBaseCurrency) || pair.getBaseCurrency().equals(getSecondCurrency()))
                            && (pair.getQuoteCurrency().equals(startBaseCurrency) || pair.getQuoteCurrency().equals(getSecondCurrency()))) {
                        String bCurrency = pair.getBaseCurrency().equals(getSecondCurrency()) ?
                                pair.getBaseCurrency() :
                                pair.getQuoteCurrency();
                        ChainPairs newChain = new ChainPairs(pair, bCurrency, this);
                        newChain.setTypeOrder(newChain.getBaseCurrency().equals(newChain.getBasePair().getQuoteCurrency()) ?
                                TypeOrder.BUY :
                                TypeOrder.SELL);
                        children.add(newChain);
                    }
                }
            });
        }
    }

    public String getSecondCurrency() {
        if (basePair.getBaseCurrency().equals(baseCurrency))
            return basePair.getQuoteCurrency();
        else
            return basePair.getBaseCurrency();
    }

    public List<ChainPairs> getYoungestChildren() {
        if (children.size() > 0) {
            List<ChainPairs> childs = new LinkedList<>();
            children.forEach(chainPairs -> childs.addAll(chainPairs.getYoungestChildren()));
            return childs;
        } else {
            return List.of(this);
        }
    }

    public String getParentsString() {
        StringBuilder sb = new StringBuilder();
        sb.append(baseCurrency)
                .append(" -> ")
                .append(basePair.getBaseCurrency().equals(baseCurrency) ?
                        basePair.getQuoteCurrency() :
                        basePair.getBaseCurrency());
        if (parentChain == null) {
            return sb.toString();
        } else {
            return parentChain.getParentsString() + " | " + sb;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Chain: ");
        sb.append(baseCurrency)
                    .append(" -> ")
                    .append(basePair.getBaseCurrency().equals(baseCurrency) ?
                            basePair.getQuoteCurrency() :
                            basePair.getBaseCurrency())
                    .append(". children: ")
                    .append(children.size());

        return sb.toString();
    }
}
