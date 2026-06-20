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

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openhab.automation.jrule.test.JRuleTestBase;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.events.ItemEventFactory;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.types.UnDefType;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TimerDemoRuleTest extends JRuleTestBase {

    @BeforeEach
    public void setup() throws ItemNotFoundException {
        registerItem(new SwitchItem(TimerDemoRule.TRIGGER_SWITCH), OnOffType.OFF);
        registerItem(new StringItem(TimerDemoRule.STATUS_ITEM), UnDefType.UNDEF);
        eventCollector.clear();
    }

    @Test
    public void testTimerFiresStatusUpdate() {
        initRule(TimerDemoRule.class);
        fireEvents(false, List.of(ItemEventFactory.createStateChangedEvent(TimerDemoRule.TRIGGER_SWITCH, OnOffType.ON,
                OnOffType.OFF, null, null)));

        Awaitility.await().atMost(3, TimeUnit.SECONDS).pollInterval(Duration.ofMillis(100))
                .untilAsserted(() -> assertTrue(eventCollector.hasUpdateEvent(TimerDemoRule.STATUS_ITEM, "timer-fired"),
                        "Expected 'timer-fired' update on status item after 1-second timer"));
    }
}
