package com.star.reward.application.assembler;

import com.star.reward.application.command.TaskExecutionQueryCommand;
import com.star.reward.domain.taskinstance.model.query.TaskInstanceQueryParam;
import com.star.reward.domain.taskinstance.model.valueobject.InstanceState;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 任务执行转换器（Application → Domain 边界）
 * Command → TaskInstanceQueryParam、Entity → Response
 */
public final class TaskExecutionAssembler {

    private TaskExecutionAssembler() {
    }

    /**
     * Command → TaskInstanceQueryParam（供 Repository listByQuery 使用）
     * page/pageSize 为 null 或 <=0 时使用默认 1、10
     */
    public static TaskInstanceQueryParam commandToTaskInstanceQueryParam(
            TaskExecutionQueryCommand command, Long executeById, Set<InstanceState> stateFilter) {
        List<String> instanceStates = stateFilter.stream()
                .map(InstanceState::getCode)
                .collect(Collectors.toList());
        TaskInstanceQueryParam param = TaskInstanceQueryParam.builder()
                .executeById(executeById)
                .instanceStates(instanceStates)
                .orderBy("create_time DESC")
                .build();
        int page = command != null && command.getPage() > 0 ? command.getPage() : 1;
        int pageSize = command != null && command.getPageSize() > 0 ? command.getPageSize() : 10;
        param.setPage(page);
        param.setPageSize(pageSize);
        return param;
    }
}
