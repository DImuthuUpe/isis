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
package org.apache.isis.viewer.restful.viewer2.resources.home;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.isis.viewer.restful.applib.resources.HomePageResource;
import org.apache.isis.viewer.restful.viewer2.representations.LinkRepresentation;
import org.apache.isis.viewer.restful.viewer2.resources.ResourceAbstract;

/**
 * Implementation note: it seems to be necessary to annotate the implementation with {@link Path} rather than the
 * interface (at least under RestEasy 1.0.2 and 1.1-RC2).
 */
@Path("/index")
public class HomePageResourceImpl extends ResourceAbstract implements HomePageResource {


    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public String resources() {
        init();
        
        HomePageRepresentation homePageRepresentation = new HomePageRepresentation();
        homePageRepresentation.setUser(linkTo("user"));

        homePageRepresentation.setServices(linkTo("services"));
        return asJson(homePageRepresentation);
    }

    protected LinkRepresentation linkTo(String url) {
        return LinkRepresentation.newBuilder(getResourceContext().representationSelfLinkTo(url), null, url).build();
    }


}