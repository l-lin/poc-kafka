package lin.louis.poc.hbp.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import lin.louis.poc.hbp.dto.HeartBeatDTO;
import lin.louis.poc.hbp.repository.HBRepository;
import lin.louis.poc.models.HeartBeatQRS;


@WebMvcTest(controllers = HBController.class)
class HBControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private HBRepository HBRepository;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void create() throws Exception {
		var heartBeatDTO = new HeartBeatDTO(123, 80, HeartBeatQRS.A);
		var requestBody = objectMapper.writeValueAsString(heartBeatDTO);

		mockMvc.perform(post("/heart-beats").contentType(MediaType.APPLICATION_JSON).content(requestBody))
			   .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
	}
}
