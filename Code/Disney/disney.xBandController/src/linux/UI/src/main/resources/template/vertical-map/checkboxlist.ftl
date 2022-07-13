<#--
/*
 * $Id: checkboxlist.ftl 1366934 2012-07-29 20:10:06Z jogep $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
-->
<#assign itemCount = 0/>
<#if parameters.list??>
<@s.iterator value="parameters.list">
    <#assign itemCount = itemCount + 1/>
    <#assign item = stack.findValue('top')/>
    <#if (item.key)??>
      <#assign itemKey = item.key/>
    <#else>
      <#assign itemKey = stack.findValue('top')/>
    </#if>
    <#if (item.value.text)??>
        <#assign itemValue = item.value.text/>
    <#else>
        <#assign itemValue = stack.findString('top')/>
    </#if>
    <#if (item.value.cssClass)??>
      <#assign itemCssClass = item.value.cssClass/>
    <#else>
      <#assign itemCssClass = ''/>
    </#if>
    <#if (item.value.cssStyle)??>
      <#assign itemCssStyle = item.value.cssStyle/>
    <#else>
      <#assign itemCssStyle = ''/>
    </#if>
    <#if (item.value.tip)??>
      <#assign itemTitle = item.value.tip/>
    <#else>
      <#assign itemTitle = ''/>
    </#if>
    <#if (item.value.disabled)?? && item.value.disabled == true>
      <#assign itemDisabled = true/>
    <#else>
      <#assign itemDisabled = false/>
    </#if>

    <#assign itemKeyStr=itemKey.toString() />
<input type="checkbox" name="${parameters.name?html}" value="${itemKeyStr?html}"
       id="${parameters.name?html}-${itemCount}"<#rt/>
    <#if tag.contains(parameters.nameValue, itemKey)>
       checked="checked"<#rt/>
    </#if>
    <#if itemDisabled>
       disabled="disabled"<#rt/>
    </#if>
    <#if itemCssClass?if_exists != "">
     class="${itemCssClass?html}"<#rt/>
    <#else>
        <#if parameters.cssClass??>
     class="${parameters.cssClass?html}"<#rt/>
        </#if>
    </#if>
    <#if itemCssStyle?if_exists != "">
     style="${itemCssStyle?html}"<#rt/>
    <#else>
        <#if parameters.cssStyle??>
     style="${parameters.cssStyle?html}"<#rt/>
        </#if>
    </#if>
    <#if itemTitle?if_exists != "">
     title="${itemTitle?html}"<#rt/>
    <#else>
        <#if parameters.title??>
     title="${parameters.title?html}"<#rt/>
        </#if>
    </#if>
    <#include "/${parameters.templateDir}/simple/css.ftl" />
    <#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
    <#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
        />
<label for="${parameters.name?html}-${itemCount}" class="checkboxLabel"
    <#if itemDisabled>
        disabled="disabled"<#rt/>
    </#if>
    <#if itemTitle?if_exists != "">
     title="${itemTitle?html}"<#rt/>
    <#else>
        <#if parameters.title??>
     title="${parameters.title?html}"<#rt/>
        </#if>
    </#if>
    >${itemValue?html}
 </label>
<br>
</@s.iterator>
    <#else>
    &nbsp;
</#if>
<input type="hidden" id="__multiselect_${parameters.id?html}" name="__multiselect_${parameters.name?html}"
       value=""<#rt/>
<#if parameters.disabled?default(false)>
       disabled="disabled"<#rt/>
</#if>
        />
