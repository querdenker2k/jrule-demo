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
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.OnOffType;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LightAutomationRuleTest extends JRuleTestBase<LightAutomationRule> {

    @BeforeEach
    public void setup() throws ItemNotFoundException {
        registerItem(new SwitchItem(LightAutomationRule.MOTION_SENSOR), OnOffType.OFF);
        registerItem(new SwitchItem(LightAutomationRule.LIGHT_SWITCH), OnOffType.OFF);
    }

    @Test
    public void testMotionOnTurnsLightOn() {
        fireStateChanged(LightAutomationRule.MOTION_SENSOR, OnOffType.ON, OnOffType.OFF);

        assertCommandSent(LightAutomationRule.LIGHT_SWITCH, JRuleOnOffValue.ON);
    }

    @Test
    public void testMotionOffTurnsLightOff() {
        fireStateChanged(LightAutomationRule.MOTION_SENSOR, OnOffType.OFF, OnOffType.ON);

        assertCommandSent(LightAutomationRule.LIGHT_SWITCH, JRuleOnOffValue.OFF);
    }
}
