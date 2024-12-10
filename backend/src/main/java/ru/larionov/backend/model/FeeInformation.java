package ru.larionov.backend.model;

import lombok.Data;

@Data
public class FeeInformation {
    private double maker;
    private double taker;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeeInformation that = (FeeInformation) o;

        if (Double.compare(that.maker, maker) != 0) return false;
        return Double.compare(that.taker, taker) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(maker);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(taker);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
