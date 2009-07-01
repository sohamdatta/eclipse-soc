/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.birt.chart.model.newtype.validation;

import org.eclipse.birt.chart.model.attribute.LeaderLineStyle;
import org.eclipse.birt.chart.model.attribute.Position;

import org.eclipse.birt.chart.model.component.Label;

/**
 * A sample validator interface for {@link org.eclipse.birt.chart.model.newtype.DonutSeries}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface DonutSeriesValidator {
	boolean validate();

	boolean validateExplosion(int value);
	boolean validateThickness(int value);
	boolean validateRotation(int value);
	boolean validateShowDonutLabels(boolean value);
	boolean validateLeaderLineStyle(LeaderLineStyle value);
	boolean validateLeaderLineLength(double value);
	boolean validateTitle(Label value);
	boolean validateTitlePosition(Position value);
}
