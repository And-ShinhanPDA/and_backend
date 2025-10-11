package com.example.alert_module.evaluation.evaluator;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionTypeMapping {
    ConditionType value();
}
