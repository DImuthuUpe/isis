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

package org.apache.isis.core.runtime.system.session;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import com.google.common.collect.Lists;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.commons.config.IsisConfigurationDefault;
import org.apache.isis.core.metamodel.adapter.oid.OidMarshaller;
import org.apache.isis.core.metamodel.spec.SpecificationLoaderSpi;
import org.apache.isis.core.runtime.authentication.AuthenticationManager;
import org.apache.isis.core.runtime.authorization.AuthorizationManager;
import org.apache.isis.core.runtime.imageloader.TemplateImageLoader;
import org.apache.isis.core.runtime.system.DeploymentType;
import org.apache.isis.core.runtime.system.persistence.PersistenceSessionFactory;
import org.apache.isis.core.runtime.userprofile.UserProfileLoader;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class IsisSessionFactoryAbstractTest_init_and_shutdown {

    public static class DomainServiceWithNoPostConstructOrPreDestroy {
    }

    public static class DomainServiceWithValidPostConstructNoParams {
        boolean called = false;
        @PostConstruct
        public void postConstruct() {
            called = true;
        }
    }

    public static class DomainServiceWithValidPostConstructPropertiesParam {
        boolean called = false;
        Map<String, String> props;
        @PostConstruct
        public void postConstruct(Map<String,String> props) {
            this.props = props;
            called = true;
        }
    }

    public static class DomainServiceWithValidPostConstructSubtypeOfPropertiesParam {
        boolean called = false;
        Object props;
        @PostConstruct
        public void postConstruct(Object props) {
            this.props = props;
            called = true;
        }
    }

    public static class DomainServiceWithInvalidPostConstructWrongNumberParams {
        @PostConstruct
        public void postConstruct(int i, Properties props) {}
    }

    public static class DomainServiceWithInvalidPostConstructWrongTypeOfParam {
        @PostConstruct
        public void postConstruct(int i) {}
    }

    public static class DomainServiceWithValidPreDestroyNoParams {
        boolean called = false;
        @PostConstruct
        public void postConstruct() {
            called = true;
        }
    }

    public static class DomainServiceWithInvalidPreDestroyWrongNumberParams {
        @PostConstruct
        public void postConstruct(int i) {}
    }

    public static class DomainServiceWithSomeId {
        public String getId() { return "someId"; }
    }

    public static class DomainServiceWithDuplicateId {
        public String getId() { return "someId"; }
    }



    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DeploymentType deploymentType;
    @Mock
    private SpecificationLoaderSpi specificationLoader;
    @Mock
    private TemplateImageLoader templateImageLoader;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AuthorizationManager authorizationManager;
    @Mock
    private UserProfileLoader userProfileLoader;
    @Mock
    private PersistenceSessionFactory persistenceSessionFactory;
    @Mock
    private OidMarshaller oidMarshaller;
    
    private IsisConfigurationDefault configuration;
    private List<Object> serviceList;

    private IsisSessionFactoryAbstract isfa;

    
    @Before
    public void setUp() throws Exception {
        configuration = new IsisConfigurationDefault();
        configuration.add("foo", "bar");
        
        serviceList = Lists.newArrayList();
        context.ignoring(deploymentType, specificationLoader, templateImageLoader, authenticationManager, authorizationManager, userProfileLoader, persistenceSessionFactory, oidMarshaller);
    }
    
    @Test
    public void emptyListOfServices() {
        isfa = createIsisSessionFactoryAbstract(serviceList);
    }

    @Test
    public void preConstruct_DomainServiceWithNoPostConstructOrPreDestroy() {
        serviceList.add(new DomainServiceWithNoPostConstructOrPreDestroy());
        isfa = createIsisSessionFactoryAbstract(serviceList);
        
        isfa.init();
        isfa.shutdown();
    }

    @Test
    public void preConstruct_DomainServiceWithValidPostConstructNoParams() {
        DomainServiceWithValidPostConstructNoParams domainService = new DomainServiceWithValidPostConstructNoParams();
        serviceList.add(domainService);
        isfa = createIsisSessionFactoryAbstract(serviceList);
        isfa.init();
        assertThat(domainService.called,is(true));
        isfa.shutdown();
    }

    @Test
    public void preConstruct_DomainServiceWithValidPostConstructPropertiesParam() {
        DomainServiceWithValidPostConstructPropertiesParam domainService = new DomainServiceWithValidPostConstructPropertiesParam();
        serviceList.add(domainService);
        isfa = createIsisSessionFactoryAbstract(serviceList);
        isfa.init();
        assertThat(domainService.called,is(true));
        assertThat(domainService.props.get("foo"), is("bar"));
        isfa.shutdown();
    }

    @Test
    public void preConstruct_DomainServiceWithValidPostConstructSubtypeOfPropertiesParam() {
        DomainServiceWithValidPostConstructSubtypeOfPropertiesParam domainService = new DomainServiceWithValidPostConstructSubtypeOfPropertiesParam();
        serviceList.add(domainService);
        isfa = createIsisSessionFactoryAbstract(serviceList);
        isfa.init();
        assertThat(domainService.called,is(true));
        assertThat(domainService.props, is(not(nullValue())));
        isfa.shutdown();
    }

    @Test(expected=IllegalStateException.class)
    public void preConstruct_DomainServiceWithInvalidPostConstructWrongNumberParams() {
        serviceList.add(new DomainServiceWithInvalidPostConstructWrongNumberParams());
        isfa = createIsisSessionFactoryAbstract(serviceList);
    }

    @Test(expected=IllegalStateException.class)
    public void preConstruct_DomainServiceWithInvalidPostConstructWrongTypeOfParam() {
        serviceList.add(new DomainServiceWithInvalidPostConstructWrongTypeOfParam());
        isfa = createIsisSessionFactoryAbstract(serviceList);
    }

    @Test
    public void preConstruct_DomainServiceWithValidPreDestroyNoParams() {
        DomainServiceWithValidPreDestroyNoParams domainService = new DomainServiceWithValidPreDestroyNoParams();
        serviceList.add(domainService);
        isfa = createIsisSessionFactoryAbstract(serviceList);
        isfa.init();
        assertThat(domainService.called,is(true));
        isfa.shutdown();
    }

    @Test(expected=IllegalStateException.class)
    public void preConstruct_DomainServiceWithInvalidPreDestroyWrongNumberParams() {
        serviceList.add(new DomainServiceWithInvalidPreDestroyWrongNumberParams());
        isfa = createIsisSessionFactoryAbstract(serviceList);
    }

    @Test(expected=IllegalStateException.class)
    public void validate_DomainServicesWithDuplicateIds() {
        serviceList.add(new DomainServiceWithSomeId());
        serviceList.add(new DomainServiceWithDuplicateId());
        isfa = createIsisSessionFactoryAbstract(serviceList);
    }


    private IsisSessionFactoryAbstract createIsisSessionFactoryAbstract(List<Object> serviceList) {
        return new IsisSessionFactoryAbstract(deploymentType, configuration, specificationLoader, templateImageLoader, authenticationManager, authorizationManager, userProfileLoader, persistenceSessionFactory, serviceList, oidMarshaller) {
        };
    }
}
