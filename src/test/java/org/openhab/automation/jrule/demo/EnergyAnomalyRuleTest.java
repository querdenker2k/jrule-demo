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
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.test.JRuleTestBase;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.UnDefType;

/**
 * Tests for {@link EnergyAnomalyRule}.
 *
 * <p>
 * Demonstrates mocking persistence via {@link #mockAverageSince}: the real
 * persistence service is never called — instead the test supplies a fixed
 * one-hour average and verifies which alert level the rule derives from it.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EnergyAnomalyRuleTest extends JRuleTestBase<EnergyAnomalyRule> {

    @BeforeEach
    public void setup() throws ItemNotFoundException {
        registerItem(new NumberItem(EnergyAnomalyRule.POWER_ITEM), new DecimalType(100));
        registerItem(new StringItem(EnergyAnomalyRule.ALERT_ITEM), UnDefType.UNDEF);
    }

    @Test
    public void testHighConsumptionPostsHighAlert() {
        mockAverageSince(EnergyAnomalyRule.POWER_ITEM, new JRuleDecimalValue(100.0));

        // 250 W > 2 × 100 W → HIGH
        fireStateChanged(EnergyAnomalyRule.POWER_ITEM, new DecimalType(250), new DecimalType(100));

        assertUpdateSent(EnergyAnomalyRule.ALERT_ITEM, "HIGH");
        assertItemHasState(EnergyAnomalyRule.ALERT_ITEM, new StringType("HIGH"));
    }

    @Test
    public void testLowConsumptionPostsLowAlert() {
        mockAverageSince(EnergyAnomalyRule.POWER_ITEM, new JRuleDecimalValue(100.0));

        // 30 W < 0.5 × 100 W → LOW
        fireStateChanged(EnergyAnomalyRule.POWER_ITEM, new DecimalType(30), new DecimalType(100));

        assertUpdateSent(EnergyAnomalyRule.ALERT_ITEM, "LOW");
    }

    @Test
    public void testNormalConsumptionPostsNormalAlert() {
        mockAverageSince(EnergyAnomalyRule.POWER_ITEM, new JRuleDecimalValue(100.0));

        // 120 W is within 0.5–2× of 100 W → NORMAL
        fireStateChanged(EnergyAnomalyRule.POWER_ITEM, new DecimalType(120), new DecimalType(100));

        assertUpdateSent(EnergyAnomalyRule.ALERT_ITEM, "NORMAL");
    }

    @Test
    public void testExactBoundaryHighIsNormal() {
        mockAverageSince(EnergyAnomalyRule.POWER_ITEM, new JRuleDecimalValue(100.0));

        // exactly 2× average is NOT strictly greater → NORMAL
        fireStateChanged(EnergyAnomalyRule.POWER_ITEM, new DecimalType(200), new DecimalType(100));

        assertUpdateSent(EnergyAnomalyRule.ALERT_ITEM, "NORMAL");
    }

    @Test
    public void testNoHistoricDataSkipsUpdate() {
        // no mock → averageSince returns empty Optional → rule does nothing
        fireStateChanged(EnergyAnomalyRule.POWER_ITEM, new DecimalType(250), new DecimalType(100));

        assertNoUpdateSent(EnergyAnomalyRule.ALERT_ITEM);
    }
}
