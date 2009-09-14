/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.shell.parser;

/**
 * Represents an <em>opaque</em> argument.
 *
 * @version $Rev$ $Date$
 */
public class ASTOpaqueString
    extends StringSupport
{
    public ASTOpaqueString(final int id) {
        super(id);
    }

    public ASTOpaqueString(final CommandLineParser p, final int id) {
        super(p, id);
    }

    public String getValue() {
        return unquote(super.getValue());
    }

    /** Accept the visitor. **/
    public Object jjtAccept(final CommandLineParserVisitor visitor, final Object data) {
        return visitor.visit(this, data);
    }
}