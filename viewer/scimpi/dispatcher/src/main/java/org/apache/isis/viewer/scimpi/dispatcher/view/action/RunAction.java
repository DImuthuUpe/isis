/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */


package org.apache.isis.viewer.scimpi.dispatcher.view.action;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.core.metamodel.spec.feature.ObjectActionParameter;
import org.apache.isis.viewer.scimpi.dispatcher.AbstractElementProcessor;
import org.apache.isis.viewer.scimpi.dispatcher.ScimpiException;
import org.apache.isis.viewer.scimpi.dispatcher.context.RequestContext;
import org.apache.isis.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import org.apache.isis.viewer.scimpi.dispatcher.processor.Request;
import org.apache.isis.viewer.scimpi.dispatcher.util.MethodsUtils;


public class RunAction extends AbstractElementProcessor {
    
    public void process(Request request) {
        RequestContext context = request.getContext();

        String objectId = request.getOptionalProperty(OBJECT);
        ObjectAdapter object = MethodsUtils.findObject(context, objectId);

        String methodName = request.getRequiredProperty(METHOD);
        ObjectAction action = MethodsUtils.findAction(object, methodName);

        String variableName = request.getOptionalProperty(RESULT_NAME);
        String scopeName = request.getOptionalProperty(SCOPE);

        ActionContent parameterBlock = new ActionContent(action);
        request.setBlockContent(parameterBlock);
        request.processUtilCloseTag();
        ObjectAdapter[] parameters = parameterBlock.getParameters(request);
        
        if (!MethodsUtils.isVisibleAndUsable(object, action)) {
            throw new ScimpiException("Action is not visible/enabled: " + action.getName());
        }
        
        // swap null parameter of the object's type to run a contributed method
        if (action.isContributed()) {
            ObjectActionParameter[] parameterSpecs = action.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i] == null && object.getSpecification().isOfType(parameterSpecs[i].getSpecification())) {
                    parameters[i] = object;
                    break;
                }
            }
        }
        
        Scope scope = RequestContext.scope(scopeName, Scope.REQUEST);
        MethodsUtils.runMethod(context, action, object, parameters, variableName, scope);
        request.popBlockContent();
    }

    public String getName() {
        return "run-action";
    }

}
