package nr.gql.graphql;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;

@Component
public class GraphQLProvider {

    private @Autowired PersonaFetcher fetPer;

    private GraphQL graphQL;

    public @Bean GraphQL graphQL() {
        return graphQL;
    }

    @PostConstruct
    public void init() throws IOException {
        URL url = Resources.getResource("schema.gql");
        String schemaString = Resources.toString(url, Charsets.UTF_8);
        GraphQLSchema graphQLSchema = buildSchema(schemaString);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    private GraphQLSchema buildSchema(String schemaString) {
        TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(schemaString);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring().type(mutationBuilder()).type(queryBuilder()).build();
    }

    private TypeRuntimeWiring.Builder mutationBuilder() {
        return TypeRuntimeWiring.newTypeWiring("Mutation")
        .dataFetcher("savePersona",fetPer.savePersona())
        .dataFetcher("deletePersona",fetPer.deletePersona());
    }

    private TypeRuntimeWiring.Builder queryBuilder() {
        return TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("getPersonas", fetPer.getPersonas())
                .dataFetcher("getPersona", fetPer.getPersona());
    }

}
