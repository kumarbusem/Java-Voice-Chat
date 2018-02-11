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

package org.teleal.cling.model.meta;

import org.teleal.cling.model.ValidationError;

import java.util.List;
import java.util.Collections;


/**
 * Describes a single action, the deprecated "query any state variable" action.
 *
 * Note: This is already deprecated in UDA 1.0!
 *
 * @author Christian Bauer
 */
public class QueryStateVariableAction<S extends Service> extends Action<S> {

    public static final String ACTION_NAME = "QueryStateVariable";
    public static final String VIRTUAL_STATEVARIABLE_INPUT = "VirtualQueryActionInput";
    public static final String VIRTUAL_STATEVARIABLE_OUTPUT = "VirtualQueryActionOutput";

    public QueryStateVariableAction() {
        this(null);
    }

    public QueryStateVariableAction(S service) {
        super(ACTION_NAME,
                new ActionArgument[]{
                        new ActionArgument("varName", VIRTUAL_STATEVARIABLE_INPUT, ActionArgument.Direction.IN),
                        new ActionArgument("return", VIRTUAL_STATEVARIABLE_OUTPUT, ActionArgument.Direction.OUT),
                }
        );
        setService(service);
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public List<ValidationError> validate() {
        return Collections.EMPTY_LIST;
    }
}