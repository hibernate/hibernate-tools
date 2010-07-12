
${pojo.getShortName()}
${pojo.shortName}

<#foreach cl in [1,2,3]>
<#include "freeinc.ftl"/> 
<#include "freeinc.ftl"/> 
<@greet person=cl/>
</#foreach>


<#macro greet person>
  <font size="+2">Hello ${person} ${pojo.shortName}!</font>
</#macro>  


<#list .data_model?keys as item>
 ${item}
</#list>