package com.bhge.dataquality.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("valid")
@Validated
public class SensorReadingProperties {

    private Double valueThreshold = 10.0;

    public Double getValueThreshold() {
        return valueThreshold;
    }

    public void setValueThreshold(Double valueThreshold) {
        this.valueThreshold = valueThreshold;
    }
}
