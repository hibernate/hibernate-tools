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

import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.maven.plugins.annotations.Parameter;

public class Fixup {

    /**
     * The regular expression to match against the generated schema DDL.
     *
     * <p>
     * This must be a valid regular expression suitable as a {@link Pattern}.
     */
    @Parameter(required = true)
    private String pattern;

    /**
     * The replacement expression to apply to all matches found.
     *
     * <p>
     * This must be a valid regular expression replacement string suitable for {@code Matcher.replaceAll()}.
     */
    @Parameter(required = true)
    private String replacement;

    public String applyTo(String string) {
        if (this.pattern == null)
            throw new IllegalArgumentException("no pattern specified");
        // Note: Maven passes empty strings as nulls, so map them here
        final String replace = Optional.ofNullable(this.replacement).orElse("");
        final Pattern regex;
        try {
            regex = Pattern.compile(this.pattern);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("invalid regular expression: " + e.getMessage(), e);
        }
        return regex.matcher(string).replaceAll(replace);
    }
}
