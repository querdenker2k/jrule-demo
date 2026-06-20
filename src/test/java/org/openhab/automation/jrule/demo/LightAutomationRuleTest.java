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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.test.JRuleTestBase;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.events.ItemEventFactory;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.OnOffType;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LightAutomationRuleTest extends JRuleTestBase {

    @BeforeEach
    public void setup() throws ItemNotFoundException {
        registerItem(new SwitchItem(LightAutomationRule.MOTION_SENSOR), OnOffType.OFF);
        registerItem(new SwitchItem(LightAutomationRule.LIGHT_SWITCH), OnOffType.OFF);
        eventCollector.clear();
    }

    @Test
    public void testMotionOnTurnsLightOn() {
        initRule(LightAutomationRule.class);
        fireEvents(false, List.of(ItemEventFactory.createStateChangedEvent(LightAutomationRule.MOTION_SENSOR,
                OnOffType.ON, OnOffType.OFF, null, null)));
        assertTrue(eventCollector.hasCommandEvent(LightAutomationRule.LIGHT_SWITCH, JRuleOnOffValue.ON),
                "Expected ON command to light when motion sensor turns ON");
    }

    @Test
    public void testMotionOffTurnsLightOff() {
        initRule(LightAutomationRule.class);
        fireEvents(false, List.of(ItemEventFactory.createStateChangedEvent(LightAutomationRule.MOTION_SENSOR,
                OnOffType.OFF, OnOffType.ON, null, null)));
        assertTrue(eventCollector.hasCommandEvent(LightAutomationRule.LIGHT_SWITCH, JRuleOnOffValue.OFF),
                "Expected OFF command to light when motion sensor turns OFF");
    }
}
