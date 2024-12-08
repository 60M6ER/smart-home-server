package ru.larionov.backend.model;


import lombok.Getter;

import javax.annotation.processing.Generated;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChainPairs {

    private PairCurrency basePair;
    @Getter
    private String baseCurrency;
    @Getter
    private ChainPairs parentChain;
    @Getter
    private int level;
    @Getter
    private int countLevels;

    @Getter
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
        pairs.forEach(pair -> {
            if (level < 1) {
                if (!pair.getQuoteCurrency().equals(baseCurrency)
                        && !pair.getBaseCurrency().equals(baseCurrency)
                        && !basePair.equals(pair)
                        && (pair.getBaseCurrency().equals(getSecondCurrency())
                        || pair.getQuoteCurrency().equals(getSecondCurrency()))) {
                    String bCurrency = pair.getBaseCurrency().equals(getSecondCurrency()) ?
                            pair.getQuoteCurrency() :
                            pair.getBaseCurrency();
                    ChainPairs newChain = new ChainPairs(pair, bCurrency, this);
                    newChain.scanChildren(pairs);
                    children.add(newChain);
                }
            } else {
                String startBaseCurrency = parentChain.getBaseCurrency();
                if ((pair.getBaseCurrency().equals(startBaseCurrency) || pair.getBaseCurrency().equals(getSecondCurrency()))
                        && (pair.getQuoteCurrency().equals(startBaseCurrency) || pair.getQuoteCurrency().equals(getSecondCurrency()))) {
                    String bCurrency = pair.getBaseCurrency().equals(getSecondCurrency()) ?
                            pair.getQuoteCurrency() :
                            pair.getBaseCurrency();
                    ChainPairs newChain = new ChainPairs(pair, bCurrency, this);
                    children.add(newChain);
                }
            }
        });
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
        sb.append(basePair.getQuoteCurrency())
                .append(" -> ")
                .append(basePair.getBaseCurrency());
        if (parentChain == null) {
            return sb.toString();
        } else {
            return parentChain.getParentsString() + " | " + sb;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Chain: ");
        sb.append(basePair.getQuoteCurrency())
                    .append(" -> ")
                    .append(basePair.getBaseCurrency())
                    .append(". children: ")
                    .append(children.size());

        return sb.toString();
    }
}
