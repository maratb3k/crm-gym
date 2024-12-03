package com.example.crm_gym.services;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;

    @Autowired
    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public MeterRegistry getMeterRegistry() {
        return meterRegistry;
    }

    public Timer.Sample startQueryTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopQueryTimer(Timer.Sample sample, String queryName) {
        sample.stop(Timer.builder(queryName)
                .register(meterRegistry));
    }
}