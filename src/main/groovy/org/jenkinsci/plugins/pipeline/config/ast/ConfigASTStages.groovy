/*
 * The MIT License
 *
 * Copyright (c) 2016, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.pipeline.config.ast

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import net.sf.json.JSONArray
import org.jenkinsci.plugins.pipeline.config.model.Stage
import org.jenkinsci.plugins.pipeline.config.model.Stages
import org.jenkinsci.plugins.pipeline.config.validator.ConfigValidator


/**
 * Represents the collection of {@link Stage}s to be executed in the build. Corresponds to {@link Stages}.
 * @author Andrew Bayer
 */
@ToString(includeSuper = true, includeSuperProperties = true)
@EqualsAndHashCode(callSuper = true)
@SuppressFBWarnings(value="SE_NO_SERIALVERSIONID")
public final class ConfigASTStages extends ConfigASTElement {
    List<ConfigASTStage> stages = []

    public ConfigASTStages(Object sourceLocation) {
        super(sourceLocation)
    }

    @Override
    public JSONArray toJSON() {
        JSONArray a = new JSONArray()
        stages.each { s ->
            a.add(s.toJSON())
        }
        return a
    }

    @Override
    public void validate(ConfigValidator validator) {
        validator.validateElement(this)

        stages.each { s ->
            s?.validate(validator)
        }
    }

    @Override
    public String toGroovy() {
        String stagesString = stages.collect { s ->
            s.toGroovy()
        }.join("\n")
        return "stages {\n${stagesString}\n}\n"
    }
}
