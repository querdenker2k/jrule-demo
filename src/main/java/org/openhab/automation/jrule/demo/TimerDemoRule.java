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

import java.time.Duration;

import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;

/**
 * Demo rule: when a trigger switch turns ON, a one-shot timer is created that
 * writes a status string to a status item after 1 second.
 */
public class TimerDemoRule extends JRule {

    public static final String TRIGGER_SWITCH = "TimerTriggerSwitch";
    public static final String STATUS_ITEM = "TimerStatusItem";

    @JRuleName("Start Status Timer On Switch")
    @JRuleWhenItemChange(item = TRIGGER_SWITCH, to = JRuleSwitchItem.ON)
    public void startStatusTimer(JRuleItemEvent event) {
        logInfo("Switch turned ON — creating 1-second status timer");
        createTimer("demo-timer", Duration.ofSeconds(1), t -> postUpdate(STATUS_ITEM, "timer-fired"));
    }
}
