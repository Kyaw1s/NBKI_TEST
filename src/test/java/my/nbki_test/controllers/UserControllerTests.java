package my.nbki_test.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.nbki_test.entities.User;
import my.nbki_test.repositories.UserRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    private final int USER_AMOUNT = 100000;

    @Test
    public void addUsers() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();

        for(int i = 1; i <= USER_AMOUNT; i++) {
            User user = new User(i, "Ivan", "Mitaevi");

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isCreated());
        }

        Assertions.assertEquals(USER_AMOUNT, userRepository.count());
    }

    @Test
    public void checkSpeed() throws InterruptedException {
        final int requestsAmount = 100;
        final int recordsToFetch = 1000000;

        final int recordsPerRequest = recordsToFetch / requestsAmount;

        ExecutorService executorService = Executors.newFixedThreadPool(requestsAmount);
        List<Callable<Long>> futures = new ArrayList<>(requestsAmount);

        for(int i = 0; i < requestsAmount; i++) {
            int startId = (i * recordsPerRequest + 1) % (USER_AMOUNT - recordsPerRequest);
            futures.add(getExecutionTimeCallable(recordsPerRequest,  startId));
        }

        List<Future<Long>> results = executorService.invokeAll(futures);
        List<Long> durations = new ArrayList<>(requestsAmount);
        for(Future<Long> result : results) {
            try {
                durations.add(result.get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();

        printStatistics(durations);
    }


    private Callable<Long> getExecutionTimeCallable(final int recordsPerRequest, final int startId) {
        return () -> {
            long start = System.currentTimeMillis();

            for(int j = 1; j <= recordsPerRequest; j++) {
                mockMvc.perform(get("/users/" + (j + startId))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
            }

            long end = System.currentTimeMillis();
            return end - start;
        };
    }

    private void printStatistics(List<Long> durations) {
        long total = durations.stream().mapToLong(Long::longValue).sum();


        List<Long> sortedDurations = durations.stream().sorted().toList();
        double median = sortedDurations.get(sortedDurations.size() / 2);
        double percentile95 = sortedDurations.get((int) (sortedDurations.size() * 0.95));
        double percentile99 = sortedDurations.get((int) (sortedDurations.size() * 0.99));

        System.out.println("Total duration: " + total + "ms");
        System.out.println("Median duration: " + median + "ms");
        System.out.println("95th percentile: " + percentile95 + "ms");
        System.out.println("99th percentile: " + percentile99 + "ms");
    }
}
