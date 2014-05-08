<#if pojo.needsEqualsHashCode() && !clazz.superclass?exists>   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !org.hibernate.Hibernate.getClass(other).equals(getClass()) ) return false;
		 ${pojo.getProxyOrDeclarationName()} castOther = ( ${pojo.getProxyOrDeclarationName()} ) other; 
         
		 return ${pojo.generateEquals("this", "castOther", jdk5)};
   }
   
   public int hashCode() {
         int result = 17;
         
<#foreach property in pojo.getAllPropertiesIterator()>         ${pojo.generateHashCode(property, "result", "this", jdk5)}
</#foreach>         return result;
   }   
</#if>