/**
*    Copyright 2011, Big Switch Networks, Inc. 
*    Originally created by David Erickson, Stanford University
* 
*    Licensed under the Apache License, Version 2.0 (the "License"); you may
*    not use this file except in compliance with the License. You may obtain
*    a copy of the License at
*
*         http://www.apache.org/licenses/LICENSE-2.0
*
*    Unless required by applicable law or agreed to in writing, software
*    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
*    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
*    License for the specific language governing permissions and limitations
*    under the License.
**/

package net.floodlightcontroller.core;

public interface IListener<T> {
    public enum Command {
        CONTINUE, STOP
    }
    
    /**
     * The name assigned to this listener
     * 监听器的名字
     * @return
     */
    public String getName();

    /**
     * Check if the module called name is a callback ordering prerequisite
     * for this module.  In other words, if this function returns true for 
     * the given name, then this listener will be called after that
     * message listener.
     * 检查名为name的模块是否是给这个模块执行回调命令的前提，
     * 换句话说，如果该方法对于给定的名称返回true，那么这个监听器会在该消息监听器后调用。
     * 
     * 如果将模块A的名称传给模块B的isCallbackOrderingPrereq方法，且该方法返回True，
     * 那就表示模块A要在模块B之前执行，如果返回False那就表示模块A不在乎自己是否在模块B之前执行
     * @param type the object type to which this applies
     * @param name the name of the module
     * @return whether name is a prerequisite.
     */
    public boolean isCallbackOrderingPrereq(T type, String name);

    /**
     * Check if the module called name is a callback ordering post-requisite
     * for this module.  In other words, if this function returns true for 
     * the given name, then this listener will be called before that
     * message listener.
     * 检查名为name的模块是否是这个模块执行回调命令的后继条件，换句话说，如果该方法对于给定的名
     * 称返回true，那么这个监听器会在该消息监听器之前调用。
     * 
     * 如果将模块A的名称传给模块B的isCallbackOrderingPostreq方法，且该方法返回True，
     * 那就表示模块A要在模块B之后执行，如果返回False那就表示模块A不在乎自己是否在模块B之后执行。
     * @param type the object type to which this applies
     * @param name the name of the module
     * @return whether name is a post-requisite.
     */
    public boolean isCallbackOrderingPostreq(T type, String name);
}
