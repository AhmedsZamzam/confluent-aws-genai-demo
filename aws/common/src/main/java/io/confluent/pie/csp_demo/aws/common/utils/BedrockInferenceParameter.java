package io.confluent.pie.csp_demo.aws.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BedrockInferenceParameter {
    private float temperature;
    private float p;
    private float k;
    private int max_tokens;

    public BedrockInferenceParameter() {
        this(0.50f, 1.0f, 250.0f, 300);
    }
}
