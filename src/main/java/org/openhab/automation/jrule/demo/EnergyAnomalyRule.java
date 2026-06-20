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

import java.time.ZonedDateTime;
import java.util.Optional;

import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;

/**
 * Demo rule: detects unusual energy consumption by comparing the current power
 * reading against the one-hour rolling average from persistence.
 *
 * <ul>
 * <li>current &gt; 2× average → posts "HIGH" to {@link #ALERT_ITEM}</li>
 * <li>current &lt; 0.5× average → posts "LOW"</li>
 * <li>otherwise → posts "NORMAL"</li>
 * </ul>
 *
 * <p>
 * If no historic data is available the rule does nothing, avoiding spurious
 * alerts on first startup.
 */
public class EnergyAnomalyRule extends JRule {

    public static final String POWER_ITEM = "PowerConsumption";
    public static final String ALERT_ITEM = "EnergyAlert";

    static final double HIGH_FACTOR = 2.0;
    static final double LOW_FACTOR = 0.5;

    @JRuleName("Detect Energy Anomaly")
    @JRuleWhenItemChange(item = POWER_ITEM)
    public void checkEnergyAnomaly(JRuleItemEvent event) {
        JRuleDecimalValue current = event.getState().as(JRuleDecimalValue.class);
        if (current == null) {
            return;
        }

        Optional<JRuleDecimalValue> avgOpt = JRuleNumberItem.forName(POWER_ITEM)
                .averageSinceAsDecimal(ZonedDateTime.now().minusHours(1));
        if (avgOpt.isEmpty()) {
            return;
        }

        double val = current.doubleValue();
        double avg = avgOpt.get().doubleValue();

        String alert;
        if (val > avg * HIGH_FACTOR) {
            alert = "HIGH";
        } else if (val < avg * LOW_FACTOR) {
            alert = "LOW";
        } else {
            alert = "NORMAL";
        }
        postUpdate(ALERT_ITEM, alert);
    }
}
