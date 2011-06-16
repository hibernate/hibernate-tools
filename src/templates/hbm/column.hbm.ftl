<#if column.isFormula()>
<formula>${column.getFormula()}</formula>
<#else>
<column name="${column.quotedName}" ${c2h.columnAttributes(column)}<#if column.comment?exists && column.comment?trim?length!=0>>
<comment>${c2h.escape(column.comment)}</comment>
</column><#else>/>
</#if>
</#if>