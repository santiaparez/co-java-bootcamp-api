package com.example.bootcamp.web.handler;

import com.example.bootcamp.domain.model.BootcampSummary;
import com.example.bootcamp.domain.model.CapabilitySummary;
import com.example.bootcamp.domain.model.PaginatedBootcamp;
import com.example.bootcamp.domain.model.TechnologySummary;
import com.example.bootcamp.web.dto.Responses.BootcampPageResponse;
import com.example.bootcamp.web.dto.Responses.BootcampResponse;
import com.example.bootcamp.web.dto.Responses.CapabilityResponse;
import com.example.bootcamp.web.dto.Responses.TechnologyResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BootcampResponseMapperTest {

  @Test
  void page_shouldMapDomainPageToResponse() {
    TechnologySummary technology = new TechnologySummary("tech-1", "Java");
    CapabilitySummary capability = new CapabilitySummary("cap-1", "Backend", "desc", List.of(technology), 1);
    BootcampSummary bootcamp = new BootcampSummary("boot-1", "Bootcamp", "description", LocalDate.EPOCH, 6, List.of(capability), 1);
    PaginatedBootcamp page = new PaginatedBootcamp(List.of(bootcamp), 0, 5, 1, 1);

    BootcampPageResponse response = BootcampResponseMapper.page(page);

    BootcampPageResponse expected = new BootcampPageResponse(
        List.of(new BootcampResponse(
            "boot-1",
            "Bootcamp",
            "description",
            LocalDate.EPOCH,
            6,
            List.of(new CapabilityResponse(
                "cap-1",
                "Backend",
                "desc",
                List.of(new TechnologyResponse("tech-1", "Java")),
                1
            )),
            1
        )),
        0,
        5,
        1,
        1
    );

    assertEquals(expected, response);
  }
}
