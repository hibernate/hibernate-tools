<many-to-many entity-name="${property.getValue().getElement().referencedEntityName}" <#-- lookup needed classname -->
<#if property.value.referencedPropertyName?exists>
        property-ref="${property.value.referencedPropertyName}"
</#if>>
  <#list elementValue.selectables as column>
        <#include "column.hbm.ftl">
  </#list>
</many-to-many>
