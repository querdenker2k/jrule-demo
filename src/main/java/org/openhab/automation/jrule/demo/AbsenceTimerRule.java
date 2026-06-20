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
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

/**
 * Demo rule: when all occupants leave (presence switch turns OFF), a 10-minute
 * timer is started before turning off the lights and marking the home as away.
 * If someone returns before the timer fires, the timer is cancelled.
 *
 * <p>
 * The 10-minute delay prevents false "away" states when occupants leave briefly.
 *
 * <p>
 * In tests the timer is fast-forwarded via
 * {@link org.openhab.automation.jrule.test.JRuleTestBase#invokeTimer(String)}
 * without waiting for the real delay.
 */
public class AbsenceTimerRule extends JRule {

    public static final String PRESENCE_SWITCH = "PresenceSwitch";
    public static final String LIGHTS_SWITCH = "LightsSwitch";
    public static final String ABSENCE_STATUS = "AbsenceStatus";

    static final String TIMER_NAME = "absence-timer";
    static final Duration ABSENCE_DELAY = Duration.ofMinutes(10);

    @JRuleName("Start Absence Timer When Everyone Leaves")
    @JRuleWhenItemChange(item = PRESENCE_SWITCH, to = JRuleSwitchItem.OFF)
    public void presenceGone(JRuleItemEvent event) {
        logInfo("Presence lost — starting {}-minute absence timer", ABSENCE_DELAY.toMinutes());
        createOrReplaceTimer(TIMER_NAME, ABSENCE_DELAY, t -> {
            logInfo("Absence timer fired — marking home as AWAY");
            boolean lightsWereOn = JRuleSwitchItem.forName(LIGHTS_SWITCH).previousState()
                    .map(h -> JRuleOnOffValue.ON.equals(h.getValue())).orElse(true);
            if (lightsWereOn) {
                sendCommand(LIGHTS_SWITCH, JRuleOnOffValue.OFF);
            }
            postUpdate(ABSENCE_STATUS, "AWAY");
        });
    }

    @JRuleName("Cancel Absence Timer When Someone Returns")
    @JRuleWhenItemChange(item = PRESENCE_SWITCH, to = JRuleSwitchItem.ON)
    public void presenceRestored(JRuleItemEvent event) {
        logInfo("Presence restored — cancelling absence timer");
        cancelTimer(TIMER_NAME);
        postUpdate(ABSENCE_STATUS, "HOME");
    }
}
