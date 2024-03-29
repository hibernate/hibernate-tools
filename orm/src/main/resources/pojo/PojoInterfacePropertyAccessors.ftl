<#-- if interface -->
<#-- Property accessors for interface -->
<#list pojo.getAllPropertiesIterator() as property><#if pojo.getMetaAttribAsBool(property, "gen-property", true)>   /**
   ${c2j.toJavaDoc(c2j.getMetaAsString(property, "field-description"), 4)} */
   ${pojo.getPropertyGetModifiers(property)} ${pojo.getJavaTypeName(property, jdk5)} ${pojo.getGetterSignature(property)}();
    
   ${pojo.getPropertySetModifiers(property)} void set${pojo.getPropertyName(property)}(${pojo.getJavaTypeName(property, jdk5)} ${property.name});
</#if></#list>