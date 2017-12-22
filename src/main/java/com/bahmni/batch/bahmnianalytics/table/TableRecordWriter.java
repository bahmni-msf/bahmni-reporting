package com.bahmni.batch.bahmnianalytics.table;

import com.bahmni.batch.bahmnianalytics.helper.FreeMarkerEvaluator;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Scope(value = "prototype")
class TableRecordWriter implements ItemWriter<Map<String, Object>> {

    @Qualifier("postgresJdbcTemplate")
    @Autowired
    private JdbcTemplate postgresJdbcTemplate;

    @Autowired
    private FreeMarkerEvaluator<TableRecordHolder> tableRecordHolderFreeMarkerEvaluator;

    @Autowired
    public TableMetadataGenerator tableMetadataGenerator;

    public void setTableData(TableData tableData) {
        this.tableData = tableData;
    }

    TableData tableData;

    @Override
    public void write(List<? extends Map<String, Object>> items) throws Exception {
        List<Map<String, Object>> recordList = new ArrayList<>();
        items.forEach(item -> {
            recordList.add(item);
        });
        TableRecordHolder tableRecordHolder = new TableRecordHolder(recordList, tableData.getName());
        String sql = tableRecordHolderFreeMarkerEvaluator.evaluate("insertObs.ftl", tableRecordHolder);
        postgresJdbcTemplate.execute(sql);
    }
}
