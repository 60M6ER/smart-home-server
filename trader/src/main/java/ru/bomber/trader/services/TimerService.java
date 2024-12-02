package ru.bomber.trader.services;

import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.bomber.trader.utils.timer.TimerListener;
import ru.bomber.trader.utils.timer.TimerObject;
import ru.bomber.trader.utils.timer.TimerProvider;

import java.util.*;

@Service
public class TimerService implements TimerProvider {

    private List<TimerObject> timers;
    private List<UUID> timersForRemove;

    @PostConstruct
    private void init() {
        timers = new ArrayList<>();
        timersForRemove = new ArrayList<>();
    }


    @Scheduled(fixedDelay = 5000)
    void watchTimers() {
        timersForRemove.forEach(id -> {
            timers.stream()
                    .filter(l -> l.getListener().getId().equals(id))
                    .findFirst()
                    .ifPresent(timerObject -> timers.remove(timerObject));
        });

        long curTime = new Date().getTime();
        timers.forEach(t -> {
            if (curTime - (t.getLastTime() + t.getDelay()) >= 0)
                t.getListener().onEventTimer(t.getMarker());
        });
    }


    @Override
    public void addListener(TimerListener listener, String marker, long delay) {
        timers.add(new TimerObject(listener, marker, delay, new Date().getTime()));
    }

    @Override
    public void delListener(UUID id) {
        timersForRemove.add(id);
    }
}
