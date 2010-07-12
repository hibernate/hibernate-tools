${pojo.getShortName()}
${pojo.shortName}

<#foreach cl in [1,2,3]>
 <@greet person=cl/>
</#foreach>


<#macro greet person>
  <font size="+2">Hello ${person} ${propertythatdoesnotexist}!</font>
</#macro>  
