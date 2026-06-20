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
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.UnDefType;

/**
 * Tests for {@link AbsenceTimerRule}.
 *
 * <p>
 * Demonstrates testing rules with long-running timers: the real 10-minute delay
 * is never waited for — instead {@link #invokeTimer(String)} executes the timer
 * callback immediately. All assertions use the helper methods from
 * {@link JRuleTestBase}.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AbsenceTimerRuleTest extends JRuleTestBase<AbsenceTimerRule> {

    @BeforeEach
    public void setup() throws ItemNotFoundException {
        registerItem(new SwitchItem(AbsenceTimerRule.PRESENCE_SWITCH), OnOffType.ON);
        registerItem(new SwitchItem(AbsenceTimerRule.LIGHTS_SWITCH), OnOffType.ON);
        registerItem(new StringItem(AbsenceTimerRule.ABSENCE_STATUS), UnDefType.UNDEF);
    }

    @Test
    public void testPresenceOffStartsTimer() {
        fireStateChanged(AbsenceTimerRule.PRESENCE_SWITCH, OnOffType.OFF, OnOffType.ON);

        assertTimerRunning(AbsenceTimerRule.TIMER_NAME);
    }

    @Test
    public void testTimerFiresTurnsOffLights() {
        fireStateChanged(AbsenceTimerRule.PRESENCE_SWITCH, OnOffType.OFF, OnOffType.ON);

        // fast-forward: execute the 10-minute timer immediately
        invokeTimer(AbsenceTimerRule.TIMER_NAME);

        assertCommandSent(AbsenceTimerRule.LIGHTS_SWITCH, JRuleOnOffValue.OFF);
    }

    @Test
    public void testTimerFiresUpdatesAbsenceStatus() {
        fireStateChanged(AbsenceTimerRule.PRESENCE_SWITCH, OnOffType.OFF, OnOffType.ON);

        // fast-forward: execute the 10-minute timer immediately
        invokeTimer(AbsenceTimerRule.TIMER_NAME);

        assertUpdateSent(AbsenceTimerRule.ABSENCE_STATUS, "AWAY");
    }

    @Test
    public void testTimerFiresSetsItemStates() {
        fireStateChanged(AbsenceTimerRule.PRESENCE_SWITCH, OnOffType.OFF, OnOffType.ON);

        invokeTimer(AbsenceTimerRule.TIMER_NAME);

        assertItemHasState(AbsenceTimerRule.LIGHTS_SWITCH, OnOffType.OFF);
        assertItemHasState(AbsenceTimerRule.ABSENCE_STATUS, new StringType("AWAY"));
    }

    @Test
    public void testPresenceReturnCancelsTimer() {
        // presence leaves → timer starts
        fireStateChanged(AbsenceTimerRule.PRESENCE_SWITCH, OnOffType.OFF, OnOffType.ON);
        assertTimerRunning(AbsenceTimerRule.TIMER_NAME);

        // presence returns → timer is cancelled before it fires
        fireStateChanged(AbsenceTimerRule.PRESENCE_SWITCH, OnOffType.ON, OnOffType.OFF);

        assertNoTimerRunning(AbsenceTimerRule.TIMER_NAME);
    }

    @Test
    public void testPresenceReturnUpdatesStatusToHome() {
        fireStateChanged(AbsenceTimerRule.PRESENCE_SWITCH, OnOffType.OFF, OnOffType.ON);
        fireStateChanged(AbsenceTimerRule.PRESENCE_SWITCH, OnOffType.ON, OnOffType.OFF);

        assertUpdateSent(AbsenceTimerRule.ABSENCE_STATUS, "HOME");
        assertItemHasState(AbsenceTimerRule.ABSENCE_STATUS, new StringType("HOME"));
    }

    // -------------------------------------------------------------------------
    // Persistence tests
    // -------------------------------------------------------------------------

    @Test
    public void testTimerTurnsOffLightsWhenPreviouslyOn() {
        mockPreviousState(AbsenceTimerRule.LIGHTS_SWITCH, JRuleOnOffValue.ON);

        fireStateChanged(AbsenceTimerRule.PRESENCE_SWITCH, OnOffType.OFF, OnOffType.ON);
        invokeTimer(AbsenceTimerRule.TIMER_NAME);

        assertCommandSent(AbsenceTimerRule.LIGHTS_SWITCH, JRuleOnOffValue.OFF);
    }

    @Test
    public void testTimerSkipsLightsCommandWhenAlreadyOff() {
        mockPreviousState(AbsenceTimerRule.LIGHTS_SWITCH, JRuleOnOffValue.OFF);

        fireStateChanged(AbsenceTimerRule.PRESENCE_SWITCH, OnOffType.OFF, OnOffType.ON);
        invokeTimer(AbsenceTimerRule.TIMER_NAME);

        assertNoCommandSent(AbsenceTimerRule.LIGHTS_SWITCH);
    }
}
