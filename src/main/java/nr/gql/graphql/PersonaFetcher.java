package nr.gql.graphql;

import static com.fasterxml.jackson.databind.DeserializationFeature.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import graphql.GraphQLException;
import graphql.schema.DataFetcher;
import nr.gql.models.Persona;
import nr.gql.repository.RPersona;

@Component
public class PersonaFetcher {

    private @Autowired RPersona rp;

    // DataFetcher<?> getPersonas = dataFetchingEnvironment -> rp.findAll();
    DataFetcher<?> getPersonas() {
        return dt -> {
            validateAuth(dt.getContext());
            return rp.findAll();
        };
    }

    DataFetcher<?> getPersona() {
        return dt -> rp.findById(dt.getArgument("id")).orElseThrow(this::gqlEr);
    }

    DataFetcher<?> savePersona() {
        return dt -> rp.save(convert(dt.getArgument("dt"), Persona.class));
    }

    DataFetcher<?> deletePersona() {
        return dt -> {
            rp.findById(dt.getArgument("id")).ifPresentOrElse(rp::delete, this::gqlEr);
            return true;
        };
    }

    GraphQLException gqlEr() {
        throw new GraphQLException("No se realizó la acción");
    }

    public static <T> T convert(Object from, Class<T> to) {
        return new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false).convertValue(from, to);
    }

    public void validateAuth(WebRequest wq) {
        if (wq.getHeader("tk") == null || wq.getHeader("tk").isEmpty() || wq.getHeader("tk").isBlank())
            throw new GraphQLException("No hay token");

        System.out.println(wq.getHeader("tk"));
    }

    // public static <T> T convert(Object fromValue, Class<T> toValueType) {
    // ObjectMapper map = new ObjectMapper();
    // return map.configure(FAIL_ON_UNKNOWN_PROPERTIES,
    // false).convertValue(fromValue, toValueType);
    // }

}
