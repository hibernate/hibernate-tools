<#if pojo.getOuterClass()??>
<#else>
${pojo.getPackageDeclaration()}
// Generated ${date} by Hibernate Tools ${version}
</#if>

<#assign classbody>
<#include "PojoTypeDeclaration.ftl"/> {

<#if !pojo.isInterface()>
<#include "PojoFields.ftl"/>

<#include "PojoConstructors.ftl"/>

<#include "PojoPropertyAccessors.ftl"/>

<#include "PojoToString.ftl"/>

<#include "PojoEqualsHashcode.ftl"/>

<#include "PojoInnerClasses.ftl"/>

<#else>
<#include "PojoInterfacePropertyAccessors.ftl"/>

</#if>
<#include "PojoExtraClassCode.ftl"/>

}
</#assign>

<#if pojo.getOuterClass()??>
<#else>
${pojo.generateImports()}
</#if>
${classbody}

