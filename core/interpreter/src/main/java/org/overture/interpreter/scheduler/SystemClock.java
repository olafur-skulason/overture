/*******************************************************************************
 *
 *	Copyright (c) 2010 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.interpreter.scheduler;

import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;
import org.overture.interpreter.messages.rtlog.RTExtendedTextMessage;
import org.overture.interpreter.messages.rtlog.RTLogger;
import org.overture.parser.config.Properties;

public class SystemClock
{
	private static long wallTime = 0;
	final static double PRECISION = 100000000.0d;

	public static synchronized long getWallTime()
	{
		return wallTime;
	}

	public static void init()
	{
		wallTime = 0;
	}

	public static synchronized void advance(long duration)
	{
		wallTime += duration;

		if (Settings.dialect == Dialect.VDM_RT && Properties.diags_timestep)
		{
			RTLogger.log(new RTExtendedTextMessage(String.format("-- Moved time by %d", duration)));
		}
	}

	/**
	 * Time unit enumeration used to specify units used in the VDM syntax
	 * 
	 * @author kela
	 */
	public enum TimeUnit
	{
		seconds(1.0, "s"),

		decisecond(Math.pow(10, -1), "ds"), centisecond(Math.pow(10, -2), "cs"), millisecond(
				Math.pow(10, -3), "ms"), microsecond(Math.pow(10, -6), "�s"), nanosecond(
				Math.pow(10, -9), "ns");

		private final Double value;
		private final String symbol;

		private TimeUnit(Double value, String symbol)
		{
			this.value = value;
			this.symbol = symbol;
		}

		public Double getValue()
		{
			return value;
		}

		@Override
		public String toString()
		{
			return symbol + " factor: " + value;
		}
	}

	/**
	 * Utility method to convert a value in the given unit to the internal time
	 * 
	 * @param unit
	 *            The unit of the time parameter
	 * @param time
	 *            The time to convert
	 * @return The internal time representation of the parameter
	 */
	public static long timeToInternal(TimeUnit unit, Double time)
	{
		return Math.round(time * unit.getValue()
				/ TimeUnit.nanosecond.getValue());
	}

	/**
	 * Utility method to convert a value in the given unit to the internal time
	 * 
	 * @param unit
	 *            The unit of the time parameter
	 * @param time
	 *            The time to convert
	 * @return The internal time representation of the parameter
	 */
	public static long timeToInternal(TimeUnit unit, long time)
	{
		return Math.round(time * unit.getValue()
				/ TimeUnit.nanosecond.getValue());
	}

	/**
	 * Utility method to convert the internal time to the given unit.
	 * 
	 * @param unit
	 *            The unit to convert the internal time to
	 * @param internalTime
	 *            The internal time
	 * @return The internal time representation of the parameter
	 */
	public static Double internalToTime(TimeUnit unit, long internalTime)
	{
		return Math.round(internalTime * TimeUnit.nanosecond.getValue()
				/ unit.getValue() * PRECISION)
				/ PRECISION;
	}

	/**
	 * Utility method to convert the internal time to the given unit.
	 * 
	 * @param unit
	 *            The unit to convert the internal time to
	 * @return The internal time representation of the parameter
	 */
	public static Double internalToTime(TimeUnit unit)
	{
		return internalToTime(unit, getWallTime());
	}
}
