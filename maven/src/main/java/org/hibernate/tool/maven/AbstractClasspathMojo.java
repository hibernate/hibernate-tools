/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2016-2020 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL),
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.maven;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Comparator;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * Support superclass for mojo's that need to put stuff (depenedencies, etc.) on the classpath.
 */
public abstract class AbstractClasspathMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;

    /**
     * Add whatever is needed to the classpath that should be visible during execution.
     */
    protected abstract void addClasspathElements(Set<URL> elements) throws DependencyResolutionRequiredException;

// AbstractMojo

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        // Get classpath elements
        final LinkedHashSet<URL> urls = new LinkedHashSet<>();
        try {
            this.addClasspathElements(urls);
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("error adding classpath elements: " + e.getMessage(), e);
        }

        // Debug
        this.getLog().debug(this.getClass().getSimpleName() + ": classpath for execution:");
        urls.forEach(url -> this.getLog().debug("  " + url));

        // Create corresponding class loader and execute
        final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        final URLClassLoader classpathLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), contextLoader);
        this.runWithLoader(classpathLoader, this::executeWithClasspath);
    }

// Subclass Hooks

    protected abstract void executeWithClasspath() throws MojoExecutionException, MojoFailureException;

// Internal Methods

    /**
     * Execute the given action while the specified {@link ClassLoader} is the current thread's context loader.
     */
    protected void runWithLoader(ClassLoader loader, MojoRunnable action) throws MojoExecutionException, MojoFailureException {
        final ClassLoader origLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        try {
            action.run();
        } finally {
            Thread.currentThread().setContextClassLoader(origLoader);
        }
    }

    /**
     * Convert a {@link File} into an {@link URL}.
     */
    protected URL toURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("unexpected error", e);
        }
    }

// MojoRunnable

    @FunctionalInterface
    public interface MojoRunnable {
        void run() throws MojoExecutionException, MojoFailureException;
    }
}
