<#--
~ Copyright 2010 - 2025 Red Hat, Inc.
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~     http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" basis,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
-->
    <one-to-one
	    name="${property.name}"
	    class="${c2j.getJavaTypeName(property, false)}"
<#if property.value.referencedPropertyName?exists> 
        property-ref="${property.value.referencedPropertyName}"
</#if>	
<#if !property.basicPropertyAccessor>
        access="${property.propertyAccessorName}"
</#if>
<#if property.cascade != "none">
        cascade="${property.cascade}"
</#if>
<#if property.value.constrained>
        constrained="true"
</#if>

<#if property.value.hasFormula()>
<#assign formula = c2h.getFormulaForProperty(property)>
<#if formula?exists>
        formula="${formula.text}"
</#if>
</#if>
    >
   </one-to-one>
