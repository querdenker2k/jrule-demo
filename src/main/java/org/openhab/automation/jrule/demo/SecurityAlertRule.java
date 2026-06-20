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

import org.openhab.automation.jrule.items.JRuleContactItem;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

/**
 * Demo rule: reacts to a door sensor and controls two different item types:
 * <ul>
 * <li>{@code sendCommand} — sends a command to a physical device (the alarm siren switch)</li>
 * <li>{@code postUpdate} — directly sets the state of a virtual status string item</li>
 * </ul>
 *
 * <p>
 * This illustrates the key difference: sendCommand travels through the binding
 * and may be rejected or modified; postUpdate sets the item state immediately.
 */
public class SecurityAlertRule extends JRule {

    public static final String DOOR_SENSOR = "DoorSensor";
    public static final String ALARM_SWITCH = "AlarmSwitch";
    public static final String ALARM_STATUS = "AlarmStatus";

    @JRuleName("Trigger Alarm On Door Open")
    @JRuleWhenItemChange(item = DOOR_SENSOR, to = JRuleContactItem.OPEN)
    public void doorOpened(JRuleItemEvent event) {
        logWarn("Door opened — activating alarm");
        sendCommand(ALARM_SWITCH, JRuleOnOffValue.ON);
        postUpdate(ALARM_STATUS, "BREACH");
    }

    @JRuleName("Cancel Alarm On Door Close")
    @JRuleWhenItemChange(item = DOOR_SENSOR, to = JRuleContactItem.CLOSED)
    public void doorClosed(JRuleItemEvent event) {
        logInfo("Door closed — deactivating alarm");
        sendCommand(ALARM_SWITCH, JRuleOnOffValue.OFF);
        postUpdate(ALARM_STATUS, "SECURE");
    }
}
