package aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CitiBikeRequestHandlerTest {

    @Test
    void handleRequest() throws IOException {
        //given
        String body = Files.readString(Path.of("request.json"));
        Context context = mock(Context.class);
        APIGatewayProxyRequestEvent event = mock(APIGatewayProxyRequestEvent.class);
        when(event.getBody()).thenReturn(body);
        CitiBikeRequestHandler handler = new CitiBikeRequestHandler();

        //when
        CitiBikeResponse response = handler.handleRequest(event, context);

        //then
        assertEquals("Lenox Ave & W 146 St", response.start.name);
        assertEquals("Berry St & N 8 St", response.end.name);
    }
}
