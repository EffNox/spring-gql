package nr.gql.graphql.auth;

import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.spring.web.servlet.GraphQLInvocation;
import graphql.spring.web.servlet.GraphQLInvocationData;

@Component
public @Primary class GraphQLCustom implements GraphQLInvocation {

    private @Autowired GraphQL graphQL;

    @Override
    public CompletableFuture<ExecutionResult> invoke(GraphQLInvocationData invocationData, WebRequest webRequest) {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(invocationData.getQuery())
                .context(webRequest).operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables()).build();
        return graphQL.executeAsync(executionInput);
    }

}
