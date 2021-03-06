package org.rakam.analysis;

import org.rakam.analysis.metadata.QueryMetadataStore;
import org.rakam.collection.SchemaField;
import org.rakam.plugin.ContinuousQuery;
import org.rakam.report.QueryExecution;
import org.rakam.util.AlreadyExistsException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class ContinuousQueryService {

    protected final QueryMetadataStore database;

    public ContinuousQueryService(QueryMetadataStore database) {
        this.database = database;
    }

    public abstract QueryExecution create(String project, ContinuousQuery report, boolean replayHistoricalData) throws AlreadyExistsException;

    public abstract CompletableFuture<Boolean> delete(String project, String tableName);

    public List<ContinuousQuery> list(String project) {
        return database.getContinuousQueries(project);
    }

    public ContinuousQuery get(String project, String tableName) {
        return database.getContinuousQuery(project, tableName);
    }

    public abstract Map<String, List<SchemaField>> getSchemas(String project);

    public abstract boolean test(String project, String query);

    public abstract QueryExecution refresh(String project, String tableName);
}
