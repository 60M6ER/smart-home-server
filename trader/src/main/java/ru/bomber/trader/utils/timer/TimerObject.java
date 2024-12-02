package ru.bomber.trader.utils.timer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimerObject {

    private TimerListener listener;
    private String marker;
    private long delay;
    private long lastTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimerObject that = (TimerObject) o;

        return this.listener.getId().equals(that.getListener().getId());
    }

    @Override
    public int hashCode() {
        int result = listener.hashCode();
        result = 31 * result + (marker != null ? marker.hashCode() : 0);
        result = 31 * result + (int) (delay ^ (delay >>> 32));
        result = 31 * result + (int) (lastTime ^ (lastTime >>> 32));
        return result;
    }
}
