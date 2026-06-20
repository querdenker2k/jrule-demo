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
import org.openhab.automation.jrule.test.JRuleTestBase;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.types.UnDefType;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TimerDemoRuleTest extends JRuleTestBase<TimerDemoRule> {

    @BeforeEach
    public void setup() throws ItemNotFoundException {
        registerItem(new SwitchItem(TimerDemoRule.TRIGGER_SWITCH), OnOffType.OFF);
        registerItem(new StringItem(TimerDemoRule.STATUS_ITEM), UnDefType.UNDEF);
    }

    @Test
    public void testTimerFiresStatusUpdate() {
        fireStateChanged(TimerDemoRule.TRIGGER_SWITCH, OnOffType.ON, OnOffType.OFF);

        assertUpdateSentEventually(TimerDemoRule.STATUS_ITEM, "timer-fired");
    }
}
