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

import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

/**
 * Demo rule: mirrors a motion sensor state to a light switch.
 * When motion is detected (sensor ON), the light turns on.
 * When motion stops (sensor OFF), the light turns off.
 */
public class LightAutomationRule extends JRule {

    public static final String MOTION_SENSOR = "MotionSensor";
    public static final String LIGHT_SWITCH = "LightSwitch";

    @JRuleName("Light On When Motion Detected")
    @JRuleWhenItemChange(item = MOTION_SENSOR, to = JRuleSwitchItem.ON)
    public void motionDetected(JRuleItemEvent event) {
        logInfo("Motion detected — turning light on");
        sendCommand(LIGHT_SWITCH, JRuleOnOffValue.ON);
    }

    @JRuleName("Light Off When No Motion")
    @JRuleWhenItemChange(item = MOTION_SENSOR, to = JRuleSwitchItem.OFF)
    public void noMotion(JRuleItemEvent event) {
        logInfo("No motion — turning light off");
        sendCommand(LIGHT_SWITCH, JRuleOnOffValue.OFF);
    }
}
