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
package integtests.repo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import integtests.AbstractIntegTest;

import java.util.List;

import dom.todo.ToDoItem;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class ToDoItems_finders extends AbstractIntegTest {

    private int notYetCompletedSize;
    private int completedSize;

    @Before
    public void setUp() throws Exception {
        final List<ToDoItem> notYetCompleteItems = wrap(toDoItems).notYetComplete();
        final List<ToDoItem> completedItems = wrap(toDoItems).complete();
        
        notYetCompletedSize = notYetCompleteItems.size();
        completedSize = completedItems.size();
        
        assertThat(notYetCompletedSize, is(Matchers.greaterThan(5)));
    }

    @Test
    public void complete_and_notYetComplete() throws Exception {
        
        // given
        List<ToDoItem> notYetCompleteItems = wrap(toDoItems).notYetComplete();
        final ToDoItem toDoItem = wrap(notYetCompleteItems.get(0));
        
        // when
        toDoItem.completed();
        
        // then
        assertThat(wrap(toDoItems).notYetComplete().size(), is(notYetCompletedSize-1));
        assertThat(wrap(toDoItems).complete().size(), is(completedSize+1));
        
        // and when
        toDoItem.notYetCompleted();
        
        // then
        assertThat(wrap(toDoItems).notYetComplete().size(), is(notYetCompletedSize));
        assertThat(wrap(toDoItems).complete().size(), is(completedSize));
    }


}