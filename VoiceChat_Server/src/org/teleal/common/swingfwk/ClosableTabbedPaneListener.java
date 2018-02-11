/*
 * Copyright (C) 2010 Teleal GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.teleal.common.swingfwk;

import java.util.EventListener;

/**
 * The listener that's notified when an tab should be closed in the
 * <code>CloseableTabbedPane</code>.
 */
public interface ClosableTabbedPaneListener extends EventListener {
    /**
     * Informs all <code>CloseableTabbedPaneListener</code>s when a tab should be
     * closed
     *
     * @param tabIndexToClose the index of the tab which should be closed
     * @return true if the tab can be closed, false otherwise
     */
    boolean closeTab(int tabIndexToClose);
}