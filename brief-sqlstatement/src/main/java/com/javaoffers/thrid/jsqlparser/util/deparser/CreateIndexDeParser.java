/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package com.javaoffers.thrid.jsqlparser.util.deparser;

import com.javaoffers.thrid.jsqlparser.statement.create.index.CreateIndex;
import com.javaoffers.thrid.jsqlparser.statement.create.table.Index;

import static java.util.stream.Collectors.joining;

public class CreateIndexDeParser extends AbstractDeParser<CreateIndex> {

    public CreateIndexDeParser(StringBuilder buffer) {
        super(buffer);
    }

    @Override
    public void deParse(CreateIndex createIndex) {
        Index index = createIndex.getIndex();

        buffer.append("CREATE ");

        if (index.getType() != null) {
            buffer.append(index.getType());
            buffer.append(" ");
        }

        buffer.append("INDEX ");
        buffer.append(index.getName());
        buffer.append(" ON ");
        buffer.append(createIndex.getTable().getFullyQualifiedName());

        String using = index.getUsing();
        if (using != null) {
            buffer.append(" USING ");
            buffer.append(using);
        }

        if (index.getColumnsNames() != null) {
            buffer.append(" (");
            buffer.append(index.getColumnWithParams().stream()
                    .map(cp -> cp.columnName + (cp.getParams() != null ? " " + String.join(" ", cp.getParams()) : ""))
                    .collect(joining(", ")));
            buffer.append(")");
        }

        if (createIndex.getTailParameters() != null) {
            for (String param : createIndex.getTailParameters()) {
                buffer.append(" ").append(param);
            }
        }
    }

}
