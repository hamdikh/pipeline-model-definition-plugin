package org.jenkinsci.plugins.pipeline.config.ast

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString;

/**
 * Either single value, or named args
 *
 * @author Kohsuke Kawaguchi
 * @author Andrew Bayer
 */
@ToString(includeSuper = true, includeSuperProperties = true)
@EqualsAndHashCode(callSuper = true)
@SuppressFBWarnings(value="SE_NO_SERIALVERSIONID")
public abstract class ConfigASTArgumentList extends ConfigASTElement {
    /*package*/ ConfigASTArgumentList(Object sourceLocation) { super(sourceLocation) }
}
