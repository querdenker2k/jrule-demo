/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.automation.jrule.demo;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;

/**
 * Demo rule: logs a warning whenever an outdoor temperature sensor crosses
 * a configurable high-temperature threshold.
 */
public class TemperatureAlertRule extends JRule {

    public static final String TEMPERATURE_ITEM = "OutdoorTemperature";
    public static final double HIGH_TEMP_THRESHOLD = 35.0;

    @JRuleName("High Temperature Alert")
    @JRuleWhenItemChange(item = TEMPERATURE_ITEM)
    public void checkTemperature(JRuleItemEvent event) {
        JRuleDecimalValue temp = event.getState().as(JRuleDecimalValue.class);
        if (temp != null && temp.doubleValue() >= HIGH_TEMP_THRESHOLD) {
            logWarn("High temperature detected: {} °C!", temp.doubleValue());
        }
    }
}
