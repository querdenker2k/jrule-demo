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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.test.JRuleTestBase;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.library.items.ContactItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.OpenClosedType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.UnDefType;

/**
 * Tests for {@link SecurityAlertRule}.
 *
 * <p>
 * Demonstrates testing rules that use both {@code sendCommand} (to control a
 * physical device) and {@code postUpdate} (to set a virtual status item).
 * All assertions use the helper methods from {@link JRuleTestBase} — no raw
 * JUnit assertions are written directly in the test body.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SecurityAlertRuleTest extends JRuleTestBase<SecurityAlertRule> {

    @BeforeEach
    public void setup() throws ItemNotFoundException {
        registerItem(new ContactItem(SecurityAlertRule.DOOR_SENSOR), OpenClosedType.CLOSED);
        registerItem(new SwitchItem(SecurityAlertRule.ALARM_SWITCH), OnOffType.OFF);
        registerItem(new StringItem(SecurityAlertRule.ALARM_STATUS), UnDefType.UNDEF);
    }

    @Test
    public void testDoorOpenSendsAlarmOnCommand() {
        fireStateChanged(SecurityAlertRule.DOOR_SENSOR, OpenClosedType.OPEN, OpenClosedType.CLOSED);

        assertCommandSent(SecurityAlertRule.ALARM_SWITCH, JRuleOnOffValue.ON);
    }

    @Test
    public void testDoorOpenUpdatesAlarmStatus() {
        fireStateChanged(SecurityAlertRule.DOOR_SENSOR, OpenClosedType.OPEN, OpenClosedType.CLOSED);

        assertUpdateSent(SecurityAlertRule.ALARM_STATUS, "BREACH");
    }

    @Test
    public void testDoorOpenSetsAlarmSwitchStateToOn() {
        fireStateChanged(SecurityAlertRule.DOOR_SENSOR, OpenClosedType.OPEN, OpenClosedType.CLOSED);

        assertItemHasState(SecurityAlertRule.ALARM_SWITCH, OnOffType.ON);
    }

    @Test
    public void testDoorOpenSetsAlarmStatusItem() {
        fireStateChanged(SecurityAlertRule.DOOR_SENSOR, OpenClosedType.OPEN, OpenClosedType.CLOSED);

        assertItemHasState(SecurityAlertRule.ALARM_STATUS, new StringType("BREACH"));
    }

    @Test
    public void testDoorCloseSendsAlarmOffCommand() {
        fireStateChanged(SecurityAlertRule.DOOR_SENSOR, OpenClosedType.CLOSED, OpenClosedType.OPEN);

        assertCommandSent(SecurityAlertRule.ALARM_SWITCH, JRuleOnOffValue.OFF);
    }

    @Test
    public void testDoorCloseUpdatesSecureStatus() {
        fireStateChanged(SecurityAlertRule.DOOR_SENSOR, OpenClosedType.CLOSED, OpenClosedType.OPEN);

        assertUpdateSent(SecurityAlertRule.ALARM_STATUS, "SECURE");
    }
}
