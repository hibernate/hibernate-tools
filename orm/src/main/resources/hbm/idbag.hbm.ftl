<#assign value = property.value>
<#assign keyValue = value.getKey()>
<#assign elementValue = value.getElement()>
<#assign elementTag = c2h.getCollectionElementTag(property)>

	<idbag name="${property.name}" 
	<#include "collection-tableattr.hbm.ftl">
	lazy="${c2h.getCollectionLazy(value)}"
	<#if property.cascade != "none">
        cascade="${property.cascade}"
	</#if>
	<#if !property.basicPropertyAccessor>
        access="${property.propertyAccessorName}"
	</#if>
	<#if c2h.hasFetchMode(property)> fetch="${c2h.getFetchMode(property)}"</#if>>
	 <#assign metaattributable=property>
	 	<#include "meta.hbm.ftl">
	 	<collection-id type="${value.identifier.typeName}" 
	 		column="${value.getIdentifier().getColumns().iterator().next().getQuotedName()}">
	 		<generator class="${value.identifier.identifierGeneratorStrategy}"/>
	 	</collection-id>
 		<key> 
        	<#list keyValue.columns as column>
          		<#include "column.hbm.ftl">
        	</#list>
        	</key>
		<#include "${elementTag}-element.hbm.ftl">
     	</idbag>
     	