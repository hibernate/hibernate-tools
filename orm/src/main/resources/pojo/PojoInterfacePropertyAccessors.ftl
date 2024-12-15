<#-- if interface -->
<#-- Property accessors for interface -->
<#list pojo.getAllPropertiesIterator() as property>
  <#if pojo.getMetaAttribAsBool(property, "gen-property", true)>

    <#if c2j.getMetaAsString(property, "field-description")?trim?length gt 0>
    /**
    ${c2j.toJavaDoc(c2j.getMetaAsString(property, "field-description"), 0)}
     */
    </#if>
    ${pojo.getPropertyGetModifiers(property)} ${pojo.getJavaTypeName(property, jdk5)} ${pojo.getGetterSignature(property)}();
    
    ${pojo.getPropertySetModifiers(property)} void set${pojo.getPropertyName(property)}(${pojo.getJavaTypeName(property, jdk5)} ${property.name});
  </#if>
</#list>
