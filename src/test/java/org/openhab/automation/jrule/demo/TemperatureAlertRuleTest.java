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
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.types.UnDefType;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TemperatureAlertRuleTest extends JRuleTestBase<TemperatureAlertRule> {

    @BeforeEach
    public void setup() throws ItemNotFoundException {
        registerItem(new NumberItem(TemperatureAlertRule.TEMPERATURE_ITEM), UnDefType.UNDEF);
    }

    @Test
    public void testHighTemperatureTriggersRule() {
        fireStateChanged(TemperatureAlertRule.TEMPERATURE_ITEM, new DecimalType(36.5), new DecimalType(20.0));

        assertRuleMethodCalled(r -> r.checkTemperature(anyArg()));
    }

    @Test
    public void testNormalTemperatureNoCommandsSent() {
        fireStateChanged(TemperatureAlertRule.TEMPERATURE_ITEM, new DecimalType(20.0), new DecimalType(19.0));

        assertNoCommandSent(TemperatureAlertRule.TEMPERATURE_ITEM);
    }
}
