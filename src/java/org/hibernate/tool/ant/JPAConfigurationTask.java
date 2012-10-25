package org.hibernate.tool.ant;

import java.io.File;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.util.ReflectHelper;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.xml.sax.EntityResolver;

public class JPAConfigurationTask extends ConfigurationTask {

	private String persistenceUnit = null;

	private String basePackage = null;

	public JPAConfigurationTask() {
		setDescription("JPA Configuration");
	}

	protected Configuration createConfiguration() {
		try {
			Map overrides = new HashMap();
			Properties p = getProperties();

			if (p != null) {
				overrides.putAll(p);
			}

			Class clazz = ReflectHelper.classForName(
					"org.hibernate.ejb.Ejb3Configuration",
					JPAConfigurationTask.class);
			Object ejb3cfg = clazz.newInstance();

			if (entityResolver != null) {
				Class resolver = ReflectHelper.classForName(entityResolver,
						this.getClass());
				Object object = resolver.newInstance();
				Method method = clazz.getMethod("setEntityResolver",
						new Class[] { EntityResolver.class });
				method.invoke(ejb3cfg, new Object[] { object });
			}

			Method method = clazz.getMethod("configure", new Class[] {
					String.class, Map.class });
			if (method.invoke(ejb3cfg, new Object[] { persistenceUnit,
					overrides }) == null) {
				throw new BuildException("Persistence unit not found: '"
						+ persistenceUnit + "'.");
			}

			if (basePackage != null) {
				Class entity = ReflectHelper.classForName(
						"javax.persistence.Entity", JPAConfigurationTask.class);
				Collection urls = ClasspathHelper.forPackage(basePackage, null);
				Scanner[] scanners = new Scanner[] { new SubTypesScanner(),
						new TypeAnnotationsScanner() };
				Reflections reflections = new Reflections(
						new ConfigurationBuilder().addUrls(urls).addScanners(
								scanners));

				Set jpaEntities = reflections.getTypesAnnotatedWith(entity);
				if (jpaEntities != null && jpaEntities.size() > 0) {
					log(MessageFormat.format(
							"Found {0} entities under package {1}",
							new Object[] { new Integer(jpaEntities.size()),
									basePackage }), Project.MSG_DEBUG);

					Method addAnnotatedClass = clazz.getMethod(
							"addAnnotatedClass", new Class[] { Class.class });
					Iterator i = jpaEntities.iterator();
					while (i.hasNext()) {
						Object entityClass = i.next();
						if (entityClass != null) {
							addAnnotatedClass.invoke(ejb3cfg,
									new Object[] { entityClass });
							log(MessageFormat.format(
									"Add annotated entity {0}.",
									new Object[] { entityClass }),
									Project.MSG_DEBUG);
						}
					}
				} else {
					log(MessageFormat.format(
							"No entities found for package \"{0}\"",
							new Object[] { basePackage }), Project.MSG_INFO);
				}
			}

			method = clazz.getMethod("getHibernateConfiguration", new Class[0]);
			return (Configuration) method.invoke(ejb3cfg, null);
		} catch (HibernateException he) {
			throw new BuildException(he);
		} catch (BuildException be) {
			throw be;
		} catch (Exception t) {
			throw new BuildException(
					"Problems in creating a configuration for JPA. Have you remembered to add hibernate EntityManager jars to the classpath ?",
					t);
		}

	}

	protected void doConfiguration(Configuration configuration) {
	}

	protected void validateParameters() throws BuildException {

	}

	public String getPersistenceUnit() {
		return persistenceUnit;
	}

	public void setPersistenceUnit(String persistenceUnit) {
		this.persistenceUnit = persistenceUnit;
	}

	public void setConfigurationFile(File configurationFile) {
		complain("configurationfile");
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public String getBasePackage() {
		return basePackage;
	}

	private void complain(String param) {
		throw new BuildException(
				"<"
						+ getTaskName()
						+ "> currently only support autodiscovery from META-INF/persistence.xml. Thus setting the "
						+ param + " attribute is not allowed");
	}

}
